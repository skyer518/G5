package cn.com.lightech.led_g5g.entity;


public abstract class DataNode {


    /**
     * 对象的时间属性
     */
    protected long unixTime = -1;

    // 模式ID1
    protected byte id1;
    // 模式ID2
    protected byte id2;

    protected DataType dataType;


    public DataNode(byte id1, byte id2) {
        this.dataType = DataType.valueOfIds(id1, id2);
        this.id1 = id1;
        this.id2 = id2;
    }

    public DataNode(byte[] ids) {
        this(ids[0], ids[1]);
    }

    public long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }

    public byte getId1() {
        return id1;
    }

    public byte getId2() {
        return id2;
    }

    public DataType getDataType() {
        return dataType;
    }

}
