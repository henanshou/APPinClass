package com.swufe.appinclass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

/*------对MyListActivity的改进,改为用listitem显示,将数据保存在了SharedPreferences生成的xml中------*/
//若直接继承ListActivity那么需要用getListView()来获取listView对象
public class MyListItem extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = "MyListActivity";
    int flag = 0;//0表示非同一天，否则表示同一天
    int flag2 = 0;//0表示是第一次运行，否则非第一次运行

    static MyListItem m =null;
    AlertDialog.Builder builder;
    ListView listView;
    MyAdapter myAdapter;
    ArrayList<HashMap<String, String>> listItems;

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


    //    Handler只是用于获取信息的工具
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            listView = findViewById(R.id.myList);

//            获取系统日期（HH:mm:ss也可以获取时分秒）
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date nowDate = new Date(System.currentTimeMillis());
            String string = simpleDateFormat.format(nowDate);//Date转换为String

            //systemTime2.xml不存在则直接解析
            if(fileIsExists("data/data/com.swufe.appinclass/shared_prefs/systemTime2.xml") == true){
                SharedPreferences sharedPreferences = getSharedPreferences("systemTime2", Activity.MODE_PRIVATE);
                String pastDate = sharedPreferences.getString("date","");
                flag2++;
                if(string.equals(pastDate)){//同一天则flag！=0
                    Log.i(TAG, "handleMessage: 是同一天");
                    flag++;
                }else {//不是同一天，更新systemTime2.XML
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("date", string);
                    editor.commit();
                    Toast.makeText(MyListItem.this, "systemTime2.XML is updated!!", Toast.LENGTH_SHORT).show();
                }
            }else {//是第一次
                SharedPreferences sharedPreferences = getSharedPreferences("systemTime2", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("date", string);
                editor.commit();
                Toast.makeText(MyListItem.this, "systemTime2.XML is created!!", Toast.LENGTH_SHORT).show();
            }

            //这里用的与exchange_rate中不同，是将HTML解析后转化成字符串再用Jsoup分析
            if (msg.what == 5 && flag == 0 && flag2 != 0) {//不是同一天
                String str = (String) msg.obj;
                Document doc = Jsoup.parse(str);
                Log.i(TAG, "MyListActivity: 不是同一天");
                Elements tables = doc.getElementsByTag("table");
                Element table = tables.get(0);//因为本网页只有一个table，等价于.first()
                Elements tds = table.getElementsByTag("td");//从table中找<td>，即列

                listItems = new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < tds.size(); i += 6) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i + 5);
                    String str1 = td1.text();
                    String val = td2.text();
                    Log.i(TAG, "MyListActivity: " + str1 + "==>" + val);
                    map.put("ItemTitle",str1);//  币种
                    map.put("ItemDetail",val);//  汇率
                    listItems.add(map);
                }
//                通过加载布局文件构造View并填充相应的数据，之后修改Activity页面，使用自定义的MyAdapter
                myAdapter = new MyAdapter(MyListItem.this,
                        R.layout.list_item,
                        listItems);
                listView.setAdapter(myAdapter);//extends ListActivity才有this.setListAdapter()
                listView.setOnItemClickListener(MyListItem.this);//添加事件监听
                listView.setOnItemLongClickListener(MyListItem.this);
                listView.setEmptyView(findViewById(R.id.noData));
            }else if(msg.what == 5 && flag == 0 && flag2 == 0){//第一次创建
                String str = (String) msg.obj;
                Document doc = Jsoup.parse(str);
                Log.i(TAG, "MyListActivity: 第一次创建");
                Elements tables = doc.getElementsByTag("table");
                Element table = tables.get(0);//因为本网页只有一个table，等价于.first()
                Elements tds = table.getElementsByTag("td");//从table中找<td>，即列

                listItems = new ArrayList<HashMap<String, String>>();

                SharedPreferences sharedPreferences = getSharedPreferences("systemTime2", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("date",string);

                for (int i = 0; i < tds.size(); i += 6) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i + 5);
                    String str1 = td1.text();
                    String val = td2.text();
                    Log.i(TAG, "MyListActivity: " + str1 + "==>" + val);
                    editor.putString(str1,val);
                    editor.commit();
                    map.put("ItemTitle",str1);//  币种
                    map.put("ItemDetail",val);//  汇率
                    listItems.add(map);
                }
                myAdapter = new MyAdapter(MyListItem.this,
                        R.layout.list_item,
                        listItems);
                listView.setAdapter(myAdapter);//extends ListActivity才有this.setListAdapter()
                listView.setOnItemClickListener(MyListItem.this);//添加事件监听
                listView.setOnItemLongClickListener(MyListItem.this);
                listView.setEmptyView(findViewById(R.id.noData));
            }else if(msg.what == 5 && flag != 0){//同一天
                String str = (String) msg.obj;
                Document doc = Jsoup.parse(str);
                Log.i(TAG, "handleMessage:  today!!!!");
                Elements tables = doc.getElementsByTag("table");
                Element table = tables.get(0);//因为本网页只有一个table，等价于.first()
                Elements tds = table.getElementsByTag("td");//从table中找<td>，即列
                SharedPreferences sharedPreferences = getSharedPreferences("systemTime2", Activity.MODE_PRIVATE);
                listItems = new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < tds.size(); i += 6) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    Element td1 = tds.get(i);
                    String str1 = td1.text();
                    String val = sharedPreferences.getString(str1,"");
                    map.put("ItemTitle",str1);//  币种
                    map.put("ItemDetail",val);//  汇率
                    listItems.add(map);
                }
                myAdapter = new MyAdapter(MyListItem.this,
                        R.layout.list_item,
                        listItems);
                listView.setAdapter(myAdapter);//extends ListActivity才有this.setListAdapter()
                listView.setOnItemClickListener(MyListItem.this);//添加事件监听
                listView.setOnItemLongClickListener(MyListItem.this);
                listView.setEmptyView(findViewById(R.id.noData));
                Toast.makeText(MyListItem.this,"Rate needn't to be updated!!",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_rate_in_listview);
        m=this;
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

//    实现接口AdapterView.OnItemClickListener,重写方法onItemClick获取数据
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView listView = findViewById(R.id.myList);

//        从ListView（即listView）中获取选中数据
        Object itemAtPosition = listView.getItemAtPosition(position);//获取ListView listView中点击的数据
        HashMap<String,String> map = (HashMap<String, String>) itemAtPosition;
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");
        Log.i(TAG, "onItemClick: titleStr=" + titleStr);
        Log.i(TAG, "onItemClick: detailStr=" + detailStr);

//        从View中获取选中数据
        TextView title = (TextView) view.findViewById(R.id.itemTitle);
        TextView detail = (TextView) view.findViewById(R.id.itemDetail);
        String title2 = String.valueOf(title.getText());
        String detail2 = String.valueOf(detail.getText());
        Log.i(TAG, "onItemClick: title2=" + title2);
        Log.i(TAG, "onItemClick: detail2=" + detail2);

//        其实用Intent对象.putExtra("键",值)就可以传递币种与汇率了
        SharedPreferences sharedPreferences = getSharedPreferences("currency", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currency",title2);
        editor.putString("rate",detail2);
        editor.commit();
        Toast.makeText(this,"currency.XML is updated",Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.setClass(MyListItem.this, ExchangeRate3.class);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention")
                .setMessage("Config Deletion?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "onClick: 对话框事件处理");
                        myAdapter.remove(listView.getItemAtPosition(position));
//                        myAdapter.notifyDataSetChanged();通过adapter删除数据时,notifyDataSetChanged()会自动调用
                    }
                })
                .setNeutralButton("No",null);
        builder.create().show();
        return true;
    }
}

