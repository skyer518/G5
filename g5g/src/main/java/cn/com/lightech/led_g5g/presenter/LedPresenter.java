package cn.com.lightech.led_g5g.presenter;

import java.util.List;

import cn.com.lightech.led_g5g.entity.Device;
import cn.com.lightech.led_g5g.entity.DeviceGroup;
import cn.com.lightech.led_g5g.net.ConnectionsManager;
import cn.com.lightech.led_g5g.net.entity.CmdType;
import cn.com.lightech.led_g5g.net.entity.Request;

/**
 * Created by 明 on 2016/4/28.
 */
public abstract class LedPresenter {
    /**
     * 闪灯
     */

    public void blinkLed(Device device) {
        Request request = new Request();
        request.setCmdType(CmdType.ConfirmLed);
        request.setIntVal(device.getGroupNumber());
        ConnectionsManager.getInstance().sendaToHost(request, device.getIp());
    }

    /**
     * 闪灯
     */
    public void blinkLed(DeviceGroup group) {
        List<Device> devices = group.getDevices();
        for (int i = 0; i < devices.size(); i++) {
            blinkLed(devices.get(i));
        }
    }
}
