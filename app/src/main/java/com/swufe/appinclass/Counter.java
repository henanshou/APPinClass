package com.swufe.appinclass;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//闪退很可能是因为没有将类加入maniefest，对类alt+enter，选add activity to maniefest在AndroidManifest.xml中注册该Activity
public class Counter extends AppCompatActivity {
    private static final String TAG = "Counter";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counter);
    }

//    因为转屏幕方向后生命周期会改变，所以要用onSaveInstanceState、onRestoreInstanceState保存数据
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String scoreA = ((TextView)findViewById(R.id.ScoreA)).getText().toString();
        String scoreB = ((TextView)findViewById(R.id.ScoreB)).getText().toString();
        Log.i(TAG, "onSaveInstanceState: ");
        outState.putString("teamA_Score",scoreA);
        outState.putString("teamB_Score",scoreB);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void teamA_AddScore(View view){
        TextView score = (TextView)findViewById(R.id.ScoreA);
        int previousScore = Integer.parseInt(score.getText().toString());
        int presentScore = Integer.parseInt(view.getTag().toString());
        score.setText(Integer.toString(previousScore+presentScore));
    }

    public void teamB_AddScore(View view){
        TextView score = (TextView)findViewById(R.id.ScoreB);
        int previousScore = Integer.parseInt(score.getText().toString());
        int presentScore = Integer.parseInt(view.getTag().toString());
        score.setText(Integer.toString(previousScore+presentScore));
    }

    public void reset(View view){
        TextView scoreA = (TextView)findViewById(R.id.ScoreA);
        TextView scoreB = (TextView)findViewById(R.id.ScoreB);
        scoreA.setText("0");
        scoreB.setText("0");
    }
}
