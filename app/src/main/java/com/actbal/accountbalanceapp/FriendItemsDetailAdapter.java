package com.actbal.accountbalanceapp;

//import static androidx.appcompat.graphics.drawable.DrawableContainerCompat.Api21Impl.getResources;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class FriendItemsDetailAdapter extends RecyclerView.Adapter<FriendItemsDetailAdapter.ViewHolder> {

    private ArrayList<ItemsDetailSG> itemsDetailSGArrayList;
    private Context context;

    public FriendItemsDetailAdapter(ArrayList<ItemsDetailSG> itemsDetailSGArrayList, Context context) {
        this.itemsDetailSGArrayList = itemsDetailSGArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frienditemsdetails_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendItemsDetailAdapter.ViewHolder holder, int position) {
        String name, emailId;
        name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        emailId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        emailId = emailId.substring(0,(emailId.length())-4);
        ItemsDetailSG modal = itemsDetailSGArrayList.get(position);
        holder.desc.setText(modal.getDesc());
        DecimalFormat df = new DecimalFormat(",####,###.00");
        //holder.totAmt.setText(modal.getTotalAmt());
        holder.totAmt.setText(df.format(new BigDecimal(Double.parseDouble(modal.getTotalAmt()))));
        holder.paidBy.setText(modal.getPaidBy());
        holder.sptAmt.setText(modal.getSplitAmt());
        holder.sptAmt.setText(df.format(new BigDecimal(Double.parseDouble(modal.getSplitAmt()))));
        holder.day.setText(modal.getDate().toString().substring(0,2));
        holder.month.setText(modal.getDate().toString().substring(3,6));
        //holder.year.setText(modal.getDate().toString().substring(7,11));
        if (name.equals(modal.getPaidBy()) || emailId.equals(modal.getPaidBy())){
            holder.oweDetails.setText("You lent");
            holder.oweDetails.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.sptAmt.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.oweDetails.setText("You borrowed");
            holder.oweDetails.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.sptAmt.setTextColor(ContextCompat.getColor(context, R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return itemsDetailSGArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView desc, totAmt, paidBy, sptAmt, day, month, year, oweDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.idDesc_FriendItemsDetail);
            totAmt = itemView.findViewById(R.id.idTotalAmount_FriendItemsDetail);
            paidBy = itemView.findViewById(R.id.idPaidBy_FriendItemsDetail);
            sptAmt = itemView.findViewById(R.id.idSplitAmt_FriendItemsDetail);
            day = itemView.findViewById(R.id.frienditemsdetails_Day);
            month = itemView.findViewById(R.id.frienditemsdetails_Month);
            //year = itemView.findViewById(R.id.frienditemsdetails_Year);
            oweDetails = itemView.findViewById(R.id.oweDetails);
        }
    }
}

