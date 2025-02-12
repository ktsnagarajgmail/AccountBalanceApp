package com.actbal.accountbalanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    String userID;
    String[] splitdropdown;
    FloatingActionButton floatingActionButton;
    TabLayout tabLayout;
    PopupWindow popupWindow;
    ViewPager2 viewPager;
    NewItemsFragmentsAdapter newItemsFragmentsAdapter;
    ArrayAdapter<String> arrayAdapter;
    TextView txtCreditValue, txtDebitValue, txtABValue, txtNameCardView;
    private EntryRVAdapter entryRVAdapter;
    private RecyclerView entryRV;
    private ArrayList<ItemsData> topItemsArrayList = new ArrayList<>();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth firebaseAuthMain;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuthMain = FirebaseAuth.getInstance();
        //userID = firebaseAuthMain.getCurrentUser().getUid();
        userID = firebaseAuthMain.getCurrentUser().getEmail();
        userID = userID.substring(0,(userID.length())-4);
        getSupportActionBar().setTitle("Account Balance");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF018786"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        txtCreditValue = findViewById(R.id.txtCreditValue);
        txtDebitValue = findViewById(R.id.txtDebitValue);
        txtABValue = findViewById(R.id.txtABValue);
        txtNameCardView = findViewById(R.id.mainCardViewName);
        txtNameCardView.setText(firebaseAuthMain.getCurrentUser().getDisplayName());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
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
                    case R.id.split_with:
                        startActivity(new Intent(getApplicationContext(), Splitwith.class));
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
                splitdropdown = getResources().getStringArray(R.array.splitdropdown);
                //arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.dropdownlistview,splitdropdown);
                popupWindow = new PopupWindow(viewable, 1000, 1750, true);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                tabLayout = viewable.findViewById(R.id.tablayoutNEFragments);
                viewPager = viewable.findViewById(R.id.viewpagerNEFragment);
                newItemsFragmentsAdapter = new NewItemsFragmentsAdapter(MainActivity.this, popupWindow);
                viewPager.setAdapter(newItemsFragmentsAdapter);
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) { }
                    @Override
                    public void onTabReselected(TabLayout.Tab tab) { }
                });
                viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        tabLayout.getTabAt(position).select();
                    }
                });
            }
        });
        dbRef.child("Users").child(userID).child("ABTotal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DecimalFormat df = new DecimalFormat(",###,###.00");
                txtCreditValue.setText(df.format(new BigDecimal(Double.parseDouble(snapshot.child("CreditTotal").getValue().toString()))));
                txtDebitValue.setText(df.format(new BigDecimal(Double.parseDouble(snapshot.child("DebitTotal").getValue().toString()))));
                Double ABTotalValue = Double.parseDouble(snapshot.child("CreditTotal").getValue().toString())-Double.parseDouble(snapshot.child("DebitTotal").getValue().toString());
                txtABValue.setText(df.format(new BigDecimal(ABTotalValue)));
            }
                @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
        dbRef.child("Users").child(userID).child("TopDebits").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    String amount = snapshot.child("Amount").getValue().toString();
                    String desc = snapshot.child("Desc").getValue().toString();
                    String date = snapshot.child("Date").getValue().toString();
                    topItemsArrayList.add(new ItemsData(amount, desc, date));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        dbRef.child("Users").child(userID).child("Contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    if (snapshot.exists()) {
                        String k = snapshot.getKey().toString();
                        dbRef.child("Users").child(k).child("info").child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    dbRef.child("Users").child(userID).child("Contacts").child(k).setValue(snapshot.getValue().toString());
                                    dbRef.child("Users").child(userID).child("Splitwith").child(k).child("Name").setValue(snapshot.getValue().toString());
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        entryRVAdapter = new EntryRVAdapter(topItemsArrayList, MainActivity.this);
        entryRV = findViewById(R.id.idRVCoursesMain);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        entryRV.setLayoutManager(linearLayoutManager);
        entryRV.setAdapter(entryRVAdapter);
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

//------------
/*equally.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.newItemsFrameLayout, new Equally(), null);
                        fragmentTransaction.commit();
                    }
                });
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(frameLayout.getId(), new Share());
                        fragmentTransaction.commit();
                    }
                });
                percentage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.newItemsFrameLayout, new Percentage());
                        fragmentTransaction.commit();
                        //NewItems.fragmentManager.beginTransaction().add(R.id.newItemsFrameLayout, new Equally(), null).commit();
                    }
                });*/
                /*emailIDslist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {*/
//------------

//------------
/*Bundle bundle = new Bundle();
                bundle.putStringArray("emailIDs", splitwithArray);
                Equally equally = new Equally(splitwithArray);
                equally.setArguments(bundle);*/
//splitswith = splitwith.getText().toString().trim();
//splitwithArray = splitswith.split(",", 0);

//        dbRef.child("Users").child(userID).child("info").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                txtNameCardView.setText(snapshot.child("Name").getValue().toString());
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
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
//------------

//------------

                    /*}
                });*/

                /*rdG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        rdB = radioGroup.findViewById(i);
                    }
                });*/
                /*add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle extras = getIntent().getExtras();
                        String selectedEmailIDs = extras.getString("EmailIDs");
                        typeNewItem = rdB.getText().toString().trim();
                        amtNewItem = amount.getText().toString().trim();
                        descriptionNewItem = desc.getText().toString().trim();
                        //splitswith = splitwith.getText().toString().trim();
                        itemViewModel.getData().observe(MainActivity.this, item -> {
                            //splitswith = item;
                            arraylistNewItemsFragSG = item;
                            //Toast.makeText(MainActivity.this, String.valueOf(arraylistNewItemsFragSG.size()), Toast.LENGTH_SHORT).show();
                        });
                        if (typeNewItem.isEmpty() && amtNewItem.isEmpty() && descriptionNewItem.isEmpty()) {
                            Toast toast = Toast.makeText(MainActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.show();
                            return;
                        }
                        if(splitswith.equals("")) {
                            hspNewItem.put("Type", typeNewItem);
                            hspNewItem.put("Amount", amtNewItem);
                            hspNewItem.put("Desc", descriptionNewItem);
                            dbRef.child("Users").child(userID).child("items").push().setValue(hspNewItem);
                        } else {
                            splitwithArray = splitswith.split(",", 0);
                            //splitwithArray = new String[]{"ktsraj@gmail.com", "ktsraja@gmail.com", "ktstest@gmail.com"};
                            switch (splitOptions) {
                                case "equally":
                                    splitAmount = Integer.parseInt(amtNewItem) / (splitwithArray.length + 1);
                            }
                            *//*stringArrayCount = 0;
                            do {
                                Toast.makeText(MainActivity.this, splitwithArray[stringArrayCount], Toast.LENGTH_SHORT).show();
                                dbRef.child("Users").orderByChild("info/Email").equalTo(splitwithArray[stringArrayCount])
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    int i = 1;
                                                    splitswithUID = dataSnapshot.getKey().toString();
                                                    Toast.makeText(MainActivity.this, splitswithUID, Toast.LENGTH_SHORT).show();
                                                    name = dataSnapshot.child("info").child("Name").getValue().toString();
                                                    splitOwesAmount = Integer.parseInt(dataSnapshot.child("Splitwith").child(userID).child("Owes").getValue().toString().substring(1));
                                                }
                                                hspSplitNewItem.put("TotalAmount", amtNewItem);
                                                hspSplitNewItem.put("SplitAmount", splitAmount);
                                                hspSplitNewItem.put("Desc", descriptionNewItem);
                                                hspSplitNewItem.put("PaidBy", payeeName);

                                                splitOwesAmount = splitOwesAmount + splitAmount;
                                                dbRef.child("Users").child(userID).child("Splitwith").child(splitswithUID).child("Items").push().setValue(hspSplitNewItem);
                                                dbRef.child("Users").child(userID).child("Splitwith").child(splitswithUID).child("Owes").setValue(splitOwesAmount);
                                                dbRef.child("Users").child(userID).child("Splitwith").child(splitswithUID).child("Name").setValue(name);
                                                dbRef.child("Users").child(splitswithUID).child("Splitwith").child(userID).child("Items").push().setValue(hspSplitNewItem);
                                                dbRef.child("Users").child(splitswithUID).child("Splitwith").child(userID).child("Owes").setValue("-"+splitOwesAmount);
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {  }
                                        });
                                splitOwesAmount = 0;
                                stringArrayCount++;
                            } while (stringArrayCount < splitwithArray.length);*//*
                            int a = 0;
                            do {
                                hspSplitNewItem.put("TotalAmount", amtNewItem);
                                hspSplitNewItem.put("SplitAmount", splitAmount);
                                hspSplitNewItem.put("Desc", descriptionNewItem);
                                hspSplitNewItem.put("PaidBy", payeeName);
                                splitOwesAmount = arraylistNewItemsFragSG.get(a).getAmount();
                                name = arraylistNewItemsFragSG.get(a).getName();
                                splitswithUID = arraylistNewItemsFragSG.get(a).getUID();
                                splitOwesAmount = splitOwesAmount + splitAmount;
                                dbRef.child("Users").child(userID).child("Splitwith").child(splitswithUID).child("Items").push().setValue(hspSplitNewItem);
                                dbRef.child("Users").child(userID).child("Splitwith").child(splitswithUID).child("Owes").setValue(splitOwesAmount);
                                dbRef.child("Users").child(userID).child("Splitwith").child(splitswithUID).child("Name").setValue(name);
                                dbRef.child("Users").child(splitswithUID).child("Splitwith").child("ktsnagaraj@gmail").child("Items").push().setValue(hspSplitNewItem);
                                dbRef.child("Users").child(splitswithUID).child("Splitwith").child("ktsnagaraj@gmail").child("Owes").setValue("-" + splitOwesAmount);
                                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        splitOption = adapterView.getItemAtPosition(i).toString();
                                        Toast.makeText(getApplicationContext(), splitOption, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                a++;
                            }while(a < arraylistNewItemsFragSG.size());
                        }
                        *//*stringArrayCount = 0;
                        do {
                        Toast.makeText(getApplicationContext(), splitswithUIDs.size() + " Owes", Toast.LENGTH_SHORT).show();
                            dbRef.child("Users").child(userID).child("Splitwith").child(splitswithUIDs.get(stringArrayCount)).child("Owes").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Toast.makeText(getApplicationContext(), "check", Toast.LENGTH_SHORT).show();
                                    splitOwesAmount = Integer.parseInt(snapshot.getValue().toString());
                                    splitOwesAmount = splitOwesAmount + splitAmount;
                                    dbRef.child("Users").child(userID).child("Splitwith").child(splitswithUID).child("Owes").setValue(splitOwesAmount);
                                    dbRef.child("Users").child(splitswithUID).child("Splitwith").child(userID).child("Owes").setValue("-"+splitOwesAmount);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            splitOwesAmount = 0;
                            stringArrayCount++;
                        } while (stringArrayCount < splitswithUIDs.size());*//*

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
                        dbRef.child("Users").child(userID).child("Splitwith").child(splitswithUID).child("Items").push().setValue(hspNewItem);
                        dbRef.child("Users").child(userID).child("Splitwith").child(splitswithUID).child("Owes").setValue(amtNewItem);
                        //splitswithUID=null;
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
                });*/
//-----------