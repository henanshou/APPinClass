package com.swufe.appinclass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ExchangeRate2 extends AppCompatActivity {
    private static final String TAG = "ExchangeRate2";

    //    获取ExchangeRate中设定好，传过来的汇率
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange_rate2);

        Intent intent = getIntent();//这个intent其实就是ExchangeRate中的config
        double dollar2 = intent.getDoubleExtra("dollar_rate_key",0.0f);
        double euro2 = intent.getDoubleExtra("euro_rate_key",0.0f);
        double won2 = intent.getDoubleExtra("won_rate_key",0.0f);

        Log.i(TAG, "onCreate: dollar2=" + dollar2);
        Log.i(TAG, "onCreate: euro2=" + euro2);
        Log.i(TAG, "onCreate: won2=" + won2);

        EditText dollar = (EditText)findViewById(R.id.rate_dollar);
        EditText euro = (EditText)findViewById(R.id.rate_euro);
        EditText won = (EditText)findViewById(R.id.rate_won);

        dollar.setText(Double.toString(dollar2));
        euro.setText(Double.toString(euro2));
        won.setText(Double.toString(won2));
    }

    //    点击SAVE保存到Bundle并带回数据到调用的页面（2020/9/28新增功能：将信息存储到SharePreferences）
    public void transportData(View view){
        Intent intent = getIntent();
        Bundle bdl = new Bundle();
        EditText dollar = (EditText)findViewById(R.id.rate_dollar);
        EditText euro = (EditText)findViewById(R.id.rate_euro);
        EditText won = (EditText)findViewById(R.id.rate_won);
        double dollar_rate2 = Double.parseDouble(dollar.getText().toString());
        double euro_rate2 = Double.parseDouble(euro.getText().toString());
        double won_rate2 = Double.parseDouble(won.getText().toString());
        bdl.putDouble("key_dollar",dollar_rate2);
        bdl.putDouble("key_euro",euro_rate2);
        bdl.putDouble("key_won",won_rate2);
        intent.putExtras(bdl);
        setResult(2,intent);//设置resultCode及带回的数据
        Log.i(TAG, "transportData: key_dollar="+dollar_rate2);
        Log.i(TAG, "transportData: key_euro="+euro_rate2);
        Log.i(TAG, "transportData: key_won="+won_rate2);

//        获取SharePreferences对象，myrate.XML用来保存汇率
//        myrate.XML在device file explorer的data/data/com.swufe.appinclass/shared_prefs中
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);//MODE_PRIVATE值仅本程序可访问
//        获取Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("key_dollar2", (float) dollar_rate2);
        editor.putFloat("key_euro2",(float) euro_rate2);
        editor.putFloat("key_won2",(float) won_rate2);
        //类似于数据库中提交
        editor.commit ();
        Toast.makeText(this,"修改后汇率已成功存入myrate.XML",Toast.LENGTH_SHORT).show();

        finish();//返回到调用页面
    }
}
