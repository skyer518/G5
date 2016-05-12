package cn.com.lightech.led_g5g.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import cn.com.lightech.led_g5g.entity.Device;
import cn.com.lightech.led_g5g.entity.DeviceGroup;
import cn.com.lightech.led_g5g.gloabal.App;
import cn.com.lightech.led_g5g.gloabal.IDataListener;
import cn.com.lightech.led_g5g.gloabal.LedProxy;
import cn.com.lightech.led_g5g.net.ConnectManager;
import cn.com.lightech.led_g5g.net.ConnectionsManager;
import cn.com.lightech.led_g5g.net.entity.CmdType;
import cn.com.lightech.led_g5g.net.entity.ConnState;
import cn.com.lightech.led_g5g.net.entity.Response;
import cn.com.lightech.led_g5g.utils.UIHelper;
import cn.com.lightech.led_g5g.view.console.IConnectView;
import cn.com.lightech.led_g5g.view.console.impl.ControlActivity;
import cn.com.lightech.led_g5g.view.console.impl.SycnDataDialog;

/**
 * Created by 明 on 2016/4/19.
 */
public class DeviceConnectLedPresenter extends LedPresenter implements IDataListener {


    private DeviceGroup group;
    private IConnectView connectView;

    private Context mContext;


    public DeviceConnectLedPresenter(Context mContext, IConnectView connectView, DeviceGroup group) {
        this.connectView = connectView;
        this.mContext = mContext;
        this.group = group;
    }


    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onConnectStateChanged(ConnState connState, ConnectManager connectManager) {
    }

    @Override
    public boolean onReceive(Response response, ConnectManager connectManager) {
        if (response == null) {
            return true;
        }
        if (response.IsOK() && response.getCmdType() == CmdType.CheckReady) {
            String host = connectManager.getHost();
            connectView.updateItem(host);
            LedProxy.sycnTime();
        } else if (response.getCmdType() == CmdType.CheckReady) {
            connectManager.checkLedReady();
        }
        return true;
    }


    public void registDataListener() {
        ConnectionsManager.getInstance().registerHigh(this, false);
    }

    public void unRegistDataListener() {
        ConnectionsManager.getInstance().unRegister(this);
    }

    public void priorityConnect() {
        List<String> deviceIp = getDeviceIp();
        for (String ip : deviceIp) {
            ConnectionsManager.getInstance().priorityConnect(ip, 8080);
        }
        ConnectionsManager.getInstance().checkLedReady(false);
    }


    public void gotoControl(int mainDevice) {
        String ip = group.getDevices().get(mainDevice).getIp();
        SycnDataDialog dialog = new SycnDataDialog(mContext, group, ip);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent intent = new Intent();
                intent.setClass(App.getInstance(), ControlActivity.class);
                intent.putExtra(ControlActivity.ARGS_DEVICE_GROUP, group);
                mContext.startActivity(intent);
            }
        });

        UIHelper.getInstance().closeConnectDialog();
    }


    private List<String> getDeviceIp() {
        List<String> addresses = new ArrayList<>(0);
        for (Device device : group.getDevices()) {
            addresses.add(device.getIp());
        }
        return addresses;
    }
}
