package cn.com.lightech.led_g5w.view.device;

import java.util.List;

import cn.com.lightech.led_g5w.entity.DeviceGroup;
import cn.com.lightech.led_g5w.view.IBaseView;

/**
 * Created by 明 on 2016/3/4.
 */
public interface IDeviceView extends IBaseView {

    void showDevices();

    void scanLoading(boolean scaning);
}
