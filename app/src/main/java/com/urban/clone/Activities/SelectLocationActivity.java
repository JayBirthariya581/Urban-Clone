package com.urban.clone.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.urban.clone.Adapters.PlacesAutoCompleteAdapter;
import com.urban.clone.Helpers.LocationHelper;
import com.urban.clone.R;
import com.urban.clone.SessionManager;
import com.urban.clone.databinding.ActivitySelectLocationBinding;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SelectLocationActivity extends AppCompatActivity  {
    private static final String TAG = "12";
    ActivitySelectLocationBinding binding;
    int AUTOCOMPLETE_REQUEST_CODE = 111;
    PlacesAutoCompleteAdapter adapter;
    private LocationRequest locationRequest;
    private Handler mHandler;
    String[] hints={"Pravin Hardware","Poonam Chamber","Eternity Mall"};
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(SelectLocationActivity.this, R.color.black));


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        //Changing location hint
        i=0;
        binding.searchHint.setMovementMethod(new ScrollingMovementMethod());
         Runnable mUpdate = new Runnable() {
            public void run() {

                binding.searchHint.setText(hints[i]);
                i++;
                mHandler.postDelayed(this, 3000);

                if(i>=hints.length){
                    i=0;
                }
            }
        };

        mHandler = new Handler();
        mHandler.post(mUpdate);




        binding.clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.search.setText("");
            }
        });


        Places.initialize(SelectLocationActivity.this,"AIzaSyBEukC5-xkyvdjalrxaI89cTw6ngV5HP1c");

        binding.placesRecyclerView.setLayoutManager(new LinearLayoutManager(SelectLocationActivity.this));
        binding.placesRecyclerView.setHasFixedSize(true);
        adapter = new PlacesAutoCompleteAdapter(SelectLocationActivity.this);


        binding.placesRecyclerView.setAdapter(adapter);
        
        
        binding.currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                getCurrentLocation();
                
            }
        });


        adapter.notifyDataSetChanged();
        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){

                    binding.clearSearch.setVisibility(View.VISIBLE);
                    binding.currentLocation.setVisibility(View.GONE);
                    binding.placesRecyclerView.setVisibility(View.VISIBLE);
                    binding.art.setVisibility(View.GONE);
                    binding.or.setVisibility(View.GONE);
                    adapter.getFilter().filter(s.toString());
                }else{
                    binding.clearSearch.setVisibility(View.GONE);
                    binding.currentLocation.setVisibility(View.VISIBLE);
                    binding.placesRecyclerView.setVisibility(View.GONE);
                    binding.or.setVisibility(View.VISIBLE);
                    binding.art.setVisibility(View.VISIBLE);
                }
            }
        });


    }







    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    getCurrentLocation();

                } else {

                    turnOnGPS();
                }
            }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }


    private void getCurrentLocation() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(SelectLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(SelectLocationActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(SelectLocationActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        Intent intent = new Intent();
                                        intent.putExtra("requestType","currentLocation");
                                        intent.putExtra("lat",String.valueOf(latitude));
                                        intent.putExtra("lng",String.valueOf(longitude));

                                        setResult(Activity.RESULT_OK,intent);
                                        finish();



                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


    private void turnOnGPS() {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(SelectLocationActivity.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(SelectLocationActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }


    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }
    
    
    
    
    
    
    
    
    
    




}