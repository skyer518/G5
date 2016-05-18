package cn.com.lightech.led_g5g.presenter.responsibility;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Handler;

import cn.com.lightech.led_g5g.entity.DeviceType;

/**
 * Created by alek on 2016/5/18.
 */
public class RequestEntity {

    public static final int WHAT_SUCCESS = 0x01;
    public static final int WHAT_LED_FAILED = 0x02;
    public static final int WHAT_WIFI_FAILED = 0x03;
    public static final int WHAT_SET_GROUP_FAILED = 0x04;
    public static final int WHAT_GET_MAC_ADDRESS_FAILED = 0x05;
    public static final int WHAT_GET_DEVICE_TYPE_FAILED = 0x06;
    public static final int WHAT_SAVE_WIFI_FAILED = 0x07;
    public static final int WHAT_TIMEOUT = 0x08;
    public static final int WHAT_UNKNOWN_DEVICE = 0x09;

    public ScanResult lanWifi;
    public String lanWifiPwd;
    public Context mContext;
    public boolean isUpdate;
    public ScanResult ledWifi;
    public byte[] mac;
    public DeviceType deviceType;
    public Handler handler;
}
