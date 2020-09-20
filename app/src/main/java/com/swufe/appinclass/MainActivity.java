package com.swufe.appinclass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        决定显示哪个页面
        setContentView(R.layout.activity_main);

    }

    public void jumpTemperature(View view) {   //跳转到温度转换页面
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, Temperature.class);
        startActivity(intent);
    }

    public void jumpCounter(View view) { //跳转到计分器页面
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, Counter.class);
        startActivity(intent);
    }

}