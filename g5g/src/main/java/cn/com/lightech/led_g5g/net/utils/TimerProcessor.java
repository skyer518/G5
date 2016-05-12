package cn.com.lightech.led_g5g.net.utils;

/**
 * 抽象类，定义定时执行的接口函数
 */
public abstract class TimerProcessor {


    /**
     * 在TimerTask中执行的操作，由调用者定制
     */
    public abstract void process();

    public abstract void stop();

}