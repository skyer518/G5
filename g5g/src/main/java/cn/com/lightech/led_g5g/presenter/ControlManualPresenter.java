package cn.com.lightech.led_g5g.presenter;

import android.content.Context;

import cn.com.lightech.led_g5g.entity.DataNode;
import cn.com.lightech.led_g5g.entity.data.ManualData;
import cn.com.lightech.led_g5g.gloabal.DataManager;
import cn.com.lightech.led_g5g.gloabal.IDataListener;
import cn.com.lightech.led_g5g.net.ConnectManager;
import cn.com.lightech.led_g5g.net.entity.ChanelType;
import cn.com.lightech.led_g5g.net.entity.ConnState;
import cn.com.lightech.led_g5g.entity.LampChannel;
import cn.com.lightech.led_g5g.net.entity.ReplyErrorCode;
import cn.com.lightech.led_g5g.net.entity.Response;
import cn.com.lightech.led_g5g.net.utils.Logger;
import cn.com.lightech.led_g5g.net.ConnectionsManager;
import cn.com.lightech.led_g5g.gloabal.LedProxy;
import cn.com.lightech.led_g5g.view.console.IManualView;

/**
 * Created by 明 on 2016/3/15.
 */
public class ControlManualPresenter implements IDataListener {

    private final Context mContext;
    private final IManualView manualView;


    public ControlManualPresenter(Context context, IManualView manualView) {
        this.mContext = context;
        this.manualView = manualView;
    }


    public void stopPreview() {
        LedProxy.stopPreview();
    }


    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public void onConnectStateChanged(ConnState connState, ConnectManager connectManager) {
        switch (connState) {
            case NoWifi:
            case DisConnected:
            case ParamError:
            case Connected:
                //loadWifiState();
                break;
        }
    }

    @Override
    public boolean onReceive(Response response, ConnectManager connectManager) {
        if (response == null)
            return true;
        switch (response.getCmdType()) {
            case SendDataToLED:
                if (response.getReplyCode() == ReplyErrorCode.OK) {
                    DataNode dataNode = response.getDataNode();
                    if (dataNode instanceof ManualData) {

                    }
                }
                break;
            case StopPreview:
                if (response.getReplyCode() == ReplyErrorCode.OK) {
                }
                break;
            case PreviewMode:
                break;
            default:
                break;
        }

        Logger.getLogger().d(
                response.getCmdType().toString() + "   "
                        + response.getReplyCode());
        return true;
    }

    public void registerDataListener() {
        ConnectionsManager.getInstance().registerHigh(this, false);
    }

    public void unRegisterDataListener() {
        ConnectionsManager.getInstance().unRegister(this);
    }

    public void loadManual() {
        LampChannel channel = DataManager.getInstance().getManualData().getChannel();
        this.manualView.showManual(channel);
    }

    public void saveChannel(ChanelType type, int progress) {
        ManualData manualData = DataManager.getInstance().getManualData();
        manualData.getChannel().setData(type, progress);
        DataManager.getInstance().saveManualDataNode(manualData, true);
        LedProxy.sendToLed(manualData);
    }

    public void preview(ChanelType type, int progress) {
        ManualData manualData = DataManager.getInstance().getManualData();
        manualData.getChannel().setData(type, progress);
        LedProxy.preview(DataManager.getInstance().getManualData().getChannel());
    }


    public void saveData() {
        LedProxy.sendToLed(DataManager.getInstance().getManualData());
    }
}
