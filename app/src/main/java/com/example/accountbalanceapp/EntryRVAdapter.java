package com.example.accountbalanceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EntryRVAdapter extends RecyclerView.Adapter<EntryRVAdapter.ViewHolder> {
    private ArrayList<ItemsData> itemsArrayList;
    private Context context;

    public EntryRVAdapter(@NonNull ArrayList<ItemsData> itemsArrayList, Context context) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_rv_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsData modal = itemsArrayList.get(position);
        holder.type.setText(modal.getType());
        holder.amount.setText(modal.getAmt());
        holder.desc.setText(modal.getDesc());
    }

    @Override
    public int getItemCount() {    return itemsArrayList.size();    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView type, amount, desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.idType);
            amount = itemView.findViewById(R.id.idAmount);
            desc = itemView.findViewById(R.id.idDesc);
        }
    }
}
