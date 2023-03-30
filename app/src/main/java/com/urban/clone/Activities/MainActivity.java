package com.urban.clone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.urban.clone.Fragments.BookingsFragment;
import com.urban.clone.Fragments.HomeFragment;
import com.urban.clone.Fragments.ProfileFragment;
import com.urban.clone.R;
import com.urban.clone.SessionManager;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectorFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.black));
        Fragment f1 = new HomeFragment();
        Fragment f2 = new BookingsFragment();
        Fragment f3 = new ProfileFragment();
        FragmentManager fm = getSupportFragmentManager();


        fm.beginTransaction().add(R.id.fragmentContainerView,f3,"3").hide(f3).commit();
        fm.beginTransaction().add(R.id.fragmentContainerView,f2,"2").hide(f2).commit();
        fm.beginTransaction().add(R.id.fragmentContainerView,f1,"1").commit();
        selectorFragment = f1;

        bottomNavigationView = findViewById(R.id.bnv);




        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()){

                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().hide(selectorFragment).show(f1).commit();
                        selectorFragment=  f1;
                        break;


                    case R.id.bookings:
                        getSupportFragmentManager().beginTransaction().hide(selectorFragment).show(f2).commit();
                        selectorFragment=  f2;
                        break;
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().hide(selectorFragment).show(f3).commit();
                        selectorFragment=  f3;
                        break;







                }




                return true;
            }
        });

    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}