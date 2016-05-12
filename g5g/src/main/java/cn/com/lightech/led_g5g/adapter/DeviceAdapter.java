package cn.com.lightech.led_g5g.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.entity.Device;

/**
 * Created by 明 on 2016/3/7.
 */
public class DeviceAdapter extends BaseAdapter {

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    private List<Device> devices;
    private LayoutInflater inflater;


    public DeviceAdapter(Context context, List<Device> devices) {
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
            convertView = inflater.inflate(R.layout.item_devices_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Device item = (Device) getItem(position);
        holder.tvDeviceip.setText(item.getMac());
        return convertView;
    }


    static class ViewHolder {
        @Bind(R.id.iv_devicePic)
        ImageView ivDevicePic;
        @Bind(R.id.tv_deviceip)
        TextView tvDeviceip;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
