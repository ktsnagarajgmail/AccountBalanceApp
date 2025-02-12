package com.actbal.accountbalanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.net.UriCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SplitActivityDetails extends AppCompatActivity {
    TextView amt, txtViewPayer, txtViewReceiver;
    EditText settleAmt;
    ImageView payer, receiver;
    ArrayList<ItemsDetailSG> itemsDetailSGSS = new ArrayList<>();
    FriendItemsDetailAdapter friendItemsDetailAdapter;
    RecyclerView recyclerView;
    Bitmap bitmapuserId, bitmapFriendsUID;
    private FirebaseAuth firebaseAuthReport;
    StorageReference storageReferenceUserId, storageReferenceFriendsUID;
    String userID, friendsUID, amount, dateNewItem;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    Button settle, reminder, submit;
    HashMap hmNewItem = new HashMap();
    HashMap hmSplitNewItem = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_details);

        getSupportActionBar().setTitle("Account Balance");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF018786"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        firebaseAuthReport = FirebaseAuth.getInstance();
        DecimalFormat df = new DecimalFormat(",###,###.00");
        //userID = firebaseAuthReport.getCurrentUser().getUid();
        userID = firebaseAuthReport.getCurrentUser().getEmail();
        userID = userID.substring(0,(userID.length())-4);
        amt = findViewById(R.id.splitAmountDetails);
        settle = findViewById(R.id.settle);
        reminder = findViewById(R.id.reminder);
        Bitmap bitmapConversion = BitmapFactory.decodeResource(this.getResources(), R.drawable.profile);
        //FirebaseStorage.getInstance().getReference().putFile()
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.split_with);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.account:
                        startActivity(new Intent(getApplicationContext(), AccountRead.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.report:
                        startActivity(new Intent(getApplicationContext(), ReportView.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.split_with:
                        return true;
                }
                return false;
            }
        });
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String dayInString;
        if(day<10){
            dayInString = "0"+String.valueOf(day);
        } else {
            dayInString = String.valueOf(day);
        }
        int month = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);
        dateNewItem = dayInString+"/"+getMonthFormat(month)+"/"+String.valueOf(year);
        Intent intent = getIntent();
        friendsUID = intent.getStringExtra("friendsUID");
        amount = intent.getStringExtra("amounts");
        storageReferenceUserId = FirebaseStorage.getInstance().getReference(userID);
        storageReferenceFriendsUID = FirebaseStorage.getInstance().getReference(friendsUID);

        try {
            File localFileuserId = File.createTempFile(userID, ".jpg");
            File localFileFriendsUID = File.createTempFile(friendsUID, ".jpg");
            storageReferenceUserId.getFile(localFileuserId).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    bitmapuserId = BitmapFactory.decodeFile(localFileuserId.getAbsolutePath());
                }
            });
            storageReferenceFriendsUID.getFile(localFileFriendsUID).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    bitmapFriendsUID = BitmapFactory.decodeFile(localFileFriendsUID.getAbsolutePath());
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (Double.parseDouble(amount)>0){
            amt.setText("Owes you: "+String.valueOf(df.format(new BigDecimal(Double.parseDouble(amount)))));
            amt.setTextColor(ContextCompat.getColor(this, R.color.green));
        } else {
            amt.setText("You Owe: "+String.valueOf(df.format(new BigDecimal(Double.parseDouble(amount)))).substring(1));
            amt.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
        friendItemsDetailAdapter = new FriendItemsDetailAdapter(itemsDetailSGSS, SplitActivityDetails.this);
        recyclerView = findViewById(R.id.idRVSplitData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SplitActivityDetails.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(friendItemsDetailAdapter);
        itemsDetailSGSS = getitemsDetailSGSS();

        settle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View viewable = layoutInflater.inflate(R.layout.activity_settlement_item, null);
                PopupWindow popupWindow = new PopupWindow(viewable, 800, 800, true);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                settleAmt = viewable.findViewById(R.id.settlementAmt);
                submit = viewable.findViewById(R.id.settlementAmtSubmit);
                txtViewPayer = viewable.findViewById(R.id.txtViewPayer);
                txtViewReceiver = viewable.findViewById(R.id.txtViewReceiver);
                payer = viewable.findViewById(R.id.settlePayer);
                receiver = viewable.findViewById(R.id.settleReceiver);
                if (Double.parseDouble(amount) > 0) {
                    txtViewPayer.setText(friendsUID + " paid ");
                    txtViewReceiver.setText(userID);
                    Glide.with(viewable).load(bitmapuserId).apply(RequestOptions.circleCropTransform()).into(receiver);
                } else {
                    txtViewPayer.setText(userID);
                    txtViewReceiver.setText(friendsUID + " paid ");
                    Glide.with(viewable).load(bitmapuserId).apply(RequestOptions.circleCropTransform()).into(payer);
                }
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        double newOweAmount;
                        if (Double.parseDouble(amount) < 0) {
                            userID = friendsUID;
                            friendsUID = firebaseAuthReport.getCurrentUser().getEmail().substring(0, firebaseAuthReport.getCurrentUser().getEmail().length()-4);
                            newOweAmount = Double.parseDouble(amount)+Double.parseDouble(settleAmt.getText().toString());
                        } else {
                            newOweAmount = Double.parseDouble(amount)-Double.parseDouble(settleAmt.getText().toString());
                        }
                        hmSplitNewItem.put("TotalAmount", settleAmt.getText().toString());
                        hmSplitNewItem.put("SplitAmount", "0");
                        hmSplitNewItem.put("Desc", "Settlement");
                        hmSplitNewItem.put("PaidBy", userID);
                        hmSplitNewItem.put("Date", dateNewItem);
                        hmNewItem.put("Type", "Credit(+)");
                        hmNewItem.put("Amount", settleAmt.getText().toString());
                        hmNewItem.put("Desc", "Split-Settlement");
                        hmNewItem.put("Date", dateNewItem);
                        dbRef.child("Users").child(userID).child("Splitwith").child(friendsUID).child("Owes").setValue(newOweAmount);
                        dbRef.child("Users").child(userID).child("items").push().setValue(hmNewItem);
                        dbRef.child("Users").child(userID).child("Splitwith").child(friendsUID).child("Items").push().setValue(hmSplitNewItem);
                        dbRef.child("Users").child(friendsUID).child("items").push().setValue(hmNewItem);
                        dbRef.child("Users").child(friendsUID).child("Splitwith").child(userID).child("Items").push().setValue(hmSplitNewItem);
                        if (newOweAmount > 0){
                            dbRef.child("Users").child(friendsUID).child("Splitwith").child(userID).child("Owes").setValue(0-newOweAmount);
                        } else {
                            dbRef.child("Users").child(friendsUID).child("Splitwith").child(userID).child("Owes").setValue(newOweAmount);
                        }
                        popupWindow.dismiss();
                    }
                });
            }
        });
    }
    public ArrayList<ItemsDetailSG> getitemsDetailSGSS(){
        dbRef.child("Users").child(userID).child("Splitwith").child(friendsUID).child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String paidBy = dataSnapshot.child("PaidBy").getValue().toString();
                    String splitAmt = dataSnapshot.child("SplitAmount").getValue().toString();
                    String totalAmt = dataSnapshot.child("TotalAmount").getValue().toString();
                    String description = dataSnapshot.child("Desc").getValue().toString();
                    String date = dataSnapshot.child("Date").getValue().toString();
                    itemsDetailSGSS.add(new ItemsDetailSG(paidBy, splitAmt, totalAmt, description, date));
                    if(friendItemsDetailAdapter != null) {
                        friendItemsDetailAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return itemsDetailSGSS;
    }
    private String getMonthFormat(int month) {
        if (month == 1) return "Jan";
        else if (month == 2) return "Feb";
        else if (month == 3) return "Mar";
        else if (month == 4) return "Apr";
        else if (month == 5) return "May";
        else if (month == 6) return "Jun";
        else if (month == 7) return "Jul";
        else if (month == 8) return "Aug";
        else if (month == 9) return "Sep";
        else if (month == 10) return "Oct";
        else if (month == 11) return "Nov";
        else if (month == 12) return "Dec";
        return "Jan";
    }
}
        /*Bundle args = getIntent().getBundleExtra("BUNDLE");
        ArrayList<SplitFriendsSG> arrayList = (ArrayList<SplitFriendsSG>) args.getSerializable("ARRAYLIST");
        /*type.setText(intent.getStringExtra("name"));
        amt.setText(intent.getStringExtra("amounts"));*/
        // Used this for arraylist transfer between intents
       /* Bundle args = getIntent().getBundleExtra("BUNDLE");
        //intent.getBundleExtra("BUNDLE");
        itemsDetailSGSS = (ArrayList<ItemsDetailSG>) args.getSerializable("ARRAYLIST");*/