package com.urban.clone.Activities.MainDomains;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.urban.clone.Activities.CompleteCheckListActivity;
import com.urban.clone.Activities.SlotBookingActivity;
import com.urban.clone.Activities.SplashActivity;
import com.urban.clone.Helpers.CustomProgressDialog;
import com.urban.clone.R;
import com.urban.clone.databinding.ActivityTwoWheelerServicingBinding;

import java.io.Serializable;
import java.util.HashMap;

public class TwoWheelerServicingActivity extends AppCompatActivity {
    ActivityTwoWheelerServicingBinding binding;
    String svPrice;

    CustomProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTwoWheelerServicingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(TwoWheelerServicingActivity.this, R.color.black));
        dialog = new CustomProgressDialog(TwoWheelerServicingActivity.this);

        dialog.show();



        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        FirebaseDatabase.getInstance().getReference("Services").child("TwoWheelerService").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot serviceDetails) {

                if(serviceDetails.exists()){
                   svPrice= serviceDetails.child("servicePrice").getValue(String.class);
                   binding.compSvPrice.setText(svPrice);
                   dialog.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.seeServiceCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TwoWheelerServicingActivity.this, CompleteCheckListActivity.class)
                        .putExtra("serviceID","TwoWheelerService")
                );
            }
        });

        binding.twSvBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap hm = new HashMap<String,String>();

                hm.put("serviceID","TwoWheelerService");
                hm.put("serviceName","Two Wheeler Service");
                hm.put("svPrice",svPrice);

                Intent i = new Intent(TwoWheelerServicingActivity.this, SlotBookingActivity.class);
                /*i.putExtra("serviceID","TwoWheelerService");
                i.putExtra("svPrice",svPrice);*/
                i.putExtra("serviceDetails",hm);
                startActivity(i);

            }
        });



    }
}