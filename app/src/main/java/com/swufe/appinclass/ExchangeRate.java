package com.swufe.appinclass;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ExchangeRate extends AppCompatActivity {

    private static final String TAG = "ExchangeRate";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange_rate);
    }

    double dollar_rate = 0.1472;
    double euro_rate = 0.1256;
    double won_rate = 171.4256;
//    类属性不要运算，不要=(EditText)findViewById(R.id.inputRMB)，要放在方法内部运行
    EditText money;

    public void exchangeRate(View button){
        money = (EditText)findViewById(R.id.inputRMB);
        if(TextUtils.isEmpty(money.getText().toString())){//没有输入(用PPT上的方法不对)
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
        }else {
            TextView output = (TextView)findViewById(R.id.exchange_rate);
            double rmb = Double.parseDouble(money.getText().toString());
            if(button.getId()==R.id.dollar){
                double dollar = dollar_rate * rmb;
                output.setText(Double.toString(dollar));
            }else if(button.getId()==R.id.euro){
                double euro = euro_rate * rmb;
                output.setText(Double.toString(euro));
            }else if(button.getId()==R.id.won){
                double won = won_rate * rmb;
                output.setText(Double.toString(won));
            }
        }
    }

//    跳转到显示汇率的界面
    public void jumpExchangeRate2(View view){
//        Intent config = new Intent();
//        config.setClass(ExchangeRate.this, ExchangeRate2.class);
        Intent config = new Intent();
        config.setClass(ExchangeRate.this, ExchangeRate2.class);
        config.putExtra("dollar_rate_key",dollar_rate);
        config.putExtra("euro_rate_key",euro_rate);
        config.putExtra("won_rate_key",won_rate);

        Log.i(TAG, "jumpExchangeRate2: dollar_rate=" + dollar_rate);
        Log.i(TAG, "jumpExchangeRate2: euro_rate=" + euro_rate);
        Log.i(TAG, "jumpExchangeRate2: won_rate=" + won_rate);

        startActivityForResult(config,1);
    }

//    自动运行，获取从exchange_rate2传回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1 && resultCode==2){
            Bundle bundle = data.getExtras();
            dollar_rate = bundle.getDouble("key_dollar",0.1f);
            euro_rate = bundle.getDouble("key_euro",0.1f);
            won_rate = bundle.getDouble("key_won",0.1f);
            Log.i(TAG, "onActivityResult: dollar_rate=" + dollar_rate);
            Log.i(TAG, "onActivityResult: euro_rate=" + euro_rate);
            Log.i(TAG, "onActivityResult: won_rate=" + won_rate);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
