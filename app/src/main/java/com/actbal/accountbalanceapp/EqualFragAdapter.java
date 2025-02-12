package com.actbal.accountbalanceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EqualFragAdapter extends RecyclerView.Adapter<EqualFragAdapter.ViewHolder> {

    private ArrayList<SplitFriendsSG> itemsArrayList;
    private Context context;

    public EqualFragAdapter(ArrayList<SplitFriendsSG> itemsArrayList, Context context) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.equallyfragment_listview, parent, false);
        return new EqualFragAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EqualFragAdapter.ViewHolder holder, int position) {
        SplitFriendsSG modal = itemsArrayList.get(position);
        holder.name.setText(modal.getName());
        holder.splitAmt.setText(modal.getAmt());
    }

    @Override
    public int getItemCount() { return itemsArrayList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name, splitAmt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtVwNameEqualFrag);
            splitAmt = itemView.findViewById(R.id.txtVwAmtEqualFrag);
        }
    }
}
