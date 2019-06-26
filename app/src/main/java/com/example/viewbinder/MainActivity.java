package com.example.viewbinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Class<MainActivity> activityClass = MainActivity.class;
        /**
         * com.example.viewbinder.MainActivity
         * MainActivity
         * com.example.viewbinder.MainActivity
         */
        Log.e(TAG, "onCreate: "+activityClass.getName());
        Log.e(TAG, "onCreate: "+activityClass.getSimpleName());
        Log.e(TAG, "onCreate: "+activityClass.getCanonicalName());
        try {
            Log.e(TAG, "onCreate: "+activityClass.getEnclosingClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Log.e(TAG, "onCreate: "+activityClass.getDeclaringClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
