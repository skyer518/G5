package cn.com.lightech.led_g5w.entity;

/**
 * Created by 明 on 2016/3/15.
 */
public class FlashDataNode extends DataNode {

    private TimeBucket time1;
    private TimeBucket time2;
    private TimeBucket time3;

    public FlashDataNode() {
        super(Mode.Flash);
    }


    public TimeBucket getTime1() {
        return time1;
    }

    public void setTime1(TimeBucket time1) {
        this.time1 = time1;
    }

    public TimeBucket getTime2() {
        return time2;
    }

    public void setTime2(TimeBucket time2) {
        this.time2 = time2;
    }

    public TimeBucket getTime3() {
        return time3;
    }

    public void setTime3(TimeBucket time3) {
        this.time3 = time3;
    }
}
