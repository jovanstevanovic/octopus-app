package com.example.jovan.octopus.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jovan.octopus.R;

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

public class ShoppingCartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "dancing_script.ttf");

        SharedPreferences sharedPreferences = getSharedPreferences("food_purchased", MODE_PRIVATE);
        String usernameString = sharedPreferences.getString(MainActivity.USERNAME, "jovan123");

        LinearLayout mainLinearLayout = findViewById(R.id.cart_main_linear_layout);
        int numberOfProducts = sharedPreferences.getInt(BuyProductActivity.NUM_PRODUCTS, 0);
        for(int i = 0; i < numberOfProducts; i++) {
            String productName = sharedPreferences.getString(BuyProductActivity.PRODUCT_NAME + i, "");

            int amount = sharedPreferences.getInt(BuyProductActivity.PRODUCT_AMOUNT + i, 0);
            String amountString = Integer.toString(amount);

            if(amount == 0) {
                continue;
            }

            String serviceIdString = sharedPreferences.getString(BuyProductActivity.PRODUCT_ID + i, "");
            String purchaseTimeString = sharedPreferences.getString(BuyProductActivity.PURCHASE_TIME + i, "");

            LinearLayout linearLayout = new LinearLayout(ShoppingCartActivity.this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.setPadding(0, 0, 0, 20);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

            TextView productNameTextView = new TextView(ShoppingCartActivity.this);
            productNameTextView.setText(productName);
            productNameTextView.setTypeface(typeface);
            productNameTextView.setTextSize(22.0f);
            productNameTextView.setTextColor(Color.WHITE);
            productNameTextView.setGravity(Gravity.CENTER);

            TextView secondPlayerName = new TextView(ShoppingCartActivity.this);
            secondPlayerName.setText(amountString);
            secondPlayerName.setTypeface(typeface);
            secondPlayerName.setTextSize(22.0f);
            secondPlayerName.setTextColor(Color.WHITE);
            secondPlayerName.setGravity(Gravity.CENTER);

            linearLayout.addView(productNameTextView, layoutParams);
            linearLayout.addView(secondPlayerName, layoutParams);
            mainLinearLayout.addView(linearLayout);

            System.out.println(serviceIdString);
            System.out.println(usernameString);
            System.out.println(purchaseTimeString);
            System.out.println(amountString);

            new AsyncAddPurchase().execute(serviceIdString, usernameString, purchaseTimeString, amountString);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString(MainActivity.USERNAME, usernameString);
        editor.apply();
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

    private class AsyncAddPurchase extends AsyncTask<String, String, String> {
        private ProgressDialog pdFetchingData;
        private HttpURLConnection httpURLConnection;
        private URL url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdFetchingData = new ProgressDialog(ShoppingCartActivity.this);
            pdFetchingData.setMessage("\tAdding purchase...");
            pdFetchingData.setCancelable(false);
            pdFetchingData.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("http://192.168.10.101:80/OctopusServer/add_purchase.php");
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

                builder.appendQueryParameter("service_id", params[0]);
                builder.appendQueryParameter("username", params[1]);
                builder.appendQueryParameter("order_time", params[2]);
                builder.appendQueryParameter("amount", params[3]);

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

            if(result.equalsIgnoreCase("true")) {
                Toast.makeText(ShoppingCartActivity.this, "Purchase is registered!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ShoppingCartActivity.this, "Something went wrong! :( Please try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
