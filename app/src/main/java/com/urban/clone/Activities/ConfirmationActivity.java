package com.urban.clone.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.urban.clone.R;
import com.urban.clone.databinding.ActivityConfirmationBinding;

import java.util.HashMap;

public class ConfirmationActivity extends AppCompatActivity {
    ActivityConfirmationBinding binding;
    HashMap<String,String> serviceDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(ConfirmationActivity.this, R.color.black));

        serviceDetails = (HashMap<String, String>) getIntent().getSerializableExtra("serviceDetails");


        binding.serviceDate.setText(serviceDetails.get("Date"));
        binding.serviceTime.setText(serviceDetails.get("Time"));
        binding.Company.setText(serviceDetails.get("Company"));
        binding.Model.setText(serviceDetails.get("Model"));
        binding.vhNo.setText(serviceDetails.get("VehicleNo"));
        binding.serviceLocation.setText(serviceDetails.get("location_txt"));




        binding.returnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConfirmationActivity.this,MainActivity.class);

                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                //finish();


            }
        });

    }
}