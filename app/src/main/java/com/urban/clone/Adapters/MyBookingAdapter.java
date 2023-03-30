package com.urban.clone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.urban.clone.Activities.BookingDetailActivity;
import com.urban.clone.R;
import com.urban.clone.databinding.CardBookingBinding;
import com.urban.clone.model.ModelFinalService;

import java.util.ArrayList;

public class MyBookingAdapter extends RecyclerView.Adapter<MyBookingAdapter.MyViewHolder> {

    Context context;
    ArrayList<ModelFinalService> bookingList;


    public MyBookingAdapter(Context context, ArrayList<ModelFinalService> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_booking,parent,false);
        return new MyBookingAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ModelFinalService booking = bookingList.get(position);


        holder.binding.serviceName.setText(booking.getVehicle().getCompany() + " | "+booking.getVehicle().getModel() + " | "+booking.getVehicle().getVehicleNo());
        holder.binding.dateTime.setText(booking.getDate()+" at "+ booking.getTime());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, BookingDetailActivity.class).putExtra("serviceID",booking.getServiceID()));


            }
        });


    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CardBookingBinding binding;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CardBookingBinding.bind(itemView);

        }
    }
}
