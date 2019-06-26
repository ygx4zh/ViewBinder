package com.example.sample.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.sample.R;
import com.example.sample.holders.SimpleHoder;

import java.util.List;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleHoder> {

    private final List<String> list;

    public SimpleAdapter(List<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public SimpleHoder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SimpleHoder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_simple, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleHoder viewHolder, int i) {
        viewHolder.bindView(list.get(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
