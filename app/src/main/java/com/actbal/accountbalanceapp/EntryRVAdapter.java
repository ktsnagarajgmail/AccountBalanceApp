package com.actbal.accountbalanceapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class EntryRVAdapter extends RecyclerView.Adapter<EntryRVAdapter.ViewHolder> {
    private ArrayList<ItemsData> itemsArrayList;
    private Context context;
    String type = "Credit(+)";

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
        //holder.type.setText(modal.getType());
        holder.desc.setText("for "+modal.getDesc());
        //holder.date.setText(modal.getDate());
        holder.day.setText(modal.getDate().toString().substring(0,2));
        holder.month.setText(modal.getDate().toString().substring(3,6));
        BigDecimal bd = new BigDecimal(Double.parseDouble(modal.getAmt()));
        DecimalFormat df = new DecimalFormat(",###,###.00");
        holder.amount.setText(df.format(bd));
        type = modal.getType();
        if (("Credit(+)").equals(type)) {
            holder.typeInImage.setImageResource(R.drawable.baseline_arrow_upward_24);
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.amountdesc.setText("You receive ");
            //holder.amount.setText("You receive "+modal.getAmt());
        } else {
            holder.typeInImage.setImageResource(R.drawable.baseline_arrow_downward_24);
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.amountdesc.setText("You spent ");
            //holder.amount.setText("You spent "+modal.getAmt());
        }

        /*holder.cardviewAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SplitActivityDetails.class);
                intent.putExtra("Type", itemsArrayList.get(position).type);
                intent.putExtra("amounts", itemsArrayList.get(position).amt);
                intent.putExtra("descp", itemsArrayList.get(position).desc);
                intent.putExtra("Date", itemsArrayList.get(position).date);
                context.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {    return itemsArrayList.size();    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView type, amount, desc, date, day, month, amountdesc;
        private ImageView typeInImage;
        //private CardView cardviewAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeInImage = itemView.findViewById(R.id.typeInImage);
            amount = itemView.findViewById(R.id.idAmount);
            desc = itemView.findViewById(R.id.idDesc);
            //date = itemView.findViewById(R.id.idDate);
            day = itemView.findViewById(R.id.entryrv_list_Day);
            month = itemView.findViewById(R.id.entryrv_list_Month);
            amountdesc = itemView.findViewById(R.id.entry_rv_list_amountdes);
            //cardviewAdapter = itemView.findViewById(R.id.cardviewAdapter);
        }
    }
}
