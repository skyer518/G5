package cn.com.lightech.led_g5w.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.List;

import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.entity.Device;
import cn.com.lightech.led_g5w.net.http.HttpPostTask;
import cn.com.lightech.led_g5w.net.utils.PostParamUtil;
import cn.com.lightech.led_g5w.net.utils.WifiTool;
import cn.com.lightech.led_g5w.utils.ProgressUtil;
import cn.com.lightech.led_g5w.view.device.IDeleteDeviceView;

/**
 * Created by 明 on 2016/3/7.
 */
public class DeviceDeleteLedPresenter extends LedPresenter {

    private static final int WHAT_SHOW_ERROR = 0;
    private static final int WHAT_GOTO_NEXT = 1;

    public static final String REG_HI_LINK_WIFI_SSID = "^HI-LINK_+[0-9ABCDEF]{4}$";
    private static final int WHAT_LED_CONNECTED = 2;
    private static final int WHAT_LED_FAILED = 3;

    private final Context mContext;
    private final IDeleteDeviceView deleteDeviceView;
    private WifiTool wifiTool;

    public DeviceDeleteLedPresenter(Context context, IDeleteDeviceView view) {
        this.mContext = context;
        this.deleteDeviceView = view;
        wifiTool = new WifiTool(context);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_GOTO_NEXT) {
            } else if (msg.what == WHAT_SHOW_ERROR) {

            } else if (msg.what == WHAT_LED_FAILED) {
                ProgressUtil.closeDialog();
                deleteDeviceView.showMessage(mContext.getString(R.string.error_led_connect_failed));
            }
        }
    };

    private void deleteLed(final String ip) {
        HttpPostTask task = new HttpPostTask() {
            @Override
            protected void onPreExecute() {
                ProgressUtil.showPogress(mContext, mContext.getString(R.string.device_wifi_save_data), false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                ProgressUtil.closeDialog();
                deleteDeviceView.remove(ip);

            }
        };
        task.execute(PostParamUtil.getPostUrl(ip), PostParamUtil.getResetWifiParams());

    }

    public void deleteDevice(List<Device> devices) {
        for (Device device : devices) {
            deleteLed(device.getIp());
        }

//        Toast.makeText(mContext, "设备正在重启，请等待1分钟后尝试连接。", Toast.LENGTH_LONG).show();
//        deleteDeviceView.closeView();
    }

}
