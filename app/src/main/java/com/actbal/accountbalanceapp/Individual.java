package com.actbal.accountbalanceapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class    Individual extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    RadioGroup rdG;
    RadioButton rdB;
    DatabaseReference dbRef;
    String userID, typeNewItem, amtNewItem, descriptionNewItem, dateNewItem;
    EditText amount, desc;
    Button add;
    double crdTotalAmt = 0, dbtTotalAmt = 0;
    HashMap hspNewItem = new HashMap();
    NewItems newItems;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_individual, container, false);
        rdG = view.findViewById(R.id.rdG);
        dbRef = FirebaseDatabase.getInstance().getReference();
        //userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userID = userID.substring(0,(userID.length())-4);
        amount = view.findViewById(R.id.editIndividualAmount);
        desc = view.findViewById(R.id.editIndividualDesc);
        add = view.findViewById(R.id.btnIndividualAdd);
        rdG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                rdB = radioGroup.findViewById(i);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeNewItem = rdB.getText().toString().trim();
                amtNewItem = amount.getText().toString().trim();
                descriptionNewItem = desc.getText().toString().trim();
                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                dateNewItem = String.valueOf(day)+"/"+getMonthFormat(month)+"/"+String.valueOf(year);
                if (typeNewItem.isEmpty() && amtNewItem.isEmpty() && descriptionNewItem.isEmpty()) {
                    Toast toast = Toast.makeText(getActivity(), "Please enter all the data..", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                    return;
                }
                hspNewItem.put("Type", typeNewItem);
                hspNewItem.put("Amount", amtNewItem);
                hspNewItem.put("Desc", descriptionNewItem);
                hspNewItem.put("Date", dateNewItem);
                dbRef.child("Users").child(userID).child("items").push().setValue(hspNewItem);
                Toast toast = Toast.makeText(getActivity(), "Entry added!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0,0);
                toast.show();
                rdG.clearCheck();
                amount.setText("");
                desc.setText("");
                //popupWindow.dismiss();
                if (typeNewItem.equals("Credit(+)")) {
                    dbRef.child("Users").child(userID).child("ABTotal").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                        double crdSnapAmt = Double.parseDouble(snapshot.child("CreditTotal").getValue().toString());
                        double crdEntryAmt = Double.parseDouble(amtNewItem);
                        crdTotalAmt = crdSnapAmt+crdEntryAmt;
                        dbRef.child("Users").child(userID).child("ABTotal").child("CreditTotal").setValue(String.valueOf(crdTotalAmt));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                } else if (typeNewItem.equals("Debit(-)")) {
                    dbRef.child("Users").child(userID).child("ABTotal").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            double dbtSnapAmt = Double.parseDouble(snapshot.child("DebitTotal").getValue().toString());
                            double dbtEntryAmt = Double.parseDouble(amtNewItem);
                            dbtTotalAmt = dbtSnapAmt + dbtEntryAmt;
                            dbRef.child("Users").child(userID).child("ABTotal").child("DebitTotal").setValue(String.valueOf(dbtTotalAmt));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                    dbRef.child("Users").child(userID).child("TopDebits").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int topDebitsSeries = 1;
                            String newEntryDebitAmount = amtNewItem;
                            String newEntryDebitDesc = descriptionNewItem;
                            String newEntryDebitDate = dateNewItem;
                            for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                                String item = "Item" + topDebitsSeries;
                                double dbtSnapAmt = Double.parseDouble(datasnapshot.child("Amount").getValue().toString());
                                if (Double.parseDouble(newEntryDebitAmount) > dbtSnapAmt) {
                                    String topDebitAmount = datasnapshot.child("Amount").getValue().toString();
                                    String topDebitDesc = datasnapshot.child("Desc").getValue().toString();
                                    String topDebitDate = datasnapshot.child("Date").getValue().toString();
                                    dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Amount").setValue(newEntryDebitAmount);
                                    dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Desc").setValue(newEntryDebitDesc);
                                    dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Date").setValue(newEntryDebitDate);
                                    newEntryDebitAmount = topDebitAmount;
                                    newEntryDebitDate = topDebitDate;
                                    newEntryDebitDesc = topDebitDesc;
                                } else {
                                    dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Amount").setValue(datasnapshot.child("Amount").getValue().toString());
                                    dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Desc").setValue(datasnapshot.child("Desc").getValue().toString());
                                    dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Date").setValue(datasnapshot.child("Date").getValue().toString());
                                }
                                topDebitsSeries++;
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }
            }
        });
        return view;
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