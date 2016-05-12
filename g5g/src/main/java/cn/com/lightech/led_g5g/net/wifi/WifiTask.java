package cn.com.lightech.led_g5g.net.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.widget.Toast;

import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.net.utils.WifiTool;
import cn.com.lightech.led_g5g.view.device.impl.dialog.ConfiguredNetworkView;
import cn.com.lightech.led_g5g.view.device.impl.dialog.CurrentNetworkView;
import cn.com.u2be.alekwifilibrary.Wifi;

/**
 * Created by 明 on 2016/3/9.
 */
public class WifiTask extends AsyncTask<Object, Void, Boolean> {

    private final WifiManager mWifiManager;
    private Context mContext;


    public WifiTask(Context context) {
        this.mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(Object... params) {

        if (params == null || params.length < 1)
            return false;
        ScanResult mScanResult = (ScanResult) params[0];
        String password = (String) params[1];
        boolean isUpdate = (boolean) params[2];
        boolean connResult;

        final String security = Wifi.ConfigSec.getScanResultSecurity(mScanResult);
        final WifiConfiguration config = Wifi.getWifiConfiguration(mWifiManager, mScanResult, security);
        if (config == null) {
            String mScanResultSecurity = Wifi.ConfigSec.getScanResultSecurity(mScanResult);
            boolean mIsOpenNetwork = Wifi.ConfigSec.isOpenNetwork(mScanResultSecurity);
            int mNumOpenNetworksKept = Settings.Secure.getInt(mContext.getContentResolver(),
                    Settings.Secure.WIFI_NUM_OPEN_NETWORKS_KEPT, 10);
            if (mIsOpenNetwork) {
                connResult = Wifi.connectToNewNetwork(mContext, mWifiManager, mScanResult, null, mNumOpenNetworksKept);
            } else {
                connResult = Wifi.connectToNewNetwork(mContext, mWifiManager, mScanResult
                        , password
                        , mNumOpenNetworksKept);
            }
        } else {
            int mNumOpenNetworksKept = Settings.Secure.getInt(mContext.getContentResolver(),
                    Settings.Secure.WIFI_NUM_OPEN_NETWORKS_KEPT, 10);
            if (isUpdate) {
                connResult = Wifi.changePasswordAndConnect(mContext, mWifiManager, config
                        , password
                        , mNumOpenNetworksKept);
            } else {
                connResult = Wifi.connectToConfiguredNetwork(mContext, mWifiManager, config, false);
            }

        }


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return connResult;

    }


}
