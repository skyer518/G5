package cn.com.lightech.led_g5w.view.spray;

import cn.com.lightech.led_g5w.utils.StringUtil;

/**
 * Created by alek on 2016/5/20.
 */
public class Timing {

    private int hour;
    private int minute;

    public Timing() {
        hour = 0;
        minute = 0;
    }

    public Timing(int hour, int minute) {
        super();
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isZero() {
        return hour == 0 && minute == 0;
    }

    public boolean valid() {
        if (this.hour == 0xff || this.minute == 0xff) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + hour;
        result = prime * result + minute;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Timing other = (Timing) obj;
        if (hour != other.hour)
            return false;
        if (minute != other.minute)
            return false;
        return true;
    }

    public int getMinuteValue() {
        return hour * 60 + minute;
    }

    public Timing minus(int number) {
        int minute = getMinuteValue() - number;
        return new Timing(minute / 60, minute % 60);
    }

    public Timing add(int number) {
        int minute = getMinuteValue() + number;
        return new Timing(minute / 60, minute % 60);
    }

    @Override
    public String toString() {
        return StringUtil.int2(hour) + ":" + StringUtil.int2(minute);
    }

}
