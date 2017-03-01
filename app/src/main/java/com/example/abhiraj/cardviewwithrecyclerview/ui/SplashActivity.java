package com.example.abhiraj.cardviewwithrecyclerview.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.abhiraj.cardviewwithrecyclerview.signup.GoogleSignInActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Splash Activity", "in oncreate");
        Intent intent = new Intent(SplashActivity.this, GoogleSignInActivity.class);
        startActivity(intent);
        finish();
    }
}
