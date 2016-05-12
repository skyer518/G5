package cn.com.lightech.led_g5g.net.utils;

import java.util.Timer;
import java.util.TimerTask;

public class TimerHelper {

    public final static int DELAY_SHORT = 4000;
    public final static int DELAY_NONE = 0;
    /**
     * 要做的事
     */
    private TimerProcessor mProcessor;
    /**
     * 延时
     */
    private long mDelay;
    /**
     * 周期
     */
    private long mPeriod;
    /**
     * 定时器
     */
    private Timer mTimer;
    /**
     * 定时线程
     */
    private TimerTask mTimerTask;

    /**
     * 构造函数 一次性timer
     *
     * @param delay     延时
     * @param processor 定时处理器，由调用者定制实现
     */
    public TimerHelper(long delay, TimerProcessor processor) {
        mProcessor = processor;
        mDelay = delay;
    }

    /**
     * 构造函数 周期性timer
     *
     * @param delay     延时
     * @param period    周期
     * @param processor 定时处理器
     */
    public TimerHelper(long delay, long period, TimerProcessor processor) {
        mProcessor = processor;
        mDelay = delay;
        mPeriod = period;
    }

    /**
     * 启动定时器
     */
    public void startTimer() {
        if (mTimer == null) {

            mTimer = new Timer(true);
            mTimerTask = new TimerTask() {

                @Override
                public void run() {
                    if (mProcessor != null) {
                        mProcessor.process();
                    }
                }

            };

            if (mPeriod > 0)
                mTimer.schedule(mTimerTask, mDelay, mPeriod);
            else
                mTimer.schedule(mTimerTask, mDelay);
        }
    }

    /**
     * 停止定时器
     */
    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mProcessor != null) {
            mProcessor.stop();
        }
    }
}
