package com.example.accountbalanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AccountEdit extends AppCompatActivity {
    Button btnSave, btnBack;
    EditText name, mobile, email, pwd;
    String nameStr, mobileStr, emailStr, pwdStr, userID;
    HashMap hspOne = new HashMap();
    FirebaseAuth firebaseAuth;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    String prevStarted = "yes";

    /*protected void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean(prevStarted, false)) {
            email.setEnabled(false);
            pwd.setEnabled(false);
            btnBack.setVisibility(View.VISIBLE);
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        getSupportActionBar().setTitle("Account Balance");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF018786"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        name = findViewById(R.id.editTextName);
        //userName = findViewById(R.id.editTextUserName);
        mobile = findViewById(R.id.editTextMobile);
        email = findViewById(R.id.editTextEmail);
        pwd = findViewById(R.id.editTextPwd);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        email.setEnabled(false);
        pwd.setEnabled(false);

        dbRef.child("Users").child(userID).child("info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("Name").getValue().toString());
                //userName.setText(snapshot.child("Username").getValue().toString());
                mobile.setText(snapshot.child("Mobile").getValue().toString());
                email.setText(snapshot.child("Email").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nameStr = name.getText().toString().trim();
                //userNameStr = userName.getText().toString().trim();
                mobileStr = mobile.getText().toString().trim();
                emailStr = email.getText().toString().trim();
                dbRef.child("Users").child(userID).child("info").child("Name").setValue(nameStr);
                dbRef.child("Users").child(userID).child("info").child("Mobile").setValue(mobileStr);
                Toast.makeText(getApplicationContext(), "Account Updated Successfully !!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), AccountRead.class));
                //Query queries = dbRef.child("Users").orderByKey().equalTo(userNameStr);

                /*ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Toast.makeText(getApplicationContext(),"account data", Toast.LENGTH_SHORT).show();
                        if(!dataSnapshot.exists()) {*/
                            /*hspOne.put("Name", nameStr);
                            hspOne.put("Username", userNameStr);
                            hspOne.put("Mobile", mobileStr);
                            hspOne.put("Email",emailStr);
                            Toast.makeText(getApplicationContext(),"account datass", Toast.LENGTH_SHORT).show();
                            dbRef.child("Users").child(userNameStr).child("info").setValue(hspOne);
                            Toast.makeText(getApplicationContext(), "Account Data Added/Edited Successfully!!", Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(prevStarted, true );
                            editor.commit();*/

                           /* firebaseAuth.createUserWithEmailAndPassword(emailStr, pwdStr).addOnCompleteListener(AccountEdit.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AccountEdit.this, "Registration Succ", Toast.LENGTH_SHORT).show();
                            userID = firebaseAuth.getCurrentUser().getUid();
                            startActivity(new Intent(getApplicationContext(), LoginPage.class));
                        } else {
                            Toast.makeText(AccountEdit.this, "Unsucc Regis", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Toast.makeText(getApplicationContext(), firebaseAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
*/
                //startActivity(new Intent(getApplicationContext(), AccountRead.class));
                        /*}
                        else {
                            Toast.makeText(getApplicationContext(),"Username already exist",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };
                queries.addListenerForSingleValueEvent(eventListener);*/
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AccountRead.class));
            }
        });
    }
}