package com.actbal.accountbalanceapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class FragmentsFriendsRVAdaptor extends RecyclerView.Adapter<FragmentsFriendsRVAdaptor.ViewHolder> {
    private ArrayList<SplitFriendsSG> itemsArrayList;
    private Context context;

    public FragmentsFriendsRVAdaptor(ArrayList<SplitFriendsSG> itemsArrayList, Context context) {
        this.itemsArrayList = itemsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragmentfriends_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SplitFriendsSG modal = itemsArrayList.get(position);
        holder.name.setText(modal.getName());
        if (Double.parseDouble(modal.getAmt())>=0){
            holder.oweText.setText("Owes you");
            holder.amount.setText(modal.getAmt());
            holder.oweText.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green));
            BigDecimal bd = new BigDecimal(Double.parseDouble(modal.getAmt()));
            DecimalFormat df = new DecimalFormat(",###,###.00");
            holder.amount.setText(df.format(bd));
        } else {
            holder.oweText.setText("You Owe");
            holder.amount.setText(modal.getAmt().substring(1));
            holder.oweText.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red));
            BigDecimal bd = new BigDecimal(Double.parseDouble(modal.getAmt().substring(1)));
            DecimalFormat df = new DecimalFormat(",###,###.00");
            holder.amount.setText(df.format(bd));
        }
        holder.cardviewAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SplitActivityDetails.class);
                intent.putExtra("name", itemsArrayList.get(position).name);
                intent.putExtra("amounts", itemsArrayList.get(position).amt);
                intent.putExtra("friendsUID", itemsArrayList.get(position).friendsUID);
                /*Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST", (Serializable)itemsArrayList.get(position).getItemDetail());
                intent.putExtra("BUNDLE", args);*/
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name, amount, oweText;
        private CardView cardviewAdapter;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.idFragName);
            oweText = itemView.findViewById(R.id.idFragOweDetails);
            amount = itemView.findViewById(R.id.idFragAmount);
            cardviewAdapter = itemView.findViewById(R.id.cardviewFragmentsAdaptor);
        }
    }
}
