package com.example.jovan.octopus.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jovan.octopus.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, View.OnTouchListener {

    private static final double[] latitudes = {44.817f, 43.321504f, 45.2551338f, 44.0082482f, 42.885298f};
    private static final double[] longitudes = {20.4568f, 21.8957301f, 19.8451756f, 20.9140472f, 20.8673427f};

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    // Error messages
    public static final String EXCEPTION_OCCURS = "ExceptionOccurs";
    public static final String HTTP_CONNECTION_FAILED = "HttpConnectionFailed";

    // Other enums
    public static final String LONGITUDE = "LONGITUDE";
    public static final String LATITUDE = "LATITUDE";
    public static final String USERNAME = "USERNAME";
    public static final int MIN_DISTANCE = 150;

    private Button swipeNextButton;
    private TextView selectedLocationName;

    private String username;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private float x1;
    private int activeMainLayout;
    private boolean registrationRequired;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_location);

        this.activeMainLayout = R.layout.activity_main_location;

        this.swipeNextButton = findViewById(R.id.next_page);
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        this.selectedLocationName = findViewById(R.id.selected_location_name);

        RelativeLayout relativeLayoutLocation = findViewById(R.id.root_view_location);
        relativeLayoutLocation.setOnTouchListener(this);
    }

    public void OnClickYourCurrentGPSLocation(View view) {
        boolean firstEntry = true;

        for(int i = 0; i < 2; i++) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if(!firstEntry) {
                    Toast.makeText(this, "Application cannot get your location without permission!", Toast.LENGTH_LONG).show();
                    break;
                }

                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                firstEntry = false;
            } else {
                fusedLocationProviderClient.getLastLocation();
                break;
            }
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    MainActivity.this.longitude = location.getLongitude();
                    MainActivity.this.latitude = location.getLatitude();

                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

                        if(addressList.size() > 0) {
                            String districtName = addressList.get(0).getSubLocality();
                            String cityName = addressList.get(0).getLocality();
                            String countryName = addressList.get(0).getCountryName();
                            String fullLocationName;

                            if(districtName != null)
                                fullLocationName = districtName + ", " + cityName + ", " + countryName;
                            else
                                fullLocationName = cityName + ", " + countryName;

                            selectedLocationName.setText(fullLocationName);
                            swipeNextButton.setVisibility(View.VISIBLE);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void OnClickChooseDifferentCityLocation(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    public void OnClickNextButton(View view) {
        this.activeMainLayout = R.layout.activity_main_login;
        setContentView(activeMainLayout);

        RelativeLayout relativeLayoutLogin = findViewById(R.id.root_view_login);
        relativeLayoutLogin.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    if (x2 > x1) { // Left to Right swipe action -> [Previous]
                        if(activeMainLayout == R.layout.activity_main_location) {
                            // No previous page from location view.
                        } else {
                            activeMainLayout = R.layout.activity_main_location;
                            setContentView(activeMainLayout);

                            RelativeLayout relativeLayoutLocation = findViewById(R.id.root_view_location);
                            relativeLayoutLocation.setOnTouchListener(this);
                        }
                    }
                    else { // Right to left swipe action -> [Next]
                        if(activeMainLayout == R.layout.activity_main_location) {
                            activeMainLayout = R.layout.activity_main_login;
                            setContentView(activeMainLayout);

                            RelativeLayout relativeLayoutLocation = findViewById(R.id.root_view_login);
                            relativeLayoutLocation.setOnTouchListener(this);
                        } else {
                            // No next page from login view.
                        }
                    }
                }
                break;
        }
        return true;
    }

    public void OnClickBackButton(View view) {
        this.activeMainLayout = R.layout.activity_main_location;
        setContentView(activeMainLayout);

        RelativeLayout relativeLayoutLogin = findViewById(R.id.root_view_location);
        relativeLayoutLogin.setOnTouchListener(this);
    }

    private static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void onClickConfirmButton(View view) {
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);

        this.username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(!registrationRequired) {
            new AsyncLogin().execute(username, password);
        } else {
            EditText confirmPasswordEditText = findViewById(R.id.confirm_password);
            EditText mailEditText = findViewById(R.id.mail);

            String confirmPassword = confirmPasswordEditText.getText().toString();
            String mail = mailEditText.getText().toString();

            if(!(username.length() > 1)) {
                Toast.makeText(this, "Username must have at least 2 characters!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!(password.length() >= 8)) {
                Toast.makeText(this, "Password should be at least 8 characters long!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!password.equalsIgnoreCase(confirmPassword)) {
                Toast.makeText(this, "Two password are not the same!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!isEmailValid(mail)) {
                Toast.makeText(this, "Mail is not valid!", Toast.LENGTH_SHORT).show();
                return;
            }

            new AsyncLogin().execute(username, password, mail);
        }
    }

    public void onClickRegisterButton(View view) {
        EditText confirmPassword = findViewById(R.id.confirm_password);
        EditText mail = findViewById(R.id.mail);

        confirmPassword.setVisibility(View.VISIBLE);
        mail.setVisibility(View.VISIBLE);

        this.registrationRequired = true;

        TextView registrationTextView = findViewById(R.id.registration_text_view);
        registrationTextView.setVisibility(View.GONE);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_belgrade:
                this.latitude = latitudes[0];
                this.longitude = longitudes[0];
                selectedLocationName.setText(R.string.belgrade);
                break;
            case R.id.id_nis:
                this.latitude = latitudes[1];
                this.longitude = longitudes[1];
                selectedLocationName.setText(R.string.nis);
                break;
            case R.id.id_novi_sad:
                this.latitude = latitudes[2];
                this.longitude = longitudes[2];
                selectedLocationName.setText(R.string.novi_sad);
                break;
            case R.id.id_kragujevac:
                this.latitude = latitudes[3];
                this.longitude = longitudes[3];
                selectedLocationName.setText(R.string.kragujevac);
                break;
            case R.id.id_kosovska_mitrovica:
                this.latitude = latitudes[4];
                this.longitude = longitudes[4];
                selectedLocationName.setText(R.string.kosovska_mitrovica);
                break;
        }

        swipeNextButton.setVisibility(View.VISIBLE);

        return true;
    }

    private class AsyncLogin extends AsyncTask<String, String, String> {
        private ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        private HttpURLConnection httpURLConnection;
        private URL url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading = new ProgressDialog(MainActivity.this);
            pdLoading.setMessage("\tChecking login...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                url = new URL("http://192.168.10.101:80/OctopusServer/login.php");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return EXCEPTION_OCCURS;
            }

            try {
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setReadTimeout(READ_TIMEOUT);
                httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                Uri.Builder builder;
                if(!registrationRequired) {
                    builder = new Uri.Builder()
                            .appendQueryParameter("username", params[0])
                            .appendQueryParameter("password", params[1]);
                } else {
                    builder = new Uri.Builder()
                            .appendQueryParameter("username", params[0])
                            .appendQueryParameter("password", params[1])
                            .appendQueryParameter("mail", params[2]);
                }

                String query = builder.build().getEncodedQuery();

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                httpURLConnection.connect();

            } catch (IOException e1) {
                e1.printStackTrace();
                return EXCEPTION_OCCURS;
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

                }else{
                    return HTTP_CONNECTION_FAILED;
                }

            } catch (IOException e) {
                e.printStackTrace();
                return EXCEPTION_OCCURS;
            } finally {
                httpURLConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();

            if(result.equalsIgnoreCase("true")) {
                Intent intent = new Intent(MainActivity.this, MainPanelActivity.class);

                intent.putExtra(LONGITUDE, longitude);
                intent.putExtra(LATITUDE, latitude);
                intent.putExtra(USERNAME, username);

                SharedPreferences sharedPreferences = getSharedPreferences("food_purchased", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(USERNAME, username);

                editor.apply();

                startActivity(intent);
                MainActivity.this.finish();
            } else
                if (result.equalsIgnoreCase("false")){
                    if(registrationRequired) {
                        Toast.makeText(MainActivity.this, "Username already exists!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid username or password!", Toast.LENGTH_LONG).show();
                    }
            } else
                if (result.equalsIgnoreCase(EXCEPTION_OCCURS) || result.equalsIgnoreCase(HTTP_CONNECTION_FAILED)) {
                    Toast.makeText(MainActivity.this, "Something went wrong! :( Check your internet connection, and then try again!", Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = getSharedPreferences("food_purchased", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        super.onDestroy();
    }
}
