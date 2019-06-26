package com.example.sample.holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.binder_tools.BinderTools;
import com.example.ity_annotation.BindView;
import com.example.sample.R;

public class SimpleHoder extends RecyclerView.ViewHolder {
    @BindView(R.id.itemSimple_tv)
    TextView tv;

    public SimpleHoder(@NonNull View itemView) {
        super(itemView);

        BinderTools.bind(this, itemView);
    }

    public void bindView(String s) {
        tv.setText(s);
    }
}
