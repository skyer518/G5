package cn.com.lightech.led_g5w.presenter.responsibility;

import android.os.Handler;

import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.net.http.HttpPostTask;
import cn.com.lightech.led_g5w.net.utils.PostParamUtil;
import cn.com.lightech.led_g5w.utils.ProgressUtil;
import cn.com.u2be.alekwifilibrary.Wifi;

/**
 * Created by alek on 2016/5/17.
 */
public class DutySaveWifi extends DutyHandler {

    public DutySaveWifi(DutyHandler successor) {
        super(successor);
    }

    public DutySaveWifi() {
    }

    @Override
    public void handleRequest(final RequestEntity request) {
        final String rawSecurity = Wifi.ConfigSec.getDisplaySecirityString(request.lanWifi);
        final HttpPostTask task = new HttpPostTask() {
            @Override
            protected void onPreExecute() {
                ProgressUtil.showPogress(request.mContext, request.mContext.getString(R.string.device_wifi_save_data), false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                ProgressUtil.closeDialog();
                request.handler.sendEmptyMessage(RequestEntity.WHAT_SUCCESS);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                ProgressUtil.closeDialog();
                request.handler.sendEmptyMessage(RequestEntity.WHAT_SUCCESS);
            }
        };
        task.execute(PostParamUtil.getPostUrl(null),
                PostParamUtil.getSaveWifiParams(request.lanWifi.SSID, "auto", request.lanWifiPwd));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!task.isCancelled()) {
                    task.cancel(false);
                }
            }
        }, 3000);
    }
}
