package com.example.accountbalanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReportView extends AppCompatActivity {

    private ArrayList<ItemsData> itemsArrayList = new ArrayList<>();
    private EntryRVAdapter entryRVAdapter;
    private RecyclerView entryRV;
    FirebaseAuth firebaseAuthReport;
    DatabaseReference dbRefReader = FirebaseDatabase.getInstance().getReference();
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_view);
        firebaseAuthReport = FirebaseAuth.getInstance();
        userID = firebaseAuthReport.getCurrentUser().getUid();
        getSupportActionBar().setTitle("Account Balance");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF018786"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.report);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.report:
                        return true;
                    case R.id.account:
                        startActivity(new Intent(getApplicationContext(), AccountRead.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        dbRefReader.child("Users").child(userID).child("items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                for (DataSnapshot snapshot:datasnapshot.getChildren()) {
                    String amount = snapshot.child("Amount").getValue().toString();
                    String type = snapshot.child("Type").getValue().toString();
                    String desc = snapshot.child("Desc").getValue().toString();
                    itemsArrayList.add(new ItemsData(type, amount, desc));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        entryRVAdapter = new EntryRVAdapter(itemsArrayList, ReportView.this);
        entryRV = findViewById(R.id.idRVCourses);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ReportView.this, RecyclerView.VERTICAL, false);
        entryRV.setLayoutManager(linearLayoutManager);

        entryRV.setAdapter(entryRVAdapter);
    }
}