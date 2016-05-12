package cn.com.lightech.led_g5g.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 明 on 2016/3/7.
 */
public class TextArrayAdapter extends BaseAdapter {

    private final Context mContext;

    public void setData(List<String> mData) {
        this.mData = mData;
    }


    private List<String> mData;

    public TextArrayAdapter(Context context, List<String> data) {
        this.mContext = context;
        this.mData = data;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, null);
            tv = (TextView) convertView;
        } else {
            tv = (TextView) convertView;
        }
        String item = (String) getItem(position);
        tv.setText(item);
        return convertView;

    }


}
