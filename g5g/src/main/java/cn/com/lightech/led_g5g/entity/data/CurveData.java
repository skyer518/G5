package cn.com.lightech.led_g5g.entity.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.lightech.led_g5g.entity.CurvePoint;
import cn.com.lightech.led_g5g.entity.DataNode;
import cn.com.lightech.led_g5g.entity.LampChannel;
import cn.com.lightech.led_g5g.net.entity.ChanelType;
import cn.com.lightech.led_g5g.net.utils.Logger;

/**
 * Created by 明 on 2016/3/15.
 */
public class CurveData extends DataNode {

    private Logger logger = Logger.getLogger(getClass());

    private final int MAX_POINT = 24;

    List<CurvePoint> points = new ArrayList<>();

    public CurveData(byte id1, byte id2) {
        super(id1, id2);
    }

    public CurveData(byte[] ids) {
        this(ids[0], ids[1]);
    }


    public List<CurvePoint> getPoints() {
        return points;
    }

    public void setPoints(List<CurvePoint> points) {
        if (points.size() > MAX_POINT)
            throw new RuntimeException("points . size > MAX_POINT");
        this.points = points;
    }

    /**
     * 添加一个时间点
     *
     * @param time 新时间点
     */
    public int addPoint(int time, LampChannel channel) {

        CurvePoint newPoint = new CurvePoint(time, channel);
        return addPoint(newPoint);
    }

    /**
     * 添加一个时间点
     *
     * @param newPoint 新时间点
     */
    public int addPoint(CurvePoint newPoint) {
        if (points == null) {
            return -1;
        }
        for (int i = 0; i < points.size(); i++) {
            CurvePoint point = points.get(i);
            if (point.isSamePoint(newPoint)) {
                points.remove(i);
                points.add(i, newPoint);
                logger.e("isSame point: %d", i);
                return i;
            }
        }
        if (isOutOfBounds())
            return -1;
        points.add(newPoint);
        Collections.sort(points);
        return points.indexOf(newPoint);
    }

    /**
     * 检查是否超 24个点
     *
     * @return
     */
    public boolean isOutOfBounds() {
        if (points.size() >= MAX_POINT) {
            return true;
        }
        return false;
    }

    /**
     * 复制现有数据并生成一条完整的数据
     * 通用数据没有 24：00
     * 结果为：现有数据 + 24:00
     * 24:00的数据为 00：00的数据
     *
     * @return
     */
    private static List<CurvePoint> genWholeData(CurveData data) {
        List<CurvePoint> points = data.getPoints();
        List<CurvePoint> result = new ArrayList<CurvePoint>(0);
        for (int i = 0; i < points.size(); i++) {
            result.add(points.get(i));
        }
        CurvePoint tlc1 = new CurvePoint(24, 0);
        tlc1.setChannel(points.get(0).getChannel());
        result.add(tlc1);
        return result;
    }

    /**
     * 生成在 图表中显示的数据
     * 共由 24*6+1 个点组成
     * 生成时中间补齐临时数据点
     *
     * @param data
     * @return
     */
    public static List<CurvePoint> genData4PreviewChart(CurveData data) {
        List<CurvePoint> timingLamps = genWholeData(data);
        List<CurvePoint> tlcs = new ArrayList<>(24 * 6 + 1);
        for (int i = 0; i < timingLamps.size(); i++) {
            // 取相邻点
            CurvePoint first = timingLamps.get(i);
            CurvePoint second = first;
            if (i < timingLamps.size() - 1) {
                second = timingLamps.get(i + 1);

            }
            int minus = second.minus(first);
            // 判断直线还是斜线
            LampChannel firstLamp = first.getChannel();
            LampChannel secondLamp = second.getChannel();
            if (minus == 0) {
                continue;
            }
            for (int j = 0; j < minus; j++) {
                CurvePoint tlc = new CurvePoint();
                // 时间
                int newTiming = first.getTime() + j;
                tlc.setTime(newTiming);
                // 数据

                LampChannel lc = new LampChannel();
                ChanelType[] values = ChanelType.values();
                for (int k = 0; k < values.length; k++) {
                    int firstLedValue = firstLamp.getData(values[k]);
                    int secondLedValue = secondLamp.getData(values[k]);
                    int lampMinus = secondLedValue - firstLedValue;
                    float ledValue = firstLedValue + lampMinus * 1.0f / minus
                            * j;
                    lc.setData(values[k], ledValue);
                }

                tlc.setChannel(lc);
                tlcs.add(tlc);
            }
        }
        tlcs.add(144, new CurvePoint(144, tlcs.get(0).getChannel()));
        return tlcs;
    }

//
//    public static boolean loadDefault() {
//        return false;
//    }
}


