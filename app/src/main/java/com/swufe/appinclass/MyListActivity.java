package com.swufe.appinclass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MyListActivity extends AppCompatActivity implements Runnable {
    private String inputStream2String(InputStream inputStream)
            throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312");
        while (true) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }

    //判断文件是否存在
    public boolean fileIsExists(String file) {
        try {
            File f = new File(file);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static final String TAG = "MyListActivity";
    int flag = 0;//0表示非同一天，否则表示同一天
    int flag2 = 0;//0表示是第一次运行，否则非第一次运行

    //    Handler只是用于获取信息的工具
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ListView listView = findViewById(R.id.myList);

//            获取系统日期（HH:mm:ss也可以获取时分秒）
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date nowDate = new Date(System.currentTimeMillis());
            String string = simpleDateFormat.format(nowDate);//Date转换为String

            //systemTime.xml不存在则直接解析
            if(fileIsExists("data/data/com.swufe.appinclass/shared_prefs/systemTime.xml") == true){
                SharedPreferences sharedPreferences = getSharedPreferences("systemTime", Activity.MODE_PRIVATE);
                String pastDate = sharedPreferences.getString("date","");
                flag2++;
                if(string.equals(pastDate)){//同一天则flag！=0
                    Log.i(TAG, "handleMessage: 是同一天");
                    flag++;
                }else {//不是同一天，更新systemTime.XML
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("date", string);
                    editor.commit();
                    Toast.makeText(MyListActivity.this, "systemTime.XML is updated!!", Toast.LENGTH_SHORT).show();
                }
            }else {//是第一次
                SharedPreferences sharedPreferences = getSharedPreferences("systemTime", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("date", string);
                editor.commit();
                Toast.makeText(MyListActivity.this, "systemTime.XML is created!!", Toast.LENGTH_SHORT).show();
            }

            //这里用的与exchange_rate中不同，是将HTML解析后转化成字符串再用Jsoup分析
            if (msg.what == 5 && flag == 0 && flag2 != 0) {//不是同一天
                String str = (String) msg.obj;
                Document doc = Jsoup.parse(str);
                Log.i(TAG, "MyListActivity: 不是同一天");
                Elements tables = doc.getElementsByTag("table");
                Element table = tables.get(0);//因为本网页只有一个table，等价于.first()
                Elements tds = table.getElementsByTag("td");//从table中找<td>，即列

                ArrayList<String> arrayList = new ArrayList<String>();
                for (int i = 0; i < tds.size(); i += 6) {
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i + 5);
                    String str1 = td1.text();
                    String val = td2.text();
                    Log.i(TAG, "MyListActivity: " + str1 + "==>" + val);
                    arrayList.add(str1 + "==>" + val);
                }
                ListAdapter adapter = new ArrayAdapter<String>(MyListActivity.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);
            }else if(msg.what == 5 && flag == 0 && flag2 == 0){//第一次创建
                String str = (String) msg.obj;
                Document doc = Jsoup.parse(str);
                Log.i(TAG, "MyListActivity: 第一次创建");
                Elements tables = doc.getElementsByTag("table");
                Element table = tables.get(0);//因为本网页只有一个table，等价于.first()
                Elements tds = table.getElementsByTag("td");//从table中找<td>，即列

                ArrayList<String> arrayList = new ArrayList<String>();

                SharedPreferences sharedPreferences = getSharedPreferences("systemTime", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("date",string);

                for (int i = 0; i < tds.size(); i += 6) {
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i + 5);
                    String str1 = td1.text();
                    String val = td2.text();
                    Log.i(TAG, "MyListActivity: " + str1 + "==>" + val);
                    editor.putString(str1,val);
                    editor.commit();
                    Toast.makeText(MyListActivity.this,"systemTime.XML is created!!",Toast.LENGTH_SHORT).show();
                    arrayList.add(str1 + "==>" + val);
                }
                ListAdapter adapter = new ArrayAdapter<String>(MyListActivity.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);
            }else if(msg.what == 5 && flag != 0){//同一天
                String str = (String) msg.obj;
                Document doc = Jsoup.parse(str);
                Log.i(TAG, "handleMessage:  today!!!!");
                Elements tables = doc.getElementsByTag("table");
                Element table = tables.get(0);//因为本网页只有一个table，等价于.first()
                Elements tds = table.getElementsByTag("td");//从table中找<td>，即列
                ArrayList<String> arrayList = new ArrayList<String>();
                SharedPreferences sharedPreferences = getSharedPreferences("systemTime", Activity.MODE_PRIVATE);
                for (int i = 0; i < tds.size(); i += 6) {
                    Element td1 = tds.get(i);
                    String str1 = td1.text();
                    String val = sharedPreferences.getString(str1,"");
                    arrayList.add(str1 + "==>" + val);
                }
                ListAdapter adapter = new ArrayAdapter<String>(MyListActivity.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);
                Toast.makeText(MyListActivity.this,"Rate needn't to be updated!!",Toast.LENGTH_SHORT).show();

//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.clear();
//                editor.commit();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_rate_in_listview);

//        创建线程
        Thread t1 = new Thread(this);
//        开启线程（到run()）
        t1.start();
    }

    //    用handler传递参数到子线程中
    @Override
    public void run() {
        Log.i(TAG, "2222222: ");
        URL url = null;
        try {
            url = new URL("https://www.usd-cny.com/bankofchina.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();
            String html = inputStream2String(in);
            Message msg = handler.obtainMessage(5, html);
            handler.sendMessage(msg);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
