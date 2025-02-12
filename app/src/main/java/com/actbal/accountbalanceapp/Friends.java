package com.actbal.accountbalanceapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class Friends extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RecyclerView recyclerViewF;
    TextView txtViewOverallOwes;
    private FragmentsFriendsRVAdaptor rvFragAdapter;
    String userID;
    View rootview;
    double overallOwes = 0.0;
    private ArrayList<SplitFriendsSG> itemsData = new ArrayList<>();
    private ArrayList<ItemsDetailSG> itemsDatainDetail = new ArrayList<>();
    FirebaseAuth firebaseAuthReport;
    DatabaseReference dbRefReader = FirebaseDatabase.getInstance().getReference();
    public Friends(){ }
    Activity context;
    DecimalFormat df;
    ArrayList<String> splitWithUIDs = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        rootview = inflater.inflate(R.layout.fragment_friends, container, false);
        firebaseAuthReport = FirebaseAuth.getInstance();
        //userID = firebaseAuthReport.getCurrentUser().getUid();
        userID = firebaseAuthReport.getCurrentUser().getEmail();
        userID = userID.substring(0,(userID.length())-4);
        recyclerViewF = rootview.findViewById(R.id.recyclerviewFriends);
        recyclerViewF.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFragAdapter = new FragmentsFriendsRVAdaptor(itemsData, context);
        recyclerViewF.setAdapter(rvFragAdapter);
        itemsData = getArrayListFrag();
        txtViewOverallOwes = rootview.findViewById(R.id.overallOwes);
        df = new DecimalFormat(",####,###.00");
        /*int i;
        for (i = 0; i < splitWithUIDs.size(); i++){
            int k = i;
            dbRefReader.child("Users").child("UID_Data").child(splitWithUIDs.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dbRefReader.child("Users").child(userID).child("Splitwith").
                            child(splitWithUIDs.get(k)).child("Name").setValue(snapshot.getValue().toString());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }*/
        return rootview;
    }
    public ArrayList<SplitFriendsSG> getArrayListFrag() {
        dbRefReader.child("Users").child(userID).child("Splitwith").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot:datasnapshot.getChildren()) {
                    if (!snapshot.getKey().toString().equals(userID) && snapshot.exists()) {
                        String name = snapshot.child("Name").getValue().toString();
                        String amount = snapshot.child("Owes").getValue().toString();
                        overallOwes = Double.parseDouble(amount) + overallOwes;
                        String friendsUID = snapshot.getKey();
                        splitWithUIDs.add(friendsUID);
                        itemsData.add(new SplitFriendsSG(name, amount, friendsUID));
                    }
                }
                if (overallOwes >= 0) {
                    txtViewOverallOwes.setText("Overall, owes you " + String.valueOf(df.format(new BigDecimal(overallOwes))));
                    txtViewOverallOwes.setTextColor(ContextCompat.getColor(context, R.color.green));
                } else {
                    txtViewOverallOwes.setText("Overall, you owe " + String.valueOf(df.format(new BigDecimal(overallOwes))));
                    txtViewOverallOwes.setTextColor(ContextCompat.getColor(context, R.color.red));
                }
                if (rvFragAdapter != null){
                    rvFragAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return itemsData;

        /*dbRefReader.child("Users").child(userID).child("Splitwith").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (DataSnapshot datasnapshot:snapshot.getChildren()) {
                    *//*dbRefReader.child("Users").child(String.valueOf(snapshot.getKey())).child("info").child("Name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            name = String.valueOf(snapshot.getValue());
                            if (rvFragAdapter != null){
                                rvFragAdapter.notifyDataSetChanged(); }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });*//*
                    String name = "Nagaraj";
                    String amount = datasnapshot.child("Owes").getValue().toString();
                    itemsData.add(new SplitFriendsSG(name,amount));
                }
                if (rvFragAdapter != null){
                    rvFragAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        //itemsData.add(new SplitFriendsSG("Nagaraj", "6"));
    }
}