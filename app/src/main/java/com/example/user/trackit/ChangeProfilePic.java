package com.example.user.trackit;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ChangeProfilePic extends AppCompatActivity {

    ImageView profile_image;
    ImageView outer_image_filter;
    TextView trackit_id_textview;
    TextView followersText;
    TextView followingText;

    String my_id = MainActivity.id;

    int followercount = 0;
    int followingcount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_pic);




        profile_image = findViewById(R.id.settings_profile_imageview);
        outer_image_filter = findViewById(R.id.settings_profile_imageview3);
        trackit_id_textview = findViewById(R.id.settings_profile_trackitid_textview);
        followersText = findViewById(R.id.follower_count);
        followingText = findViewById(R.id.following_count);

        //followingText.setText(getCount("followings"));
        //followersText.setText(getCount("followers"));
        Handler handler1 = new Handler();
        handler1.post(new Runnable() {
            @Override
            public void run() {
                Log.i("HAHA","HAHAHAHAHAHAHA 1");
                setProfileImage();
            }
        });
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getFollowerCount();
            }
        });
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getFollowingCount();
            }
        });
        my_id = MainActivity.id;



        trackit_id_textview.setText(my_id);
        outer_image_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(ChangeProfilePic.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1234);

                }
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,9999);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==9999&&resultCode==RESULT_OK){
            //Toast.makeText(getContext(),"HAHAHAHAHA",Toast.LENGTH_SHORT).show();
            Uri imageuri = data.getData();
            final ProgressDialog dialog = new ProgressDialog(ChangeProfilePic.this);
            //Glide.with(SettingsFragment.this).load(imageuri).into(profile_imageview);
            Picasso.get().load(imageuri).resize(70,70).into(profile_image);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference storageReference = storage.getReference().child("profile_pics").child(MainActivity.id);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference databaseReference = database.getReference().child("profile_pics_uri").child(MainActivity.id);
            dialog.setMessage("Changing profile picture...");
            dialog.show();
            storageReference.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            databaseReference.setValue(uri.toString());
                        }
                    });
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                }
            });
        }
    }

    public void setProfileImage(){
        Log.i("HAHA","HAHAHAHAHAHAHA 2");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference().child("profile_pics_uri");
        Log.i("HAHA","HAHAHAHAHAHAHA 3");
        Log.i("HAHA",my_id+" HAHA");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("HAHA","HAHAHAHAHAHAHA 4");
                if(dataSnapshot.getValue()!=null){
                    //Picasso.get().load(dataSnapshot.getValue().toString()).resize(70,70).into(profile_image);
                    if(dataSnapshot.getKey().equals(MainActivity.id)) {
                        Log.i("HAHA", "HAHAHAHAHAHAHA 5");
                        Glide.with(getApplicationContext()).load(dataSnapshot.getValue().toString()).into(profile_image);
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
    public String getFollowerCount(){
        followercount=0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("followers").child(my_id);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue()!=null) {
                    if(!dataSnapshot.getValue().toString().equals("")) {
                        followercount = followercount + 1;
                        followersText.setText(Integer.toString(followercount));
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
        return Integer.toString(followercount);
    }
    public String getFollowingCount(){
        followingcount=0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("followings").child(my_id);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue()!=null) {
                    if(!dataSnapshot.getValue().toString().equals("")) {
                        followingcount = followingcount + 1;
                        followingText.setText(Integer.toString(followingcount));
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
        return Integer.toString(followingcount);
    }
}

