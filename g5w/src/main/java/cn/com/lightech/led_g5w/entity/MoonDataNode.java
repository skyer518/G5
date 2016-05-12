package cn.com.lightech.led_g5w.entity;

/**
 * Created by 明 on 2016/3/15.
 */
public class MoonDataNode extends DataNode {

    private TimeBucket time;


    private int lastFullMoonDay;

    public MoonDataNode() {
        super(Mode.Moon);
        lastFullMoonDay = 1;
    }


    public TimeBucket getTime() {
        return time;
    }

    public void setTime(TimeBucket time) {
        this.time = time;
    }

    public int getLastFullMoonDay() {
        return lastFullMoonDay;
    }

    public void setLastFullMoonDay(int lastFullMoonDay) {
        this.lastFullMoonDay = lastFullMoonDay;
    }
}
