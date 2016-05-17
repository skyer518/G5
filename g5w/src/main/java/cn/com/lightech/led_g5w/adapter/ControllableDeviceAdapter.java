package cn.com.lightech.led_g5w.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.entity.Device;
import cn.com.lightech.led_g5w.entity.DeviceGroup;

/**
 * Created by 明 on 2016/3/4.
 */
public abstract class ControllableDeviceAdapter extends BaseAdapter {

    private List<Device> mData = new ArrayList<>(0);

    private Context mContext;

    public ControllableDeviceAdapter(Context context, List<Device> devices) {
        this.mContext = context;
        this.mData = devices;
    }

    public ControllableDeviceAdapter(Context context) {
        this.mContext = context;
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
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_devices_item, null);
            holder = new ViewHolder(convertView);

            holder.btnControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Device device = (Device) v.getTag();
                    OnControlButtonClick(device.getIp());
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Device device = (Device) getItem(position);
        holder.btnControl.setTag(device);
        holder.tvDeviceip.setText(device.getMac());
        //holder.tvDeviceIp.setText(device.getIp());
        return convertView;
    }


    public synchronized List<Device> getData() {
        return mData;
    }

    public synchronized void setData(List<Device> mData) {
        this.mData = mData;
    }

    public abstract void OnControlButtonClick(String deviceIp);

    static class ViewHolder {
        @Bind(R.id.iv_devicePic)
        ImageView ivDevicePic;
        @Bind(R.id.tv_deviceNumber)
        TextView tvDeviceNumber;
        @Bind(R.id.tv_deviceip)
        TextView tvDeviceip;
        @Bind(R.id.btn_control)
        Button btnControl;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
