package com.example.routerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.commonlibs.service.ServiceFactory;
import com.example.login.LoginActivity;
import com.example.login.LoginUtils;
import com.example.share.ShareActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_share).setOnClickListener(v -> {
            startActivity(new Intent(this, ShareActivity.class));
        });

        findViewById(R.id.btn_login).setOnClickListener(v -> {
           startActivity(new Intent(this, LoginActivity.class));
        });
    }
}