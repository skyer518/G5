package cn.com.lightech.led_g5g.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.net.wifi.WifiReceiver;
import cn.com.lightech.led_g5g.net.wifi.WifiTask;
import cn.com.lightech.led_g5g.utils.ProgressUtil;
import cn.com.lightech.led_g5g.view.device.IAddDeviceView;
import cn.com.lightech.led_g5g.view.device.impl.WifiActivity;

/**
 * Created by 明 on 2016/3/7.
 */
public class ScanLedPresenter {

    private static final int WHAT_SHOW_ERROR = 0;
    private static final int WHAT_GOTO_NEXT = 1;

    public static final String REG_HI_LINK_WIFI_SSID = "^((HI-LINK_+[0-9ABCDEF]{4})|(CTLite-+[0-9ABCDEF]{4})){1}$";
    private static final int WHAT_LED_CONNECTED = 2;
    private static final int WHAT_LED_FAILED = 3;

    private final Context mContext;
    private final WifiManager mWifiManager;
    private final IAddDeviceView newDeviceView;


    public ScanLedPresenter(Context context, IAddDeviceView view) {
        this.mContext = context;
        this.newDeviceView = view;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_GOTO_NEXT) {
            } else if (msg.what == WHAT_SHOW_ERROR) {

            } else if (msg.what == WHAT_LED_CONNECTED) {
                gotoNext((ScanResult) msg.obj);

            } else if (msg.what == WHAT_LED_FAILED) {
                ProgressUtil.closeDialog();
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                    newDeviceView.showMessage(mContext.getString(R.string.tips_m_delete_configured_wifi));
                } else {
                    newDeviceView.showMessage(mContext.getString(R.string.error_led_connect_failed));
                }
            }
        }
    };

    private void gotoNext(ScanResult obj) {
        Intent intent = new Intent();
        intent.setClass(mContext, WifiActivity.class);
        intent.putExtra(mContext.getString(R.string.intent_key_ledwifi), obj);
        mContext.startActivity(intent);
        ((Activity) mContext).finish();
    }


    public void connectDevice(final ScanResult mScanResult) {
        ProgressUtil.showPogress(mContext, mContext.getString(R.string.device_newdevice_connecting), false);

        String password = mContext.getString(R.string.app_default_led_password);
        final WifiTask task = new WifiTask(mContext) {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    mContext.registerReceiver(new WifiReceiver() {
                        @Override
                        public void onWifiConnected(WifiInfo wifiInfo) {

                            if (wifiInfo.getSSID().equals("\"" + mScanResult.SSID + "\"")) {
                                super.onWifiConnected(wifiInfo);
                                Message msg = new Message();
                                msg.what = WHAT_LED_CONNECTED;
                                msg.obj = mScanResult;
                                handler.sendMessage(msg);
                            } else {
                                handler.sendEmptyMessage(WHAT_LED_FAILED);
                            }
                            mContext.unregisterReceiver(this);
                        }
                    }, getIntentFiler());
                } else {
                    handler.sendEmptyMessage(WHAT_LED_FAILED);
                }
            }

            @Override
            protected void onCancelled(Boolean aBoolean) {
                handler.sendEmptyMessage(WHAT_LED_FAILED);
            }
        };
        task.execute(mScanResult, password, false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!task.isCancelled()) {
                    task.cancel(false);
                }
            }
        }, 15000);


    }


    private List<ScanResult> deviceWifiFilter(String regular, List<ScanResult> results) {
        List<ScanResult> result = new ArrayList<>(0);
        if (TextUtils.isEmpty(regular) || results == null || results.size() == 0) {
            return result;
        }
        for (ScanResult scanResult : results) {
            if (scanResult.SSID.matches(regular)) {
                result.add(scanResult);
            }
        }
        return result;
    }

    private IntentFilter getIntentFiler() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        return filter;
    }


    public List<ScanResult> getWifiScanResult() {
        List<ScanResult> scanResults = mWifiManager.getScanResults();
        return deviceWifiFilter(REG_HI_LINK_WIFI_SSID, scanResults);
    }
}
