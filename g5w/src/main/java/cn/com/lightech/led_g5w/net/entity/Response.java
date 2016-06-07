package cn.com.lightech.led_g5w.net.entity;

import cn.com.lightech.led_g5w.entity.DataNode;
import cn.com.lightech.led_g5w.entity.DeviceType;
import cn.com.lightech.led_g5w.entity.LampState;
import cn.com.lightech.led_g5w.view.spray.entity.WaveNode;

/*LED返回的Response*/
public class Response {


    private DeviceType deviceType;

    // 命令类型
    private CmdType cmdType;

    // 返回代码
    private ReplyErrorCode replyErrorCode;

    // 灯状态
    private LampState lampState;

    private int version1;
    private int version2;

    // 模式节点数据
    private DataNode dataNode;

    public WaveNode getWaveNode() {
        return waveNode;
    }

    public void setWaveNode(WaveNode waveNode) {
        this.waveNode = waveNode;
    }

    private WaveNode waveNode;

    // 模式序号
    private int ModeIndex;

    // 返回的字节数组，当返回的数据无法解析成Response时，把数据放进这里，方便调试用，正式代码不要使用这个字段
    private byte[] byteArray;

    // 包ID
    private byte[] packageId;

    private long unixTime;

    /* 组号 */
    private int groupNum;
    /*MAC 地址*/
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
        return this.replyErrorCode == ReplyErrorCode.OK;
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

    public ReplyErrorCode getReplyErrorCode() {
        return replyErrorCode;
    }

    public void setReplyErrorCode(ReplyErrorCode replyErrorCode) {
        this.replyErrorCode = replyErrorCode;
    }

    public cn.com.lightech.led_g5w.entity.LampState getLampState() {
        return lampState;
    }

    public void setLampState(LampState lampState) {
        this.lampState = lampState;
    }

    public Response() {

    }

    public Response(ReplyErrorCode replyErrorCode, LampState ls) {
        this.replyErrorCode = replyErrorCode;
        this.lampState = ls;
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

    public int getVersion1() {
        return version1;
    }

    public void setVersion1(int version1) {
        this.version1 = version1;
    }

    public int getVersion2() {
        return version2;
    }

    public void setVersion2(int version2) {
        this.version2 = version2;
    }
}
