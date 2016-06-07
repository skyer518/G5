package cn.com.lightech.led_g5g.presenter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.net.wifi.WifiTask;
import cn.com.lightech.led_g5g.presenter.responsibility.DutyConnectLed;
import cn.com.lightech.led_g5g.presenter.responsibility.DutyConnectWifi;
import cn.com.lightech.led_g5g.presenter.responsibility.DutyGetDeviceType;
import cn.com.lightech.led_g5g.presenter.responsibility.DutyGetMac;
import cn.com.lightech.led_g5g.presenter.responsibility.DutySaveWifi;
import cn.com.lightech.led_g5g.presenter.responsibility.DutySetGroup;
import cn.com.lightech.led_g5g.presenter.responsibility.RequestEntity;
import cn.com.lightech.led_g5g.utils.ProgressUtil;
import cn.com.lightech.led_g5g.view.device.IWifiView;

/**
 * Created by 明 on 2016/3/7.
 */
public class ScanWifiPresenter {


    private final Context mContext;
    private final IWifiView wifiView;
    private final ScanResult ledWifi;
    private WifiManager wifiManager;

    private RequestEntity entity;

    public ScanWifiPresenter(Context context, IWifiView view, ScanResult ledWifi) {
        entity = new RequestEntity();
        entity.mContext = context;
        entity.ledWifi = ledWifi;
        entity.handler = handler;

        this.mContext = context;
        this.wifiView = view;
        this.ledWifi = ledWifi;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case RequestEntity.WHAT_LED_FAILED:
                    ProgressUtil.closeDialog();
                    wifiView.showMessage(mContext.getString(R.string.error_led_connect_failed));
                    break;
                case RequestEntity.WHAT_WIFI_FAILED:
                    ProgressUtil.closeDialog();
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                        wifiView.showMessage(mContext.getString(R.string.tips_m_delete_configured_wifi));
                    } else {
                        wifiView.showMessage(mContext.getString(R.string.error_wifi_connect_failed));
                    }
                    break;
                case RequestEntity.WHAT_GET_MAC_ADDRESS_FAILED:
                    ProgressUtil.closeDialog();
                    wifiView.showMessage("Get data failed");
                    break;
                case RequestEntity.WHAT_SET_GROUP_FAILED:
                    ProgressUtil.closeDialog();
                    wifiView.showMessage(mContext.getString(R.string.error_set_flag_failed));
                    break;
                case RequestEntity.WHAT_GET_DEVICE_TYPE_FAILED:
                    wifiView.showMessage("Get device type failed");
                    break;
                case RequestEntity.WHAT_SAVE_WIFI_FAILED:
                    wifiView.showMessage("Save data failed");
                    break;
                case RequestEntity.WHAT_TIMEOUT:
                    wifiView.showMessage("Time out");
                    break;
                case RequestEntity.WHAT_UNKNOWN_DEVICE:
                    wifiView.showMessage("Unknown device");
                    break;
                case RequestEntity.WHAT_SUCCESS:
                    gotoNext();
                    break;
            }
        }


    };


    public void connectWifi(final ScanResult wifi, final String password, boolean isUpdate) {
        entity.lanWifi = wifi;
        entity.lanWifiPwd = password;
        entity.isUpdate = isUpdate;
        DutySaveWifi saveWifi = new DutySaveWifi();
//        DutySetGroup setGroup = new DutySetGroup(saveWifi);
//        DutyGetDeviceType getDeviceType = new DutyGetDeviceType(setGroup);
//        DutyGetMac getMac = new DutyGetMac(getDeviceType);
        DutyConnectLed connectLed = new DutyConnectLed(saveWifi);
        DutyConnectWifi connectWifi = new DutyConnectWifi(connectLed);
        connectWifi.handleRequest(entity);

    }

    private List<ScanResult> excludeDeviceFilter(String regular, List<ScanResult> results) {
        List<ScanResult> result = new ArrayList<>(0);
        if (TextUtils.isEmpty(regular) || results == null || results.size() == 0) {
            return result;
        }
        for (ScanResult scanResult : results) {
            if (!scanResult.SSID.matches(regular)) {
                result.add(scanResult);
            }
        }
        return result;
    }

    //

    public void gotoNext() {
        Toast.makeText(mContext, R.string.device_device_rebooting, Toast.LENGTH_LONG).show();
        new WifiTask(mContext).execute(entity.lanWifi, entity.lanWifiPwd, false);
        wifiView.successSetting();

    }

    //
    public List<ScanResult> getWifiScanResult() {
        List<ScanResult> scanResults = excludeDeviceFilter(ledWifi.SSID, wifiManager.getScanResults());
        return excludeDeviceFilter("\\s*", scanResults);
    }


}
