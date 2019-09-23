package com.example.jovan.octopus.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jovan.octopus.R;
import com.example.jovan.octopus.adapters.AdapterFirm;
import com.example.jovan.octopus.data.FirmData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainPanelActivity extends AppCompatActivity implements View.OnTouchListener{

    public static final String SEARCH_BY_NAME = "-2";

    public static final String FIRM_ICON = "FIRM_ICON";
    public static final String FIRM_NAME = "FIRM_NAME";
    public static final String FIRM_PRICE = "FIRM_PRICE";
    public static final String FIRM_DESCRIPTION = "FIRM_DESCRIPTION";
    public static final String FIRM_WORK_TIME = "FIRM_WORK_TIME";

    private static final int FACTOR = 10;

    private String username;

    private double longitude;
    private double latitude;
    private float x1;
    private int activeMainLayout;

    private String currentCategoriesSelected;

    private TextView noRowsFoundTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search);

        this.activeMainLayout = R.layout.activity_main_search;
        RelativeLayout relativeLayout = findViewById(R.id.activity_main_search_relative_layout);
        relativeLayout.setOnTouchListener(this);

        Intent data = getIntent();
        this.username = data.getStringExtra(MainActivity.USERNAME);
        this.longitude = data.getDoubleExtra(MainActivity.LONGITUDE, 0.0f);
        this.latitude = data.getDoubleExtra(MainActivity.LATITUDE, 0.0f);
    }

    private void onChangeLayout() {
        final EditText searchEditText = findViewById(R.id.search_firm_by_name_text_view);
        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    new AsyncFetch().execute(SEARCH_BY_NAME, searchEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        this.noRowsFoundTextView = findViewById(R.id.no_result_text_view);
    }

    private String selectedCategories() {
        StringBuilder stringBuilder = new StringBuilder();

        CheckBox taxiCheckBox = findViewById(R.id.taxi_agency);
        CheckBox hotelCheckBox = findViewById(R.id.hotels_motels);
        CheckBox fastFoodCheckBox = findViewById(R.id.food_restaurants);

        if(taxiCheckBox.isChecked()) {
            stringBuilder.append("Taxi");
        }

        if(hotelCheckBox.isChecked()) {
            if(stringBuilder.length() > 0) {
                stringBuilder.append(",Hotel,Motel");
            } else {
                stringBuilder.append("Hotel,Motel");
            }
        }

        if(fastFoodCheckBox.isChecked()) {
            if(stringBuilder.length() > 0) {
                stringBuilder.append(",Restaurant");
            } else {
                stringBuilder.append("Restaurant");
            }
        }

        if(stringBuilder.length() == 0) {
            stringBuilder.append("NothingSelected!");
        }

        return stringBuilder.toString();
    }

    private int sortingCategories() {
        RadioButton distanceAsc = findViewById(R.id.sort_by_distance_asc);
        RadioButton distanceDesc = findViewById(R.id.sort_by_distance_desc);
        RadioButton priceAsc = findViewById(R.id.sort_by_price_asc);
        RadioButton priceDesc = findViewById(R.id.sort_by_price_desc);

        if(distanceAsc.isChecked()) {
            if(priceAsc.isChecked()) {
                return 4;
            } else {
                if(priceDesc.isChecked()) {
                    return 5;
                } else {
                    return 0;
                }
            }
        }

        if(distanceDesc.isChecked()) {
            if(priceAsc.isChecked()) {
                    return 6;
            } else {
                if(priceDesc.isChecked()) {
                    return 7;
                } else {
                    return 1;
                }
            }
        }

        if(priceAsc.isChecked()) {
            if(distanceAsc.isChecked()) {
                return 4;
            } else {
                if(distanceDesc.isChecked()) {
                    return 5;
                } else {
                    return 2;
                }
            }
        }

        if(priceAsc.isChecked()) {
            if(distanceAsc.isChecked()) {
                return 6;
            } else {
                if(distanceDesc.isChecked()) {
                    return 7;
                } else {
                    return 3;
                }
            }
        }

        return -1;
    }

    public void OnClickSearchButton(View view) {
        int sortingCategories = sortingCategories();
        String selectedCategories = selectedCategories();

        this.currentCategoriesSelected = selectedCategories;

        if(selectedCategories.equalsIgnoreCase("NothingSelected!")) {
            sortingCategories += FACTOR;
        }

        String longitude = Double.toString(this.longitude);
        String latitude = Double.toString(this.latitude);

        new AsyncFetch().execute(Integer.toString(sortingCategories), selectedCategories, longitude, latitude);

        activeMainLayout = R.layout.activity_main_panel;
        setContentView(activeMainLayout);

        onChangeLayout();
        RelativeLayout relativeLayoutLocation = findViewById(R.id.activity_main_panel_relative_layout);
        relativeLayoutLocation.setOnTouchListener(this);
    }

    public void onClickSpecificFirm(View view) {
        Intent specificFirmIntent = new Intent(this, SpecificFirmActivity.class);

        ImageView firmIconImageView = view.findViewById(R.id.firm_icon);
        TextView firmNameTextView = view.findViewById(R.id.firm_name);
        TextView firmPriceTextView = view.findViewById(R.id.firm_price);
        TextView firmDescriptionTextView = view.findViewById(R.id.firm_description);
        TextView workTimeTextView = findViewById(R.id.work_time);

        Bitmap bitmap = ((BitmapDrawable) firmIconImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageInByte = byteArrayOutputStream.toByteArray();

        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String firmNameString = firmNameTextView.getText().toString();
        String firmPriceString = firmPriceTextView.getText().toString();
        String firmDescriptionString = firmDescriptionTextView.getText().toString();
        String workTimeString = workTimeTextView.getText().toString();

        specificFirmIntent.putExtra(FIRM_ICON, imageInByte);
        specificFirmIntent.putExtra(FIRM_NAME, firmNameString);
        specificFirmIntent.putExtra(FIRM_PRICE, firmPriceString);
        specificFirmIntent.putExtra(FIRM_DESCRIPTION, firmDescriptionString);
        specificFirmIntent.putExtra(FIRM_WORK_TIME, workTimeString);

        startActivity(specificFirmIntent);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
            break;
            case MotionEvent.ACTION_UP:
                float x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MainActivity.MIN_DISTANCE) {
                    if (x2 > x1) { // Left to Right swipe action -> [Previous]
                        if(activeMainLayout == R.layout.activity_main_panel) {
                            activeMainLayout = R.layout.activity_main_search;
                            setContentView(activeMainLayout);

                            RelativeLayout relativeLayoutLocation = findViewById(R.id.activity_main_search_relative_layout);
                            relativeLayoutLocation.setOnTouchListener(this);
                        } else {
                            // No previous page from search panel view.
                        }
                    }
                    else { // Right to left swipe action -> [Next]
                        if(activeMainLayout == R.layout.activity_main_search) {
                            activeMainLayout = R.layout.activity_main_panel;
                            setContentView(activeMainLayout);

                            onChangeLayout();
                            RelativeLayout relativeLayoutLocation = findViewById(R.id.activity_main_panel_relative_layout);
                            relativeLayoutLocation.setOnTouchListener(this);
                        } else {
                            // No next page from main panel view.
                        }
                    }
            }
            break;
        }
        return true;
    }

    public void OnClickButtonFromFooter(View view) {
        if(activeMainLayout == R.layout.activity_main_panel) {
            activeMainLayout = R.layout.activity_main_search;
            setContentView(activeMainLayout);

            RelativeLayout relativeLayoutLocation = findViewById(R.id.activity_main_search_relative_layout);
            relativeLayoutLocation.setOnTouchListener(this);
        } else {
            activeMainLayout = R.layout.activity_main_panel;
            setContentView(activeMainLayout);

            onChangeLayout();
            RelativeLayout relativeLayoutLocation = findViewById(R.id.activity_main_panel_relative_layout);
            relativeLayoutLocation.setOnTouchListener(this);
        }
    }

    public void OnClickHeaderButton(View view) {
        switch (view.getId()) {
            case R.id.side_nav_bar_main_panel_button:
                LinearLayout sideLinearLayout = findViewById(R.id.side_nav_bar_main_panel_view);
                sideLinearLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.cart_button_main_panel:
                Intent shoppingCartIntent = new Intent(this, ShoppingCartActivity.class);
                startActivity(shoppingCartIntent);
                break;
        }
    }

    public void OnClickDismissSideBar(View view) {
        LinearLayout sideLinearLayout = findViewById(R.id.side_nav_bar_main_panel_view);
        sideLinearLayout.setVisibility(View.GONE);
    }

    private class AsyncFetch extends AsyncTask<String, String, String> {
        private ProgressDialog pdFetchingData;
        private HttpURLConnection httpURLConnection;
        private URL url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdFetchingData = new ProgressDialog(MainPanelActivity.this);
            pdFetchingData.setMessage("\tFetching data...");
            pdFetchingData.setCancelable(false);
            pdFetchingData.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("http://192.168.10.101:80/OctopusServer/search_firm.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return MainActivity.EXCEPTION_OCCURS;
            }
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(MainActivity.READ_TIMEOUT);
                httpURLConnection.setConnectTimeout(MainActivity.CONNECTION_TIMEOUT);
                httpURLConnection.setRequestMethod("GET");

                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder();

                int sortingType = Integer.parseInt(params[0]);
                if(sortingType == 9) {
                    // No firm or sorting type selected.
                } else {
                    if(sortingType > 9) { // No firm type but some sorting type is selected.
                        builder.appendQueryParameter("orderFormat", Integer.toString(sortingType - FACTOR));
                        builder.appendQueryParameter("longitude", params[2]);
                        builder.appendQueryParameter("latitude", params[3]);
                    } else {
                        if(sortingType == -2) {
                            builder.appendQueryParameter("firmName", params[1]);
                            if(!currentCategoriesSelected.equalsIgnoreCase("NothingSelected!")) {
                                builder.appendQueryParameter("firmType", currentCategoriesSelected);
                            }
                        } else {
                            if(sortingType == -1) {
                                builder.appendQueryParameter("firmType", params[1]);
                            } else {
                                builder.appendQueryParameter("firmType", params[1]);
                                builder.appendQueryParameter("orderFormat", Integer.toString(sortingType));
                                builder.appendQueryParameter("longitude", params[2]);
                                builder.appendQueryParameter("latitude", params[3]);
                            }
                        }
                    }
                }

                String query = builder.build().getEncodedQuery();

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                httpURLConnection.connect();
            } catch (IOException e1) {
                e1.printStackTrace();
                return MainActivity.EXCEPTION_OCCURS;
            }

            try {
                int response_code = httpURLConnection.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    return result.toString();
                } else {
                    return MainActivity.HTTP_CONNECTION_FAILED;
                }

            } catch (IOException e) {
                e.printStackTrace();
                return MainActivity.EXCEPTION_OCCURS;
            } finally {
                httpURLConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdFetchingData.dismiss();
            List<FirmData> data = new ArrayList<>();

            try {

                if(result.equalsIgnoreCase("No rows found!")) {
                    noRowsFoundTextView.setVisibility(View.VISIBLE);
                    return;
                }

                JSONArray jArray = new JSONArray(result);

                for(int i=0;i < jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    FirmData firmData = new FirmData();

                    firmData.name = json_data.getString("name");
                    firmData.rank = json_data.getInt("rank");
                    firmData.type = json_data.getString("type");
                    firmData.work_time = json_data.getString("work_time");
                    firmData.image = json_data.getString("image");
                    firmData.description = json_data.getString("discription");

                    data.add(firmData);
                }

                RecyclerView firmListRecyclerView = findViewById(R.id.firm_list_recycler_view);
                AdapterFirm firmListAdapter = new AdapterFirm(MainPanelActivity.this, data);
                firmListRecyclerView.setAdapter(firmListAdapter);
                firmListRecyclerView.setLayoutManager(new LinearLayoutManager(MainPanelActivity.this));

            } catch (JSONException e) {
                Toast.makeText(MainPanelActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
