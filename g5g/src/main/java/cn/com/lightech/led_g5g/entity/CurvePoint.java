package cn.com.lightech.led_g5g.entity;

import android.text.TextUtils;

import cn.com.lightech.led_g5g.net.entity.ChanelType;

/**
 * TODO： 曲线点
 * Created by 明 on 2016/3/21.
 */
public class CurvePoint implements Comparable<CurvePoint> {

    private int time;
    private LampChannel channel;

    public CurvePoint() {
        channel = new LampChannel();
    }

    public CurvePoint(int time) {
        setTime(time);
        setChannel(new LampChannel());
    }

    public CurvePoint(int hour, int minute) {
        setTime(hour, minute);
        setChannel(new LampChannel());
    }

    public CurvePoint(LampChannel channel) {
        setChannel(channel);
    }

    public CurvePoint(int time, LampChannel channel) {
        setTime(time);
        setChannel(channel);
    }

    public CurvePoint(int hour, int minute, LampChannel channel) {
        setTime(hour, minute);
        setChannel(channel);
    }

    public int getTime() {
        return time;
    }

    public int getHour() {
        return time / 6;
    }

    public int getMinute() {
        return time % 6;
    }


    public void setTime(int hour, int minute) {
        setTime(hour * 6 + minute);
    }

    public void setTime(int time) {
        this.time = time;
        if (time > 24 * 6) {
            this.time = 24 * 6;
        }
        if (time < 0) {
            this.time = 0;
        }
    }

    public LampChannel getChannel() {
        return channel;
    }

    public void setChannel(LampChannel channel) {
        this.channel = channel;
    }

    @Override
    public int compareTo(CurvePoint another) {
        return minus(another);
    }


    /**
     * 比较时间差
     *
     * @param another
     * @return
     */
    public int minus(CurvePoint another) {
        return time - another.time;
    }

    /**
     * 是否为相同的时间点
     */
    public boolean isSamePoint(CurvePoint another) {
        return this.time == another.time;
    }

    /**
     * 转换成String 用于保存
     */
    public String toString(CurvePoint node) {
        if (node == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(node.time).append("_")
                .append(node.getChannel().getData(ChanelType.Bule)).append("_")
                .append(node.getChannel().getData(ChanelType.Green)).append("_")
                .append(node.getChannel().getData(ChanelType.Red)).append("_")
                .append(node.getChannel().getData(ChanelType.PurPle)).append("_")
                .append(node.getChannel().getData(ChanelType.White));
        return sb.toString();
    }

    public static CurvePoint fromString(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String[] strArr = str.split("_");
        if (strArr.length != 6) {
            return null;
        }
        CurvePoint node = new CurvePoint();
        node.setTime(Integer.parseInt(strArr[0]));
        node.setChannel(new LampChannel());
        node.getChannel().setData(ChanelType.Bule, Byte.parseByte(strArr[2]));
        node.getChannel().setData(ChanelType.Green, Byte.parseByte(strArr[3]));
        node.getChannel().setData(ChanelType.Red, Byte.parseByte(strArr[4]));
        node.getChannel().setData(ChanelType.PurPle, Byte.parseByte(strArr[5]));
        node.getChannel().setData(ChanelType.White, Byte.parseByte(strArr[6]));
        return node;
    }

}
