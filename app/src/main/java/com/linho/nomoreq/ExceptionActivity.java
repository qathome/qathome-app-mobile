package com.linho.nomoreq;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.linho.nomoreq.utils.ExceptionHandler;

/**
 * Created by Carlo on 11/07/2015.
 */
public class ExceptionActivity extends FragmentActivity {

    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.exception_activity_layout);
        error = (TextView) findViewById(R.id.error);
        error.setText(getIntent().getStringExtra("error"));
    }
}