package cn.com.lightech.led_g5w.presenter.responsibility;

import android.util.Log;

import cn.com.lightech.led_g5w.gloabal.CmdBuilder;
import cn.com.lightech.led_g5w.gloabal.IDataListener;
import cn.com.lightech.led_g5w.net.ConnectManager;
import cn.com.lightech.led_g5w.net.entity.CmdType;
import cn.com.lightech.led_g5w.net.entity.ConnState;
import cn.com.lightech.led_g5w.net.entity.Request;
import cn.com.lightech.led_g5w.net.entity.Response;
import cn.com.lightech.led_g5w.utils.ProgressUtil;

/**
 * Created by alek on 2016/5/18.
 */
public abstract class LedTask implements IDataListener {


    ConnectManager connect = new ConnectManager();

    boolean isOver = false;

    public LedTask() {
        connectLED();
    }

    void connectLED() {
        connect.Connect("192.168.16.254", 8080);
        connect.Register(this);
    }


    public boolean isOver() {
        return isOver();
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public void onConnectStateChanged(ConnState connState, ConnectManager connectManager) {
        Log.i("wiwiPersentr", "onConnectStateChanged :" + connState);
    }

    public abstract void sendCommond(ConnectManager connectManager);

    @Override
    public boolean onReceive(Response response, ConnectManager connectManager) {
        if (response.getCmdType() == CmdType.CheckReady) {
            if (response.IsOK()) {
                sendCommond(connectManager);
            }
        }
        return false;
    }


    void over(int successs) {
        ProgressUtil.closeDialog();
        onOver(successs);

        connect.UnRegister(this);
        connect.closeConnection();
        isOver = true;
    }

    public abstract void onOver(int success);


}
