package com.swufe.appinclass;

import androidx.appcompat.app.AppCompatActivity;

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
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
//            对接口的方法重写，new后面的View.OnClickListener是匿名类
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                double num = Double.parseDouble(str)*1.8+32;
                CharSequence cs = String.valueOf(num);
                outobj.setText(cs);//setText不能直接显示浮点数
            }
        });
    }
}