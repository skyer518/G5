package cn.com.lightech.led_g5w.net.entity;

import cn.com.lightech.led_g5w.entity.DataNode;
import cn.com.lightech.led_g5w.entity.DeviceType;
import cn.com.lightech.led_g5w.entity.LampState;

/*LED返回的Response*/
public class Response {


    private DeviceType deviceType;

    // 命令类型
    private CmdType cmdType;

    // 返回代码
    private ReplyErrorCode ReplyCode;

    // 灯状态
    private LampState LampState;

    // 模式节点数据
    private DataNode dataNode;

    // 模式序号
    private int ModeIndex;

    // 返回的字节数组，当返回的数据无法解析成Response时，把数据放进这里，方便调试用，正式代码不要使用这个字段
    private byte[] byteArray;

    // 包ID
    private byte[] packageId;

    private long unixTime;

    /* 组号 */
    private int groupNum;
    private byte[] mac;

    public long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
        if (this.dataNode != null)
            this.dataNode.setUnixTime(unixTime);
    }

    public boolean IsOK() {
        return this.ReplyCode == ReplyErrorCode.OK;
    }

    public CmdType getCmdType() {
        return cmdType;
    }

    public void setCmdType(CmdType cmdType) {
        this.cmdType = cmdType;
    }

    public int getModeIndex() {
        return ModeIndex;
    }

    public void setModeIndex(int modeIndex) {
        ModeIndex = modeIndex;
    }

    public ReplyErrorCode getReplyCode() {
        return ReplyCode;
    }

    public void setReplyCode(ReplyErrorCode replyCode) {
        ReplyCode = replyCode;
    }

    public cn.com.lightech.led_g5w.entity.LampState getLampState() {
        return LampState;
    }

    public void setLampState(LampState lampState) {
        LampState = lampState;
    }

    public Response() {

    }

    public Response(ReplyErrorCode replyCode, LampState ls) {
        this.ReplyCode = replyCode;
        this.LampState = ls;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public byte[] getPackageId() {
        return packageId;
    }

    public void setPackageId(byte[] packageId) {
        this.packageId = packageId;
    }

    public int getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(int groupNum) {
        this.groupNum = groupNum;
    }

    public DataNode getDataNode() {
        return dataNode;
    }

    public void setDataNode(DataNode dataNode) {
        this.dataNode = dataNode;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public void setMac(byte[] mac) {
        this.mac = mac;
    }

    public byte[] getMac() {
        return mac;
    }
}
