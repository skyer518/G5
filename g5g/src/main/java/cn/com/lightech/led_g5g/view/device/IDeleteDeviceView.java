package cn.com.lightech.led_g5g.view.device;

import android.net.wifi.ScanResult;

import java.util.List;

import cn.com.lightech.led_g5g.view.IBaseView;

/**
 * Created by 明 on 2016/3/4.
 */
public interface IDeleteDeviceView extends IBaseView {


    void closeView();

    void remove(String ip);
}
