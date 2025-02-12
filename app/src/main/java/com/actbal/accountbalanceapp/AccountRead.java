package com.actbal.accountbalanceapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
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

public class AccountRead extends AppCompatActivity {

    Button edit, deleteAccount, logoutAccount, rateAccountBalance, contactCustomerSupport;
    TextView txtViewName, txtViewMobile, txtViewEmail, deleteAct, logout, privacypolicy;
    FirebaseAuth firebaseAuthProfile;
    String userID;
    ImageView imageView;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    StorageReference storageReference;

    ActivityResultLauncher activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode()==RESULT_OK){
                Intent data = result.getData();
                Uri uriData = data.getData();
                imageView.setImageURI(uriData);
                Glide.with(AccountRead.this).load(uriData).apply(RequestOptions.circleCropTransform()).into(imageView);
                storageReference.putFile(uriData);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_read);
        firebaseAuthProfile = FirebaseAuth.getInstance();
        //userID = firebaseAuthProfile.getCurrentUser().getUid();
        userID = firebaseAuthProfile.getCurrentUser().getEmail();
        userID = userID.substring(0,(userID.length())-4);
        getSupportActionBar().setTitle("Account Balance");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF018786"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //edit = findViewById(R.id.btnEdit);

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
                    case R.id.split_with:
                        startActivity(new Intent(getApplicationContext(), Splitwith.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        /*edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AccountEdit.class));
            }
        });*/

        txtViewName = findViewById(R.id.txtViewNameAccount);
        //txtViewUserName = findViewById(R.id.txtViewUserNameAccount);
        //txtViewMobile = findViewById(R.id.txtViewMobileAccount);
        txtViewEmail = findViewById(R.id.txtViewEmailAccount);
        deleteAccount = findViewById(R.id.btn_act_read_delete_account);
        logoutAccount = findViewById(R.id.btn_act_read_logout);
        privacypolicy = findViewById(R.id.tv_act_read_privacy_policy);
        imageView = findViewById(R.id.profileImageView);
        rateAccountBalance = findViewById(R.id.btn_rate_account_balance);
        contactCustomerSupport = findViewById(R.id.btn_contact_support_team);
        storageReference = FirebaseStorage.getInstance().getReference(userID);
        /*txtViewName.setText(firebaseAuthProfile.getCurrentUser().getDisplayName());
        txtViewEmail.setText(firebaseAuthProfile.getCurrentUser().getEmail());*/

        try {
            File localFile = File.createTempFile(userID, ".jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    Glide.with(AccountRead.this).load(bitmap).apply(RequestOptions.circleCropTransform()).into(imageView);
                    //imageView.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AccountRead.this, "Profile Photo not loaded, contact Support Team", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        rateAccountBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                intent.setPackage("com.android.vending");
                startActivity(intent);
            }
        });
        contactCustomerSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendmail = new Intent(Intent.ACTION_SEND);
                sendmail.putExtra(Intent.EXTRA_EMAIL, new String[] {"nexzsofttechnologies@gmail.com"});
                sendmail.putExtra(Intent.EXTRA_SUBJECT, "Account Balance_Customer Feedback");
                sendmail.setType("message/rfc822");
                startActivity(Intent.createChooser(sendmail, "Choose an email client"));
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        dbRef.child("Users").child(userID).child("info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtViewName.setText(snapshot.child("Name").getValue().toString());
                //txtViewUserName.setText(snapshot.child("Username").getValue().toString());
                //txtViewMobile.setText(snapshot.child("Mobile").getValue().toString());
                txtViewEmail.setText(snapshot.child("Email").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        logoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuthProfile.signOut();
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        });
        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nexzsoft.com/")));
            }
        });
        /*deleteAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        firebaseAuthProfile.getInstance().getCurrentUser().delete();
                        startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        });*/
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountRead.this);
                builder.setTitle("Confirm")
                .setMessage("Are you sure to Delete Account?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuthProfile.getInstance().getCurrentUser().delete();
                        dialog.dismiss();
                        firebaseAuthProfile.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), LoginPage.class));
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.cancel();
                    }
                }).show();
            }
        });
    }
}