package cn.com.lightech.led_g5g.presenter;

import android.content.Context;

import java.util.List;

import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.entity.Device;
import cn.com.lightech.led_g5g.entity.DeviceType;
import cn.com.lightech.led_g5g.gloabal.IDataListener;
import cn.com.lightech.led_g5g.net.ConnectManager;
import cn.com.lightech.led_g5g.net.ConnectionsManager;
import cn.com.lightech.led_g5g.net.entity.CmdType;
import cn.com.lightech.led_g5g.net.entity.ConnState;
import cn.com.lightech.led_g5g.net.entity.Request;
import cn.com.lightech.led_g5g.net.entity.Response;
import cn.com.lightech.led_g5g.net.utils.MacUtil;
import cn.com.lightech.led_g5g.utils.ProgressUtil;
import cn.com.lightech.led_g5g.view.device.IDeleteGroupView;

/**
 * Created by 明 on 2016/3/7.
 */
public class DeviceDeleteGroupPresenter extends LedPresenter implements IDataListener {

    private final Context mContext;
    private final IDeleteGroupView deleteDeviceView;

    private int num = 0;

    public DeviceDeleteGroupPresenter(Context context, IDeleteGroupView view) {
        this.mContext = context;
        this.deleteDeviceView = view;
    }

    public void regist() {
        ConnectionsManager.getInstance().registerHigh(this, true);
    }

    public void unRegist() {
        ConnectionsManager.getInstance().unRegister(this);
    }

    public void deleteGroup(List<Device> devices) {

        ProgressUtil.showPogress(mContext, mContext.getString(R.string.device_wifi_save_data_2_led), false);
        num = devices.size();
        for (Device device : devices) {
            createGroup(device);
        }
    }

    private void createGroup(String host, String mac, DeviceType led) {

        Request request = new Request();
        request.setIntVal(0);
        request.setDeviceType(led);
        request.setCmdType(CmdType.SetGroup);
        request.setByteArray(MacUtil.convertMac(mac));
        ConnectionsManager.getInstance().sendaToHost(request, host);
    }

    private void createGroup(Device device) {
        createGroup(device.getIp(), device.getMac(), device.getType());
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

        if (response != null && response.IsOK() && response.getCmdType() == CmdType.SetGroup) {
            num--;
            if (num == 0) {
                ProgressUtil.closeDialog();
                deleteDeviceView.closeView();
            }
        } else if (response != null && response.getCmdType() == CmdType.SetGroup) {
            createGroup(connectManager.getHost(), connectManager.getMac(), DeviceType.Led);
        }

        return false;
    }


}
