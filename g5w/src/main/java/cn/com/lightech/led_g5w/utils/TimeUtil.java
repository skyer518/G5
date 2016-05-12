package cn.com.lightech.led_g5w.utils;

/**
 * Created by 明 on 2016/3/25.
 */
public class TimeUtil {

    public static boolean isVali(int time) {
        if (time >= 0 && time <= 24 * 60) {
            return true;
        }
        return false;
    }

    public static int gethour(int time) {
        if (isVali(time)) {
            return time / 60;
        }
        return 0xff;
    }

    public static int getMinute(int time) {
        if (isVali(time)) {
            return time % 60;
        }
        return 0xff;
    }

    public static int getTime(int hour, int minute) {
        int time = hour * 60 + minute;
        if (isVali(time))
            return time;
        else
            return 0xff;
    }

}
