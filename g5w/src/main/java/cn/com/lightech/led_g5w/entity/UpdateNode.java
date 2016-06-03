package cn.com.lightech.led_g5w.entity;

/**
 * Created by 明 on 2016/4/27.
 */
public class UpdateNode {
    // 模式ID1
    protected final byte ID1 = 0x04;
    // 模式ID2
    protected byte ID2;


    private byte[] data;

    public UpdateNode() {
    }

    public UpdateNode(byte ID2) {
        this.ID2 = ID2;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte getID1() {
        return ID1;
    }

    public byte getID2() {
        return ID2;
    }

    public void setID2(byte ID2) {
        this.ID2 = ID2;
    }
}
