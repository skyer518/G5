package cn.com.lightech.led_g5w.net.entity;

/**
 * 命令类型
 */
public enum CmdType {

    Unknow(0),
    /**
     * 同步时间
     */
    SyncTime(0x01),
    /**
     * 查询设备类型
     */
    QueryType(0x02),

    /**
     * 查询灯的状态
     */
    QueryState(0x03),

    /**
     * 设置灯的状态
     */
    SetState(0x04),

    /**
     * 开关灯
     */
    OnOff(0x06),

    /**
     * 预览模式命令
     */
    PreviewMode(0x07),

    /**
     * 预览曲线命令
     */
    PreViewCurve(0x08),

    /**
     * 结束预览命令
     */
    StopPreview(0x09),

    /**
     * 从灯建立与当前主灯关联命令
     */
    AttachSub(0x10),

    /**
     * 查询组号命令
     */
    QueryGroup0x1A(0x1A),

    /**
     * 查询组号命令
     */
    QueryGroup0xF1(0xF1),

    /**
     * 指定组号命令
     */
    SetGroup(0x1B),

    /**
     * 查询LED是否就绪
     */
    CheckReady(0x1C),

    /**
     * 下载曲线数据到单片机
     */
    SendDataToLED(0x20),

    /**
     * 上传曲线数据到平板端
     */
    RecvDataFromLED(0x21),

    /**
     * 查询曲线数据有效性
     */
    ValidateData(0x22),

    /**
     * 查询单片机软件版本号
     */
    GetVersion(0x24),

    /**
     * 确认灯具
     */
    ConfirmLed(0xFF),

    /**
     * 查找灯具
     */
    FindLed(0xFFFF),

    /**
     * ID格式错误  补充类型，不是真实命令
     */
    IDFormatError(0xfd),

    /**
     * 数据包校验和失败  补充类型，不是真实命令
     */
    ValidateSumFailed(0xfe);


    private int value;

    CmdType(int value) {
        this.value = value;
    }

    public static CmdType Parse(int value) {
        for (CmdType type : CmdType.values()) {
            if (type.value == value)
                return type;
        }
        return CmdType.Unknow;
    }
}
