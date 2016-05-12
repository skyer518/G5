package cn.com.lightech.led_g5g.net.wifi;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by 明 on 2016/3/8.
 */
public abstract class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
            //signal strength changed
        } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接上与否
            System.out.println("网络状态改变");
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                System.out.println("wifi网络连接断开");
                onWifiDisconnected();
            } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {

                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                //获取当前wifi名称
                System.out.println("连接到网络 " + wifiInfo.getSSID());
                onWifiConnected(wifiInfo);
            }

        } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {//wifi打开与否
            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);

            if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                System.out.println("系统关闭wifi");
                onWifiDisabled();
            } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                System.out.println("系统开启wifi");
                onWifiEnabled();
            }
        }
    }

    /**
     * wifi打开
     */
    public void onWifiEnabled() {
    }

    ;

    /**
     * wifi 关闭
     */
    public void onWifiDisabled() {
    }

    ;

    /**
     * wifi断开
     */
    public void onWifiDisconnected() {
    }

    ;

    /**
     * wifi连接
     *
     * @param wifiInfo
     */
    public void onWifiConnected(WifiInfo wifiInfo) {
    }

    ;


}
