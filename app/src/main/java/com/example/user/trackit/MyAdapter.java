package com.example.user.trackit;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    int counter1=0;
    int counter2=0;
    String followerKey = "";
    String followingKey = "";
    ArrayList<String> id_dataset = new ArrayList<>();
    ArrayList<String> downloaduri_dataset = new ArrayList<>();
    Context context;
    boolean following = false;
    boolean withButton = true;
    //private HomeFragment.ItemClickListener clickListener;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public MyAdapter(ArrayList<String> id_dataset,ArrayList<String> downloaduri_dataset, Context context,boolean withButton) {
        this.id_dataset = id_dataset;
        this.context = context;
        this.downloaduri_dataset = downloaduri_dataset;
        this.withButton = withButton;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        following=false;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_recycler_item_layout,parent,false);

        ImageView imageView = view.findViewById(R.id.recycler_item_imageview);
        TextView textView = view.findViewById(R.id.recycler_item_textview);
        final Button button = view.findViewById(R.id.my_followbutton);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        SharedPreferences userdata = context.getSharedPreferences("UserDataPref",MODE_PRIVATE);
       // final String id = userdata.getString("id","");
        final String id = MainActivity.id;
        TextView textView = holder.view.findViewById(R.id.recycler_item_textview);
        ImageView imageView = holder.view.findViewById(R.id.recycler_item_imageview);


        textView.setText(id_dataset.get(position));
        if(position<(downloaduri_dataset.size())) {
            Log.i("HAHA",downloaduri_dataset.get(position)+"HAHAHAHAHA");
            Picasso.get().load(downloaduri_dataset.get(position)).resize(70,70).into(imageView);
        }
        final Button button = holder.view.findViewById(R.id.my_followbutton);


        if(!withButton){
             button.setVisibility(View.GONE);
            RelativeLayout relativeLayout = (RelativeLayout)holder.view.findViewById(R.id.recycler_item_relativelayout);
            relativeLayout.setGravity(Gravity.LEFT);
        }


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("followings").child(MainActivity.id);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue()!=null){
                    if(dataSnapshot.getValue().toString().equals(id_dataset.get(position))){
                        button.setText("Unfollow");
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

         button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                counter1=0;
                counter2=0;
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReferencefollowers = firebaseDatabase.getReference().child("followers").child(id_dataset.get(position));
                final DatabaseReference databaseReferencefollowing = firebaseDatabase.getReference().child("followings").child(id);
                databaseReferencefollowing.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                       if(dataSnapshot.getValue()!=null){
                           if(dataSnapshot.getValue().toString().equals(id_dataset.get(position))) {
                               counter1++;
                               if (counter1 == 2) {
                                   databaseReferencefollowing.child(followingKey).removeValue();
                                   dataSnapshot.getRef().removeValue();
                                   following=false;
                                   button.setText("Follow");
                               }else if(counter1==1){
                                   followingKey = dataSnapshot.getKey();
                               }
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
                databaseReferencefollowers.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.getValue() != null) {
                            if (dataSnapshot.getValue().toString().equals(id)) {
                                counter2++;
                                if (counter2 == 2) {
                                    databaseReferencefollowers.child(followerKey).removeValue();
                                    dataSnapshot.getRef().removeValue();
                                    button.setText("Follow");
                                    following=false;
                                }else if(counter2==1){
                                    followerKey = dataSnapshot.getKey();
                                }
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

                addfollowingsandfollowers(id,position,button,databaseReferencefollowers,databaseReferencefollowing);
            }
        });


    }

    public void addfollowingsandfollowers(String id,int position,Button button,DatabaseReference databaseReferencefollowers,DatabaseReference databaseReferencefollowing){
        if(!following) {
            databaseReferencefollowers.push().setValue(id);
            databaseReferencefollowing.push().setValue(id_dataset.get(position));
            button.setText("Unfollow");
        }else{
            button.setText("Follow");
        }
    }

    @Override
    public int getItemCount() {
        return id_dataset.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View view;
        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(itemClickListener!=null){itemClickListener.onClick(v,getAdapterPosition());}
        }
    }
}
