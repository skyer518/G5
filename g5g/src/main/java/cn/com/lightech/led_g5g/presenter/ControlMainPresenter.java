package cn.com.lightech.led_g5g.presenter;

import android.content.Context;
import android.widget.Toast;

import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.gloabal.DataManager;
import cn.com.lightech.led_g5g.gloabal.IDataListener;
import cn.com.lightech.led_g5g.net.ConnectManager;
import cn.com.lightech.led_g5g.net.entity.ConnState;
import cn.com.lightech.led_g5g.entity.LampState;
import cn.com.lightech.led_g5g.net.entity.Response;
import cn.com.lightech.led_g5g.net.utils.Logger;
import cn.com.lightech.led_g5g.net.ConnectionsManager;
import cn.com.lightech.led_g5g.gloabal.LedProxy;
import cn.com.lightech.led_g5g.utils.ProgressUtil;
import cn.com.lightech.led_g5g.view.console.IControlView;
import cn.com.lightech.led_g5g.view.console.impl.UpdateLedDialog;

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
            //ProgressUtil.showPogress(mContext, "初始化", false);
            queryLedState();
        }
    }

    private void setLedState(ConnectManager connectManager, byte mode) {
        if (isReady) {
            LampState state = DataManager.getInstance().getState();
            if (state != null)
                LedProxy.stopPreview();
            LedProxy.setState(connectManager, mode, state.isLighting(), state.isMoon(), state.isAcclimation());
        }
    }

    public void setLedState(int workMode) {
        if (isReady) {
            LampState state = DataManager.getInstance().getState();
            if (state != null)
                LedProxy.stopPreview();
            LedProxy.setState(workMode, state.isLighting(), state.isMoon(), state.isAcclimation());
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
                    this.controlView.switchMode(ls.getMode());
                    isReady = true;
                    // ProgressUtil.closeDialog();
                } else {
                    queryLedState();
                }
                break;

            case SetState:
                if (!response.IsOK()) {
//                    Toast.makeText(mContext, mContext.getString(R.string.set_state_success), Toast.LENGTH_LONG)
//                            .show();

                    setLedState(connectManager, DataManager.getInstance().getState().getMode());
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
