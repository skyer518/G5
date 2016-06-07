package cn.com.lightech.led_g5w.gloabal;

import cn.com.lightech.led_g5w.entity.DataNode;
import cn.com.lightech.led_g5w.entity.UpdateNode;
import cn.com.lightech.led_g5w.net.ConnectManager;
import cn.com.lightech.led_g5w.net.entity.CmdType;
import cn.com.lightech.led_g5w.entity.LampChannel;
import cn.com.lightech.led_g5w.entity.LampState;
import cn.com.lightech.led_g5w.net.entity.Request;
import cn.com.lightech.led_g5w.net.ConnectionsManager;

public class LedProxy {

    /**
     * 预览即时曲线
     */
    public static boolean preview(LampChannel lc) {
        if (lc == null)
            return false;
        Request req = new Request();
        req.setCmdType(CmdType.PreviewMode);
        LampState ls = new LampState();
        ls.white = (byte) lc.getWhite();
        ls.red = (byte) lc.getRed();
        ls.green = (byte) lc.getGreen();
        ls.blue = (byte) lc.getBlue();
        ls.purple = (byte) lc.getPurple();
        req.setLampState(ls);
        ConnectionsManager.getInstance().sendToLed(req, false);
        return true;
    }

    /**
     * 停止预览
     */
    public static void stopPreview() {
        Request request = new Request();
        request.setCmdType(CmdType.StopPreview);
        ConnectionsManager.getInstance().sendToLed(request, false);
    }

    /**
     * 预览曲线
     */
    public static void previewCurve() {
        Request request = new Request();
        request.setIntVal(1);// 正常速度预览
        request.setCmdType(CmdType.PreViewCurve);
        ConnectionsManager.getInstance().sendToLed(request, false);
    }

    /**
     * 设置状态
     */
    public static void setState(int workMode, boolean flash, boolean moon, boolean acclimation) {
        Request request = new Request();
        LampState ls = new LampState();
        ls.On = true;
        ls.mode = (byte) workMode;
        ls.lighting = flash;
        ls.moon = moon;
        ls.acclimation = acclimation;
        request.setLampState(ls);
        request.setCmdType(CmdType.SetState);
        ConnectionsManager.getInstance().sendToLed(request, false);
    }

    /**
     * 设置状态
     */
    public static void setState(ConnectManager connectManager, int workMode, boolean flash, boolean moon, boolean acclimation) {
        Request request = new Request();
        LampState ls = new LampState();
        ls.On = true;
        ls.mode = (byte) workMode;
        ls.lighting = flash;
        ls.moon = moon;
        ls.acclimation = acclimation;
        request.setLampState(ls);
        request.setCmdType(CmdType.SetState);
        connectManager.SendToLed(request);
    }

    /**
     * 刷新状态
     */
    public static void queryState() {

        Request request = new Request();
        request.setCmdType(CmdType.QueryState);
        ConnectionsManager.getInstance().sendToLed(request, false);
    }

    /**
     * 发送数据到灯
     */
    public static void sendToLed(DataNode dataNode) {
        Request request = new Request();
        request.setCmdType(CmdType.SendDataToLED);
        request.setData(dataNode);
        ConnectionsManager.getInstance().sendToLed(request, false);
    }

    public static void sendToLed(UpdateNode updateNode) {
        Request request = new Request();
        request.setCmdType(CmdType.SendDataToLED);
        request.setData(updateNode);
        ConnectionsManager.getInstance().sendToLed(request, false);
    }

    /**
     * 得到灯的曲线数据
     */
    public static void recvDataFromLED(byte[] pkgId) {
        Request req = new Request();
        req.setByteArray(pkgId);
        req.setCmdType(CmdType.RecvDataFromLED);
        ConnectionsManager.getInstance().sendToLed(req, false);
    }

    /**
     * 验证曲线有效性
     */
    public static void validateData(byte[] pkgId) {
        Request req = new Request();
        req.setByteArray(pkgId);
        req.setCmdType(CmdType.ValidateData);
        ConnectionsManager.getInstance().sendToLed(req, false);
    }

    /**
     * 查询组号
     */
    public static void queryGroup(boolean allSend) {
        Request req = new Request();
        req.setCmdType(CmdType.QueryGroup0xF1);
        byte[] data = CmdBuilder.Build(req);
        ConnectionsManager.getInstance().sendToLed(data, allSend);
    }

    /**
     * 得到灯的曲线数据
     */
    public static void queryData(byte[] pckId) {
        Request request = new Request();
        request.setByteArray(pckId);
        request.setCmdType(CmdType.RecvDataFromLED);
        ConnectionsManager.getInstance().sendToLed(request, false);

    }

    /**
     * 同步时间
     */
    public static void sycnTime() {
        Request request = new Request();
        request.setCmdType(CmdType.SyncTime);
        ConnectionsManager.getInstance().sendToLed(request, false);

    }


    public static void getVersion() {
        Request request = new Request();
        request.setCmdType(CmdType.GetVersion);
        ConnectionsManager.getInstance().sendToLed(request, false);
    }


}
