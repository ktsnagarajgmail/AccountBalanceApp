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
import android.view.Gravity;
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

public class AccountReg extends AppCompatActivity {
    Button btnRegSave;
    EditText nameReg, mobileReg, emailReg, pwdReg;
    String nameRegStr, mobileRegStr, emailRegStr, pwdRegStr, userID;
    HashMap hspOne = new HashMap();
    FirebaseAuth firebaseAuth;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    String prevStarted = "yes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_reg);

        getSupportActionBar().setTitle("Account Balance");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF018786"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        nameReg = findViewById(R.id.editTextRegName);
        //userNameReg = findViewById(R.id.editTextRegUserName);
        mobileReg = findViewById(R.id.editTextRegMobile);
        emailReg = findViewById(R.id.editTextRegEmail);
        pwdReg = findViewById(R.id.editTextRegPwd);
        btnRegSave = findViewById(R.id.btnRegSave);

        btnRegSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nameRegStr = nameReg.getText().toString().trim();
                //userNameRegStr = userNameReg.getText().toString().trim();
                mobileRegStr = mobileReg.getText().toString().trim();
                emailRegStr = emailReg.getText().toString().trim();
                pwdRegStr = pwdReg.getText().toString().trim();
                firebaseAuth.createUserWithEmailAndPassword(emailRegStr, pwdRegStr).addOnCompleteListener(AccountReg.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                userID = firebaseAuth.getCurrentUser().getUid();
                                Toast toast = Toast.makeText(AccountReg.this, "Account Created!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM, 0,0);
                                toast.show();
                                hspOne.put("Name", nameRegStr);
                                //hspOne.put("Username", userID);
                                hspOne.put("Mobile", mobileRegStr);
                                hspOne.put("Email", emailRegStr);
                                dbRef.child("Users").child(userID).child("info").setValue(hspOne);
                                dbRef.child("Users").child(userID).child("ABTotal").child("CreditTotal").setValue("0");
                                dbRef.child("Users").child(userID).child("ABTotal").child("DebitTotal").setValue("0");
                                dbRef.child("Users").child(userID).child("items").setValue("1");
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item1").child("Amount").setValue(0.00);
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item1").child("Desc").setValue("XX");
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item1").child("Type").setValue("Debit(-)");
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item2").child("Amount").setValue(0.00);
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item2").child("Desc").setValue("XX");
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item2").child("Type").setValue("Debit(-)");
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item3").child("Amount").setValue(0.00);
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item3").child("Desc").setValue("XX");
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item3").child("Type").setValue("Debit(-)");
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item4").child("Amount").setValue(0.00);
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item4").child("Desc").setValue("XX");
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item4").child("Type").setValue("Debit(-)");
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item5").child("Amount").setValue(0.00);
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item5").child("Desc").setValue("XX");
                                dbRef.child("Users").child(userID).child("TopDebits").child("Item5").child("Type").setValue("Debit(-)");

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(prevStarted, true );
                                editor.apply();
                                startActivity(new Intent(getApplicationContext(), LoginPage.class));
                            } else {
                                Toast toast = Toast.makeText(AccountReg.this, "Account Not Created!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.BOTTOM,0,0);
                                toast.show();
                            }
                        }
                });
            }
        });
    }
}