package cn.com.lightech.led_g5w.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.entity.Device;
import cn.com.lightech.led_g5w.entity.DeviceGroup;

/**
 * Created by 明 on 2016/3/4.
 */
public abstract class ExpDeviceAdapter extends BaseExpandableListAdapter {

    private List<DeviceGroup> mData = new ArrayList<>(0);

    private Context mContext;

    public ExpDeviceAdapter(Context context, List<DeviceGroup> groups) {
        this.mContext = context;
        this.mData = groups;
    }

    public ExpDeviceAdapter(Context context) {
        this.mContext = context;
    }


    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mData.get(groupPosition).getDevices().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mData.get(groupPosition).getDevices().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_devices_group, null);
            holder = new GroupViewHolder(convertView);

            holder.btnControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeviceGroup group = (DeviceGroup) v.getTag();
                    OnControlButtonClick(group.getNumber());
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        DeviceGroup group = (DeviceGroup) getGroup(groupPosition);
        holder.btnControl.setTag(group);
        holder.ivDeviceGroupNumber.setText(mContext.getString(R.string.group) + (group.getNumber() + 1));
        convertView.setTag(R.id.expend_listview_group_tag, groupPosition);
        convertView.setTag(R.id.expend_listview_child_tag, -1);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ItemViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_devices_item, null);
            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }
        Device device = (Device) getChild(groupPosition, childPosition);
        holder.tvDeviceIp.setText(device.getMac());
        //holder.tvDeviceIp.setText(device.getIp());
        convertView.setTag(R.id.expend_listview_group_tag, groupPosition);
        convertView.setTag(R.id.expend_listview_child_tag, childPosition);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    static class GroupViewHolder {
        @Bind(R.id.iv_deviceGroupPic)
        ImageView ivDeviceGroupPic;
        @Bind(R.id.iv_deviceGroupNumber)
        TextView ivDeviceGroupNumber;
        @Bind(R.id.btn_control)
        Button btnControl;


        GroupViewHolder(View view) {
            ButterKnife.bind(this, view);
        }


    }

    static class ItemViewHolder {
        @Bind(R.id.iv_devicePic)
        ImageView ivDevicePic;
        @Bind(R.id.tv_deviceNumber)
        TextView tvDeviceNumber;

        @Bind(R.id.tv_deviceip)
        TextView tvDeviceIp;

        ItemViewHolder(View view) {
            ButterKnife.bind(this, view);

        }
    }


    public List<DeviceGroup> getData() {
        return mData;
    }

    public synchronized void setData(List<DeviceGroup> mData) {
        this.mData = mData;
    }

    public abstract void OnControlButtonClick(int groupPosition);

}
