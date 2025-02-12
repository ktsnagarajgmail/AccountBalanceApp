package com.actbal.accountbalanceapp;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainActivityFragmentsAdapter extends FragmentStateAdapter {

    String[] emailIDs;
    String payeeName, amt;
    View view;
    public MainActivityFragmentsAdapter(@NonNull FragmentActivity fragmentActivity, String[] emailIDs, String payeeName, String amt, View view) {
        super(fragmentActivity);
        this.emailIDs = emailIDs;
        this.payeeName = payeeName;
        this.amt = amt;
        this.view = view;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new Equally(emailIDs, payeeName, amt, view);
            case 1: return new Share(emailIDs, payeeName, amt, view);
            case 2: return new Percentage(emailIDs, payeeName, amt, view);
            default: return new Equally(emailIDs, payeeName, amt, view);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
