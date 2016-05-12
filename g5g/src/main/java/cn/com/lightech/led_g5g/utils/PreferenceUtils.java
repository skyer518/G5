package cn.com.lightech.led_g5g.utils;

import android.content.Context;
import android.content.SharedPreferences;

import cn.com.lightech.led_g5g.entity.data.CurveData;
import cn.com.lightech.led_g5g.entity.CurvePoint;
import cn.com.lightech.led_g5g.entity.data.FlashData;
import cn.com.lightech.led_g5g.entity.data.ManualData;
import cn.com.lightech.led_g5g.entity.data.MoonData;
import cn.com.lightech.led_g5g.entity.TimeBucket;
import cn.com.lightech.led_g5g.net.entity.ChanelType;
import cn.com.lightech.led_g5g.gloabal.App;
import cn.com.lightech.led_g5g.gloabal.DataManager;
import cn.com.lightech.led_g5g.gloabal.Constants;
import cn.com.lightech.led_g5g.entity.LampChannel;
import cn.com.lightech.led_g5g.net.ConnectionsManager;

/**
 * Created by 明 on 2016/3/25.
 */
public class PreferenceUtils {

    public static String genLampkey(int pointPosition, int chanelPosition) {
        StringBuilder sb = new StringBuilder(LampChannel.class.getName());
        sb.append("_").append(pointPosition).append("_").append(chanelPosition);
        return sb.toString();
    }


    public static String genCurvePointKey(int pointPosition) {
        StringBuilder sb = new StringBuilder(CurvePoint.class.getName());
        return sb.append("_").append(pointPosition).toString();
    }

    private static String genLampkey(int chanelPosition) {
        StringBuilder sb = new StringBuilder(LampChannel.class.getName());
        return sb.append("_").append(chanelPosition).toString();
    }

    private static String genStartTimeKey(int j) {
        StringBuilder sb = new StringBuilder(Integer.class.getName());
        return sb.append("_").append(j).toString();
    }

//
//    public static CurveData readAutoDataFromFile() {
//        long saveTime = -1;
//        CurveData node = null;
//        String filename = getFileName(CurveData.class.getName());
//        SharedPreferences sp = App.getInstance().getSharedPreferences(
//                filename, Context.MODE_PRIVATE);
//        saveTime = sp.getLong("unixtime", saveTime);
//        // 读ScheduleMode
//        if (saveTime == -1) {
//            // 未保存过
//            node = DataManager.loadDefaultAuto();
//
//        } else {
//            node = new CurveData();
//            node.setUnixTime(saveTime);
//            // TODO
//            for (int i = 0; i < Constants.HOUR_NUM; ++i) {
//                CurvePoint cp = new CurvePoint();
//                int time = sp.getInt(genCurvePointKey(i), -1);
//                if (time == -1) {
//                    continue;
//                }
//                cp.setTime(time);
//                LampChannel lc = new LampChannel();
//                for (int j = 0; j < ChanelType.values().length; j++) {
//                    lc.setData(ChanelType.values()[j], (byte) sp.getInt(genLampkey(i, j), 0));//.setData(j, (byte) sp.getInt(genLampkey(i, j), 0));
//                    cp.setChannel(lc);
//                }
//                node.addPoint(cp);
//
//            }
//
//        }
//        return node;
//    }
//
//    public static ManualData readManualDataFromFile() {
//        long saveTime = -1;
//        ManualData node = null;
//        String filename = getFileName(ManualData.class.getName());
//        SharedPreferences sp = App.getInstance().getSharedPreferences(
//                filename, Context.MODE_PRIVATE);
//        saveTime = sp.getLong("unixtime", saveTime);
//        // 读ScheduleMode
//        if (saveTime == -1) {
//            // 未保存过
//            node = DataManager.loadDefaultManual();
//        } else {
//            node = new ManualData();
//            node.setUnixTime(saveTime);
//            // TODO
//            LampChannel lc = new LampChannel();
//            for (int j = 0; j < ChanelType.values().length; j++) {
//                lc.setData(ChanelType.values()[j], (byte) sp.getInt(genLampkey(j), 0));//.setData(j, (byte) sp.getInt(genLampkey(i, j), 0));
//            }
//            node.setChannel(lc);
//        }
//        return node;
//    }
//
//    public static FlashData readFlashDataFromFile() {
//        long saveTime = -1;
//        FlashData node = null;
//        String filename = getFileName(FlashData.class.getName());
//        SharedPreferences sp = App.getInstance().getSharedPreferences(
//                filename, Context.MODE_PRIVATE);
//        saveTime = sp.getLong("unixtime", saveTime);
//        // 读ScheduleMode
//        if (saveTime == -1) {
//            // 未保存过
//            node = DataManager.loadDefaultFlash();
//        } else {
//            node = new FlashData();
//            node.setUnixTime(saveTime);
//            // TODO
//            node.setTime1(new TimeBucket(sp.getInt(genLampkey(1), 0), sp.getInt(genLampkey(-1), 0)));
//            node.setTime1(new TimeBucket(sp.getInt(genLampkey(2), 0), sp.getInt(genLampkey(-2), 0)));
//            node.setTime1(new TimeBucket(sp.getInt(genLampkey(3), 0), sp.getInt(genLampkey(-3), 0)));
//        }
//        return node;
//    }
//
//
//    public static MoonData readMoonDataFromFile() {
//        long saveTime = -1;
//        MoonData node = null;
//        String filename = getFileName(MoonData.class.getName());
//        SharedPreferences sp = App.getInstance().getSharedPreferences(
//                filename, Context.MODE_PRIVATE);
//        saveTime = sp.getLong("unixtime", saveTime);
//        // 读ScheduleMode
//        if (saveTime == -1) {
//            // 未保存过
//            node = DataManager.loadDefaultMoon();
//        } else {
//            node = new MoonData();
//            node.setUnixTime(saveTime);
//            // TODO
//            node.setLastFullMoonDay(sp.getInt("lastFullMoonDay", 1));
//            node.setTime(new TimeBucket(sp.getInt(genLampkey(1), 0), sp.getInt(genLampkey(-1), 0)));
//        }
//        return node;
//    }


    public static boolean saveToFile(CurveData node, boolean updateUnixTime) {

        if (!ConnectionsManager.getInstance().isConnected(false)) {
            return false;
        }

        String filename = getFileName(CurveData.class.getName());
        SharedPreferences sp = App.getInstance().getSharedPreferences(
                filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        long curTime = node.getUnixTime();
        if (updateUnixTime)
            curTime = System.currentTimeMillis() / 1000;
        edit.putLong("unixtime", curTime);
        for (int i = 0; i < node.getPoints().size(); ++i) {

            CurvePoint cp = node.getPoints().get(i);
            if (cp == null)
                continue;

            edit.putInt(PreferenceUtils.genCurvePointKey(i), cp.getTime());
            for (int j = 0; j < ChanelType.values().length; j++) {
                edit.putInt(PreferenceUtils.genLampkey(i, j), cp.getChannel().getData(ChanelType.values()[j]));
            }
        }

        boolean success = edit.commit();
        if (!success)
            return false;

        node.setUnixTime(curTime);
        return true;
    }

    public static boolean saveToFile(ManualData node, boolean updateUnixTime) {

        if (!ConnectionsManager.getInstance().isConnected(false)) {
            return false;
        }

        String filename = getFileName(ManualData.class.getName());
        SharedPreferences sp = App.getInstance().getSharedPreferences(
                filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        long curTime = node.getUnixTime();
        if (updateUnixTime)
            curTime = System.currentTimeMillis() / 1000;
        edit.putLong("unixtime", curTime);

        for (int j = 0; j < Constants.LED_NUM; j++) {
            edit.putInt(PreferenceUtils.genLampkey(j), node.getChannel().getData(ChanelType.values()[j]));
        }

        boolean success = edit.commit();
        if (!success)
            return false;

        node.setUnixTime(curTime);
        return true;

    }


    public static boolean saveToFile(MoonData node, boolean updateUnixTime) {
        if (!ConnectionsManager.getInstance().isConnected(false)) {
            return false;
        }

        String filename = getFileName(MoonData.class.getName());
        SharedPreferences sp = App.getInstance().getSharedPreferences(
                filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        long curTime = node.getUnixTime();
        if (updateUnixTime)
            curTime = System.currentTimeMillis() / 1000;
        edit.putLong("unixtime", curTime);
        edit.putInt("lastFullMoonDay", node.getLastFullMoonDay());
        edit.putInt(PreferenceUtils.genStartTimeKey(1), node.getTime().getStart());
        edit.putInt(PreferenceUtils.genStartTimeKey(-1), node.getTime().getEnd());

        boolean success = edit.commit();
        if (!success)
            return false;

        node.setUnixTime(curTime);
        return true;
    }


    public static boolean saveToFile(FlashData node, boolean updateUnixTime) {
        if (!ConnectionsManager.getInstance().isConnected(false)) {
            return false;
        }

        String filename = getFileName(MoonData.class.getName());
        SharedPreferences sp = App.getInstance().getSharedPreferences(
                filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        long curTime = node.getUnixTime();
        if (updateUnixTime)
            curTime = System.currentTimeMillis() / 1000;
        edit.putLong("unixtime", curTime);

        edit.putInt(PreferenceUtils.genStartTimeKey(1), node.getTime1().getStart());
        edit.putInt(PreferenceUtils.genStartTimeKey(-1), node.getTime1().getEnd());
        edit.putInt(PreferenceUtils.genStartTimeKey(2), node.getTime2().getStart());
        edit.putInt(PreferenceUtils.genStartTimeKey(-2), node.getTime2().getEnd());
        edit.putInt(PreferenceUtils.genStartTimeKey(3), node.getTime3().getStart());
        edit.putInt(PreferenceUtils.genStartTimeKey(-3), node.getTime3().getEnd());

        boolean success = edit.commit();
        if (!success)
            return false;

        node.setUnixTime(curTime);
        return true;
    }


    public static String getFileName(String className) {
        return DataManager.getGroupFlagStr() + className;
    }

    public static void saveWifiConfig(String SSID, String password) {
        SharedPreferences sp = App.getInstance().getSharedPreferences(Constants.SHAREDPREFERENCES_WIFICONFIGURATION, Context.MODE_PRIVATE);
        sp.edit().putString(SSID, password).commit();
    }

    public static void removeWifiConfig(String SSID) {
        SharedPreferences sp = App.getInstance().getSharedPreferences(Constants.SHAREDPREFERENCES_WIFICONFIGURATION, Context.MODE_PRIVATE);
        sp.edit().remove(SSID).commit();
    }

    public static String getWifiPassword(String SSID) {
        SharedPreferences sp = App.getInstance().getSharedPreferences(Constants.SHAREDPREFERENCES_WIFICONFIGURATION, Context.MODE_PRIVATE);
        return sp.getString(SSID, null);
    }
}
