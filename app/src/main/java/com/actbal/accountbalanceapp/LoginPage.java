package com.actbal.accountbalanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginPage extends AppCompatActivity {

    EditText email, pwd;
    Button loginBtn;
    TextView regis;
    FirebaseAuth mAuth;
    FirebaseAuth firebaseAuth;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    String prevStarted="yes", userID;
    SignInButton signInButton;
    GoogleIdTokenCredential googleIdTokenCredential;

    /*protected void onResume() {
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
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editorSharedPreference = sharedPreferences.edit();

        getSupportActionBar().setTitle("Account Balance");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FF018786"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.loginEmailID);
        pwd = findViewById(R.id.loginPwd);
        loginBtn = findViewById(R.id.loginBtn);
        regis = findViewById(R.id.loginTxttoRegis);
        signInButton = findViewById(R.id.googleSignIn);

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(LoginPage.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(LoginPage.this, MainActivity.class));
                //Toast.makeText(MainActivity.this, "Authentication Successful", Toast.LENGTH_SHORT).show();
                /*userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                currentUserUID =FirebaseAuth.getInstance().getUid();
                signInButton.setVisibility(View.GONE);
                db = FirebaseDatabase.getInstance().getReference();
                itemsListViewAdapter = new ItemsListViewAdapter(itemsDataSG, MainActivity.this);
                recyclerView = findViewById(R.id.recyclerView);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(itemsListViewAdapter);
                itemsDataSG = getItemsDataSG();*/
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginPage.this, "Authentication Failed, please use device pattern to authenticate.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginPage.this, LoginPage.class));
            }
        });
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock the App")
                .setSubtitle("Use biometrics to access your account")
                .setDescription("Scan your fingerprint.")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();


        if (firebaseAuth.getCurrentUser()==null) {
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String emailogin = email.getText().toString().trim();
                    String pwdLogin = pwd.getText().toString().trim();
                    mAuth.signInWithEmailAndPassword(emailogin, pwdLogin).addOnSuccessListener(LoginPage.this, new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast toast = Toast.makeText(LoginPage.this, "Login Successful!", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                                    toast.show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }
                            })
                            .addOnFailureListener(LoginPage.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast toast = Toast.makeText(LoginPage.this, "Authentication Failed!", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 0, 0);
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
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CredentialManager credentialManager = CredentialManager.create(getApplicationContext());
                    GetGoogleIdOption getGoogleIdOption = new GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId("319140683369-fe38beg4rtu1qhgrfmetjgcsn1i4blkd.apps.googleusercontent.com")
                            .setAutoSelectEnabled(true)
                            .build();
                    GetCredentialRequest getCredentialRequest = new GetCredentialRequest.Builder()
                            .addCredentialOption(getGoogleIdOption)
                            .build();
                    credentialManager.getCredentialAsync(
                            getApplicationContext(),
                            getCredentialRequest,
                            new CancellationSignal(),
                            Executors.newSingleThreadExecutor(),
                            new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                                @Override
                                public void onResult(GetCredentialResponse result) { handleSignIn(result); }
                                private void handleSignIn(GetCredentialResponse result) {
                                    Credential credential = result.getCredential();
                                    if (credential instanceof CustomCredential) {
                                        if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
                                            googleIdTokenCredential = GoogleIdTokenCredential.createFrom(((CustomCredential) credential).getData());
                                            AuthCredential authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.getIdToken(), null);
                                            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        firebaseAuth = FirebaseAuth.getInstance();
                                                        Toast.makeText(getApplicationContext(), "Signed in Successfully!", Toast.LENGTH_SHORT).show();
                                                        if (sharedPreferences.getBoolean("firstLogin", true)) {
                                                            userID = googleIdTokenCredential.getId().substring(0, (googleIdTokenCredential.getId().length()) - 4);
                                                            /*userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                                            currentUserUID =FirebaseAuth.getInstance().getUid();*/
                                                            //dbRef.child("Users").child("UID_Data").child(emailRegStr.substring(0,emailReg.length()-4)).setValue(nameReg);
                                                            dbRef.child("Users").child(userID).child("ABTotal").child("CreditTotal").setValue("0");
                                                            dbRef.child("Users").child(userID).child("ABTotal").child("DebitTotal").setValue("0");
                                                            /*dbRef.child("Users").child(userID).child("items").setValue("1");
                                                            dbRef.child("Users").child(userID).child("Splitwith").setValue("A");
                                                            dbRef.child("Users").child(userID).child("Contacts").setValue("A");*/
                                                            dbRef.child("Users").child(userID).child("info").child("Email").setValue(firebaseAuth.getCurrentUser().getEmail());
                                                            dbRef.child("Users").child(userID).child("info").child("Name").setValue(firebaseAuth.getCurrentUser().getDisplayName());
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item1").child("Amount").setValue(0.00);
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item1").child("Desc").setValue("XX");
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item1").child("Date").setValue("-/-/-");
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item2").child("Amount").setValue(0.00);
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item2").child("Desc").setValue("XX");
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item2").child("Date").setValue("-/-/-");
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item3").child("Amount").setValue(0.00);
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item3").child("Desc").setValue("XX");
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item3").child("Date").setValue("-/-/-");
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item4").child("Amount").setValue(0.00);
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item4").child("Desc").setValue("XX");
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item4").child("Date").setValue("-/-/-");
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item5").child("Amount").setValue(0.00);
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item5").child("Desc").setValue("XX");
                                                            dbRef.child("Users").child(userID).child("TopDebits").child("Item5").child("Date").setValue("-/-/-");
                                                            signInButton.setVisibility(View.GONE);
                                                            startActivity(new Intent(LoginPage.this, MainActivity.class));
                                                            editorSharedPreference.putBoolean("firstLogin", false);
                                                            editorSharedPreference.apply();
                                                        } else {
                                                            startActivity(new Intent(LoginPage.this, MainActivity.class));
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onError(@NonNull GetCredentialException e) { handleFailure(e); }
                                private void handleFailure(GetCredentialException e) { }
                            }
                    );
                }
            });
        } else {
            biometricPrompt.authenticate(promptInfo);
        }
    }
}