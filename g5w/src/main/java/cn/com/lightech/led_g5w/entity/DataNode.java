package cn.com.lightech.led_g5w.entity;


public abstract class DataNode {

    /**
     * ScheduleNode 所表示的索引，默认null
     */
    protected Mode mode;

    /**
     * 对象的时间属性
     */
    protected long unixTime = -1;

    // 模式ID1
    protected byte ID1;
    // 模式ID2
    protected byte ID2;

    DataNode(Mode mode) {
        setMode(mode);
    }


    protected void setMode(Mode mode) {
        this.mode = mode;
        this.ID1 = PackageId.getModePackageId(mode.toInt())[0];
        this.ID2 = PackageId.getModePackageId(mode.toInt())[1];
    }

    public long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }

    public Mode getScheduleMode() {
        return mode;
    }

    public byte getID1() {
        return ID1;
    }

    public byte getID2() {
        return ID2;
    }

    public byte[] getID() {
        return new byte[]{this.ID1, this.ID2};
    }


}
