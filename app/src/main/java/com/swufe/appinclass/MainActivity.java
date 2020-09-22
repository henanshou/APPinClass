package com.swufe.appinclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//显示菜单项
        getMenuInflater().inflate(R.menu.first_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//判断点击了哪个菜单项，并实现功能
        if(item.getItemId()==R.id.menu1){
            //事件处理代码

        }
        return super.onOptionsItemSelected(item);
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

    public void jumpExchangeRate(View view) { //跳转到计分器页面
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, ExchangeRate.class);
        startActivity(intent);
    }
}