package cn.com.lightech.led_g5g.net.socket;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import cn.com.lightech.led_g5g.gloabal.App;

public class NetworkHelper {

    public static boolean CheckWifi() {
        Context context = App.getInstance();
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi != null && mWifi.isConnected()) {
            return true;
        }
        return false;
    }

    public static String GetWifiName() {
        if (!CheckWifi())
            return "No Wifi";
        WifiManager wifiMgr = (WifiManager) App.getInstance().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        return info != null ? info.getSSID().replace('"', ' ') : "DisConnect";//TODO:国际化
    }
}
