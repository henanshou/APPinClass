package com.swufe.appinclass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//自定义的Adapter适配器MyAdapter
public class MyAdapter extends ArrayAdapter {
    private static final String TAG = "MyAdapter";

    public MyAdapter(Context context,
                   int resource,
                   ArrayList<HashMap<String,String>> list){
        super(context, resource, list);
    }

    public MyAdapter(Context context,
                 int resource,
                 List<RateItem> list){
    super(context, resource, list);
}

//    为列表提供显示所需要的视图
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View itemView = convertView;
        if(itemView == null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,
                    parent,
                    false);
        }
//        --------注释掉的是MyListItem用的----------
//        Map<String,String> map = (Map<String, String>) getItem(position);
        RateItem rateItem = (RateItem)getItem(position);
        TextView title = (TextView) itemView.findViewById(R.id.itemTitle);
        TextView detail = (TextView) itemView.findViewById(R.id.itemDetail);
//        title.setText(map.get("ItemTitle"));
//        detail.setText(map.get("ItemDetail"));
        title.setText(String.valueOf(rateItem.getCurName()));
        detail.setText(String.valueOf(rateItem.getCurRate()));
        return itemView;
    }
}
