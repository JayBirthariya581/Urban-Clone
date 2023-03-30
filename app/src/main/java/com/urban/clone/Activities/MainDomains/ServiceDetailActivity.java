package com.urban.clone.Activities.MainDomains;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.urban.clone.Activities.CompleteCheckListActivity;
import com.urban.clone.Activities.DiscountActivity;
import com.urban.clone.Activities.MainActivity;
import com.urban.clone.Activities.PaymentActivity;
import com.urban.clone.Activities.SlotBookingActivity;
import com.urban.clone.Adapters.ServiceCheckListAdapter;
import com.urban.clone.Helpers.CustomProgressDialog;
import com.urban.clone.R;
import com.urban.clone.databinding.ActivityServiceDetailBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class ServiceDetailActivity extends AppCompatActivity {
    ActivityServiceDetailBinding binding;
    String serviceID;
    ServiceCheckListAdapter adapter;
    ArrayList<String> svCheckList;
    String svPrice;
    CustomProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(ServiceDetailActivity.this, R.color.black));
        HashMap<String, String> hm = (HashMap) getIntent().getSerializableExtra("serviceDetails");
        svPrice = hm.get("svPrice");
        dialog = new CustomProgressDialog(ServiceDetailActivity.this);
        dialog.show();
        serviceID = hm.get("serviceID");
        binding.svPrice.setText(svPrice);
        binding.itemPrice.setText(svPrice);

        svCheckList = new ArrayList<>();
        adapter = new ServiceCheckListAdapter(ServiceDetailActivity.this, svCheckList, serviceID);

        binding.rvSvCl.setAdapter(adapter);
        binding.rvSvCl.setLayoutManager(new LinearLayoutManager(ServiceDetailActivity.this));
        binding.rvSvCl.setHasFixedSize(true);
        binding.rvSvCl.setNestedScrollingEnabled(false);


        FirebaseDatabase.getInstance().getReference("Services").child(serviceID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot serviceDetails) {

                        if (serviceDetails.exists()) {
                            binding.svName.setText(serviceDetails.child("serviceName").getValue(String.class));

                            binding.doorStepPrice.setText(serviceDetails.child("AdditionalFee").child("DoorStepFee").getValue(String.class));

                            binding.totalPrice.setText(String.valueOf((Integer.valueOf(binding.itemPrice.getText().toString()) + Integer.valueOf(binding.doorStepPrice.getText().toString())) - Integer.valueOf(binding.discountPrice.getText().toString())));


                            binding.svBtnPrice.setText(binding.totalPrice.getText().toString());

                            for (DataSnapshot cl_value : serviceDetails.child("serviceCheckList").getChildren()) {
                                svCheckList.add(cl_value.getValue(String.class));

                                if (svCheckList.size() > 4) {
                                    binding.seeServiceCheckList.setVisibility(View.VISIBLE);

                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.itemDate.setText(hm.get("Date"));
        binding.itemPrice.setText(hm.get("svPrice"));
        binding.itemTime.setText(hm.get("Time"));
        binding.itemC.setText(hm.get("Company"));
        binding.itemM.setText(hm.get("Model"));
        binding.itemLocation.setText(hm.get("location_txt"));
        binding.itemVhNo.setText(hm.get("VehicleNo"));

        binding.seeServiceCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ServiceDetailActivity.this, CompleteCheckListActivity.class)
                        .putExtra("serviceID", serviceID)
                );
            }
        });

        binding.continueBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ServiceDetailActivity.this, PaymentActivity.class);
                hm.put("totalPrice",binding.totalPrice.getText().toString());
                i.putExtra("serviceDetails",hm);

                startActivity(i);
            }
        });


        ActivityResultLauncher<Intent> lau = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Intent data = result.getData();


                    binding.discountPrice.setText(data.getStringExtra("couponValue"));

                    binding.totalPrice.setText(String.valueOf((Integer.valueOf(binding.itemPrice.getText().toString()) + Integer.valueOf(binding.doorStepPrice.getText().toString())) - Integer.valueOf(binding.discountPrice.getText().toString())));


                    binding.svBtnPrice.setText(binding.totalPrice.getText().toString());

                    binding.discountPriceBox.setVisibility(View.VISIBLE);
                }
            }
        });


        binding.disBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ServiceDetailActivity.this, DiscountActivity.class);
                i.putExtra("svPrice", svPrice);
                i.putExtra("serviceID", serviceID);
                lau.launch(i);
            }
        });





    }
}