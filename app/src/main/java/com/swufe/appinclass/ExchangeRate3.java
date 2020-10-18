package com.swufe.appinclass;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ExchangeRate3 extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String currency;
    double rate;
    TextView cur;
    EditText editText;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("currency", Activity.MODE_PRIVATE);
        currency = sharedPreferences.getString("currency","");
        rate = Double.parseDouble(sharedPreferences.getString("rate",""));

        setContentView(R.layout.exchange_rate3);
        cur = (TextView)findViewById(R.id.currency);
        editText = (EditText)findViewById(R.id.RMB);
        cur.setText(currency);//显示币种
        rate = 100 / rate;//计算出人民币兑其他币的汇率

        //清空currency.xml中的数据
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

//        这种直接将new TextWatcher()作为参数的叫匿名类，因为没有赋给具体的对象
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Double num = 0.0;
                try{
                    num = Double.parseDouble(s.toString());
                }catch (Exception e){
                    cur.setText("error");
                    return;
                }
                cur.setText(num * rate + "");
            }
        });
    }
}
