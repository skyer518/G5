package cn.com.lightech.led_g5g.entity.data;

import cn.com.lightech.led_g5g.entity.DataNode;
import cn.com.lightech.led_g5g.entity.PackageId;
import cn.com.lightech.led_g5g.entity.TimeBucket;

/**
 * Created by 明 on 2016/3/15.
 */
public class MoonData extends DataNode {

    private TimeBucket time;


    private int lastFullMoonDay;

    public MoonData() {
        super(PackageId.Moon);
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
