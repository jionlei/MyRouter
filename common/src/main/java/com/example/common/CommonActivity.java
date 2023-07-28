package com.example.common;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.annotation.MyRouter;


@MyRouter(path = "/common/CommonActivity", group = "common")
public class CommonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
    }
}