package com.urban.clone.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.urban.clone.Activities.MainDomains.PlacePickerActivity;
import com.urban.clone.Activities.MainDomains.TwoWheelerServicingActivity;
import com.urban.clone.Adapters.DateBoxAdapter;
import com.urban.clone.Adapters.TimeBoxAdapter;
import com.urban.clone.Adapters.TwBrandListAdapter;
import com.urban.clone.Adapters.TwModelListAdapter;
import com.urban.clone.Helpers.CustomProgressDialog;
import com.urban.clone.R;
import com.urban.clone.databinding.ActivitySlotBookingBinding;
import com.urban.clone.databinding.SelectBrandDialogBinding;
import com.urban.clone.model.DateBoxModel;
import com.urban.clone.model.TimeBoxModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SlotBookingActivity extends AppCompatActivity {
    ActivitySlotBookingBinding binding;
    TwBrandListAdapter adapter;
    TwModelListAdapter ModelAdapter;
    ArrayList<String> bl, CompanyList, ModelList;
    ArrayList<DateBoxModel> mList;
    DatabaseReference DBref;
    ArrayList<TimeBoxModel> timeList;
    DateBoxAdapter dateBoxAdapter;
    TimeBoxAdapter timeBoxAdapter;
    RecyclerView cL;
    HashMap<String, String> serviceDetails;
    int Place_Picker_Request = 1;
    CustomProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySlotBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(ContextCompat.getColor(SlotBookingActivity.this, R.color.black));
        dialog = new CustomProgressDialog(SlotBookingActivity.this);

        dialog.setCancelable(false);
        dialog.show();

        serviceDetails = (HashMap<String, String>) getIntent().getSerializableExtra("serviceDetails");

        binding.svDetailHead.setText(serviceDetails.get("serviceName"));

        DBref = FirebaseDatabase.getInstance().getReference("Services").child("TwoWheelerService").child("CompanyList");


        //Select Brand
        manageDateBox();


        manageVehicleBox();


        binding.continueBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueCheck();

            }
        });


        generateClick();


    }


    public void continueCheck() {
        if (dateBoxAdapter.getSelectedPosition() != (-1) && timeBoxAdapter.getSelectedPosition() != (-1) && !binding.brandTv.getText().toString().isEmpty()
                && !binding.modelTv.getText().toString().isEmpty() && !binding.vhNoTv.getText().toString().isEmpty()) {

            Intent i = new Intent(SlotBookingActivity.this, PlacePickerActivity.class);

            serviceDetails.put("Date", mList.get(dateBoxAdapter.getSelectedPosition()).getDateF());
            serviceDetails.put("Time", timeBoxAdapter.getMlist().get(timeBoxAdapter.getSelectedPosition()).getTime());
            serviceDetails.put("Company", binding.brandTv.getText().toString());
            serviceDetails.put("Model", binding.modelTv.getText().toString());
            serviceDetails.put("VehicleNo", binding.vhNoTv.getText().toString());


            i.putExtra("serviceDetails", serviceDetails);

            startActivity(i);
        } else {

            Toast.makeText(SlotBookingActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();

        }


    }


    private void manageVehicleBox() {


        ModelList = new ArrayList<>();
        ModelAdapter = new TwModelListAdapter(ModelList, SlotBookingActivity.this, binding);


        CompanyList = new ArrayList<>();
        adapter = new TwBrandListAdapter(CompanyList, SlotBookingActivity.this, binding);


        adapter.setModelListAdapter(ModelAdapter);


        DBref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot companyList) {


                if (companyList.exists()) {
                    CompanyList.clear();


                    for (DataSnapshot company : companyList.getChildren()) {

                        CompanyList.add(company.getKey());
                    }


                    adapter.setCompanyList(companyList);

                    binding.brandTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            SelectBrandDialogBinding b = SelectBrandDialogBinding.inflate(getLayoutInflater());
                            AlertDialog.Builder builder = new AlertDialog.Builder(SlotBookingActivity.this);
                            builder.setView(b.getRoot());


                            SlotBookingActivity.this.cL = b.brandList;

                            b.heading.setText("Select Brand");
                            b.brandList.setAdapter(adapter);
                            b.brandList.setLayoutManager(new LinearLayoutManager(SlotBookingActivity.this));
                            b.brandList.setHasFixedSize(true);


                            //builder.create().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            adapter.notifyDataSetChanged();
                            AlertDialog a = builder.show();
                            a.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            a.getWindow().setLayout(800, 1000);
                            adapter.setAlertDialog(a);


                        }
                    });

                    binding.modelTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            SelectBrandDialogBinding mb = SelectBrandDialogBinding.inflate(getLayoutInflater());
                            AlertDialog.Builder builder = new AlertDialog.Builder(SlotBookingActivity.this);
                            builder.setView(mb.getRoot());


                            mb.heading.setText("Select Model");
                            mb.brandList.setAdapter(ModelAdapter);
                            mb.brandList.setLayoutManager(new LinearLayoutManager(SlotBookingActivity.this));
                            mb.brandList.setHasFixedSize(true);


                            //builder.create().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            ModelAdapter.notifyDataSetChanged();
                            AlertDialog am = builder.show();
                            am.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            am.getWindow().setLayout(800, 1000);
                            ModelAdapter.setAlertDialog(am);


                        }
                    });


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void generateClick() {

        final int pos = 0;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.dateBox.findViewHolderForAdapterPosition(pos).itemView.performClick();
                //cL.findViewHolderForAdapterPosition(pos).itemView.performClick();
                dialog.dismiss();


            }
        }, 1000);
    }


    private void manageDateBox() {
        mList = new ArrayList<>();
        timeList = new ArrayList<>();
        timeBoxAdapter = new TimeBoxAdapter(timeList, SlotBookingActivity.this);
        dateBoxAdapter = new DateBoxAdapter(mList, SlotBookingActivity.this);
        binding.dateBox.setAdapter(dateBoxAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(SlotBookingActivity.this, LinearLayoutManager.HORIZONTAL, false);
        // manager.setStackFromEnd(true);
        binding.dateBox.setLayoutManager(manager);
        binding.dateBox.setHasFixedSize(true);


        //Setting timebox
        binding.timeBox.setAdapter(timeBoxAdapter);
        binding.timeBox.setLayoutManager(new LinearLayoutManager(SlotBookingActivity.this, LinearLayoutManager.HORIZONTAL, false));
        binding.timeBox.setHasFixedSize(true);


        dateBoxAdapter.setTimeBoxAdapter(timeBoxAdapter);
        dateBoxAdapter.setDialog(dialog);

        FirebaseDatabase.getInstance().getReference("AppManager").child("SlotManager").child("MechanicCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot mechanicCount) {
                if (mechanicCount.exists()) {

                    dateBoxAdapter.setMechanicCount(Integer.valueOf(mechanicCount.getValue(String.class)));


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        HashMap<Integer, String> dayFromCode = new HashMap<>();
        dayFromCode.put(1, "Sun");
        dayFromCode.put(2, "Mon");
        dayFromCode.put(3, "Tue");
        dayFromCode.put(4, "Wed");
        dayFromCode.put(5, "Thu");
        dayFromCode.put(6, "Fri");
        dayFromCode.put(7, "Sat");


        Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);

        dateBoxAdapter.setHourOfDay(hour);
        dateBoxAdapter.setDateOfDay(c.get(Calendar.DAY_OF_MONTH));


        int limit = 8;

        for (int i = 0; i < limit; i++) {

            if (i != 0) {
                c.add(Calendar.DATE, +1);

            }


            if (c.get(Calendar.DAY_OF_WEEK) != 2) {
                SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");


                String date = String.valueOf(c.get(Calendar.DAY_OF_MONTH));

                DateBoxModel m = new DateBoxModel();
                m.setDate(date);
                m.setDateF(s.format(c.getTime()));


                m.setDay(dayFromCode.get(c.get(Calendar.DAY_OF_WEEK)));
                mList.add(m);


            } else {
                limit++;
            }


        }

        dateBoxAdapter.notifyDataSetChanged();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Place_Picker_Request) {
            Place place = PlacePicker.getPlace(data, SlotBookingActivity.this);
            StringBuilder stringBuilder = new StringBuilder();
            String Lat = String.valueOf(place.getLatLng().latitude);
            String Lng = String.valueOf(place.getLatLng().longitude);


            //Toast.makeText(SlotBookingActivity.this, Lat + " " + Lng, Toast.LENGTH_SHORT).show();

        }

    }
}