package cn.com.lightech.led_g5w.view.spray.entity;

public class WaveNode {
    /**
     * Data1: 功能: 1=同步2=异步3=长开4=顺序
     */
    private byte function = 1;
    /**
     * Data2: 效果: 1=正常2=隨机3=潮汐4=珊瑚5=近岸
     */
    private byte effect = 1;
    /**
     * Data3: 脉冲时间（秒）如：3.99，该数据发3
     */
    private byte pulseS;
    /**
     * Data4: 脉冲时间（十分之一及百分之一秒）如3.99，该数据发99
     */
    private byte pulseMs;
    /**
     * Data5: 马达功率：30~100 （30%~100%）
     */
    private byte power = 30;
    /**
     * Data6: 无线频道：0~99
     */
    private int channel;
    /**
     * Data7: 喂食开关：0 关 1开
     */
    private boolean feed;
    /**
     * Data8: 自动寻浪开关：0 关 1开
     */
    private boolean autoWave;
    /**
     * Data9: 日夜间开关：0 关 1开
     */
    private boolean dayOrNight;
    /**
     * Data10: 上次月圆距今日 0~30
     */
    private byte daysAgo;
    /**
     * Data11: 实际时间（时） Data12: 实际时间（分）
     */
    private int time;
    /**
     * Data13: M1时间（秒）0~99
     */
    private byte m1;
    /**
     * Data14: M1时间（秒）0~99
     */
    private byte m2;
    /**
     * Data15: M1时间（秒）0~99
     */
    private byte m3;
    /**
     * Data16: M1时间（秒）0~99
     */
    private byte m4;
    private long unixTime;
    private byte ID1;
    private byte ID2;

    public WaveNode() {
    }


    public byte getFunction() {
        return function;
    }

    public void setFunction(byte function) {
        this.function = function;
    }

    public byte getEffect() {
        return effect;
    }

    public void setEffect(byte effect) {
        this.effect = effect;
    }

    public byte getPulseS() {
        return pulseS;
    }

    public void setPulseS(byte pulseS) {
        this.pulseS = pulseS;
    }

    public byte getPulseMs() {
        return pulseMs;
    }

    public void setPulseMs(byte pulseMs) {
        this.pulseMs = pulseMs;
    }

    public byte getPower() {
        return power;
    }

    public void setPower(byte power) {
        this.power = power;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public boolean isFeed() {
        return feed;
    }

    public void setFeed(boolean feed) {
        this.feed = feed;
    }

    public boolean isAutoWave() {
        return autoWave;
    }

    public void setAutoWave(boolean autoWave) {
        this.autoWave = autoWave;
    }

    public boolean isDayOrNight() {
        return dayOrNight;
    }

    public void setDayOrNight(boolean dayOrNight) {
        this.dayOrNight = dayOrNight;
    }

    public byte getDaysAgo() {
        return daysAgo;
    }

    public void setDaysAgo(byte daysAgo) {
        this.daysAgo = daysAgo;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public byte getM1() {
        return m1;
    }

    public void setM1(byte m1) {
        this.m1 = m1;
    }

    public byte getM2() {
        return m2;
    }

    public void setM2(byte m2) {
        this.m2 = m2;
    }

    public byte getM3() {
        return m3;
    }

    public void setM3(byte m3) {
        this.m3 = m3;
    }

    public byte getM4() {
        return m4;
    }

    public void setM4(byte m4) {
        this.m4 = m4;
    }

    public void setID2(byte id2) {
        this.ID2 = id2;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public byte getID1() {
        return ID1;
    }

    public void setID1(byte ID1) {
        this.ID1 = ID1;
    }

    public byte getID2() {
        return ID2;
    }
}
