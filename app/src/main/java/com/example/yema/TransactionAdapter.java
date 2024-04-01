package com.example.yema;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.Inner> {
    Context context;
    ArrayList<TransactionDataModel> arrayList;
    public TransactionAdapter(Context context, ArrayList<TransactionDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public TransactionAdapter.Inner onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_data_items, parent, false);
        return new Inner(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.Inner holder, int position) {
        holder.icon.setImageResource(arrayList.get(position).getCardIcon());
        holder.category.setText(arrayList.get(position).getCategory());
        holder.description.setText(arrayList.get(position).getDescription());
        holder.transactionTime.setText(arrayList.get(position).getTransactionTime());
        holder.transactionAmount.setText(arrayList.get(position).getTransactionAmount());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class Inner extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView category, description, transactionAmount, transactionTime;
        public Inner(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.cardIcon);
            category = itemView.findViewById(R.id.cardCategory);
            description = itemView.findViewById(R.id.cardDescription);
            transactionAmount = itemView.findViewById(R.id.cardAmount);
            transactionTime = itemView.findViewById(R.id.cardTime);
        }
    }
}