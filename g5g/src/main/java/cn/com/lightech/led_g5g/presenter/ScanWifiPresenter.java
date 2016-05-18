package cn.com.lightech.led_g5g.presenter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.com.lightech.led_g5g.R;
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
//                case WHAT_WIFI_CONNECTED:
//                    connectLED();
//                    break;
//                case WHAT_LED_CONNECTED:
//                    getMacAdress();
//                    break;
//                case WHAT_SUCCESS:
//                    gotoNext();
//                    break;
//                case WHAT_SET_GROUP_SUCCESS:
//                    saveData2Wifi(lanWifi, lanWifiPwd);
//                    break;
//                case WHAT_SET_WIFI_SUCCESS:
//                    reboot2Led();
//                    break;
//                case WHAT_GET_MAC_ADDRESS_SUCCESS:
//                    saveData2LED();
//                    break;
                case RequestEntity.WHAT_LED_FAILED:
                    ProgressUtil.closeDialog();
                    wifiView.showMessage(mContext.getString(R.string.error_led_connect_failed));
                    break;
                case RequestEntity.WHAT_WIFI_FAILED:
                    ProgressUtil.closeDialog();
                    wifiView.showMessage(mContext.getString(R.string.error_wifi_connect_failed));
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
            }
        }


    };


    public void connectWifi(final ScanResult wifi, final String password, boolean isUpdate) {
        entity.lanWifi = wifi;
        entity.lanWifiPwd = password;
        entity.isUpdate = isUpdate;
        DutySaveWifi saveWifi = new DutySaveWifi();
        DutySetGroup setGroup = new DutySetGroup(saveWifi);
        DutyGetDeviceType getDeviceType = new DutyGetDeviceType(setGroup);
        DutyGetMac getMac = new DutyGetMac(getDeviceType);
        DutyConnectLed connectLed = new DutyConnectLed(getMac);
        DutyConnectWifi connectWifi = new DutyConnectWifi(connectLed);
        connectWifi.handleRequest(entity);


//        this.lanWifi = wifi;
//        this.lanWifiPwd = password;
//        PreferenceUtils.saveWifiConfig(lanWifi.SSID, lanWifiPwd);
//        ProgressUtil.showPogress(mContext, mContext.getString(R.string.device_wifi_connecting), false);
//        final WifiTask task = new WifiTask(mContext) {
//            @Override
//            protected void onPostExecute(Boolean aBoolean) {
//                if (aBoolean) {
//                    mContext.registerReceiver(new WifiReceiver() {
//
//                        @Override
//                        public void onWifiConnected(WifiInfo wifiInfo) {
//                            super.onWifiConnected(wifiInfo);
//                            if (wifiInfo.getSSID().equals("\"" + wifi.SSID + "\"")) {
//                                handler.sendEmptyMessage(WHAT_WIFI_CONNECTED);
//                            } else {
//                                handler.sendEmptyMessage(WHAT_WIFI_FAILED);
//                            }
//                            mContext.unregisterReceiver(this);
//                        }
//
//                    }, getIntentFiler());
//                } else {
//                    handler.sendEmptyMessage(WHAT_WIFI_FAILED);
//                }
//            }
//
//            @Override
//            protected void onCancelled(Boolean aBoolean) {
//                handler.sendEmptyMessage(WHAT_WIFI_FAILED);
//            }
//        };
//        task.execute(wifi, password, isUpdate);
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!task.isCancelled()) {
//                    task.cancel(false);
//                }
//            }
//        }, 10000);

    }

    //
//    public void connectLED() {
//
//        ProgressUtil.showPogress(mContext, mContext.getString(R.string.device_newdevice_connecting), false);
//        final WifiTask task = new WifiTask(mContext) {
//            @Override
//            protected void onPostExecute(Boolean aBoolean) {
//                if (aBoolean) {
//                    mContext.registerReceiver(new WifiReceiver() {
//
//                        @Override
//                        public void onWifiConnected(WifiInfo wifiInfo) {
//                            if (wifiInfo.getSSID().equals("\"" + ledWifi.SSID + "\"")) {
//                                super.onWifiConnected(wifiInfo);
//                                handler.sendEmptyMessage(WHAT_LED_CONNECTED);
//                            } else {
//                                handler.sendEmptyMessage(WHAT_LED_FAILED);
//                            }
//                            mContext.unregisterReceiver(this);
//                        }
//                    }, getIntentFiler());
//                } else {
//                    handler.sendEmptyMessage(WHAT_LED_FAILED);
//                }
//            }
//
//
//            @Override
//            protected void onCancelled(Boolean aBoolean) {
//                handler.sendEmptyMessage(WHAT_LED_FAILED);
//            }
//        };
//        task.execute(ledWifi, mContext.getString(R.string.app_default_led_password), false);
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!task.isCancelled()) {
//                    task.cancel(false);
//                }
//            }
//        }, 10000);
//    }
//
//    private IntentFilter getIntentFiler() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        return filter;
//    }
//
//
//    private void getMacAdress() {
//        final HttpPostTask task = new HttpPostTask() {
//            @Override
//            protected void onPreExecute() {
//                ProgressUtil.showPogress(mContext, mContext.getString(R.string.device_wifi_save_data), false);
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                ProgressUtil.closeDialog();
//                Log.i(tag, s);
//                /*
//                <html><head><title>My Title</title><link rel="stylesheet" href="/style/normal_ws.css"				type="text/css"><meta http-equiv="content-type" content="text/html;				charset=utf-8"></head>
//                <body>at+Get_MAC=? 20:F4:1B:79:FB:79 ,20:F4:1B:79:FB:78</body></html>
//                */
//
//                final Pattern pattern = Pattern.compile(REG_MAC);
//                final Matcher matcher = pattern.matcher(s);
//                if (matcher.find()) {
//                    mac = MacUtil.convertMac(matcher.group());
//                    handler.sendEmptyMessage(WHAT_GET_MAC_ADDRESS_SUCCESS);
//                } else {
//                    handler.sendEmptyMessage(WHAT_GET_MAC_ADDRESS_FAILED);
//                }
//            }
//
//            @Override
//            protected void onCancelled() {
//                super.onCancelled();
//                handler.sendEmptyMessage(WHAT_GET_MAC_ADDRESS_FAILED);
//            }
//        };
//        task.execute(PostParamUtil.getPostUrl(null), PostParamUtil.getGetMacParams());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!task.isCancelled()) {
//                    task.cancel(false);
//                }
//            }
//        }, 3000);
//    }
//
//    private void saveData2Wifi(final ScanResult wifi, String password) {
//        final String rawSecurity = Wifi.ConfigSec.getDisplaySecirityString(wifi);
//        final HttpPostTask task = new HttpPostTask() {
//            @Override
//            protected void onPreExecute() {
//                ProgressUtil.showPogress(mContext, mContext.getString(R.string.device_wifi_save_data), false);
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                ProgressUtil.closeDialog();
////                handler.sendEmptyMessage(WHAT_SET_WIFI_SUCCESS);
//                handler.sendEmptyMessage(WHAT_SUCCESS);
//            }
//
//            @Override
//            protected void onCancelled() {
//                super.onCancelled();
//                handler.sendEmptyMessage(WHAT_SUCCESS);
//            }
//        };
//        task.execute(PostParamUtil.getPostUrl(null),
//                PostParamUtil.getSaveWifiParams(wifi.SSID, "auto", password));
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!task.isCancelled()) {
//                    task.cancel(false);
//                }
//            }
//        }, 3000);
//    }
//
//    private void saveData2LED() {
//
//        ProgressUtil.showPogress(mContext, mContext.getString(R.string.device_wifi_save_data_2_led), false);
//
//        final TcpTask task = new TcpTask();
//        task.setGroup();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!task.isOver)
//                    task.over(false);
//            }
//        }, 3000);
//
//    }
//
//    private void reboot2Led() {
//        final HttpPostTask task = new HttpPostTask() {
//            @Override
//            protected void onPreExecute() {
//                ProgressUtil.showPogress(mContext, mContext.getString(R.string.device_wifi_reboot), false);
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                ProgressUtil.closeDialog();
//                handler.sendEmptyMessage(WHAT_SUCCESS);
//            }
//
//            @Override
//            protected void onCancelled() {
//                super.onCancelled();
//                ProgressUtil.closeDialog();
//                handler.sendEmptyMessage(WHAT_SUCCESS);
//            }
//        };
//        task.execute(PostParamUtil.getPostUrl(null), PostParamUtil.getRebootWifiParams());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!task.isCancelled()) {
//                    task.cancel(false);
//                }
//            }
//        }, 2000);
//    }
//
//
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
//
//    public void gotoNext() {
//        Toast.makeText(mContext, R.string.device_device_rebooting, Toast.LENGTH_LONG).show();
//        new WifiTask(mContext).execute(lanWifi, lanWifiPwd, false);
//        wifiView.successSetting();
//
//    }
//
    public List<ScanResult> getWifiScanResult() {
        List<ScanResult> scanResults = excludeDeviceFilter(ledWifi.SSID, wifiManager.getScanResults());
        return excludeDeviceFilter("\\s*", scanResults);
    }
//
//
//    class TcpTask implements IDataListener {
//
//        ConnectManager connect = new ConnectManager();
//
//        boolean isOver = false;
//
//        public void setGroup() {
//            connect.Connect("192.168.16.254", 8080);
//            connect.Register(this);
//        }
//
//        public boolean isOver() {
//            return isOver();
//        }
//
//        @Override
//        public int getPriority() {
//            return 10;
//        }
//
//        @Override
//        public void onConnectStateChanged(ConnState connState, ConnectManager connectManager) {
//            Log.i("wiwiPersentr", "onConnectStateChanged :" + connState);
//        }
//
//        void over(boolean successs) {
//            ProgressUtil.closeDialog();
//            if (successs) {
//                handler.sendEmptyMessage(WHAT_SET_GROUP_SUCCESS);
//            } else {
//                handler.sendEmptyMessage(WHAT_SET_GROUP_FAILED);
//            }
//
//            connect.UnRegister(this);
//            connect.closeConnection();
//            isOver = true;
//        }
//
//        @Override
//        public boolean onReceive(Response response, ConnectManager connectManager) {
//            if (response.getCmdType() == CmdType.SetGroup) {
//                if (response.IsOK()) {
//                    over(true);
//                } else {
//                    over(false);
//                }
//            } else if (response.getCmdType() == CmdType.CheckReady) {
//                if (response.IsOK()) {
//                    Request request = new Request();
//                    request.setCmdType(CmdType.SetGroup);
//                    request.setIntVal(0);
//                    request.setByteArray(mac);
//                    connect.SendToLed(CmdBuilder.Build(request));
//                }
//            }
//            return true;
//        }
//
//
//    }


}
