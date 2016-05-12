package cn.com.lightech.led_g5w.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.lightech.led_g5w.net.entity.ChanelType;

/**
 * Created by 明 on 2016/3/15.
 */
public class AutoDataNode extends DataNode {

    private final int MAX_POINT = 24;

    List<CurvePoint> points = new ArrayList<>();
    private boolean preview;

    public AutoDataNode(Mode mode) {
        super(mode);
    }

    public AutoDataNode() {
        this(Mode.Auto);
    }


    public static boolean loadDefault() {
        return false;
    }

    public List<CurvePoint> getPoints() {
        return points;
    }

    public void setPoints(List<CurvePoint> points) {
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

    public int addPoint(CurvePoint newPoint) {
        if (points == null) {
            return -1;
        }
        for (int i = 0; i < points.size(); i++) {
            CurvePoint point = points.get(i);
            if (point.isSamePoint(newPoint)) {
                points.remove(i);
                points.add(i, newPoint);
                return i;
            }
        }

        points.add(newPoint);
        Collections.sort(points);
        return points.indexOf(newPoint);
    }

    /**
     * 生成在 图表中显示的数据
     *
     * @return
     */
    public List<CurvePoint> genData4Chart() {
        List<CurvePoint> points = getPoints();
        List<CurvePoint> result = new ArrayList<CurvePoint>(0);
        for (int i = 0; i < points.size(); i++) {
            result.add(points.get(i));
        }
        CurvePoint tlc1 = new CurvePoint(24, 0);
        tlc1.setChannel(points.get(0).getChannel());
        result.add(tlc1);
        return result;
    }

    public List<CurvePoint> genData4PreviewChart() {
        List<CurvePoint> timingLamps = genData4Chart();
        List<CurvePoint> tlcs = new ArrayList<CurvePoint>(
                24 * 6 + 1);
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
                // tlcs.add(first);
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


    public boolean isPreview() {
        return preview;
    }

}


