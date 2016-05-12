package cn.com.lightech.led_g5g.presenter;

import android.content.Context;

import java.util.List;

import cn.com.lightech.led_g5g.entity.data.CurveData;
import cn.com.lightech.led_g5g.entity.CurvePoint;
import cn.com.lightech.led_g5g.entity.DataNode;
import cn.com.lightech.led_g5g.entity.PackageId;
import cn.com.lightech.led_g5g.gloabal.DataManager;
import cn.com.lightech.led_g5g.gloabal.IDataListener;
import cn.com.lightech.led_g5g.gloabal.LedProxy;
import cn.com.lightech.led_g5g.net.ConnectManager;
import cn.com.lightech.led_g5g.net.ConnectionsManager;
import cn.com.lightech.led_g5g.net.entity.ConnState;
import cn.com.lightech.led_g5g.net.entity.Response;
import cn.com.lightech.led_g5g.net.utils.Logger;
import cn.com.lightech.led_g5g.view.console.ISycnDataView;

/**
 * Created by 明 on 2016/4/18.
 */
public class SycnDataPresenter implements IDataListener {

    private final ISycnDataView sycnDataView;
    private Logger logger = Logger.getLogger(SycnDataPresenter.class);
    private static final int TOTAL_MODE = 14;
    private int modeIndex;
    private String ip;

    public SycnDataPresenter(Context context, ISycnDataView sycnDataView, String ip) {
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
                    recvData(response);
                    syncNext();
                    break;
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
        Logger.getLogger().d(
                response.getCmdType().toString() + "   "
                        + response.getReplyCode());
        return false;

    }

    private int count = 0;

    /**
     * 同步
     */
    public void syncData() {
        // 1、逐个查询模式的曲线时间截
        byte[] pkgId = PackageId.getPackageIdByIndex(this.modeIndex);
        if (pkgId == null) {
            finish();
            return;
        }
        count++;
        LedProxy.recvDataFromLED(pkgId);
//        LedProxy.validateData(pkgId);


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
            DataManager.getInstance().saveDataNode(wn, false);
//            if (response.getPackageId()[1] == 0x01) {
//                CurveData node = (CurveData) wn;
//                List<CurvePoint> points = node.getPoints();
//
//                for (int i = 0; i < points.size(); i++) {
//                    CurvePoint point = points.get(i);
//                    point.setChannel(DataManager.getInstance().getCurveData().getPoints().get(i).getChannel());
//                }
//                DataManager.getInstance().saveDataNode(node, false);
//            } else {
//            }
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
//                DataType.valueOf(modeIndex));
//
//        if (appNode == null) {
//            // APP数据为空
//            if (unixTime > 0)
//                LedProxy.recvDataFromLED(pkgId);// LED的数据较新
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
////            LedProxy.sendToLed(appNode);
//        } else {
//            // LED的数据较新
//            LedProxy.recvDataFromLED(pkgId);
//        }
//
//    }
}
