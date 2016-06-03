package cn.com.lightech.led_g5w.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.lightech.led_g5w.entity.Device;
import cn.com.lightech.led_g5w.entity.DeviceType;
import cn.com.lightech.led_g5w.gloabal.App;
import cn.com.lightech.led_g5w.gloabal.CmdBuilder;
import cn.com.lightech.led_g5w.gloabal.Constants;
import cn.com.lightech.led_g5w.gloabal.IDataListener;
import cn.com.lightech.led_g5w.gloabal.LedProxy;
import cn.com.lightech.led_g5w.net.ConnectManager;
import cn.com.lightech.led_g5w.net.ConnectionsManager;
import cn.com.lightech.led_g5w.net.entity.CmdType;
import cn.com.lightech.led_g5w.net.entity.ConnState;
import cn.com.lightech.led_g5w.net.entity.Request;
import cn.com.lightech.led_g5w.net.entity.Response;
import cn.com.lightech.led_g5w.net.utils.MacUtil;
import cn.com.lightech.led_g5w.view.console.impl.ControlActivity;
import cn.com.lightech.led_g5w.view.device.IDeviceView;
import cn.com.lightech.led_g5w.view.device.impl.AddDeviceActivity;
import cn.com.lightech.led_g5w.view.spray.SycnDataDialog;
import cn.com.lightech.led_g5w.view.spray.WaveActivity;
import cn.com.u2be.xbase.net.IMulticastListener;
import cn.com.u2be.xbase.net.MulticastManager;

/**
 *
 */
public class SprayListPresenter extends LedPresenter implements Serializable
        //,IMulticastListener, IDataListener
{

    // private static final int WHAT_UDP_SCAN_STOPED = 0xf1;
    private final Context mContext;
    private IDeviceView deviceView;
//    private Timer udpScanTimer;
//    private int scanCount;

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }

    private ArrayList<Device> devices;

//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case WHAT_UDP_SCAN_STOPED:
//                    deviceView.scanLoading(false);
//                    break;
//
//            }
//
//        }
//    };


    public SprayListPresenter(Context context, IDeviceView deviceView) {
        this.deviceView = deviceView;
        this.mContext = context;
        devices = new ArrayList<>(0);

    }


    public void gotoControl(String ip) {
        SycnDataDialog dialog = new SycnDataDialog(mContext, ip);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent intent = new Intent();
                intent.setClass(App.getInstance(), WaveActivity.class);
                mContext.startActivity(intent);
            }
        });
    }


    private void queryGroup(ConnectManager connectManager) {
        Request request = new Request();
        request.setCmdType(CmdType.QueryGroup0x1A);
        connectManager.SendToLed(request);
    }


    public void addDevice(Device device) {
        devices.add(device);
    }


    public void removeDeivce(String host) {
        for (int i = 0; i < devices.size(); i++) {
            Device device = devices.get(i);
            if (host.equals(device.getIp())) {
                devices.remove(i);
                break;
            }
        }


    }


//    public void start() {
//        MulticastManager.getInstance().registListener(this);
//        MulticastManager.getInstance().connect();
//        scanDevice();
//    }
//
//    public void stop() {
//        stopUdpScan();
//        ConnectionsManager.getInstance().unRegister(this);
//        MulticastManager.getInstance().unRegistListener(this);
//    }


    public void deleteDevice() {
        deviceView.gotoDeleteDeviceFragment();
    }

    public void addNewDevice() {
        Intent intent = new Intent();
        intent.setClass(mContext, AddDeviceActivity.class);
        mContext.startActivity(intent);

    }
}
