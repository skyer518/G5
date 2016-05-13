package cn.com.u2be.myutils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetworkUtil {

    public static boolean CheckWifi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi != null && mWifi.isConnected()) {
            return true;
        }
        return false;
    }

    public static String GetWifiName(Context context) {
        if (!CheckWifi(context))
            return "No Wifi";
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        return info != null ? info.getSSID().replace('"', ' ') : "DisConnect";//TODO:国际化
    }
}
