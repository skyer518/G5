package cn.com.lightech.led_g5g.entity;

/**
 * 一个时间段
 * Created by 明 on 2016/3/25.
 */
public class TimeBucket {

    private int start = (byte) 0xff;
    private int end = (byte) 0xff;

    public TimeBucket(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public TimeBucket(int startHour, int startMinute, int endhour, int endMinute) {
        this.start = startHour * 60 + startMinute;
        this.end = endhour * 60 + endMinute;
    }

    public TimeBucket() {

    }


    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStartHour() {
        return start / 60;
    }

    public int getStartMinute() {
        return start % 60;
    }

    public int getEndHour() {
        return end / 60;
    }

    public int getEndMinute() {
        return end % 60;
    }


}
