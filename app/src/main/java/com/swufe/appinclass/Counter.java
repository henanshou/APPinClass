package com.swufe.appinclass;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//闪退很可能是因为没有将类加入maniefest，对类alt+enter，选add activity to maniefest在AndroidManifest.xml中注册该Activity
public class Counter extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counter);
    }

    public void addScore(View view){
        TextView score = (TextView)findViewById(R.id.textView2);
        int previousScore = Integer.parseInt(score.getText().toString());
        int presentScore = Integer.parseInt(view.getTag().toString());
        score.setText(Integer.toString(previousScore+presentScore));
    }

    public void Reset(View view){
        TextView score = (TextView)findViewById(R.id.textView2);
        score.setText("0");
    }
}
