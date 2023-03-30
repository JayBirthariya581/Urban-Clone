package com.urban.clone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.urban.clone.R;
import com.urban.clone.databinding.ActivitySlotBookingBinding;
import com.urban.clone.databinding.TwBrandCardBinding;

import java.util.ArrayList;

public class TwModelListAdapter extends RecyclerView.Adapter<TwModelListAdapter.MyViewHoldeer> {
    ArrayList<String> modelList;
    Context context;
    ActivitySlotBookingBinding b;
    AlertDialog alertDialog;

    public void setAlertDialog(AlertDialog alertDialog) {
        this.alertDialog = alertDialog;
    }

    public TwModelListAdapter(ArrayList<String> modelList, Context context, ActivitySlotBookingBinding b) {
        this.modelList = modelList;
        this.context = context;
        this.b = b;

    }





    public ArrayList<String> getModelList() {
        return modelList;
    }

    public void setModelList(ArrayList<String> modelList) {
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public MyViewHoldeer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tw_brand_card,parent,false);
        return new TwModelListAdapter.MyViewHoldeer(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoldeer holder, int position) {

        holder.binding.brandName.setText(modelList.get(position));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.modelTv.setText(modelList.get(position));
                alertDialog.dismiss();
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class MyViewHoldeer extends RecyclerView.ViewHolder{
        TwBrandCardBinding binding;
        public MyViewHoldeer(@NonNull View itemView) {
            super(itemView);
            binding = TwBrandCardBinding.bind(itemView);
        }
    }

}
