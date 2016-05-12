package cn.com.lightech.led_g5w.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.entity.Device;
import cn.com.lightech.led_g5w.entity.DeviceGroup;

/**
 * Created by 明 on 2016/3/4.
 */
public class ExpDeleteDeviceAdapter extends BaseExpandableListAdapter {

    @Bind(R.id.cb_itemChecked)
    CheckBox cbChecked;
    @Bind(R.id.iv_devicePic)
    ImageView ivDevicePic;
    @Bind(R.id.tv_deviceNumber)
    TextView tvDeviceNumber;
    private List<DeviceGroup> mData = new ArrayList<>(0);

    public Map<Integer, Boolean> getCheckedMap() {
        return checkedMap;
    }

    private Map<Integer, Boolean> checkedMap = new HashMap<>(0);

    private Context mContext;

    public ExpDeleteDeviceAdapter(Context context, List<DeviceGroup> groups) {
        this.mContext = context;
        this.mData = groups;
    }

    public ExpDeleteDeviceAdapter(Context context) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_devices_delete_group, null);
            holder = new GroupViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        DeviceGroup group = (DeviceGroup) getGroup(groupPosition);
        holder.ivDeviceGroupNumber.setText(mContext.getString(R.string.group) + (group.getNumber() + 1));
        convertView.setTag(R.id.expend_listview_group_tag, groupPosition);
        convertView.setTag(R.id.expend_listview_child_tag, -1);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ItemViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_devices_delete_item, null);
            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }
        final Boolean checked = checkedMap.get(new Integer(groupPosition * 1000 + childPosition));
        if (checked != null && checked) {
            holder.cbItemChecked.setChecked(true);
        } else {
            holder.cbItemChecked.setChecked(false);
        }
        holder.cbItemChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkedMap.put(new Integer(groupPosition * 1000 + childPosition), true);
                } else {
                    checkedMap.remove(new Integer(groupPosition * 1000 + childPosition));
                }

                onChecked(checkedMap);
            }
        });
        Device group = (Device) getChild(groupPosition, childPosition);
        holder.tvDeviceNumber.setText(group.getMac());
        convertView.setTag(R.id.expend_listview_group_tag, groupPosition);
        convertView.setTag(R.id.expend_listview_child_tag, childPosition);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    protected void onChecked(Map<Integer, Boolean> checkedMap) {
    }


    class GroupViewHolder {
        @Bind(R.id.iv_deviceGroupPic)
        ImageView ivDeviceGroupPic;
        @Bind(R.id.iv_deviceGroupNumber)
        TextView ivDeviceGroupNumber;

        GroupViewHolder(View view) {
            ButterKnife.bind(this, view);
        }


    }

    class ItemViewHolder {

        @Bind(R.id.cb_itemChecked)
        CheckBox cbItemChecked;
        @Bind(R.id.iv_devicePic)
        ImageView ivDevicePic;
        @Bind(R.id.tv_deviceNumber)
        TextView tvDeviceNumber;

        ItemViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    public List<DeviceGroup> getData() {
        return mData;
    }

    public void setData(List<DeviceGroup> mData) {
        this.mData = mData;
    }


}
