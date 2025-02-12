package com.actbal.accountbalanceapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class NestedFragItemDataAdapter extends RecyclerView.Adapter<NestedFragItemDataAdapter.ViewHolder> {
    private ArrayList<SplitFriendsSG> itemsArrayList;
    String[] splitAmountTotalMatch;
    ArrayList<String> emailIds;
    View view;
    private Context context;
    String totalAmount, userID;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    TextView amtAlertTxtView;

    public NestedFragItemDataAdapter(ArrayList<SplitFriendsSG> itemsArrayList, ArrayList<String> emailIds, Context context, View view, String totalAmount, String userID) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
        this.totalAmount = totalAmount;
        this.userID = userID;
        this.view = view;
        this.emailIds = emailIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nestedfragment_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NestedFragItemDataAdapter.ViewHolder holder, int position) {
        amtAlertTxtView = view.findViewById(R.id.amtAlertTxtViewFragment);
        splitAmountTotalMatch = new String[emailIds.size()];
        for (int i = 0; i<splitAmountTotalMatch.length; i++){
            splitAmountTotalMatch[i] = "0";
        }
        SplitFriendsSG modal = itemsArrayList.get(position);
        holder.name.setText(modal.getName());
        holder.amt.setText(modal.getAmt());
        holder.amt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                dbRef.child("Users").child(userID).child("Splitwith").
                        child(holder.name.getText().toString().substring(0,(holder.name.getText().toString().length())-4)).
                        child("SplitAmount").setValue(holder.amt.getText().toString());
                    double d = 0.00;
                String percentValue = holder.amt.getText().toString();
                if ((percentValue.substring(percentValue.length()-1, percentValue.length())).equals("%")){
                    splitAmountTotalMatch[position] = percentValue.substring(0, percentValue.length()-1);
                    for (int i = 0; i<splitAmountTotalMatch.length; i++){
                       d = d + Double.parseDouble(splitAmountTotalMatch[i]);
                    }
                    if (d > 100.00){
                        amtAlertTxtView.setText("Split Amount exceeds Total Amount.");
                    } else {
                        amtAlertTxtView.setText(" ");
                    }
                } else {
                    splitAmountTotalMatch[position] = holder.amt.getText().toString();
                    for (int i = 0; i<splitAmountTotalMatch.length; i++){
                        d = d + Double.parseDouble(splitAmountTotalMatch[i]);
                    }
                    if (d > Double.parseDouble(totalAmount)){
                        amtAlertTxtView.setText("Split Amount exceeds Total Amount.");
                    } else {
                        amtAlertTxtView.setText(" ");
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        EditText amt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtViewName);
            amt = itemView.findViewById(R.id.edtTxtViewAmount);
        }
    }
}
