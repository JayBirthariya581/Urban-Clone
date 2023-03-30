package com.urban.clone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.urban.clone.Activities.MainDomains.ServiceDetailActivity;
import com.urban.clone.R;
import com.urban.clone.SessionManager;
import com.urban.clone.databinding.ActivityBookingDetailBinding;
import com.urban.clone.model.ModelFinalService;

import java.util.HashMap;

public class BookingDetailActivity extends AppCompatActivity {
    ActivityBookingDetailBinding binding;
    String serviceID;
    HashMap<String,String> userDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.black));
        userDetails = new SessionManager(BookingDetailActivity.this).getUsersDetailsFromSessions();
        serviceID = getIntent().getStringExtra("serviceID");




        FirebaseDatabase.getInstance().getReference("Users").child(userDetails.get(SessionManager.KEY_UID)).child("services")
                .child(serviceID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot service) {

                if(service.exists()){

                    ModelFinalService serviceModel = service.getValue(ModelFinalService.class);



                   binding.itemTime.setText(serviceModel.getTime());
                   binding.itemDate.setText(serviceModel.getDate());
                   binding.itemLocation.setText(serviceModel.getLocation().getTxt());


                   binding.itemC.setText(serviceModel.getVehicle().getCompany());
                   binding.itemM.setText(serviceModel.getVehicle().getModel());
                   binding.itemVhNo.setText(serviceModel.getVehicle().getVehicleNo());






                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        binding.help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });




    }
}