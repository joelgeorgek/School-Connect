package com.example.user.trackit;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,SearchFragment.OnFragmentInteractionListener,SettingsFragment.OnFragmentInteractionListener {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment;
    SearchFragment searchFragment;
    SettingsFragment settingsFragment;

    String email;
    static String id;
    Boolean tracking = false;

    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},4567);
        }else{
            Intent locationSenderIntent = new Intent(MainActivity.this,MyLocationSenderService.class);
            startService(locationSenderIntent);
        }


        SharedPreferences userdata = getSharedPreferences("UserDataPref",MODE_PRIVATE);
        email = userdata.getString("email","");
        id = userdata.getString("id","");

        homeFragment = new HomeFragment();
        setFragment(homeFragment);

        bottomNavigationView = findViewById(R.id.my_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Home:
                        HomeFragment homeFragment = new HomeFragment();
                        setFragment(homeFragment);
                        return true;
                    case R.id.Find:

                        SearchFragment searchFragment = new SearchFragment();
                        setFragment(searchFragment);
                        return true;
                    case R.id.Settings:

                        SettingsFragment settingsFragment = new SettingsFragment();
                        setFragment(settingsFragment);
                        return true;
                }
                return true;
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            startActivity(new Intent(getApplicationContext(),SignupActivity.class));
        }
        email = getIntent().getStringExtra("email");
        if(email!=null){
            SharedPreferences userdata = getSharedPreferences("UserDataPref",MODE_PRIVATE);
            SharedPreferences.Editor editor = userdata.edit();
            editor.putString("email",email);
            editor.putString("id",id);
            id = userdata.getString("id","");
            editor.apply();
        }else{
            SharedPreferences userdata = getSharedPreferences("UserDataPref",MODE_PRIVATE);
            email = userdata.getString("email","");
            id = userdata.getString("id","");
        }
        //Toast.makeText(getApplicationContext(),email,Toast.LENGTH_SHORT).show();}

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.child("email").getValue()!=null){
                    if(dataSnapshot.child("email").getValue().toString().equals(email)){
                        id = dataSnapshot.child("id").getValue().toString();
                        tracking = (Boolean) dataSnapshot.child("tracking").getValue();
                        //Toast.makeText(getApplicationContext(),tracking+"",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void setFragment(Fragment fragment){
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.myframeLayout,fragment);
        fragmentTransaction.commit();

    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
