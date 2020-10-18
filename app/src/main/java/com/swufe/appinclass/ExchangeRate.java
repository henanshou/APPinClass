package com.swufe.appinclass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/*---------
创建线程辅助类，实现Runnable接口
（实现2种方法分隔网上获取的汇率：1、正则表达式+ArrayList；2、jsoup——要在build.gradle里面加配置）*/
public class ExchangeRate extends AppCompatActivity implements Runnable{

    private static final String TAG = "ExchangeRate";

    //    这是2020/9/21的自己定义的非实时汇率
    double dollar_rate = 0.1472;
    double euro_rate = 0.1256;
    double won_rate = 171.4256;
    EditText money;// 类属性不要运算，不要=(EditText)findViewById(R.id.inputRMB)，要放在方法内部运行
    //    2020/9/28改为从网络实时获取汇率
//    double dollar_rate;
//    double euro_rate;
//    double won_rate;

//    将输入流转化为字符串(run()中要用)
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

//    -------方法一：将HTML得到的字符串分别装入ArrayList--------
    public ArrayList<String> getRate(String str){
//        for(String a:str.split("\\s+")){
//            Log.i(TAG, "getRate: newstr:\n"+a);
//        }
        ArrayList<String> arrayList = new ArrayList<String>();
//      ---------正则中---------:  ()是用来分组的；.是正则表达式中的特殊字符，所以要\\.转义后匹配
        Pattern pattern = Pattern.compile("(?<=.htm\">).*(?=</a></td>)");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()){
            String e = matcher.group(0);
            if(!(e.equals("美元")||e.equals("欧元")||e.equals("韩元"))){
                continue;
            }else if(e.equals("美元")){
                Pattern pattern1 = Pattern.compile("(美元</a></td>\n" +
                        "\\s{2,}<td>[-0-9\\.]{1,6}</td>\n" +
                        "\\s{2,}<td>[-0-9\\.]{1,6}</td>\n" +
                        "\\s{2,}<td>[-0-9\\.]{1,6}</td>\n" +
                        "\\s{2,}<td>[-0-9\\.]{1,6}</td>\n" +
                        "\\s{2,}<td>)[0-9]\\d*\\.?\\d*(</td>)");
                Matcher matcher1 = pattern1.matcher(str);
                while (matcher1.find()){
                    String rate = matcher1.group(0);
                    Pattern pattern2 = Pattern.compile("(?<=<td>).*(?=</td>)");
                    Matcher matcher2 = pattern2.matcher(rate);
                    int count = 0;
                    while (matcher2.find()){
                        count++;
                        if(count == 5){
                            rate = matcher2.group(0);
                        }
                    }
                    arrayList.add(rate);
                }
            }else if(e.equals("欧元")){
                Pattern pattern1 = Pattern.compile("(欧元</a></td>\n" +
                        "\\s{2,}<td>[-0-9\\.]{1,6}</td>\n" +
                        "\\s{2,}<td>[-0-9\\.]{1,6}</td>\n" +
                        "\\s{2,}<td>[-0-9\\.]{1,6}</td>\n" +
                        "\\s{2,}<td>[-0-9\\.]{1,6}</td>\n" +
                        "\\s{2,}<td>)[0-9]\\d*\\.?\\d*(</td>)");
                Matcher matcher1 = pattern1.matcher(str);
                while (matcher1.find()){
                    String rate = matcher1.group(0);
                    Pattern pattern2 = Pattern.compile("(?<=<td>).*(?=</td>)");
                    Matcher matcher2 = pattern2.matcher(rate);
                    int count = 0;
                    while (matcher2.find()){
                        count++;
                        if(count == 5){
                            rate = matcher2.group(0);
                        }
                    }
                    arrayList.add(rate);
                }
            }else if(e.equals("韩元")){
                Pattern pattern1 = Pattern.compile("(韩元</a></td>\n" +
                        "\\s{2,}<td>[-0-9\\.]{1,6}</td>\n" +
                        "\\s{2,}<td>[-0-9\\.]{1,6}</td>\n" +
                        "\\s{2,}<td>[-0-9\\.]{1,6}</td>\n" +
                        "\\s{2,}<td>[-0-9\\.]{1,6}</td>\n" +
                        "\\s{2,}<td>)[0-9]\\d*\\.?\\d*(</td>)");
                Matcher matcher1 = pattern1.matcher(str);
                while (matcher1.find()){
                    String rate = matcher1.group(0);
                    Pattern pattern2 = Pattern.compile("(?<=<td>).*(?=</td>)");
                    Matcher matcher2 = pattern2.matcher(rate);
                    int count = 0;
                    while (matcher2.find()){
                        count++;
                        if(count == 5){
                            rate = matcher2.group(0);
                        }
                    }
                    arrayList.add(rate);
                }
            }
//            Log.i(TAG, "getRate: e:\n"+e);
        }
        Log.i(TAG, "getRate: rateAll:\n"+arrayList);
        return arrayList;
    }

//    -----------方法二：用jsoup解析网页数据---------
    int length = 3;
    public double[] getRate2(){
        double rate[] = new double[length];
        String url = "https://www.usd-cny.com/bankofchina.htm";
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "getRate2: "+doc.title());
        Elements tables = doc.getElementsByTag("table");
        Element table = tables.get(0);//因为本网页只有一个table，等价于.first()
        Elements tds = table.getElementsByTag("td");//从table中找<td>，即列
        for (int i = 0;i < tds.size();i+=6){
            Element td1 = tds.get(i);
            Element td2 = tds.get(i+5);
            String str1 = td1.text();
            String val = td2.text();
            Log.i(TAG, "getRate2: "+str1+"==>"+val);
            if(str1.equals("欧元")){
                rate[0] = 100d / Double.parseDouble(val);
                Log.i(TAG, "getRate2: 欧元"+rate[0]);
            }else if(str1.equals("韩元")){
                rate[1] = 100d / Double.parseDouble(val);
                Log.i(TAG, "getRate2: 韩元"+rate[1]);
            }else if(str1.equals("美元")){
                rate[2] = 100d / Double.parseDouble(val);
                Log.i(TAG, "getRate2: 美元"+rate[2]);
            }
        }

//        按行找的如下：
//        Elements trs = table.getElementsByTag("td");
//        for(Element tr : trs){
//            Elements tds  = tr.getElementsByTag("td");
//            if(tds.size()>0){
//                String td1 = tds.get(0).text();//tds.get(0)是Element类型
//                String td2 = tds.get(5).text();
//                System.out.println(td1+"==>"+td2);
//            }
//        }
        return rate;
    }

//    复写run，定义线程行为（即线程要执行的操作）
    @Override
    public void run() {
        Log.i(TAG, "run: run()……");

        // 获取网络数据
        URL url = null;
        ArrayList<String> arrayList;
        try {
            url = new URL("https://www.usd-cny.com/bankofchina.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();
            String html = inputStream2String(in);
            Log.i(TAG, "run: htmlCode:\n"+html);

//            -------方法一：用正则分离出的汇率-------
//            将ArrayList<String>类型的动态数组转为string
            arrayList = getRate(html);
            StringBuilder stringBuilder = new StringBuilder();
            int count1 = 0;
            int count2 = 2;
            for(String rate:arrayList){
                if(count1 == 0){
                    stringBuilder.append("euro:");
                    count1++;
                }else if (count1 == 1){
                    stringBuilder.append("won:");
                    count1++;
                }else if (count1 == count2){
                    stringBuilder.append("dollar:");
                    count1++;
                }
                stringBuilder.append(rate);
                stringBuilder.append("\t");
            }
            String rate = stringBuilder.toString();
            Log.i(TAG, "run: rate:"+rate);
//            将字符串转化为double
            Pattern pattern = Pattern.compile("(?<=euro:)[0-9]\\d*\\.?\\d*(?=\\s+)");
            Matcher matcher = pattern.matcher(rate);
            while (matcher.find()){
                euro_rate = 100 / Double.parseDouble(matcher.group(0));
            }
            Log.i(TAG, "run: euro_rateFromNet:"+euro_rate);
            Pattern pattern1 = Pattern.compile("(?<=won:)[0-9]\\d*\\.?\\d*(?=\\s+)");
            Matcher matcher1 = pattern1.matcher(rate);
            while (matcher1.find()){
                won_rate = 100 / Double.parseDouble(matcher1.group(0));
            }
            Log.i(TAG, "run: won_rateFromNet:"+won_rate);
            Pattern pattern2 = Pattern.compile("(?<=dollar:)[0-9]\\d*\\.?\\d*(?=\\s+)");
            Matcher matcher2 = pattern2.matcher(rate);
            while (matcher2.find()){
                dollar_rate = 100 / Double.parseDouble(matcher2.group(0));
            }
            Log.i(TAG, "run: won_rateFromNet:"+dollar_rate);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        ------方法2：用jsoup分离出的汇率--------
        double rate[] = getRate2();
        euro_rate = rate[0];
        won_rate = rate[1];
        dollar_rate = rate[2];
        Log.i(TAG, "run: euro_rateByJsoup="+euro_rate+"\n");
        Log.i(TAG, "run: won_rateByJsoup="+won_rate+"\n");
        Log.i(TAG, "run: dollar_rateByJsoup="+dollar_rate+"\n");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange_rate);

//        开启子线程
        Thread t = new Thread(this);
        t.start();

        Handler handler;
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==5){
                    String str = (String) msg.obj;
                    Log.i(TAG, "handleMessage: getMessage msg = " + str);
//                    show.setText(str);
                }
                super.handleMessage(msg);
            }
        };
//        获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        msg.what = 5;//这里设置是为了MyListActivity中要用
        msg.obj = "Hello from run()";
        handler.sendMessage(msg);
    }

//    判断点击的按钮，使用响应汇率计算出兑换结果
    public void exchangeRate(View button){
        money = (EditText)findViewById(R.id.inputRMB);
        if(TextUtils.isEmpty(money.getText().toString())){//如果没有输入(用PPT上的方法不对)
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
            //方法a：读取bundle中传回数据
            Bundle bundle = data.getExtras();
            dollar_rate = bundle.getDouble("key_dollar",0.1f);
            euro_rate = bundle.getDouble("key_euro",0.1f);
            won_rate = bundle.getDouble("key_won",0.1f);
            Log.i(TAG, "onActivityResult: dollar_rate=" + dollar_rate);
            Log.i(TAG, "onActivityResult: euro_rate=" + euro_rate);
            Log.i(TAG, "onActivityResult: won_rate=" + won_rate);

            //方法b：使用myrate.XML中汇率看是否成功存入、读取
            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            dollar_rate = sharedPreferences.getFloat("key_dollar2",0.0f);
            euro_rate = sharedPreferences.getFloat("key_euro2",0.0f);
            won_rate = sharedPreferences.getFloat("key_won2",0.0f);
//            //清除当前文件中数据
//            sharedPreferences.edit().clear().commit();

            Log.i(TAG, "onActivityResult: dollar_rate2=" + dollar_rate);
            Log.i(TAG, "onActivityResult: euro_rate2=" + euro_rate);
            Log.i(TAG, "onActivityResult: won_rate2=" + won_rate);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    点击后，跳转到show_rate_in_listview的方法
    public void jumpToListView(View view){
        Intent intent = new Intent();
        intent.setClass(this, MyListActivity.class);
        startActivity(intent);
    }
//    点击后，跳转到list_item的方法
    public void jumpToListItem(View view){
        Intent intent = new Intent();
        intent.setClass(this, MyListItem.class);
        startActivity(intent);
    }
}
