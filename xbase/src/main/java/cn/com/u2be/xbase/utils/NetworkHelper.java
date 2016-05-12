package cn.com.u2be.xbase.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by 明 on 2016/3/7.
 */


public class NetworkHelper {

    public static boolean CheckWifi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi != null && mWifi.isConnected()) {
            return true;
        }
        return false;
    }

    public static String GetCurrentWifiName(Context context) {
        if (!CheckWifi(context))
            return "No Wifi";
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        return info != null ? info.getSSID().replace('"', ' ') : "DisConnect";//TODO:国际化
    }
}


