package com.actbal.accountbalanceapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Equally extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    View view, rootView;
    FloatingActionButton btnOk;
    ItemViewModel itemViewModel;
    String[] emailids;
    ArrayList<String> arrayListEmailIds = new ArrayList<>();
    ArrayList<SplitFriendsSG> emailsArrayLimit = new ArrayList<>();
    String splitswithUID, name, userID, payeeName, totalAmount;
    double splitOwesAmount, splitAmount;
    RecyclerView recyclerView;
    DatabaseReference dbRef;
    FirebaseAuth firebaseAuth;
    EqualFragAdapter equalFragAdapter;
    ArrayList<NewitemsFragmentSG> arrayList = new ArrayList<>();
    ArrayList<String> splitWithUIDs = new ArrayList<>();

    public Equally(String[] emailIds, String payeeName, String totalAmount, View view){
        this.emailids = emailIds;
        this.payeeName = payeeName;
        this.totalAmount = totalAmount;
        this.rootView = view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_equally, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewEqualFrag);
        btnOk = view.findViewById(R.id.btnOKEqualFrag);
        dbRef = FirebaseDatabase.getInstance().getReference();
        rootView.findViewById(R.id.btnAdd).setVisibility(View.INVISIBLE);
        //userID = firebaseAuth.getInstance().getCurrentUser().getUid();
        //userID = firebaseAuth.getInstance().getCurrentUser().getEmail();
        //userID = userID.substring(0,(userID.length())-4);
        splitAmount = Double.parseDouble(totalAmount)/emailids.length;
        userID = payeeName.substring(0,(payeeName.length())-4);
        for (int i = 0; i< emailids.length; i++){
            emailsArrayLimit.add(new SplitFriendsSG(emailids[i], String.valueOf(splitAmount)));
            arrayListEmailIds.add(emailids[i].substring(0,(emailids[i].length()) - 4));
        }
        equalFragAdapter = new EqualFragAdapter(emailsArrayLimit, view.getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(equalFragAdapter);
        itemViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        /*for (int i = 0; i<emailids.length; i++){
                arrayListEmailIds.add(emailids[i].substring(0,(emailids[i].length()) - 4));
        }*/
        dbRef.child("Users").child(userID).child("Splitwith").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String s = dataSnapshot.getKey().toString();
                    splitWithUIDs.add(s);                               // Can optimize
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0; i<emailids.length; i++){
                    String sArray = emailids[i].substring(0, (emailids[i].length()) - 4); // Can optimize with arrayListEmailIds
                    if (!splitWithUIDs.contains(sArray)){
                        dbRef.child("Users").child(userID).child("Splitwith").child(sArray).child("Name").setValue(emailids[i]);
                        dbRef.child("Users").child(userID).child("Splitwith").child(sArray).child("Owes").setValue("0");
                        dbRef.child("Users").child(userID).child("Splitwith").child(sArray).child("Items").setValue("A");
                        dbRef.child("Users").child(userID).child("Contacts").child(sArray).setValue(sArray);
                        dbRef.child("Users").child(sArray).child("Contacts").child(userID).setValue(userID);
                        /*if (userID.equals(sArray)) {
                            dbRef.child("Users").child(userID).child("info").child("Name").setValue(emailids[i]);
                        } else {

                        }*/
                    }
                }
                dbRef.child("Users").child(userID).child("Splitwith").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (arrayListEmailIds.contains(snapshot.getKey().toString())) {
                                splitswithUID = snapshot.getKey().toString();
                                name = snapshot.child("Name").getValue().toString();
                                splitOwesAmount = Double.parseDouble(snapshot.child("Owes").getValue().toString());
                                arrayList.add(new NewitemsFragmentSG(splitswithUID, name, splitOwesAmount, splitAmount));
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                itemViewModel.setData(arrayList);
                rootView.findViewById(R.id.btnAdd).setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    //-------------
    /*for(int i=0; i<listView.getCount(); i++){
                    if (listView.isItemChecked(i)){
                        selectedEmailIDsConsolidated+=listView.getItemAtPosition(i) + ",";
                        arrayListEmailIds.add(String.valueOf(listView.getItemAtPosition(i)));
                    }
                }*/
    //selectedEmailIDs = selectedEmailIDsConsolidated.split(",",0);
    //itemViewModel.setData(selectedEmailIDsConsolidated);
    //Toast.makeText(view.getContext(), "do " + String.valueOf(selectedEmailIDs.length), Toast.LENGTH_SHORT).show();
    //Toast.makeText(view.getContext(), "default val " + String.valueOf(stringArrayCount), Toast.LENGTH_SHORT).show();
                /*do {
                        //Toast.makeText(view.getContext(), selectedEmailIDs[stringArrayCount], Toast.LENGTH_SHORT).show();
                        dbRef.child("Users").orderByChild("info/Email").equalTo(selectedEmailIDs[stringArrayCount])
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Toast.makeText(view.getContext(), String.valueOf(stringArrayCount), Toast.LENGTH_SHORT).show();
                                        //arrayListDataSnapshot.add(snapshot);
                        *//*for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            int i = 1;
                            splitswithUID = dataSnapshot.getKey().toString();
                            //Toast.makeText(view.getContext(), splitswithUID, Toast.LENGTH_SHORT).show();
                            name = dataSnapshot.child("info").child("Name").getValue().toString();
                            splitOwesAmount = Integer.parseInt(dataSnapshot.child("Splitwith").child(userID).child("Owes").getValue().toString().substring(1));
                            arrayList.add(new NewitemsFragmentSG(splitswithUID, name, splitOwesAmount));
                        }*//*
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {  }
                                });
                    splitOwesAmount = 0;
                    stringArrayCount++;
                    //Toast.makeText(view.getContext(), String.valueOf(arrayListDataSnapshot.size()), Toast.LENGTH_SHORT).show();
                } while(stringArrayCount < selectedEmailIDs.length);*/
                /*do {
                        //Toast.makeText(view.getContext(), selectedEmailIDs[stringArrayCount], Toast.LENGTH_SHORT).show();
                        dbRef.child("Users").orderByChild("info/Email").equalTo(selectedEmailIDs[stringArrayCount])
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Toast.makeText(view.getContext(), String.valueOf(stringArrayCount), Toast.LENGTH_SHORT).show();
                                        //arrayListDataSnapshot.add(snapshot);
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            int i = 1;
                                            splitswithUID = snapshot.getKey().toString();
                                            Toast.makeText(view.getContext(), splitswithUID, Toast.LENGTH_SHORT).show();
                                            name = snapshot.child("info").child("Name").getValue().toString();
                                            splitOwesAmount = Integer.parseInt(snapshot.child("Splitwith").child(userID).child("Owes").getValue().toString().substring(1));
                                            arrayList.add(new NewitemsFragmentSG(splitswithUID, name, splitOwesAmount));
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {  }
                                });
                    splitOwesAmount = 0;
                    Toast.makeText(view.getContext(), "While " + stringArrayCount,Toast.LENGTH_SHORT).show();
                    firebaseDBRun = new FirebaseDBRun(selectedEmailIDs, view.getContext(), stringArrayCount);
                    stringArrayCount++;

                    //Toast.makeText(view.getContext(), String.valueOf(arrayListDataSnapshot.size()), Toast.LENGTH_SHORT).show();
                    *//*try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }*//*
                } while(stringArrayCount < selectedEmailIDs.length);*/
    //-------------
}