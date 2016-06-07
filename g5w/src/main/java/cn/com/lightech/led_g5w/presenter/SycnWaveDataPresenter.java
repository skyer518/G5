package cn.com.lightech.led_g5w.presenter;

import android.content.Context;

import cn.com.lightech.led_g5w.gloabal.DataManager;
import cn.com.lightech.led_g5w.gloabal.IDataListener;
import cn.com.lightech.led_g5w.net.ConnectManager;
import cn.com.lightech.led_g5w.net.ConnectionsManager;
import cn.com.lightech.led_g5w.net.entity.ConnState;
import cn.com.lightech.led_g5w.net.entity.Response;
import cn.com.lightech.led_g5w.net.utils.Logger;
import cn.com.lightech.led_g5w.view.console.ISycnDataView;
import cn.com.lightech.led_g5w.view.spray.SparyProxy;
import cn.com.lightech.led_g5w.view.spray.entity.WaveNode;

/**
 * Created by 明 on 2016/4/18.
 */
public class SycnWaveDataPresenter implements IDataListener {

    private final ISycnDataView sycnDataView;
    private Logger logger = Logger.getLogger(SycnWaveDataPresenter.class);
    private static final int TOTAL_MODE = 1;
    private int modeIndex;
    private String ip;

    public SycnWaveDataPresenter(Context context, ISycnDataView sycnDataView, String ip) {
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
                        + response.getReplyErrorCode());
        return false;

    }

    private int count = 0;

    /**
     * 同步
     */
    public void syncData() {
        // 1、逐个查询模式的曲线时间截
        if (modeIndex > 1) {
            finish();
            return;
        }
        count++;
        SparyProxy.getWaveFromDevice();


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
        WaveNode wn = response.getWaveNode();
        if (wn != null) {
            DataManager.getInstance().saveWaveNode(wn, false);

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
