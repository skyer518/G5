package cn.com.lightech.led_g5w.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.entity.DeviceGroup;

/**
 * Created by 明 on 2016/3/4.
 */
public class DeleteGroupAdapter extends BaseAdapter {

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

    public DeleteGroupAdapter(Context context, List<DeviceGroup> groups) {
        this.mContext = context;
        this.mData = groups;
    }

    public DeleteGroupAdapter(Context context) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ItemViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_devices_delete_item, null);
            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }

        DeviceGroup group = (DeviceGroup) getItem(position);
        if (group.getNumber() == 0)
            holder.cbItemChecked.setEnabled(false);
        final Boolean checked = checkedMap.get(new Integer(position));
        if (checked != null && checked) {
            holder.cbItemChecked.setChecked(true);
        } else {
            holder.cbItemChecked.setChecked(false);
        }

        holder.tvDeviceNumber.setText((group.getNumber() + 1) + "");
        holder.cbItemChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkedMap.put(position, isChecked);
                } else {
                    checkedMap.remove(position);
                }

                onChecked(checkedMap);
            }


        });

        return convertView;
    }

    protected void onChecked(Map<Integer, Boolean> checkedMap) {
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

    static class ViewHolder {
        @Bind(R.id.cb_itemChecked)
        CheckBox cbItemChecked;
        @Bind(R.id.iv_devicePic)
        ImageView ivDevicePic;
        @Bind(R.id.tv_deviceNumber)
        TextView tvDeviceNumber;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


//    public List<DeviceGroup> getData() {
//        return mData;
//    }
//
//    public void setData(List<DeviceGroup> mData) {
//        this.mData = mData;
//    }


}
