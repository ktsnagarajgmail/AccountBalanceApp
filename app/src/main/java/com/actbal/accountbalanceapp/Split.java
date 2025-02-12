package com.actbal.accountbalanceapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class Split extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    TabLayout tabLayout;
    ViewPager2 viewPager;
    View rootView;
    PopupWindow popupWindow;
    MainActivityFragmentsAdapter mainActivityFragmentsAdapter;
    EditText amount, desc, splitwith;
    Button emailIDslist, add;
    String splitswith, amtNewItem, descriptionNewItem, userID, payeeName, name, splitswithUID, dateNewItem, payeeNameInRecord;
    String[] splitwithArray;
    ArrayAdapter<String> arrayAdapter;
    AutoCompleteTextView autoCompleteTextView;
    ItemViewModel itemViewModel;
    ArrayList<NewitemsFragmentSG> arraylistNewItemsFragSG;
    HashMap hspNewItem = new HashMap();
    HashMap hspSplitNewItem = new HashMap();
    DatabaseReference dbRef;
    double splitAmount, splitOwesAmount;
    double dbtTotalAmt;

    public Split(PopupWindow popupWindow) {
        this.popupWindow = popupWindow;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_split, container, false);
        dbRef = FirebaseDatabase.getInstance().getReference();
        //userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userID = userID.substring(0,(userID.length())-4);
        String self = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        amount = rootView.findViewById(R.id.editAmount);
        desc = rootView.findViewById(R.id.editDesc);
        splitwith = rootView.findViewById(R.id.splitwith);
        emailIDslist = rootView.findViewById(R.id.newItemsEmailIdsBtn);
        add = rootView.findViewById(R.id.btnAdd);
        add.setVisibility(View.INVISIBLE);
        tabLayout = rootView.findViewById(R.id.tablayoutFragments);
        viewPager = rootView.findViewById(R.id.viewpagerFragment);
        itemViewModel = new ViewModelProvider(getActivity()).get(ItemViewModel.class);
        autoCompleteTextView = rootView.findViewById(R.id.autocompletedrodown);
        emailIDslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                splitswith = splitwith.getText().toString().trim();
                if (splitswith.isEmpty()) {
                    Toast toast = Toast.makeText(getActivity(), "Enter the split details", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                splitwithArray = splitswith.concat(","+self).split(",", 0);
                arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.dropdownlistview,splitwithArray);
                autoCompleteTextView.setAdapter(arrayAdapter);
            }
        });
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                payeeName = adapterView.getItemAtPosition(i).toString();
                dbRef.child("Users").child(payeeName.substring(0,(payeeName.length())-4)).child("info").child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            payeeNameInRecord = snapshot.getValue().toString();
                        } else {
                            payeeNameInRecord = payeeName;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
                amtNewItem = amount.getText().toString().trim();
                descriptionNewItem = desc.getText().toString().trim();
                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                String dayInString;
                if(day<10){
                    dayInString = "0"+String.valueOf(day);
                } else {
                    dayInString = String.valueOf(day);
                }
                int month = cal.get(Calendar.MONTH)+1;
                int year = cal.get(Calendar.YEAR);
                dateNewItem = dayInString+"/"+getMonthFormat(month)+"/"+String.valueOf(year);
                mainActivityFragmentsAdapter = new MainActivityFragmentsAdapter(getActivity(), splitwithArray, payeeName, amtNewItem, rootView);
                viewPager.setAdapter(mainActivityFragmentsAdapter);
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) { }
                    @Override
                    public void onTabReselected(TabLayout.Tab tab) { }
                });
                viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        tabLayout.getTabAt(position).select();
                    }
                });
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payeeName = payeeName.substring(0,(payeeName.length())-4);
                itemViewModel.getData().observe(getActivity(), item -> {
                    arraylistNewItemsFragSG = item;
                });
                if (amtNewItem.isEmpty() && descriptionNewItem.isEmpty() && arraylistNewItemsFragSG.isEmpty()) {
                    Toast toast = Toast.makeText(getActivity(), "Please enter all the data..", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                    return;
                }
                //splitwithArray = splitswith.split(",", 0);  // Not used
                int a = 0;
                do {
                    splitOwesAmount = arraylistNewItemsFragSG.get(a).getAmount();
                    name = arraylistNewItemsFragSG.get(a).getName();
                    splitswithUID = arraylistNewItemsFragSG.get(a).getUID();
                    splitAmount = arraylistNewItemsFragSG.get(a).getSplitAmount();
                    splitOwesAmount = splitOwesAmount + splitAmount;
                    hspSplitNewItem.put("TotalAmount", amtNewItem);
                    hspSplitNewItem.put("SplitAmount", splitAmount);
                    hspSplitNewItem.put("Desc", descriptionNewItem);
                    hspSplitNewItem.put("PaidBy", payeeNameInRecord);
                    hspSplitNewItem.put("Date", dateNewItem);
                    hspNewItem.put("Type", "Debit(-)");
                    hspNewItem.put("Amount", splitAmount);
                    hspNewItem.put("Desc", "Split-" + descriptionNewItem);
                    hspNewItem.put("Date", dateNewItem);
                    if (splitswithUID.equals(payeeName)) {
                        dbRef.child("Users").child(payeeName).child("items").push().setValue(hspNewItem);
                    } else {
                        dbRef.child("Users").child(payeeName).child("Splitwith").child(splitswithUID).child("Items").push().setValue(hspSplitNewItem);
                        dbRef.child("Users").child(payeeName).child("Splitwith").child(splitswithUID).child("Owes").setValue(splitOwesAmount);
                        dbRef.child("Users").child(payeeName).child("Splitwith").child(splitswithUID).child("Name").setValue(name);
                        dbRef.child("Users").child(splitswithUID).child("Splitwith").child(payeeName).child("Items").push().setValue(hspSplitNewItem);
                        dbRef.child("Users").child(splitswithUID).child("Splitwith").child(payeeName).child("Owes").setValue(0 - splitOwesAmount);
                        dbRef.child("Users").child(splitswithUID).child("items").push().setValue(hspNewItem);
                        dbRef.child("Users").child(splitswithUID).child("Splitwith").child(payeeName).child("Name").setValue(payeeName+".com");
                    }
                    a++;
                }while(a < arraylistNewItemsFragSG.size());
                Toast toast = Toast.makeText(getActivity(), "Entry added!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0,0);
                toast.show();
                amount.setText("");
                desc.setText("");
                popupWindow.dismiss();
                dbRef.child("Users").child(userID).child("ABTotal").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        double dbtSnapAmt = Double.parseDouble(snapshot.child("DebitTotal").getValue().toString());
                        double dbtEntryAmt = Double.parseDouble(String.valueOf(splitAmount));
                        dbtTotalAmt = dbtSnapAmt+dbtEntryAmt;
                        dbRef.child("Users").child(userID).child("ABTotal").child("DebitTotal").setValue(String.valueOf(dbtTotalAmt));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                dbRef.child("Users").child(userID).child("TopDebits").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int topDebitsSeries = 1;
                        String newEntryDebitAmount = String.valueOf(splitAmount);
                        String newEntryDebitDesc = "Split-"+descriptionNewItem;
                        String newEntryDebitDate = dateNewItem;
                        for (DataSnapshot datasnapshot:snapshot.getChildren()) {
                            String item = "Item" + topDebitsSeries;
                            double dbtSnapAmt = Double.parseDouble(datasnapshot.child("Amount").getValue().toString());
                            if (Double.parseDouble(newEntryDebitAmount)>dbtSnapAmt){
                                String topDebitsAmount = datasnapshot.child("Amount").getValue().toString();
                                String topDebitsDesc = datasnapshot.child("Desc").getValue().toString();
                                String topDebitsDate = datasnapshot.child("Date").getValue().toString();
                                dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Amount").setValue(newEntryDebitAmount);
                                dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Desc").setValue(newEntryDebitDesc);
                                dbRef.child("Users").child(userID).child("TopDebits").child(item).child("Date").setValue(newEntryDebitDate);
                                newEntryDebitAmount = topDebitsAmount;
                                newEntryDebitDate = topDebitsDate;
                                newEntryDebitDesc = topDebitsDesc;
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
                //mainActivity.popupWindow.dismiss();
            }
        });
        return rootView;
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