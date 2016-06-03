package cn.com.lightech.led_g5w.presenter;

import android.content.Context;
import android.widget.Toast;

import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.gloabal.DataManager;
import cn.com.lightech.led_g5w.gloabal.IDataListener;
import cn.com.lightech.led_g5w.net.ConnectManager;
import cn.com.lightech.led_g5w.net.entity.ConnState;
import cn.com.lightech.led_g5w.entity.LampState;
import cn.com.lightech.led_g5w.net.entity.Response;
import cn.com.lightech.led_g5w.net.utils.Logger;
import cn.com.lightech.led_g5w.net.ConnectionsManager;
import cn.com.lightech.led_g5w.gloabal.LedProxy;
import cn.com.lightech.led_g5w.utils.ProgressUtil;
import cn.com.lightech.led_g5w.view.console.IControlView;
import cn.com.lightech.led_g5w.view.console.impl.UpdateLedDialog;

/**
 * Created by 明 on 2016/3/15.
 */
public class ControlMainPresenter implements IDataListener {

    private final Context mContext;
    private final IControlView controlView;

    private Logger logger = Logger.getLogger(ControlMainPresenter.class);
    private boolean isReady;


    public ControlMainPresenter(Context context, IControlView controlView) {
        this.mContext = context;
        this.controlView = controlView;
        isReady = false;
        ConnectionsManager.getInstance().registerHigh(this, false);
    }

    public void initData() {
        if (!isReady) {
            ProgressUtil.showPogress(mContext, "初始化", false);
            queryLedState();
        }
    }


    public void setLedState(int workMode) {
        if (isReady) {
            LampState state = DataManager.getInstance().getState();
            if (state != null)
                LedProxy.stopPreview();
            LedProxy.setState(workMode, state.lighting, state.moon, state.acclimation);
        }
    }


    public void setLedState(ConnectManager connectManager, int workMode) {
        if (isReady) {
            LampState state = DataManager.getInstance().getState();
            if (state != null)
                LedProxy.stopPreview();
            LedProxy.setState(connectManager, workMode, state.lighting, state.moon, state.acclimation);
        }
    }


    public void registerDataListener() {
        ConnectionsManager.getInstance().registerHigh(this, false);
    }

    public void unRegisterDataListener() {
        ConnectionsManager.getInstance().unRegister(this);
    }


    private void queryLedState() {
        LedProxy.queryState();
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
            case QueryState: //查询完状态
                if (response.IsOK()) {
                    // 查询状态成功后，再获取曲线数据到手机
                    LampState ls = response.getLampState();
                    DataManager.getInstance().setState(ls);
                    this.controlView.switchMode(ls.mode);
                    isReady = true;
                    ProgressUtil.closeDialog();
                } else {
                    queryLedState();
                }
                break;

            case SetState:
                if (!response.IsOK()) {
//                    Toast.makeText(mContext, mContext.getString(R.string.set_state_success), Toast.LENGTH_LONG)
//                            .show();
                    setLedState(connectManager, DataManager.getInstance().getState().mode);
                }
                break;

            case SendDataToLED:
                break;

            default:
                break;
        }
        Logger.getLogger().d(
                response.getCmdType().toString() + "   "
                        + response.getReplyCode());
        return true;
    }


}
