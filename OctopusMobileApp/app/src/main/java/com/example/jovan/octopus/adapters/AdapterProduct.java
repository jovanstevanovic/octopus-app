package com.example.jovan.octopus.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jovan.octopus.R;
import com.example.jovan.octopus.data.ProductData;

import java.util.List;

public class AdapterProduct extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<ProductData> data;

    public AdapterProduct(Context context, List<ProductData> data){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.product_data_holder, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder= (MyHolder) holder;
        ProductData current = data.get(position);

        myHolder.productName.setText(current.serviceName);
        myHolder.productDescription.setText(current.serviceDescription);
        myHolder.productPrice.setText(Double.toString(current.servicePrice) + "rsd");
        myHolder.productPrice.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        myHolder.setImageFromBase64String(current.serviceImage);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class MyHolder extends RecyclerView.ViewHolder {

        private ImageView productIcon;
        private TextView productName;
        private TextView productDescription;
        private TextView productPrice;

        MyHolder(View itemView) {
            super(itemView);

            productName =  itemView.findViewById(R.id.product_name);
            productIcon = itemView.findViewById(R.id.product_icon);
            productDescription =  itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
        }

        private void setImageFromBase64String(String base64Image) {
            byte[] imageByteArray = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
            productIcon.setImageBitmap(Bitmap.createScaledBitmap(bmp, 100,
                    100, false));
        }
    }
}
