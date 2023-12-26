package com.example.accountbalanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Button add, account, report;
    String typeNewItem, amtNewItem, descriptionNewItem, userID;
    FloatingActionButton floatingActionButton;
    TextView txtCreditValue, txtDebitValue, txtABValue, txtNameCardView, oneAmt, oneDesc, oneType, twoAmt, twoDesc, twoType;
    TextView threeAmt, threeDesc, threeType, fourAmt, fourDesc, fourType, fiveAmt, fiveDesc, fiveType;
    private RadioGroup rdG;
    private RadioButton rdB;
    private EditText amount, desc;
    private ArrayList<ItemsData> accountDataArrayList;
    private EntryRVAdapter entryRVAdapter;
    private RecyclerView entryRV;
    double crdTotalAmt = 0, dbtTotalAmt = 0;
    FirebaseAuth firebaseAuthMain;
    private ArrayList<ItemsData> topItemsArrayList = new ArrayList<>();
    String top1DBAmt, top1DBType,top1DBDesc, top2DBAmt,top2DBType, top2DBDesc, top3DBAmt, top3DBType, top3DBDesct, top3DBDesc, top4DBAmt, top4DBType, top4DBDesc, top5DBAmt, top5DBType, top5DBDesc;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    HashMap hspNewItem = new HashMap();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuthMain = FirebaseAuth.getInstance();
        userID = firebaseAuthMain.getCurrentUser().getUid();
        getSupportActionBar().setTitle("Account Balance");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF018786"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);


        /*account = findViewById(R.id.btnAct);
        report = findViewById(R.id.btnReport);

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AccountRead.class));
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ReportView.class));
            }
        });*/

        txtCreditValue = findViewById(R.id.txtCreditValue);
        txtDebitValue = findViewById(R.id.txtDebitValue);
        txtABValue = findViewById(R.id.txtABValue);
        txtNameCardView = findViewById(R.id.mainCardViewName);
        /*oneAmt = findViewById(R.id.itemOneAmt);
        oneDesc = findViewById(R.id.itemOneDesc);
        oneType = findViewById(R.id.itemOneType);
        twoAmt = findViewById(R.id.itemTwoAmt);
        twoDesc = findViewById(R.id.itemTwoDesc);
        twoType = findViewById(R.id.itemTwoType);
        threeAmt = findViewById(R.id.itemThreeAmt);
        threeDesc = findViewById(R.id.itemThreeDesc);
        threeType = findViewById(R.id.itemThreeType);
        fourAmt = findViewById(R.id.itemFourAmt);
        fourDesc = findViewById(R.id.itemFourDesc);
        fourType = findViewById(R.id.itemFourType);
        fiveAmt = findViewById(R.id.itemFiveAmt);
        fiveDesc = findViewById(R.id.itemFiveDesc);
        fiveType = findViewById(R.id.itemFiveType);*/

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        return true;
                    case R.id.account:
                        startActivity(new Intent(getApplicationContext(), AccountRead.class));
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

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View viewable = layoutInflater.inflate(R.layout.activity_new_items, null);
                PopupWindow popupWindow = new PopupWindow(viewable, 1000, 730, true);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                rdG = viewable.findViewById(R.id.rdG);
                add = viewable.findViewById(R.id.btnAdd);
                amount = viewable.findViewById(R.id.editAmount);
                desc = viewable.findViewById(R.id.editDesc);
                rdG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        rdB = radioGroup.findViewById(i);
                    }
                });
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        typeNewItem = rdB.getText().toString().trim();
                        amtNewItem = amount.getText().toString().trim();
                        descriptionNewItem = desc.getText().toString().trim();

                        if (typeNewItem.isEmpty() && amtNewItem.isEmpty() && descriptionNewItem.isEmpty()) {
                            Toast toast = Toast.makeText(MainActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.show();
                            return;
                        }
                        hspNewItem.put("Type", typeNewItem);
                        hspNewItem.put("Amount", amtNewItem);
                        hspNewItem.put("Desc", descriptionNewItem);
                        dbRef.child("Users").child(userID).child("items").push().setValue(hspNewItem);
                        Toast toast = Toast.makeText(MainActivity.this, "Entry added!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0,0);
                        toast.show();
                        rdG.clearCheck();
                        amount.setText("");
                        desc.setText("");
                        popupWindow.dismiss();
                        if (typeNewItem.equals("Credit(+)")) {
                                dbRef.child("Users").child(userID).child("ABTotal").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        double crdSnapAmt = Double.parseDouble(snapshot.child("CreditTotal").getValue().toString());
                                        double crdEntryAmt = Integer.parseInt(amtNewItem);
                                        crdTotalAmt = crdSnapAmt+crdEntryAmt;
                                        txtCreditValue.setText(String.valueOf(crdTotalAmt));
                                        dbRef.child("Users").child(userID).child("ABTotal").child("CreditTotal").setValue(String.valueOf(crdTotalAmt));
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                        } else if (typeNewItem.equals("Debit(-)")) {
                            dbRef.child("Users").child(userID).child("ABTotal").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    double dbtSnapAmt = Double.parseDouble(snapshot.child("DebitTotal").getValue().toString());
                                    double dbtEntryAmt = Integer.parseInt(amtNewItem);
                                    dbtTotalAmt = dbtSnapAmt+dbtEntryAmt;
                                    txtDebitValue.setText(String.valueOf(dbtTotalAmt));
                                    dbRef.child("Users").child(userID).child("ABTotal").child("DebitTotal").setValue(String.valueOf(dbtTotalAmt));
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            dbRef.child("Users").child(userID).child("TopDebits").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int topDebitsSeries = 1;
                                    String newEntryDebitAmount = amtNewItem;
                                    String newEntryDebitDesc = descriptionNewItem;
                                    String newEntryDebittype = typeNewItem;
                                    for (DataSnapshot datasnapshot:snapshot.getChildren()) {
                                        String item = "Item" + topDebitsSeries;
                                        double dbtSnapAmt = Double.parseDouble(datasnapshot.child("Amount").getValue().toString());
                                        if (Integer.parseInt(newEntryDebitAmount)>dbtSnapAmt){
                                            String topDebitsAmount = datasnapshot.child("Amount").getValue().toString();
                                            String topDebitsDesc = datasnapshot.child("Desc").getValue().toString();
                                            String topDebitsType = datasnapshot.child("Type").getValue().toString();
                                            dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Amount").setValue(newEntryDebitAmount);
                                            dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Desc").setValue(newEntryDebitDesc);
                                            dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Type").setValue(newEntryDebittype);
                                            newEntryDebitAmount = topDebitsAmount;
                                            newEntryDebittype = topDebitsType;
                                            newEntryDebitDesc = topDebitsDesc;
                                        } else {
                                            dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Amount").setValue(datasnapshot.child("Amount").getValue().toString());
                                            dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Desc").setValue(datasnapshot.child("Desc").getValue().toString());
                                            dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Type").setValue(datasnapshot.child("Type").getValue().toString());
                                        }
                                        topDebitsSeries++;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });
            }
        });
            dbRef.child("Users").child(userID).child("ABTotal").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    txtCreditValue.setText(snapshot.child("CreditTotal").getValue().toString());
                    txtDebitValue.setText(snapshot.child("DebitTotal").getValue().toString());
                    txtABValue.setText(String.valueOf(Double.parseDouble(snapshot.child("CreditTotal").getValue().toString())-Double.parseDouble(snapshot.child("DebitTotal").getValue().toString())));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        dbRef.child("Users").child(userID).child("info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtNameCardView.setText(snapshot.child("Name").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        dbRef.child("Users").child(userID).child("TopDebits").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    String amount = snapshot.child("Amount").getValue().toString();
                    String type = snapshot.child("Type").getValue().toString();
                    String desc = snapshot.child("Desc").getValue().toString();
                    topItemsArrayList.add(new ItemsData(type, amount, desc));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        entryRVAdapter = new EntryRVAdapter(topItemsArrayList, MainActivity.this);
        entryRV = findViewById(R.id.idRVCoursesMain);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        entryRV.setLayoutManager(linearLayoutManager);
        entryRV.setAdapter(entryRVAdapter);
        /*dbRef.child("Users").child(userID).child("TopDebits").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                int topDebitsSeries = 1;
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    String amount = snapshot.child("Amount").getValue().toString();
                    String type = snapshot.child("Type").getValue().toString();
                    String desc = snapshot.child("Desc").getValue().toString();
                    String item = "item" + topDebitsSeries;
                    if (item == "item1"){
                        oneAmt.setText(amount);
                        oneDesc.setText(desc);
                        oneType.setText(type);
                    } else if (item == "item2") {
                        twoAmt.setText(amount);
                        twoDesc.setText(desc);
                        twoType.setText(type);
                    } else if (item == "item3") {
                        threeAmt.setText(amount);
                        threeDesc.setText(desc);
                        threeType.setText(type);
                    } else if (item == "item4") {
                        fourAmt.setText(amount);
                        fourDesc.setText(desc);
                        fourType.setText(type);
                    } else {
                        fiveAmt.setText(amount);
                        fiveDesc.setText(desc);
                        fiveType.setText(type);
                    }
                    topDebitsSeries++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.btnLogout:
                firebaseAuthMain.signOut();
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
                break;
            case R.id.rateApp:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                    intent.setPackage("com.android.vending");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.contactSupportTeam:
                Intent sendmail = new Intent(Intent.ACTION_SEND);
                sendmail.putExtra(Intent.EXTRA_EMAIL, new String[] {"nexzsofttechnologies@gmail.com"});
                sendmail.putExtra(Intent.EXTRA_SUBJECT, "Account Balance_Customer Feedback");
                sendmail.setType("message/rfc822");
                startActivity(Intent.createChooser(sendmail, "Choose an email client"));
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}