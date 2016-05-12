package cn.com.lightech.led_g5g.view.device;

import android.net.wifi.ScanResult;

import java.util.List;

import cn.com.lightech.led_g5g.view.IBaseView;

/**
 * Created by 明 on 2016/3/4.
 */
public interface IAddDeviceView extends IBaseView {

    void showWifiScanResult(List<ScanResult> scanResults);

}
