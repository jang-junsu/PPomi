package com.andrstudy.pushtest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private ConstraintLayout background;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();

        background = (ConstraintLayout)findViewById(R.id.background);

//        background.setBackgroundColor(Color.parseColor("#FFFFE9"));

        Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                startActivity(new Intent(MainActivity.this, SubActivity.class));
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0, 1000);

    }
}
