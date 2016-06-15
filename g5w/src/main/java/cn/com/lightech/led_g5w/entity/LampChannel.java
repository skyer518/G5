package cn.com.lightech.led_g5w.entity;

import cn.com.lightech.led_g5w.net.entity.ChanelType;

/* 灯通道
 * [0,100]
 * */
public class LampChannel {


    //public final static int MAX_INDEX=4;

    private float blue;
    private float white;
    private float purple;
    private float red;
    private float green;

    public LampChannel(float blue, float white, float purple, float red, float green) {
        setBlue(blue);
        setWhite(white);
        setPurple(purple);
        setRed(red);
        setGreen(green);
    }

    public LampChannel() {
    }

    public float getTempWhite() {
        return white;
    }

    public int getWhite() {
        return getValue(white);
    }

    public int getValue(float value) {
        return new Float(value).intValue();
    }

    public void setWhite(float white) {
        this.white = valid(white);
    }

    public float getTempBlue() {
        return blue;
    }

    public int getBlue() {
        return getValue(blue);
    }

    public void setBlue(float blue) {
        this.blue = valid(blue);
    }

    public float getTempPurple() {
        return purple;
    }

    public int getPurple() {
        return getValue(purple);
    }

    public void setPurple(float purple) {
        this.purple = valid(purple);
    }

    public float getTempRed() {
        return red;
    }

    public int getRed() {
        return getValue(red);
    }

    public void setRed(float red) {
        this.red = valid(red);
    }

    public float getTempGreen() {
        return green;
    }

    public int getGreen() {
        return getValue(green);
    }

    public void setGreen(float green) {
        this.green = valid(green);
    }

    private float valid(float val) {
        if (val < 0)
            return 0;
        if (val > 100)
            return 100;
        return val;
    }

    /**
     * 获取某种灯的数据
     *
     * @param type 对应Constants中灯索引
     */
    public int getData(ChanelType type) {
        float ret = 0;
        switch (type) {
            case White:
                ret = getWhite();
                break;
            case Bule:
                ret = getBlue();
                break;
            case PurPle:
                ret = getPurple();
                break;
            case Red:
                ret = getRed();
                break;
            case Green:
                ret = getGreen();
                break;
            default:
                break;
        }
        return getValue(ret);
    }

    /**
     * 设置某种灯的数据
     *
     * @param type 对应Constants中灯索引
     */
    public void setData(ChanelType type, float value) {
        switch (type) {
            case White:
                setWhite(value);
                break;
            case Bule:
                setBlue(value);
                break;
            case PurPle:
                setPurple(value);
                break;
            case Red:
                setRed(value);
                break;
            case Green:
                setGreen(value);
                break;
            default:
                break;
        }

    }

    /**
     * 获得通道对应索引的数据的数组形式
     *
     * @return
     */
    public int[] getDataArray() {
        int dataArray[] = new int[ChanelType.values().length];
        for (ChanelType type : ChanelType.values()) {
            dataArray[type.ordinal()] = getData(type);
        }

//        for (int ledIndex = Constants.LED_START_INDEX; ledIndex <= Constants.LED_MAX_INDEX; ledIndex++) {
//            dataArray[ledIndex] = getData(ledIndex);
//        }
        return dataArray;
    }

    /**
     * 通过数组形式设置5通道
     *
     * @param dataArray 按等索引存储数据
     * @return dataArray为null 或长度不等于灯长度时，返回false，否则true
     */
    public boolean setDataArray(int[] dataArray) {
        ChanelType[] values = ChanelType.values();
        if (dataArray == null || dataArray.length != values.length) {
            return false;
        }
        for (ChanelType type : values) {
            setData(type, (float) dataArray[type.ordinal()]);
        }
        return true;
    }

}
