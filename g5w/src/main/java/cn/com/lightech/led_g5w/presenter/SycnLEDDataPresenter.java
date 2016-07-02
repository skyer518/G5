package cn.com.lightech.led_g5w.presenter;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import cn.com.lightech.led_g5w.entity.AutoDataNode;
import cn.com.lightech.led_g5w.entity.CurvePoint;
import cn.com.lightech.led_g5w.entity.DataNode;
import cn.com.lightech.led_g5w.entity.LampChannel;
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
                    logger.i("RecvDataFromLED IDFormatError failed modeIndex:%d",
                            this.modeIndex);
                }
            default:
                if (count < 10) {
                    logger.e("cmdType:%s", response.getCmdType().toString());
                    syncData();
                } else {
                    syncNext();
                    return true;
                }
        }

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
            final byte[] packageId = response.getPackageId();
            if (packageId[1] == 0x01) {// AutoTiming
                AutoDataNode node = (AutoDataNode) wn;
                List<CurvePoint> points = node.getPoints();
                List<CurvePoint> dataPoints = DataManager.getInstance().getAutoDataNode().getPoints();
                for (int i = 0; i < points.size(); i++) {
                    CurvePoint point = points.get(i);
                    try {
                        point.setChannel(dataPoints.get(i).getChannel());
                    } catch (IndexOutOfBoundsException e) {
                        point.setChannel(new LampChannel());
                    }
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


}
