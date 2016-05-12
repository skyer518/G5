package cn.com.lightech.led_g5g.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.lightech.led_g5g.entity.Device;
import cn.com.lightech.led_g5g.entity.DeviceGroup;
import cn.com.lightech.led_g5g.entity.DeviceType;
import cn.com.lightech.led_g5g.gloabal.CmdBuilder;
import cn.com.lightech.led_g5g.gloabal.Constants;
import cn.com.lightech.led_g5g.gloabal.IDataListener;
import cn.com.lightech.led_g5g.gloabal.LedProxy;
import cn.com.lightech.led_g5g.net.ConnectManager;
import cn.com.lightech.led_g5g.net.ConnectionsManager;
import cn.com.lightech.led_g5g.net.entity.CmdType;
import cn.com.lightech.led_g5g.net.entity.ConnState;
import cn.com.lightech.led_g5g.net.entity.Request;
import cn.com.lightech.led_g5g.net.entity.Response;
import cn.com.lightech.led_g5g.net.utils.MacUtil;
import cn.com.lightech.led_g5g.utils.UIHelper;
import cn.com.lightech.led_g5g.view.device.IDeviceView;
import cn.com.u2be.xbase.net.IMulticastListener;
import cn.com.u2be.xbase.net.MulticastManager;

/**
 *
 */
public class LedListPresenter extends LedPresenter implements Serializable, IMulticastListener, IDataListener {

    private static final int WHAT_UDP_SCAN_STOPED = 0xf1;
    private final Context mContext;
    private IDeviceView deviceView;
    private Timer udpScanTimer;
    private int scanCount;
    private Comparator<? super DeviceGroup> comparator = new Comparator<DeviceGroup>() {
        @Override
        public int compare(DeviceGroup lhs, DeviceGroup rhs) {
            return lhs.getNumber() - rhs.getNumber();
        }
    };

    public ArrayList<DeviceGroup> getDeviceGroups() {
        return deviceGroups;
    }

    public void setDeviceGroups(ArrayList<DeviceGroup> deviceGroups) {
        this.deviceGroups = deviceGroups;
    }

    private ArrayList<DeviceGroup> deviceGroups;

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


    public LedListPresenter(Context context, IDeviceView deviceView) {
        this.deviceView = deviceView;
        this.mContext = context;
        deviceGroups = new ArrayList<>(0);
        deviceGroups.add(new DeviceGroup());

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


        initdeviceGroups();

    }

    public void stopUdpScan() {
        if (udpScanTimer != null)
            udpScanTimer.cancel();
        scanCount = 0;
        udpScanTimer = null;
        mHandler.sendEmptyMessage(WHAT_UDP_SCAN_STOPED);
    }

    public void initdeviceGroups() {
        DeviceGroup defaultGroup = deviceGroups.remove(0);
        deviceGroups.clear();
        defaultGroup.getDevices().clear();
        deviceGroups.add(defaultGroup);
        deviceView.showDevices();
    }


    public void gotoControl(int groupNo) {
        DeviceGroup group = null;
        for (DeviceGroup deviceGroup : deviceGroups) {
            if (deviceGroup.getNumber() == groupNo)
                group = deviceGroup;
        }
        if (group != null)
            UIHelper.getInstance().showConnectDialog(mContext, false, group);

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
        Log.i("" + LedListPresenter.class.getName(), host);
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
        int groupNo = device.getGroupNumber();
        DeviceGroup deviceGroup = getDeviceGroup(groupNo);
        if (deviceGroup == null) {
            deviceGroup = new DeviceGroup(groupNo);
            deviceGroups.add(deviceGroup);
            Collections.sort(deviceGroups, comparator);
        }
        deviceGroup.addDevice(device);
    }


    public void removeDeivce(String host) {
        for (int j = 0; j < deviceGroups.size(); j++) {
            DeviceGroup group = deviceGroups.get(j);
            for (int i = 0; i < group.getDevices().size(); i++) {
                Device device = group.getDevices().get(i);
                if (host.equals(device.getIp())) {
                    group.getDevices().remove(i);
                    if (deviceGroups.size() > 1 && group.getDevices().size() == 0) {
                        deviceGroups.remove(group);
                        Collections.sort(deviceGroups, comparator);
                    }
                    break;
                }
            }

        }
    }

    private DeviceGroup getDeviceGroup(int groupNo) {
        for (DeviceGroup group : deviceGroups) {
            if (groupNo == group.getNumber()) {
                return group;
            }
        }
        return null;
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


}
