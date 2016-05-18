package cn.com.lightech.led_g5g.net.entity;

import cn.com.lightech.led_g5g.entity.DataNode;
import cn.com.lightech.led_g5g.entity.DeviceType;
import cn.com.lightech.led_g5g.entity.LampState;
import cn.com.lightech.led_g5g.entity.data.UpdateData;

/*发送给LED的Request*/
public class Request {
    // 命令类型
    private CmdType cmdType;

    // 灯状态
    private LampState lampState;

    // 模式节点数据
    private DataNode data;

    // 模式序号
    private int modeIndex;

    // 布尔类型的值
    private boolean boolVal;

    // int类型值
    private int intVal;

    private byte[] byteArray;

    private UpdateData updateData;

    public DeviceType getDeviceType() {
        return deviceType;
    }

    private DeviceType deviceType;

    public DataNode getData() {
        return data;
    }

    public void setData(DataNode data) {
        this.data = data;
    }

    public int getIntVal() {
        return intVal;
    }

    public void setIntVal(int intVal) {
        this.intVal = intVal;
    }

    public boolean getBoolVal() {
        return boolVal;
    }

    public void setBoolVal(boolean boolVal) {
        this.boolVal = boolVal;
    }

    public CmdType getCmdType() {
        return cmdType;
    }

    public void setCmdType(CmdType cmdType) {
        this.cmdType = cmdType;
    }

    public int getModeIndex() {
        return modeIndex;
    }

    public void setModeIndex(int modeIndex) {
        this.modeIndex = modeIndex;
    }

    public LampState getLampState() {
        return lampState;
    }

    public void setLampState(LampState lampState) {
        this.lampState = lampState;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }


    public void setUpdateData(UpdateData updateData) {
        this.updateData = updateData;
    }

    public UpdateData getUpdateData() {
        return updateData;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
}
