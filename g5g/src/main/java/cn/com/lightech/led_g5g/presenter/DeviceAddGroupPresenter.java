package cn.com.lightech.led_g5g.presenter;

import android.content.Context;

import cn.com.lightech.led_g5g.entity.Device;
import cn.com.lightech.led_g5g.entity.DeviceGroup;
import cn.com.lightech.led_g5g.net.ConnectionsManager;
import cn.com.lightech.led_g5g.net.entity.CmdType;
import cn.com.lightech.led_g5g.net.entity.Request;
import cn.com.lightech.led_g5g.net.utils.MacUtil;
import cn.com.lightech.led_g5g.view.device.IGroupView;

/**
 * Created by 明 on 2016/3/28.
 */
public class DeviceAddGroupPresenter extends LedPresenter {


    private final Context mContext;
    private final IGroupView mGroupView;


    public DeviceAddGroupPresenter(Context context, IGroupView groupView) {
        this.mContext = context;
        this.mGroupView = groupView;
    }


    public void saveGroup(DeviceGroup group) {
        for (Device device : group.getDevices()) {
            Request req = new Request();
            req.setIntVal(group.getNumber());
            req.setByteArray(MacUtil.convertMac(device.getMac()));
            req.setCmdType(CmdType.SetGroup);
            req.setDeviceType(device.getType());
            ConnectionsManager.getInstance().sendaToHost(req, device.getIp());
        }

    }


}
