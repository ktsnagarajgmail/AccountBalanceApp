package com.actbal.accountbalanceapp;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FirebaseDBRun {

    ArrayList<NewitemsFragmentSG> arrayList = new ArrayList<>();
    FirebaseAuth firebaseAuth;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    int splitOwesAmount, stringArrayCount;
    String splitswithUID, userID = firebaseAuth.getInstance().getCurrentUser().getUid(), name;

    public FirebaseDBRun (String[] stringArray, Context context, int ArrayCount){
        splitOwesAmount = 0;
        stringArrayCount = ArrayCount;
        dbRef.child("Users").orderByChild("info/Email").equalTo(stringArray[stringArrayCount])
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Toast.makeText(context, String.valueOf(stringArrayCount), Toast.LENGTH_SHORT).show();
                        //arrayListDataSnapshot.add(snapshot);
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            int i = 1;
                            splitswithUID = snapshot.getKey().toString();
                            Toast.makeText(context, splitswithUID, Toast.LENGTH_SHORT).show();
                            name = snapshot.child("info").child("Name").getValue().toString();
                            splitOwesAmount = Integer.parseInt(snapshot.child("Splitwith").child(userID).child("Owes").getValue().toString().substring(1));
                            double d = 1.00;
                            arrayList.add(new NewitemsFragmentSG(splitswithUID, name, splitOwesAmount, d));
                        }
                        //Toast.makeText(context, "Class Data " + String.valueOf(arrayList.size()), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {  }
                });
    }

    public ArrayList<NewitemsFragmentSG> getData(){
        return arrayList;
    }
}
