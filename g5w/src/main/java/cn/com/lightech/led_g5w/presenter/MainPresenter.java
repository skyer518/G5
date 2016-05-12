package cn.com.lightech.led_g5w.presenter;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import cn.com.lightech.led_g5w.entity.DeviceGroup;
import cn.com.lightech.led_g5w.view.device.IMainDeviceView;
import cn.com.lightech.led_g5w.view.device.impl.AddDeviceActivity;
import cn.com.lightech.led_g5w.view.device.impl.EditGroupActivity;
import cn.com.lightech.led_g5w.view.device.impl.HelpActivity;
import cn.com.u2be.xbase.net.IUdp;
import cn.com.u2be.xbase.net.UDPManager;

/**
 * Created by 明 on 2016/4/11.
 */
public class MainPresenter {

    private final Context mContext;
    private IMainDeviceView mainDeviceView;

    public MainPresenter(Context context, IMainDeviceView mainDeviceView) {
        this.mainDeviceView = mainDeviceView;
        this.mContext = context;
    }


    public void addGroup(List<DeviceGroup> deviceGroups) {
        Intent intent = new Intent();
        intent.setClass(mContext, EditGroupActivity.class);
        intent.putExtra(EditGroupActivity.ARGS_DEFAULT_DEVICE_GROUP, deviceGroups.get(0));
        intent.putExtra(EditGroupActivity.ARGS_NEW_GROUP_NUMBER, deviceGroups.get(deviceGroups.size() - 1).getNumber() + 1);
        mContext.startActivity(intent);
    }

    public void deleteDevice() {
        mainDeviceView.gotoDeleteDeviceFragment();
    }

    public void addNewDevice() {
        Intent intent = new Intent();
        intent.setClass(mContext, AddDeviceActivity.class);
        mContext.startActivity(intent);

    }


    public void deleteGroup() {
        mainDeviceView.gotoDeleteGroupFragment();
    }

    public void showHelp() {
        Intent intent = new Intent();
        intent.setClass(mContext, HelpActivity.class);
        mContext.startActivity(intent);
    }
}
