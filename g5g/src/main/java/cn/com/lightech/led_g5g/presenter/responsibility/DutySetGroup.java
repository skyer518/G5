package cn.com.lightech.led_g5g.presenter.responsibility;

import android.os.Handler;

import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.gloabal.CmdBuilder;
import cn.com.lightech.led_g5g.net.ConnectManager;
import cn.com.lightech.led_g5g.net.entity.CmdType;
import cn.com.lightech.led_g5g.net.entity.Request;
import cn.com.lightech.led_g5g.net.entity.Response;
import cn.com.lightech.led_g5g.utils.ProgressUtil;

/**
 * Created by alek on 2016/5/17.
 */
public class DutySetGroup extends DutyHandler {

    public static final int RESULT_OK = 1;
    public static final int RESULT_TIMEOUT = 0;
    public static final int RESULT_UNKONW_TYPE = -1;
    public static final int RESULT_ERROR = -10;

    public DutySetGroup(DutyHandler successor) {
        super(successor);
    }

    public DutySetGroup() {
    }

    @Override
    public void handleRequest(final RequestEntity request) {
        ProgressUtil.showPogress(request.mContext, request.mContext.getString(R.string.device_wifi_save_data_2_led), false);

        final LedTask task = new LedTask() {
            @Override
            public void sendCommond(ConnectManager connectManager) {
                Request cmd = new Request();
                cmd.setCmdType(CmdType.SetGroup);
                cmd.setIntVal(0);
                cmd.setByteArray(request.mac);
                cmd.setDeviceType(request.deviceType);
                connectManager.SendToLed(CmdBuilder.Build(cmd));
            }

            @Override
            public boolean onReceive(Response response, ConnectManager connectManager) {
                super.onReceive(response, connectManager);
                if (response.getCmdType() == CmdType.SetGroup) {
                    if (response.IsOK()) {
                        over(RESULT_OK);
                    } else {
                        over(RESULT_ERROR);
                    }
                }
                return true;
            }

            @Override
            public void onOver(int success) {
                if (success == RESULT_OK) {
                    handNext(request);
                } else {
                    request.handler.sendEmptyMessage(RequestEntity.WHAT_SET_GROUP_FAILED);
                }
            }
        };


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!task.isOver)
                    task.over(RESULT_TIMEOUT);
            }
        }, 3000);
    }
}
