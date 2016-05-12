package cn.com.lightech.led_g5w.presenter;

import android.content.Context;

import java.util.List;

import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.entity.Device;
import cn.com.lightech.led_g5w.gloabal.IDataListener;
import cn.com.lightech.led_g5w.net.ConnectManager;
import cn.com.lightech.led_g5w.net.ConnectionsManager;
import cn.com.lightech.led_g5w.net.entity.CmdType;
import cn.com.lightech.led_g5w.net.entity.ConnState;
import cn.com.lightech.led_g5w.net.entity.Request;
import cn.com.lightech.led_g5w.net.entity.Response;
import cn.com.lightech.led_g5w.net.utils.MacUtil;
import cn.com.lightech.led_g5w.utils.ProgressUtil;
import cn.com.lightech.led_g5w.view.device.IDeleteGroupView;

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
            setGroup(device.getIp(), device.getMac());
        }
    }

    private void setGroup(String host, String mac) {
        Request request = new Request();
        request.setIntVal(0);
        request.setCmdType(CmdType.SetGroup);
        request.setByteArray(MacUtil.convertMac(mac));
        ConnectionsManager.getInstance().sendaToHost(request, host);
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
            setGroup(connectManager.getHost(),connectManager.getMac());
        }

        return false;
    }
}
