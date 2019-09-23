package com.example.jovan.octopus.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jovan.octopus.R;

import java.util.Date;

public class BuyProductActivity extends AppCompatActivity {

    public static final String NUM_PRODUCTS = "NUM_PRODUCTS";
    public static final String PRODUCT_ID = "PRODUCT_ID_";
    public static final String PRODUCT_NAME = "PRODUCT_NAME_";
    public static final String PRODUCT_AMOUNT = "PRODUCT_AMOUNT_";
    public static final String PURCHASE_TIME = "PURCHASE_TIME_";

    private TextView amountPickedTextView;
    private TextView productNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Intent data = getIntent();

        TextView serviceIdTextView = findViewById(R.id.service_id_text_view_main);
        ImageView productIconImageView = findViewById(R.id.product_icon_main);
        this.productNameTextView = findViewById(R.id.product_name_main);
        TextView productPriceTextView = findViewById(R.id.product_price_main);
        TextView productDescriptionTextView = findViewById(R.id.product_description_main);

        String serviceIdString = data.getStringExtra(SpecificFirmActivity.SERVICE_ID);
        byte[] imageByteArray = data.getByteArrayExtra(SpecificFirmActivity.PRODUCT_ICON);
        String productNameString = data.getStringExtra(SpecificFirmActivity.PRODUCT_NAME);
        String productPriceString = data.getStringExtra(SpecificFirmActivity.PRODUCT_PRICE);
        String productDescriptionString = data.getStringExtra(SpecificFirmActivity.PRODUCT_DESCRIPTION);

        Bitmap bmp = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        productIconImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 100,
                100, false));

        serviceIdTextView.setText(serviceIdString);
        productNameTextView.setText(productNameString);
        productPriceTextView.setText(productPriceString);
        productDescriptionTextView.setText(productDescriptionString);

        this.amountPickedTextView = findViewById(R.id.amount_picked);
    }

    public void OnClickCartButton(View view) {
        String productName = productNameTextView.getText().toString();

        String amountString = amountPickedTextView.getText().toString();
        int amount = Integer.parseInt(amountString);

        if(amount == 0) {
            Toast.makeText(this, "Cannot purchase with zero amount!", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView serviceIdTextView = findViewById(R.id.service_id_text_view_main);
        String serviceIdString = serviceIdTextView.getText().toString();

        Date purchaseDate = new Date();

        SharedPreferences sharedPreferences = getSharedPreferences("food_purchased", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int currentProductNumber = sharedPreferences.getInt(NUM_PRODUCTS, 0);

        editor.putString(PRODUCT_NAME + currentProductNumber, productName);
        editor.putInt(PRODUCT_AMOUNT + currentProductNumber, amount);
        editor.putString(PRODUCT_ID + currentProductNumber, serviceIdString);
        editor.putString(PURCHASE_TIME + currentProductNumber, purchaseDate.toString());
        editor.putInt(NUM_PRODUCTS, currentProductNumber + amount);

        editor.apply();

        Toast.makeText(this, "Product added to cart!", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void OnClickAmountPickButton(View view) {
        String amountPicketString = amountPickedTextView.getText().toString();
        int amount = Integer.parseInt(amountPicketString);

        switch (view.getId()) {
            case R.id.add_amount_button:
                amount++;
                break;
            case R.id.minus_amount_button:
                amount--;
                break;
        }

        this.amountPickedTextView.setText(Integer.toString(amount));
    }

    public void OnClickGoToCartButton(View view) {
        Intent intent = new Intent(this, ShoppingCartActivity.class);
        startActivity(intent);
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
}
