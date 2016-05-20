package cn.com.lightech.led_g5w.view.spray;

import cn.com.lightech.led_g5w.entity.PackageId;
import cn.com.lightech.led_g5w.net.ConnectionsManager;
import cn.com.lightech.led_g5w.net.entity.CmdType;
import cn.com.lightech.led_g5w.net.entity.Request;
import cn.com.lightech.led_g5w.view.spray.entity.WaveNode;

/**
 * Created by alek on 2016/5/20.
 */
public class SparyProxy {
    public static void sendWaveToDevice(WaveNode node) {
        Request request = new Request();
        request.setCmdType(CmdType.SendDataToLED);
        request.setData(node);
        ConnectionsManager.getInstance().sendToLed(request, false);
    }

    public static void getWaveFromDevice() {
        Request req = new Request();
        req.setByteArray(PackageId.Wave);
        req.setCmdType(CmdType.RecvDataFromLED);
        ConnectionsManager.getInstance().sendToLed(req, false);
    }


}
