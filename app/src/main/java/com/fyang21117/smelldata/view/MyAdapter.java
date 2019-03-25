package com.fyang21117.smelldata.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.fyang21117.smelldata.R;
import java.util.List;

public class MyAdapter extends BaseAdapter {
    private List<Item> itemList;
    private LayoutInflater inflater;
    public MyAdapter() {}

    public MyAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return itemList==null?0:itemList.size();
    }

    @Override
    public Item getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        View view = inflater.inflate(R.layout.simple_list_item_2,null);
        Item item=getItem(position);
        //在view视图中查找id为image_photo的控件

        TextView tv_name=   view.findViewById(R.id.t1);
        TextView tv_age=   view.findViewById(R.id.t2);

        tv_name.setText(String.valueOf(item.getName()));
        tv_age.setText(String.valueOf(item.getInfo()));
        return view;
    }
}
