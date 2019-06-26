package com.example.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.binder_tools.BinderTools;
import com.example.ity_annotation.BindView;
import com.example.ity_annotation.OnClick;
import com.example.sample.adapter.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_tv)
    TextView tv;
    @BindView(R.id.main_iv)
    ImageView iv;
    @BindView(R.id.main_recyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BinderTools.bind(this);
        tv.setText("WTF - HAHAHA");
        iv.setImageResource(R.drawable.ic_launcher_background);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("Item " + i);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRecyclerView.setAdapter(new SimpleAdapter(list));
    }

    @OnClick({R.id.main_tv, R.id.main_iv, R.id.main_tv_wtf})
    public void test(View view) {
        Toast.makeText(this, "onClick: " + view.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
    }
}
