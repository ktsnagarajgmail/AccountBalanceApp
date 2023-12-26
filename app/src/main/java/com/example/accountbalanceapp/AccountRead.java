package com.example.accountbalanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountRead extends AppCompatActivity {

    Button edit;
    TextView txtViewName, txtViewMobile, txtViewEmail;
    FirebaseAuth firebaseAuthProfile;
    String userID;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_read);
        firebaseAuthProfile = FirebaseAuth.getInstance();
        userID = firebaseAuthProfile.getCurrentUser().getUid();

        getSupportActionBar().setTitle("Account Balance");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF018786"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        edit = findViewById(R.id.btnEdit);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.account);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.account:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.report:
                        startActivity(new Intent(getApplicationContext(), ReportView.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AccountEdit.class));
            }
        });

        txtViewName = findViewById(R.id.txtViewNameAccount);
        //txtViewUserName = findViewById(R.id.txtViewUserNameAccount);
        txtViewMobile = findViewById(R.id.txtViewMobileAccount);
        txtViewEmail = findViewById(R.id.txtViewEmailAccount);

        dbRef.child("Users").child(userID).child("info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtViewName.setText(snapshot.child("Name").getValue().toString());
                //txtViewUserName.setText(snapshot.child("Username").getValue().toString());
                txtViewMobile.setText(snapshot.child("Mobile").getValue().toString());
                txtViewEmail.setText(snapshot.child("Email").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}