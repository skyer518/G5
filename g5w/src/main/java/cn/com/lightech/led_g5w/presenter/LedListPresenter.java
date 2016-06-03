package cn.com.lightech.led_g5w.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.lightech.led_g5w.entity.Device;
import cn.com.lightech.led_g5w.entity.DeviceGroup;
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
import cn.com.lightech.led_g5w.view.device.impl.AddDeviceActivity;
import cn.com.lightech.led_g5w.view.device.impl.EditGroupActivity;
import cn.com.u2be.xbase.net.IMulticastListener;
import cn.com.u2be.xbase.net.MulticastManager;

/**
 *
 */
public class LedListPresenter extends LedPresenter
        implements Serializable //, IMulticastListener, IDataListener
{

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


    public LedListPresenter(Context context, IDeviceView deviceView) {
        this.deviceView = deviceView;
        this.mContext = context;
        deviceGroups = new ArrayList<>(0);
        deviceGroups.add(new DeviceGroup());

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




    public void deleteGroup() {
        deviceView.gotoDeleteGroupFragment();
    }

    public void addGroup(List<DeviceGroup> deviceGroups) {
        Intent intent = new Intent();
        intent.setClass(mContext, EditGroupActivity.class);
        intent.putExtra(EditGroupActivity.ARGS_DEFAULT_DEVICE_GROUP, deviceGroups.get(0));
        intent.putExtra(EditGroupActivity.ARGS_NEW_GROUP_NUMBER, deviceGroups.get(deviceGroups.size() - 1).getNumber() + 1);
        mContext.startActivity(intent);
    }


    public void deleteDevice() {
        deviceView.gotoDeleteDeviceFragment();
    }

    public void addNewDevice() {
        Intent intent = new Intent();
        intent.setClass(mContext, AddDeviceActivity.class);
        mContext.startActivity(intent);

    }
}
