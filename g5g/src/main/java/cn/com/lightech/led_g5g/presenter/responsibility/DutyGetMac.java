package cn.com.lightech.led_g5g.presenter.responsibility;

import android.os.Handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.net.http.HttpPostTask;
import cn.com.lightech.led_g5g.net.utils.Logger;
import cn.com.lightech.led_g5g.net.utils.MacUtil;
import cn.com.lightech.led_g5g.net.utils.PostParamUtil;
import cn.com.lightech.led_g5g.utils.ProgressUtil;

/**
 * Created by alek on 2016/5/17.
 */
public class DutyGetMac extends DutyHandler {

    public static final String REG_MAC = "([0-9a-fA-F]{2})(([/\\s:-][0-9a-fA-F]{2}){5})";

    Logger log = Logger.getLogger(DutyGetMac.class);

    public DutyGetMac(DutyHandler successor) {
        super(successor);
    }

    public DutyGetMac() {
    }

    @Override
    public void handleRequest(final RequestEntity request) {
        final HttpPostTask task = new HttpPostTask() {
            @Override
            protected void onPreExecute() {
                ProgressUtil.showPogress(request.mContext, request.mContext.getString(R.string.device_wifi_save_data), false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                ProgressUtil.closeDialog();
                log.i(s);
                /*
                <html><head><title>My Title</title><link rel="stylesheet" href="/style/normal_ws.css"				type="text/css"><meta http-equiv="content-type" content="text/html;				charset=utf-8"></head>
                <body>at+Get_MAC=? 20:F4:1B:79:FB:79 ,20:F4:1B:79:FB:78</body></html>
                */

                final Pattern pattern = Pattern.compile(REG_MAC);
                final Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    request.mac = MacUtil.convertMac(matcher.group());
                    handNext(request);
                } else {
                    request.handler.sendEmptyMessage(RequestEntity.WHAT_GET_MAC_ADDRESS_FAILED);
                }
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                request.handler.sendEmptyMessage(RequestEntity.WHAT_GET_MAC_ADDRESS_FAILED);
            }
        };
        task.execute(PostParamUtil.getPostUrl(null), PostParamUtil.getGetMacParams());
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
