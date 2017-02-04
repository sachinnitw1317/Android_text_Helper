package com.wsdc.a.accessibility;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent I = new Intent(MainActivity.this,ToastOrNotificationTestActivity.class);
        startActivity(I);

    }
}
