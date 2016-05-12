package cn.com.lightech.led_g5g.view.device;

import java.util.List;

import cn.com.lightech.led_g5g.entity.DeviceGroup;
import cn.com.lightech.led_g5g.view.IBaseView;

/**
 * Created by 明 on 2016/3/4.
 */
public interface IDeviceView extends IBaseView {

    void showDevices();

    void scanLoading(boolean scaning);
}
