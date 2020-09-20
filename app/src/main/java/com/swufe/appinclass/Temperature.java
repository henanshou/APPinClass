package com.swufe.appinclass;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
//闪退很可能是因为没有将类加入maniefest，对类alt+enter，选add activity to maniefest在AndroidManifest.xml中注册该Activity
public class Temperature extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temperature);
    }

    //            对接口的方法重写，new后面的View.OnClickListener是匿名类
    public void convert(View v) {
        Button button = findViewById(R.id.button);
//                这是获取TextView（msg）显示的信息的代码

//                直接输入logi就可以TextView（msg）显示的信息打出来下面行，输出日志
////                Log.i(TAG, "onClick: ");这行是直接显示出来的，下面行不要TAG直接打"loading"，tag:会自动加
//                Log.i("loading", "onClick: ");
//                TextView outobj = findViewById(R.id.msg);//获取文本TextView（msg）显示的信息
//                outobj.setText("kkkkkk");//初始显示
//                EditText inp = findViewById(R.id.edit);
//                outobj.setText(inp.getText().toString());


//                摄氏度转华氏度的代码
        Log.i("loading", "onClick: ");
        TextView outobj = findViewById(R.id.msg);//将TextView组件msg与TextView outobj绑定
        EditText inp = findViewById(R.id.edit);
        String str = inp.getText().toString();
        double num = Double.parseDouble(str) * 1.8 + 32;
        CharSequence cs = String.valueOf(num);
        outobj.setText(cs);//setText不能直接显示浮点数
    }
}
