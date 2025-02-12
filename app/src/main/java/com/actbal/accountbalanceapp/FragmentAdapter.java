package com.actbal.accountbalanceapp;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) { super(fragmentActivity);  }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new Friends();
            //case 1: return new Groups();      // If needed, will use GROUP in future
            default: return new Friends();
        }
    }
    @Override
    public int getItemCount() {
        return 1/*2*/;
    }
}
