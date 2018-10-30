package com.example.user.trackit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    EditText email_field;
    EditText pass_field;
    EditText confirem_pass_field;
    EditText trakitid_field;
    ProgressDialog dialog;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    boolean exist = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email_field = findViewById(R.id.email_field);
        pass_field = findViewById(R.id.pass_field);
        confirem_pass_field = findViewById(R.id.confirm_pass_field);
        trakitid_field = findViewById(R.id.trackitid_field);

        dialog = new ProgressDialog(SignupActivity.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users");
    }
    public void onClickSignup(View view){
        final String email = email_field.getText().toString();
        final String pass = pass_field.getText().toString();
        final String confirm_pass = confirem_pass_field.getText().toString();
        final String Track_it_ID = trakitid_field.getText().toString();
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        exist=false;

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.child("id").getValue()!=null) {
                    if (dataSnapshot.child("id").getValue().toString().equals(Track_it_ID)) {
                        exist = true;
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


        if(!email.equals("")&&!pass.equals("")){
            dialog.setMessage("Signing in");
            dialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Signed in successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Intent intent = new Intent(SignupActivity.this,MainActivity.class);
                        intent.putExtra("email",email);
                        startActivity(intent);
                    } else {
                        dialog.setMessage("Creating account");
                        if (!confirm_pass.equals("")&&!Track_it_ID.equals("")) {
                            if(Track_it_ID.toCharArray().length<=6){
                                dialog.dismiss();
                                trakitid_field.setError("should be more than 6 characters in length", ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_error_black_24dp));
                            }else {
                                if(!exist) {
                                    if (confirm_pass.equals(pass)) {
                                        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    DatabaseReference user_ref = databaseReference.push();
                                                    user_ref.child("email").setValue(email);
                                                    user_ref.child("password").setValue(pass);
                                                    user_ref.child("id").setValue(Track_it_ID);
                                                    user_ref.child("tracking").setValue(false);
                                                    Toast.makeText(getApplicationContext(), "Signed up successfully", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(SignupActivity.this,MainActivity.class);
                                                    intent.putExtra("email",email);
                                                    startActivity(intent);
                                                } else {
                                                    dialog.dismiss();
                                                    TextView error_view = findViewById(R.id.error_textview);
                                                    if (task.getException().getLocalizedMessage().contains("email")) {
                                                        email_field.setError(task.getException().getLocalizedMessage(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_error_black_24dp));
                                                    } else if (task.getException().getLocalizedMessage().contains("password")) {
                                                        pass_field.setError(task.getException().getLocalizedMessage(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_error_black_24dp));
                                                    }
                                                    //error_view.setText(task.getException().getLocalizedMessage());
                                                }
                                            }
                                        });
                                    } else {
                                        dialog.dismiss();
                                        confirem_pass_field.setError("Passwords do not match", ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_error_black_24dp));
                                    }
                                }else{
                                    dialog.dismiss();
                                    trakitid_field.setError("This Track it ID aldready exists", ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_error_black_24dp));

                                }
                            }
                        } else {
                            dialog.dismiss();
                            if (confirm_pass.equals("")) {
                                confirem_pass_field.setError("You can't leave this blank", ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_error_black_24dp));
                            }
                            if (Track_it_ID.equals("")) {
                                trakitid_field.setError("You can't leave this blank", ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_error_black_24dp));
                            }
                        }
                    }
                }
            });
        }else{
            if (email.equals("")) {
                email_field.setError("You can't leave this blank", ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_error_black_24dp));
            }
            if (pass.equals("")) {
                pass_field.setError("You can't leave this blank", ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_error_black_24dp));
            }
        }
        /*
        firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Signed up successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }else{
                    TextView error_view = findViewById(R.id.error_textview);
                    if(task.getException().getLocalizedMessage().contains("email")) {
                        email_field.setError(task.getException().getLocalizedMessage(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_error_black_24dp));
                    }else if(task.getException().getLocalizedMessage().contains("password")){
                        pass_field.setError(task.getException().getLocalizedMessage(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_error_black_24dp));
                    }
                    //error_view.setText(task.getException().getLocalizedMessage());
                }
            }
        });
        */
    }
}
