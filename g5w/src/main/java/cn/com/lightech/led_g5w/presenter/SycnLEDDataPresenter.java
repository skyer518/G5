package cn.com.lightech.led_g5w.presenter;

import android.content.Context;

import java.util.List;

import cn.com.lightech.led_g5w.entity.AutoDataNode;
import cn.com.lightech.led_g5w.entity.CurvePoint;
import cn.com.lightech.led_g5w.entity.DataNode;
import cn.com.lightech.led_g5w.entity.PackageId;
import cn.com.lightech.led_g5w.gloabal.DataManager;
import cn.com.lightech.led_g5w.gloabal.IDataListener;
import cn.com.lightech.led_g5w.gloabal.LedProxy;
import cn.com.lightech.led_g5w.net.ConnectManager;
import cn.com.lightech.led_g5w.net.ConnectionsManager;
import cn.com.lightech.led_g5w.net.entity.ConnState;
import cn.com.lightech.led_g5w.net.entity.Response;
import cn.com.lightech.led_g5w.net.utils.Logger;
import cn.com.lightech.led_g5w.view.console.ISycnDataView;

/**
 * Created by 明 on 2016/4/18.
 */
public class SycnLEDDataPresenter implements IDataListener {

    private final ISycnDataView sycnDataView;
    private Logger logger = Logger.getLogger(SycnLEDDataPresenter.class);
    private static final int TOTAL_MODE = 5;
    private int modeIndex;
    private String ip;
    private byte[] pkgId;

    public SycnLEDDataPresenter(Context context, ISycnDataView sycnDataView, String ip) {
        this.ip = ip;
        this.sycnDataView = sycnDataView;
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
        if (response == null)
            return false;
        switch (response.getCmdType()) {

            case RecvDataFromLED:
                if (response.IsOK()) {
                    final byte[] packageId = response.getPackageId();

                    if (packageId != null && packageId.length == 2
                            && pkgId != null && pkgId.length == 2
                            && packageId[0] == pkgId[0] && packageId[1] == pkgId[1]) {
                        recvData(response);
                        syncNext();
                        break;
                    }
                }
            case ValidateSumFailed:
            case IDFormatError: // 出现错误就跳过继续下一个吧
                if (!response.IsOK()) {
                    logger.e("RecvDataFromLED IDFormatError failed modeIndex:%d",
                            this.modeIndex);
                }
            default:
                if (count < 3)
                    syncData();
                else
                    syncNext();
                return true;
        }
        Logger.getLogger().e(
                response.getCmdType().toString() + "   "
                        + response.getReplyErrorCode());
//        Logger.getLogger().e(
//                response.getCmdType().toString() + "   "
//                        + response.getReplyErrorCode() + "  \n byte data:" + Arrays.toString(response.getByteArray()));
        return false;

    }

    private int count = 0;

    /**
     * 同步
     */
    public void syncData() {
        // 1、逐个查询模式的曲线时间截
        this.pkgId = PackageId.getModePackageId(this.modeIndex);
        if (pkgId == null) {
            finish();
            return;
        }
        count++;
        LedProxy.recvDataFromLED(pkgId);
//        SparyProxy.validateData(pkgId);


        String txt = String.format("current: %d  ; total: %d  ;",
                this.modeIndex, TOTAL_MODE);
        logger.e(txt);

    }

    /**
     * 同步下一个模式
     */
    private boolean syncNext() {
        count = 0;
        if (updateIndex()) {
            syncData();// 继续同步下一个
            return true;
        } else {
            finish();
            ConnectionsManager.getInstance().unRegister(this);
            return false;
        }
    }


    /* 收到Led的模式曲线数据，更新到本地 */
    private void recvData(Response response) {
        if (response == null)
            return;
        DataNode wn = response.getDataNode();
        if (wn != null) {
            if (response.getPackageId()[1] == 0x01) {
                AutoDataNode node = (AutoDataNode) wn;
                List<CurvePoint> points = node.getPoints();

                for (int i = 0; i < points.size(); i++) {
                    CurvePoint point = points.get(i);
                    point.setChannel(DataManager.getInstance().getAutoDataNode().getPoints().get(i).getChannel());
                }
                DataManager.getInstance().saveDataNode(node, false);
            } else {
                DataManager.getInstance().saveDataNode(wn, false);
            }
        }
    }

    /* 更新模式索引 */
    private boolean updateIndex() {
        if (this.modeIndex < TOTAL_MODE) {
            this.modeIndex++;
            return true;
        } else {
            return false;
        }

    }


    private void finish() {
        String txt = String.format("stop ; current: %d  ; total: %d  ;",
                this.modeIndex, TOTAL_MODE);
        logger.e(txt);
        sycnDataView.stopSycn();
    }

    public void register() {
        ConnectionsManager.getInstance().registerHigh(this, ip, false);
    }

    public void unRegister() {
        ConnectionsManager.getInstance().unRegister(this);
    }


    //    /**
//     * 处理验证包返回的数据
//     */
//    private void proc(Response response) {
//        if (response == null) {
//            syncNext();// 继续同步下一个
//            return;
//        }
//
//        byte[] pkgId = response.getPackageId();
//        long unixTime = response.getUnixTime();
//
//        DataNode appNode = DataManager.getInstance().getDataNode(
//                Mode.valueOf(modeIndex));
//
//        if (appNode == null) {
//            // APP数据为空
//            if (unixTime > 0)
//                SparyProxy.recvDataFromLED(pkgId);// LED的数据较新
//            else
//                syncNext();// APP和LED都没有数据，下一个
//            return;
//        }
//
//        // 对比包ID是否一致，如果不是，说明返回数据有误，直接跳过
//        if (!Arrays.equals(pkgId, appNode.getID())) {
//            // 继续同步下一个
//            syncNext();
//            return;
//        }
//
//        // 对比时间截，哪个新用哪个。如果app的新，则获取，旧则发送.如果相等，或者都是0，则跳过
//        if (unixTime == appNode.getUnixTime()) {
//            // 继续同步下一个
//            syncNext();
//            return;
////        } else if (unixTime < appNode.getUnixTime()) {
////            // APP的数据较新
////            SparyProxy.sendToLed(appNode);
//        } else {
//            // LED的数据较新
//            SparyProxy.recvDataFromLED(pkgId);
//        }
//
//    }
}
