package com.urban.clone.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.urban.clone.Activities.LocationMenuActivity;
import com.urban.clone.Activities.MainActivity;
import com.urban.clone.Activities.MainDomains.TwoWheelerServicingActivity;
import com.urban.clone.Activities.PlacesAutoActivity;
import com.urban.clone.Activities.SelectLocationActivity;
import com.urban.clone.Adapters.ServiceCatlogAdapter;
import com.urban.clone.Adapters.SliderAdapter;
import com.urban.clone.Helpers.CustomProgressDialog;
import com.urban.clone.Helpers.LocationHelper;
import com.urban.clone.Helpers.SpacesItemDecoration;
import com.urban.clone.R;
import com.urban.clone.SessionManager;
import com.urban.clone.databinding.FragmentHomeBinding;
import com.urban.clone.model.ServiceDomainModel;
import com.urban.clone.model.SliderItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    List<SliderItem> sliderItems;
    SliderAdapter sliderAdapter;
    CustomProgressDialog dialog;
    SessionManager sessionManager;
    ServiceCatlogAdapter serviceCatlogAdapter;
    ArrayList<ServiceDomainModel> serviceDomainModels;
    private Handler sliderHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        sessionManager = new SessionManager(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sliderItems = new ArrayList<>();
        serviceDomainModels = new ArrayList<>();
        dialog = new CustomProgressDialog(getContext());
        dialog.show();



        binding.locationText.setText(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_LOCATION_Txt));


        ActivityResultLauncher<Intent> lau = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    dialog.show();


                    if (data.getStringExtra("requestType").equals("currentLocation")) {
                        double latitude = Double.valueOf(data.getStringExtra("lat"));
                        double longitude = Double.valueOf(data.getStringExtra("lng"));

                        Geocoder geocoder = new Geocoder(getContext());

                        try {
                            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                            if (addressList != null && addressList.size() > 0) {
                                String locality = addressList.get(0).getAddressLine(0);
                                String country = addressList.get(0).getCountryName();

                                if (locality != null && country != null) {
                                    binding.locationText.setText(locality);
                                    LocationHelper location = new LocationHelper(String.valueOf(latitude), String.valueOf(longitude), locality);

                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("location")
                                            .setValue(location).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                SessionManager sessionManager = new SessionManager(getContext());

                                                SharedPreferences.Editor editor = sessionManager.getEditor();

                                                editor.putString(SessionManager.KEY_LOCATION_Lat, String.valueOf(latitude));
                                                editor.putString(SessionManager.KEY_LOCATION_Lng, String.valueOf(longitude));
                                                editor.putString(SessionManager.KEY_LOCATION_Txt, locality);


                                                editor.commit();

                                                dialog.dismiss();



                                            }


                                        }
                                    });


                                } else {
                                    //resutText.setText("Location could not be fetched...");

                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } else {


                        PlacesClient placesClient = Places.createClient(getContext());


                        String placeId = String.valueOf(data.getStringExtra("placeId"));

                        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
                        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                            @Override
                            public void onSuccess(FetchPlaceResponse response) {


                                Place place = response.getPlace();

                                try {
                                    binding.locationText.setText(place.getAddress());

                                    LocationHelper location = new LocationHelper(String.valueOf(place.getLatLng().latitude), String.valueOf(place.getLatLng().longitude), place.getAddress());

                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("location")
                                            .setValue(location).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {


                                                SharedPreferences.Editor editor = sessionManager.getEditor();

                                                editor.putString(SessionManager.KEY_LOCATION_Lat, String.valueOf(place.getLatLng().latitude));
                                                editor.putString(SessionManager.KEY_LOCATION_Lng, String.valueOf(place.getLatLng().longitude));
                                                editor.putString(SessionManager.KEY_LOCATION_Txt, place.getAddress());


                                                editor.commit();
                                                dialog.dismiss();

                                            }


                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                if (exception instanceof ApiException) {
                                    Toast.makeText(getContext(), exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }


                }
            }
        });


        binding.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lau.launch(new Intent(getContext(), SelectLocationActivity.class));
            }
        });


        sliderItems.add(new SliderItem("https://img.cinemablend.com/filter:scale/quill/2/8/f/a/c/5/28fac50c775d0d6fb15a241afd9f0877bbef604e.jpg?mw=600"));
        sliderItems.add(new SliderItem("https://static.wikia.nocookie.net/marvelmovies/images/f/fa/Avengers_%28The_Whole_Team%29.png/revision/latest?cb=20190526222205"));

        sliderItems.add(new SliderItem("https://img.cinemablend.com/filter:scale/quill/2/8/f/a/c/5/28fac50c775d0d6fb15a241afd9f0877bbef604e.jpg?mw=600"));

        sliderItems.add(new SliderItem("https://cdn.vox-cdn.com/thumbor/nNU6oP549KYvbc5Vk-M-zaLHFog=/0x0:1920x1080/1200x800/filters:focal(887x293:1193x599)/cdn.vox-cdn.com/uploads/chorus_image/image/64224190/surprise_marvel_releases_a_new_full_trailer_and_poster_for_avengers_endgame_social.0.jpg"));
        sliderItems.add(new SliderItem("https://cdn.vox-cdn.com/thumbor/nNU6oP549KYvbc5Vk-M-zaLHFog=/0x0:1920x1080/1200x800/filters:focal(887x293:1193x599)/cdn.vox-cdn.com/uploads/chorus_image/image/64224190/surprise_marvel_releases_a_new_full_trailer_and_poster_for_avengers_endgame_social.0.jpg"));
        sliderAdapter = new SliderAdapter(sliderItems, binding.viewPagerImageSlider);
        binding.viewPagerImageSlider.setAdapter(sliderAdapter);
        binding.viewPagerImageSlider.setClipChildren(false);
        binding.viewPagerImageSlider.setClipToPadding(false);
        binding.viewPagerImageSlider.setOffscreenPageLimit(3);
        binding.viewPagerImageSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);


        setUpBannerIndicators();
        setCurrentBannerIndicator(0);

        binding.viewPagerImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
                if (position < binding.ll.getChildCount()) {
                    setCurrentBannerIndicator(position);
                } else {
                    setCurrentBannerIndicator((position % binding.ll.getChildCount()));
                }

            }
        });

        binding.tws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), TwoWheelerServicingActivity.class));
            }
        });

        fillCatlog();


    }

    private void fillCatlog() {
        serviceCatlogAdapter = new ServiceCatlogAdapter(getContext(), serviceDomainModels);
        binding.rvSvCatlog.setAdapter(serviceCatlogAdapter);
        binding.rvSvCatlog.setLayoutManager(new GridLayoutManager(getContext(), 4));
        binding.rvSvCatlog.setNestedScrollingEnabled(false);
        //binding.rvSvCatlog.addItemDecoration(new SpacesItemDecoration(10));
        DatabaseReference svCatRef = FirebaseDatabase.getInstance().getReference("ServiceCatlog");

        svCatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot serviceDomainList) {
                if (serviceDomainList.exists()) {

                    for (DataSnapshot serviceDomain : serviceDomainList.getChildren()) {
                        serviceDomainModels.add(serviceDomain.getValue(ServiceDomainModel.class));
                    }
                    serviceCatlogAdapter.notifyDataSetChanged();

                }
                 dialog.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void setUpBannerIndicators() {
        ImageView[] indicators = new ImageView[sliderAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getContext());

            indicators[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dot_light));

            indicators[i].setLayoutParams(layoutParams);

            binding.ll.addView(indicators[i]);

        }


    }

    private void setCurrentBannerIndicator(int index) {
        int childCount = binding.ll.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) binding.ll.getChildAt(i);
            if (index == i) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dot_black));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dot_light));

            }

        }
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            binding.viewPagerImageSlider.setCurrentItem(binding.viewPagerImageSlider.getCurrentItem() + 1);
        }
    };

}