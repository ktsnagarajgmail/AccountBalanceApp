package com.actbal.accountbalanceapp;


import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class NewItemsFragmentsAdapter extends FragmentStateAdapter {
    PopupWindow popupWindow;
    public NewItemsFragmentsAdapter(@NonNull FragmentActivity fragmentActivity, PopupWindow popupWindow) {
        super(fragmentActivity);
        this.popupWindow = popupWindow;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new Split(popupWindow);
            case 1: return new Individual();
            default: return new Split(popupWindow);
        }
    }

    @Override
    public int getItemCount() { return 2;}
}
