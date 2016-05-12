package cn.com.lightech.led_g5w.presenter;

import android.content.Context;

import cn.com.lightech.led_g5w.entity.Device;
import cn.com.lightech.led_g5w.entity.DeviceGroup;
import cn.com.lightech.led_g5w.net.ConnectionsManager;
import cn.com.lightech.led_g5w.net.entity.CmdType;
import cn.com.lightech.led_g5w.net.entity.Request;
import cn.com.lightech.led_g5w.net.utils.MacUtil;
import cn.com.lightech.led_g5w.view.device.IGroupView;

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
            ConnectionsManager.getInstance().sendaToHost(req, device.getIp());
        }

    }


}
