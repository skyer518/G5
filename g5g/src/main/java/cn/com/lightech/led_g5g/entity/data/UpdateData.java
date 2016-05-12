package cn.com.lightech.led_g5g.entity.data;

import cn.com.lightech.led_g5g.entity.DataNode;

/**
 * Created by 明 on 2016/4/27.
 */
public class UpdateData extends DataNode {

    private byte[] data;

    public UpdateData(byte id2) {
        super((byte) 0x04, id2);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
