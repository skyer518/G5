package cn.com.u2be.testnetlib;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 明 on 2016/3/7.
 */
public class WifiAdapter extends BaseAdapter {

    public void setScanResult(List<ScanResult> scanResult) {
        this.scanResult = scanResult;
        Collections.sort(this.scanResult, comparator);
    }

    private List<ScanResult> scanResult;
    private LayoutInflater inflater;

    private Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
        @Override
        public int compare(ScanResult lhs, ScanResult rhs) {
            return rhs.level - lhs.level;
        }
    };

    public WifiAdapter(Context context, List<ScanResult> scanResults) {
        this.inflater = LayoutInflater.from(context);
        setScanResult(scanResults);
    }

    @Override
    public int getCount() {
        return scanResult.size();
    }

    @Override
    public Object getItem(int position) {
        return scanResult.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_wifi_listview, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        ScanResult item = (ScanResult) getItem(position);

        int signalLevel = WifiManager.calculateSignalLevel(item.level, 5);
//        switch (signalLevel) {
//            case 1:
//                holder.wifiStronger.setImageResource(R.mipmap.wifi_1);
//                break;
//            case 2:
//                holder.wifiStronger.setImageResource(R.mipmap.wifi_2);
//                break;
//            case 3:
//                holder.wifiStronger.setImageResource(R.mipmap.wifi_3);
//                break;
//            case 4:
//                holder.wifiStronger.setImageResource(R.mipmap.wifi_4);
//                break;
//            default:
//                holder.wifiStronger.setImageResource(R.mipmap.wifi_1);
//                break;
//        }

        holder.wifiSSID.setText(item.SSID);
        return convertView;
    }


    static class ViewHolder {
        @Bind(R.id.wifi_stronger)
        ImageView wifiStronger;
        @Bind(R.id.wifi_SSID)
        TextView wifiSSID;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
