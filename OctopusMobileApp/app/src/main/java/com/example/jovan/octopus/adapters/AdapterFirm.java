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

import com.example.jovan.octopus.data.FirmData;
import com.example.jovan.octopus.R;

import java.util.List;

public class AdapterFirm extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<FirmData> data;

    public AdapterFirm(Context context, List<FirmData> data){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.firm_data_holder, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder= (MyHolder) holder;
        FirmData current = data.get(position);

        myHolder.firmName.setText(current.name);
        myHolder.firmDescription.setText(current.description);
        myHolder.workTime.setText(current.work_time);
        myHolder.firmRank.setText(Integer.toString(current.rank));
        myHolder.firmRank.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        myHolder.setImageFromBase64String(current.image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class MyHolder extends RecyclerView.ViewHolder {

        private ImageView firmIcon;
        private TextView firmName;
        private TextView firmDescription;
        private TextView workTime;
        private TextView firmRank;

        MyHolder(View itemView) {
            super(itemView);

            firmName =  itemView.findViewById(R.id.firm_name);
            firmIcon = itemView.findViewById(R.id.firm_icon);
            firmDescription =  itemView.findViewById(R.id.firm_description);
            workTime =  itemView.findViewById(R.id.work_time);
            firmRank = itemView.findViewById(R.id.firm_price);
        }

        private void setImageFromBase64String(String base64Image) {
            byte[] imageByteArray = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
            firmIcon.setImageBitmap(Bitmap.createScaledBitmap(bmp, 100,
                    100, false));
        }
    }
}
