package cn.com.lightech.led_g5w.view.console;

import cn.com.lightech.led_g5w.entity.LampChannel;
import cn.com.lightech.led_g5w.view.IBaseView;

/**
 * Created by 明 on 2016/3/15.
 */
public interface IManualView extends IBaseView {
    void showManual(LampChannel channel);
}
