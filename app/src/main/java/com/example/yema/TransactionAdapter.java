package com.example.yema;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.Inner> {
    @NonNull
    @Override
    public TransactionAdapter.Inner onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.Inner holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class Inner extends RecyclerView.ViewHolder {
        public Inner(@NonNull View itemView) {
            super(itemView);
        }
    }
}