package cn.com.lightech.led_g5g.view.console;

import cn.com.lightech.led_g5g.view.IBaseView;

/**
 * Created by 明 on 2016/3/15.
 */
public interface IAutoView extends IBaseView {
    void stopPreview();

    void preview();

    void drawChart();

    void spikToCurrent(int time);

    void enableDeleteButton(boolean enable);

    void enableEditButton(boolean enable);

    void enableAddButton(boolean enable);
}

