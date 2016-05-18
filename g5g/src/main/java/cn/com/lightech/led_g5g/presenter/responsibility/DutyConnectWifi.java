package cn.com.lightech.led_g5g.presenter.responsibility;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.net.wifi.WifiReceiver;
import cn.com.lightech.led_g5g.net.wifi.WifiTask;
import cn.com.lightech.led_g5g.utils.PreferenceUtils;
import cn.com.lightech.led_g5g.utils.ProgressUtil;

/**
 * Created by alek on 2016/5/17.
 */
public class DutyConnectWifi extends DutyHandler {
    public DutyConnectWifi(DutyHandler successor) {
        super(successor);
    }

    public DutyConnectWifi() {
    }

    @Override
    public void handleRequest(final RequestEntity request) {
        PreferenceUtils.saveWifiConfig(request.lanWifi.SSID, request.lanWifiPwd);
        ProgressUtil.showPogress(request.mContext, request.mContext.getString(R.string.device_wifi_connecting), false);
        final WifiTask task = new WifiTask(request.mContext) {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    request.mContext.registerReceiver(new WifiReceiver() {

                        @Override
                        public void onWifiConnected(WifiInfo wifiInfo) {
                            super.onWifiConnected(wifiInfo);
                            if (wifiInfo.getSSID().equals("\"" + request.lanWifi.SSID + "\"")) {
                                handNext(request);
                            } else {
                                request.handler.sendEmptyMessage(RequestEntity.WHAT_WIFI_FAILED);
                            }
                            request.mContext.unregisterReceiver(this);
                        }

                    }, getIntentFiler());
                } else {
                    request.handler.sendEmptyMessage(RequestEntity.WHAT_WIFI_FAILED);
                }
            }

            @Override
            protected void onCancelled(Boolean aBoolean) {
                request.handler.sendEmptyMessage(RequestEntity.WHAT_WIFI_FAILED);
            }
        };

        task.execute(request.lanWifi, request.lanWifiPwd, request.isUpdate);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!task.isCancelled()) {
                    task.cancel(false);
                }
            }
        }, 10000);
    }


    private IntentFilter getIntentFiler() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        return filter;
    }
}
