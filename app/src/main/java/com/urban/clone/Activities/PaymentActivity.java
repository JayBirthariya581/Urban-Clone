package com.urban.clone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.urban.clone.Activities.MainDomains.ServiceDetailActivity;
import com.urban.clone.Helpers.FinalServiceHelper;
import com.urban.clone.Helpers.LocationHelper;
import com.urban.clone.Helpers.PaymentHelper;
import com.urban.clone.Helpers.VehicleHelper;
import com.urban.clone.R;
import com.urban.clone.SessionManager;
import com.urban.clone.databinding.ActivityPaymentBinding;

import java.util.HashMap;

public class PaymentActivity extends AppCompatActivity {
    ActivityPaymentBinding binding;
    HashMap<String,String> serviceDetails;
    DatabaseReference DBref;
    ProgressDialog dialog;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(PaymentActivity.this, R.color.black));

        dialog = new ProgressDialog(PaymentActivity.this);
        dialog.setMessage("Please Wait");
        dialog.setCancelable(false);

        sessionManager = new SessionManager(PaymentActivity.this);
        serviceDetails = (HashMap<String, String>) getIntent().getSerializableExtra("serviceDetails");


        binding.paymentAmount.setText(serviceDetails.get("totalPrice"));


        DBref = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());

        binding.payAfterService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                processpayment("payAfterService","onHold");




            }
        });






    }


    private void processpayment(String paymentType,String paymentStatus){

        try {
            dialog.show();



            String phone = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_PHONE);

            String serviceID = DBref.child("services").push().getKey();





            PaymentHelper paymentHelper = new PaymentHelper(serviceDetails.get("totalPrice"),paymentType,paymentStatus);

            VehicleHelper vehicleHelper = new VehicleHelper(serviceDetails.get("Company"),serviceDetails.get("Model"),serviceDetails.get("VehicleNo"));

            LocationHelper locationHelper = new LocationHelper(serviceDetails.get("lat"),serviceDetails.get("lng"),serviceDetails.get("location_txt"));





            FinalServiceHelper finalServiceHelper = new FinalServiceHelper(serviceID,phone,serviceDetails.get("Date"),serviceDetails.get("Time"),vehicleHelper,locationHelper,paymentHelper);






            DBref.child("services").child(serviceID).setValue(finalServiceHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        dialog.dismiss();


                        Intent i = new Intent(PaymentActivity.this,ConfirmationActivity.class);
                        i.putExtra("serviceDetails",serviceDetails);
                        finishAffinity();

                        startActivity(i);
                        //finish();


                    }


                }
            });







        }catch (Exception e){

        }

    }


}