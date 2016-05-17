package cn.com.lightech.led_g5w.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.lightech.led_g5w.entity.Device;
import cn.com.lightech.led_g5w.entity.DeviceType;
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
import cn.com.lightech.led_g5w.utils.UIHelper;
import cn.com.lightech.led_g5w.view.device.IDeviceView;
import cn.com.lightech.led_g5w.view.device.impl.EditGroupActivity;
import cn.com.u2be.xbase.net.IMulticastListener;
import cn.com.u2be.xbase.net.MulticastManager;

/**
 *
 */
public class SprayListPresenter extends LedPresenter implements Serializable, IMulticastListener, IDataListener {

    private static final int WHAT_UDP_SCAN_STOPED = 0xf1;
    private final Context mContext;
    private IDeviceView deviceView;
    private Timer udpScanTimer;
    private int scanCount;

    public ArrayList<Device> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }

    private ArrayList<Device> devices;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_UDP_SCAN_STOPED:
                    deviceView.scanLoading(false);
                    break;

            }

        }
    };


    public SprayListPresenter(Context context, IDeviceView deviceView) {
        this.deviceView = deviceView;
        this.mContext = context;
        devices = new ArrayList<>(0);

    }


    public void scanDevice() {
        scanCount = 0;
        if (udpScanTimer == null) {
            deviceView.scanLoading(true);
            Request request = new Request();
            request.setCmdType(CmdType.FindLed);
            final byte[] data = CmdBuilder.Build(request);
            udpScanTimer = new Timer();
            udpScanTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (scanCount > 4) {
                        stopUdpScan();
                    }
                    LedProxy.queryGroup(true);
                    MulticastManager.getInstance().send(data);
                    scanCount++;
                }
            }, 1, 3000);
        }


        initDevices();

    }

    public void stopUdpScan() {
        if (udpScanTimer != null)
            udpScanTimer.cancel();
        scanCount = 0;
        udpScanTimer = null;
        mHandler.sendEmptyMessage(WHAT_UDP_SCAN_STOPED);
    }

    public void initDevices() {
        devices.clear();
        deviceView.showDevices();
    }


    public void gotoControl(String ip) {
        // UIHelper.getInstance().showConnectDialog(mContext, false, group);

    }


    @Override
    public void onStartReciverThread() {

    }

    @Override
    public void onStopReciverThread() {

    }

    @Override
    public boolean onReceive(byte[] data, String host) {
        conect(host);
        return true;
    }

    private void conect(String host) {
        Log.i("" + SprayListPresenter.class.getName(), host);
        ConnectionsManager.getInstance().connect(host, Constants.LED_PORT);
        ConnectionsManager.getInstance().registerHigh(this, true);
    }


    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onConnectStateChanged(ConnState connState, ConnectManager connectManager) {

        if (connState == ConnState.DisConnected) {

            System.err.println(connectManager.getHost() + " is colsed");
            removeDeivce(connectManager.getHost());
            deviceView.showDevices();

        } else if (connState == ConnState.Connected) {
            queryGroup(connectManager);
        }
    }

    private void queryGroup(ConnectManager connectManager) {
        Request request = new Request();
        request.setCmdType(CmdType.QueryGroup);
        connectManager.SendToLed(request);
    }


    @Override
    public boolean onReceive(Response response, ConnectManager connectManager) {
        if (response.IsOK() && response.getCmdType() == CmdType.CheckReady) {
            queryGroup(connectManager);
        } else if (response.getCmdType() == CmdType.QueryGroup) {
            if (response.IsOK()) {
                int groupNum = response.getGroupNum();
                DeviceType deviceType = response.getDeviceType();
                String host = connectManager.getHost();

                Device device = new Device(groupNum, 0, host);
                device.setType(deviceType);
                final String mac = MacUtil.convertMac(response.getMac());
                device.setMac(mac);
                connectManager.setMac(mac);
                addDevice(device);
                deviceView.showDevices();
            }
        }
        return true;
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


    public void start() {
        MulticastManager.getInstance().registListener(this);
        MulticastManager.getInstance().connect();
        scanDevice();
    }

    public void stop() {
        stopUdpScan();
        ConnectionsManager.getInstance().unRegister(this);
        MulticastManager.getInstance().unRegistListener(this);
    }


    public void deleteDevice() {
        deviceView.gotoDeleteDeviceFragment();
    }

}
