package com.example.user.trackit;

import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyLocationSenderService extends Service implements LocationListener {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    double last_lat;
    double last_lng;

    @Override
    public void onCreate() {
        super.onCreate();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Log.i("HAHA","IN LOCATION SENDER SERVICE");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }else {
            Log.i("HAHA","LOCATION PERMISSIONS GRANTED");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            Location location = null;
            while (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        long current_time = System.currentTimeMillis();
        Location location_from = new Location("");
        location_from.setLatitude(last_lat);
        location_from.setLongitude(last_lng);

        last_lat = location.getLatitude();
        last_lng = location.getLongitude();

        float Speed = location_from.distanceTo(location)/1;
        SharedPreferences userdata = getSharedPreferences("UserDataPref",MODE_PRIVATE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("location").child(userdata.getString("id",""));
        reference.child("latitude").setValue(location.getLatitude());
        reference.child("longitude").setValue(location.getLongitude());
        reference.child("speed").setValue(Double.toString(Speed));
        Log.i("HAHA","Lat : "+location.getLatitude());
        Log.i("HAHA","Lon : "+location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
