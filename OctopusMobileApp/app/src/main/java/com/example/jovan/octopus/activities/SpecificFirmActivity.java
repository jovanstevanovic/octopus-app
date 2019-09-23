package com.example.jovan.octopus.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jovan.octopus.R;
import com.example.jovan.octopus.adapters.AdapterProduct;
import com.example.jovan.octopus.data.ProductData;

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

public class SpecificFirmActivity extends AppCompatActivity {

    public static final String SERVICE_ID = "SERVICE_ID";
    public static final String PRODUCT_ICON = "PRODUCT_ICON";
    public static final String PRODUCT_NAME = "PRODUCT_NAME";
    public static final String PRODUCT_PRICE = "PRODUCT_PRICE";
    public static final String PRODUCT_DESCRIPTION = "PRODUCT_DESCRIPTION";

    private TextView noProductsFoundTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_firm);

        noProductsFoundTextView = findViewById(R.id.no_product_text_view);

        Intent data = getIntent();

        ImageView firmIconImageView = findViewById(R.id.firm_icon_main);
        TextView firmNameTextView = findViewById(R.id.firm_name_main);
        TextView firmPriceTextView = findViewById(R.id.firm_price_main);
        TextView firmDescriptionTextView = findViewById(R.id.firm_description_main);
        TextView workTimeTextView = findViewById(R.id.work_time_main);

        byte[] imageByteArray = data.getByteArrayExtra(MainPanelActivity.FIRM_ICON);
        String firmNameString = data.getStringExtra(MainPanelActivity.FIRM_NAME);
        String firmPriceString = data.getStringExtra(MainPanelActivity.FIRM_PRICE);
        String firmDescriptionString = data.getStringExtra(MainPanelActivity.FIRM_DESCRIPTION);
        String firmWorkTimeString = data.getStringExtra(MainPanelActivity.FIRM_WORK_TIME);

        Bitmap bmp = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        firmIconImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 100,
                100, false));

        firmNameTextView.setText(firmNameString);
        firmPriceTextView.setText(firmPriceString);
        firmDescriptionTextView.setText(firmDescriptionString);
        workTimeTextView.setText(firmWorkTimeString);

        new AsyncFetch().execute(firmNameString);
    }

    public void onClickSpecificProduct(View view) {
        Intent specificProductIntent = new Intent(this, BuyProductActivity.class);

        TextView serviceIdTextView = view.findViewById(R.id.service_id_text_view);
        ImageView productIconImageView = view.findViewById(R.id.product_icon);
        TextView productNameTextView = view.findViewById(R.id.product_name);
        TextView productPriceTextView = view.findViewById(R.id.product_price);
        TextView productDescriptionTextView = view.findViewById(R.id.product_description);

        Bitmap bitmap = ((BitmapDrawable) productIconImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageInByte = byteArrayOutputStream.toByteArray();

        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String serviceIdString = serviceIdTextView.getText().toString();
        String productNameString = productNameTextView.getText().toString();
        String productPriceString = productPriceTextView.getText().toString();
        String productDescriptionString = productDescriptionTextView.getText().toString();

        specificProductIntent.putExtra(SERVICE_ID, serviceIdString);
        specificProductIntent.putExtra(PRODUCT_ICON, imageInByte);
        specificProductIntent.putExtra(PRODUCT_NAME, productNameString);
        specificProductIntent.putExtra(PRODUCT_PRICE, productPriceString);
        specificProductIntent.putExtra(PRODUCT_DESCRIPTION, productDescriptionString);

        startActivity(specificProductIntent);
    }

    public void OnClickHeaderButton(View view) {
        switch (view.getId()) {
            case R.id.side_nav_bar_product_panel_button:
                LinearLayout sideLinearLayout = findViewById(R.id.side_nav_bar_main_panel_view);
                sideLinearLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.cart_product_panel_button:
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

            pdFetchingData = new ProgressDialog(SpecificFirmActivity.this);
            pdFetchingData.setMessage("\tFetching data...");
            pdFetchingData.setCancelable(false);
            pdFetchingData.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                this.url = new URL("http://192.168.10.101:80/OctopusServer/fetch_products.php");
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
                builder.appendQueryParameter("firmName", params[0]);

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
            List<ProductData> data = new ArrayList<>();

            try {
                if(result.equalsIgnoreCase("No rows found!")) {
                    noProductsFoundTextView.setVisibility(View.VISIBLE);
                    return;
                }

                JSONArray jArray = new JSONArray(result);

                for(int i=0;i < jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    ProductData productData = new ProductData();

                    productData.serviceName = json_data.getString("service_name");
                    productData.servicePrice = json_data.getDouble("price");
                    productData.serviceImage = json_data.getString("image");
                    productData.serviceDescription = json_data.getString("description");

                    data.add(productData);
                }

                RecyclerView productListRecyclerView = findViewById(R.id.product_list_recycler_view);
                AdapterProduct productAdapter = new AdapterProduct(SpecificFirmActivity.this, data);
                productListRecyclerView.setAdapter(productAdapter);
                productListRecyclerView.setLayoutManager(new LinearLayoutManager(SpecificFirmActivity.this));

            } catch (JSONException e) {
                Toast.makeText(SpecificFirmActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
