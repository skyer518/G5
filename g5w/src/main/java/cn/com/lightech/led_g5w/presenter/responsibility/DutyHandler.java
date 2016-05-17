package cn.com.lightech.led_g5w.presenter.responsibility;

/**
 * Created by alek on 2016/5/17.
 */
public abstract class DutyHandler {

    private DutyHandler successor;

    public abstract void handleRequest();


}
