package com.actbal.accountbalanceapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Collections;
import java.util.List;

public class Share extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    View view, rootView;
    RecyclerView recyclerView;
    FloatingActionButton btnOk;
    DatabaseReference dbRef;
    ItemViewModel itemViewModel;
    String[] emailIds;
    double sum = 0;
    String payeeName, totalAmount, userID;
    NestedFragItemDataAdapter nestedFragItemDataAdapter;
    ArrayList<SplitFriendsSG> emailsArrayLimit = new ArrayList<>();
    ArrayList<String> arrayListEmailIds = new ArrayList<>();
    ArrayList<String> splitWithUIDs = new ArrayList<>();
    ArrayList<NewitemsFragmentSG> arrayList = new ArrayList<>();
    public Share(String[] emailIds, String payeeName, String totalAmount, View view) {
        this.emailIds = emailIds;
        this.payeeName = payeeName;
        this.totalAmount = totalAmount;
        this.rootView = view;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_share, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewShareFragments);
        rootView.findViewById(R.id.btnAdd).setVisibility(View.INVISIBLE);
        btnOk = view.findViewById(R.id.btnOkShareFrag);
        dbRef = FirebaseDatabase.getInstance().getReference();
        for (int i = 0; i< emailIds.length; i++){
            emailsArrayLimit.add(new SplitFriendsSG(emailIds[i], "0.00"));
        }
        userID = payeeName.substring(0,(payeeName.length())-4);
        itemViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        for (int i = 0; i<emailIds.length; i++){
            arrayListEmailIds.add(emailIds[i].substring(0,(emailIds[i].length()) - 4));
        }
        nestedFragItemDataAdapter = new NestedFragItemDataAdapter(emailsArrayLimit, arrayListEmailIds, view.getContext(), view, totalAmount, userID);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(nestedFragItemDataAdapter);


        dbRef.child("Users").child(userID).child("Splitwith").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String s = dataSnapshot.getKey().toString();
                    splitWithUIDs.add(s);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0; i<emailIds.length; i++){
                    String sArray = emailIds[i].substring(0, (emailIds[i].length()) - 4);
                    if (!splitWithUIDs.contains(sArray)){
                        dbRef.child("Users").child(userID).child("Splitwith").child(sArray).child("Items").setValue("A");
                        dbRef.child("Users").child(userID).child("Splitwith").child(sArray).child("Name").setValue(sArray);
                        dbRef.child("Users").child(userID).child("Splitwith").child(sArray).child("Owes").setValue("0");
                        dbRef.child("Users").child(userID).child("Splitwith").child(sArray).child("SplitAmount").setValue("0");
                    } else {
                        dbRef.child("Users").child(userID).child("Splitwith").child(sArray).child("SplitAmount").setValue("0");
                    }
                }

                dbRef.child("Users").child(userID).child("Splitwith").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (arrayListEmailIds.contains(snapshot.getKey().toString())) {
                                String splitswithUID = snapshot.getKey().toString();
                                String name = snapshot.child("Name").getValue().toString();
                                String splitAmount = snapshot.child("SplitAmount").getValue().toString();
                                sum = sum + Double.parseDouble(splitAmount);
                                double splitOwesAmount = Double.parseDouble(snapshot.child("Owes").getValue().toString());
                                arrayList.add(new NewitemsFragmentSG(splitswithUID, name, splitOwesAmount, Double.parseDouble(splitAmount)));
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
                if (sum<Double.parseDouble(totalAmount)){
                    Toast.makeText(view.getContext(), "Don't match with total amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                itemViewModel.setData(arrayList);
                rootView.findViewById(R.id.btnAdd).setVisibility(View.VISIBLE);
            }
        });
        return view;
    }
}