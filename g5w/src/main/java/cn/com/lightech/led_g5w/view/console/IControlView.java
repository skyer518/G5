package cn.com.lightech.led_g5w.view.console;

import cn.com.lightech.led_g5w.view.IBaseView;

/**
 * Created by 明 on 2016/3/15.
 */
public interface IControlView extends IBaseView {
    /** TODO: workmode 0:auto / 4:manual */
    void switchMode(int workMode);
}
