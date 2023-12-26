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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    EditText email, pwd;
    Button loginBtn;
    TextView regis;
    FirebaseAuth mAuth;
    String prevStarted="yes";

    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(prevStarted, false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(prevStarted, Boolean.TRUE);
            editor.apply();
        } else {
            startActivity(new Intent(this, AccountReg.class));
        }

    }
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        getSupportActionBar().setTitle("Account Balance");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF018786"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.loginEmailID);
        pwd = findViewById(R.id.loginPwd);
        loginBtn = findViewById(R.id.loginBtn);
        regis = findViewById(R.id.loginTxttoRegis);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailogin = email.getText().toString().trim();
                String pwdLogin = pwd.getText().toString().trim();
                mAuth.signInWithEmailAndPassword(emailogin, pwdLogin).addOnSuccessListener(LoginPage.this,new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast toast = Toast.makeText(LoginPage.this, "Login Successful!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM,0,0);
                        toast.show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                })
                        .addOnFailureListener(LoginPage.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast toast = Toast.makeText(LoginPage.this, "Authentication Failed!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM,0,0);
                                toast.show();
                            }
                        });
            }
        });
        regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AccountReg.class));
            }
        });
    }
}