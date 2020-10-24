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
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

/*-----对MyListItem的改进,使用数据库SQLLite来存储数据----*/
public class MyUpgradedListItem extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = "MyUpgradedListItem";
    int flag = 0;//0表示非同一天，否则表示同一天
    int flag2 = 0;//0表示是第一次运行，否则非第一次运行

    static MyUpgradedListItem m =null;
    AlertDialog.Builder builder;
    ListView listView;
    MyAdapter myAdapter;
//    ArrayList<HashMap<String, String>> listItems;
    List<String> retList;
    List<RateItem> rateList;

////    相对MyListItem新增的两个属性
//    private String longDate = "";//保存从SharedPreferences里获得的数据
//    private final  String DATE_SP_KEY = "lastRateDateStr";//用于记录保存在SharedPreferences中的数据KEY

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

            //systemTime3.xml不存在则直接解析
            if(fileIsExists("data/data/com.swufe.appinclass/shared_prefs/systemTime3.xml") == true){
                SharedPreferences sharedPreferences = getSharedPreferences("systemTime3", Activity.MODE_PRIVATE);
                String pastDate = sharedPreferences.getString("date","");
                flag2++;
                if(string.equals(pastDate)){//同一天则flag！=0
                    Log.i(TAG, "handleMessage: 是同一天");
                    flag++;
                }else {//不是同一天，更新systemTime3.XML中的日期
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("date", string);
                    editor.commit();
                    Toast.makeText(MyUpgradedListItem.this, "systemTime3.XML is updated!!", Toast.LENGTH_SHORT).show();
                }
            }else {//是第一次
                SharedPreferences sharedPreferences = getSharedPreferences("systemTime3", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("date", string);
                editor.commit();
                Toast.makeText(MyUpgradedListItem.this, "systemTime3.XML is created!!", Toast.LENGTH_SHORT).show();
            }

            //这里用的与exchange_rate中不同，是将HTML解析后转化成字符串再用Jsoup分析
            if (msg.what == 5 && flag == 0 && flag2 != 0) {//不是同一天
                String str = (String) msg.obj;
                Document doc = Jsoup.parse(str);
                retList = new ArrayList<String>();
                rateList = new ArrayList<RateItem>();
                Log.i(TAG, "MyListActivity: 不是同一天");
                Elements tables = doc.getElementsByTag("table");
                Element table = tables.get(0);//因为本网页只有一个table，等价于.first()
                Elements tds = table.getElementsByTag("td");//从table中找<td>，即列

                SharedPreferences sharedPreferences = getSharedPreferences("systemTime3", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("date",string);

                for (int i = 0; i < tds.size(); i += 6) {
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i + 5);
                    String str1 = td1.text();
                    String val = td2.text();
                    Double rate = 100 / Double.parseDouble(val);
                    Log.i(TAG, "MyListActivity: " + str1 + "==>" + rate);

                    retList.add(str1 + "->" + rate);

                    RateItem rateItem = new RateItem(str1,rate);
                    rateList.add(rateItem);
                }
                RateManager rateManager = new RateManager(MyUpgradedListItem.this);
                rateManager.deleteAll();
                rateManager.addAll(rateList);

//                通过加载布局文件构造View并填充相应的数据，之后修改Activity页面，使用自定义的MyAdapter
                myAdapter = new MyAdapter(MyUpgradedListItem.this,
                        R.layout.list_item,
                        rateList);
                listView.setAdapter(myAdapter);//extends ListActivity才有this.setListAdapter()
                listView.setOnItemClickListener(MyUpgradedListItem.this);//添加事件监听
                listView.setOnItemLongClickListener(MyUpgradedListItem.this);
                listView.setEmptyView(findViewById(R.id.noData));
            }else if(msg.what == 5 && flag == 0 && flag2 == 0){//第一次创建
                String str = (String) msg.obj;
                Document doc = Jsoup.parse(str);
                Log.i(TAG, "MyUpgradedListItem: 第一次创建");
                retList = new ArrayList<String>();
                rateList = new ArrayList<RateItem>();
                Elements tables = doc.getElementsByTag("table");
                Element table = tables.get(0);//因为本网页只有一个table，等价于.first()
                Elements tds = table.getElementsByTag("td");//从table中找<td>，即列

                SharedPreferences sharedPreferences = getSharedPreferences("systemTime3", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("date",string);
                editor.commit();

                for (int i = 0; i < tds.size(); i += 6) {
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i + 5);
                    String str1 = td1.text();
                    String val = td2.text();
                    Double rate = 100 / Double.parseDouble(val);
                    Log.i(TAG, "MyUpgradedListItem: " + str1 + "==>" + rate);

                    retList.add(str1 + "->" + rate);

                    RateItem rateItem = new RateItem(str1,rate);
                    rateList.add(rateItem);
                }
                RateManager rateManager = new RateManager(MyUpgradedListItem.this);
                rateManager.addAll(rateList);
                
                myAdapter = new MyAdapter(MyUpgradedListItem.this,
                        R.layout.list_item,
                        rateList);
                listView.setAdapter(myAdapter);//extends ListActivity才有this.setListAdapter()
                listView.setOnItemClickListener(MyUpgradedListItem.this);//添加事件监听
                listView.setOnItemLongClickListener(MyUpgradedListItem.this);
                listView.setEmptyView(findViewById(R.id.noData));
            }else if(msg.what == 5 && flag != 0){//同一天
                Log.i(TAG, "handleMessage:  today!!!!");
                retList = new ArrayList<String>();
                rateList = new ArrayList<RateItem>();
                RateManager rateManager = new RateManager(MyUpgradedListItem.this);
                for(RateItem rateItem:rateManager.listAll()){
                    String curName =  rateItem.getCurName();
                    Double curRate = rateItem.getCurRate();
                    retList.add(curName + "=>" + curRate);

                    RateItem rateItem2 = new RateItem(curName,curRate);
                    rateList.add(rateItem2);
                }
                myAdapter = new MyAdapter(MyUpgradedListItem.this,
                        R.layout.list_item,
                        rateList);
                listView.setAdapter(myAdapter);//extends ListActivity才有this.setListAdapter()
                listView.setOnItemClickListener(MyUpgradedListItem.this);//添加事件监听
                listView.setOnItemLongClickListener(MyUpgradedListItem.this);
                listView.setEmptyView(findViewById(R.id.noData));
                Toast.makeText(MyUpgradedListItem.this,"Rate needn't to be updated!!",Toast.LENGTH_SHORT).show();
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
        RateItem rateItem = (RateItem) itemAtPosition;
        String titleStr = rateItem.getCurName();
        Double detailDou = rateItem.getCurRate();
        Log.i(TAG, "onItemClick: titleStr=" + titleStr);
        Log.i(TAG, "onItemClick: detailStr=" + detailDou);

//        从View中获取选中数据
        TextView title = (TextView) view.findViewById(R.id.itemTitle);
        TextView detail = (TextView) view.findViewById(R.id.itemDetail);
        String titleStr2 = String.valueOf(title.getText());
        Float detailDou2 = Float.parseFloat(String.valueOf(detail.getText()));
        Log.i(TAG, "onItemClick: title2=" + titleStr2);
        Log.i(TAG, "onItemClick: detail2=" + detailDou2);

//        其实用Intent对象.putExtra("键",值)就可以传递币种与汇率了
        SharedPreferences sharedPreferences = getSharedPreferences("currency2", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currency",titleStr2);
        editor.putFloat("rate",detailDou2);
        editor.commit();
        Toast.makeText(this,"currency2.XML is updated",Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.setClass(MyUpgradedListItem.this, ExchangeRate3.class);
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

