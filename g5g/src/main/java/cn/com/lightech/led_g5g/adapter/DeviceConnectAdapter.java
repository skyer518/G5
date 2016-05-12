package cn.com.lightech.led_g5g.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.entity.Device;

/**
 * Created by 明 on 2016/3/7.
 */
public class DeviceConnectAdapter extends BaseAdapter {

    private int currentPosition;

    public int getCurrentPosition() {
        return currentPosition;
    }


    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    private List<Device> devices;
    private LayoutInflater inflater;

    Map<Integer, Boolean> isConnected = new HashMap<>(0);


    public DeviceConnectAdapter(Context context, List<Device> devices) {
        this.inflater = LayoutInflater.from(context);
        setDevices(devices);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_devices_connect_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Boolean aBoolean = isConnected.get(position);
        if (aBoolean == null || !aBoolean) {
            holder.tvConnected.setVisibility(View.GONE);
            holder.pbConnect.setVisibility(View.VISIBLE);
        } else {
            holder.tvConnected.setVisibility(View.VISIBLE);
            holder.pbConnect.setVisibility(View.GONE);
        }
        if (position == this.currentPosition) {
            holder.ivDevicePic.setImageResource(R.mipmap.led_parent);
        } else {
            holder.ivDevicePic.setImageResource(R.mipmap.led);
        }
        Device item = (Device) getItem(position);
        holder.tvDeviceNumber.setText(item.getMac());
        return convertView;
    }

    public void addConnect(int position) {
        isConnected.put(new Integer(position), true);
    }

    public boolean isAllConnect() {
        return isConnected.size() == devices.size();
    }

    public void setCurrentPosition(int position) {
        this.currentPosition = position;
    }


    static class ViewHolder {
        @Bind(R.id.iv_devicePic)
        ImageView ivDevicePic;
        @Bind(R.id.tv_deviceNumber)
        TextView tvDeviceNumber;
        @Bind(R.id.pb_connect)
        ProgressBar pbConnect;
        @Bind(R.id.tv_connected)
        TextView tvConnected;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
