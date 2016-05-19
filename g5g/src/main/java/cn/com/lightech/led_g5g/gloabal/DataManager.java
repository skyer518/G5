package cn.com.lightech.led_g5g.gloabal;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;

import cn.com.lightech.led_g5g.entity.PackageId;
import cn.com.lightech.led_g5g.entity.data.CurveData;
import cn.com.lightech.led_g5g.entity.CurvePoint;
import cn.com.lightech.led_g5g.entity.DataNode;
import cn.com.lightech.led_g5g.entity.data.FlashData;
import cn.com.lightech.led_g5g.entity.LampState;
import cn.com.lightech.led_g5g.entity.data.ManualData;
import cn.com.lightech.led_g5g.entity.DataType;
import cn.com.lightech.led_g5g.entity.data.MoonData;
import cn.com.lightech.led_g5g.entity.TimeBucket;
import cn.com.lightech.led_g5g.net.ConnectManager;
import cn.com.lightech.led_g5g.net.entity.ChanelType;
import cn.com.lightech.led_g5g.net.entity.ConnState;
import cn.com.lightech.led_g5g.entity.LampChannel;
import cn.com.lightech.led_g5g.net.entity.Response;
import cn.com.lightech.led_g5g.net.socket.NetworkHelper;
import cn.com.lightech.led_g5g.net.utils.StringUtil;
import cn.com.lightech.led_g5g.net.ConnectionsManager;
import cn.com.lightech.led_g5g.utils.PreferenceUtils;

public class DataManager implements IDataListener {
    private static DataManager inst;

    private CurveData seedling;
    private CurveData clone;
    private CurveData vegetation;
    private CurveData flowering;
    private CurveData fruiting;
    private CurveData self;

    private ManualData manualData;
    private FlashData flashData;
    private MoonData moonData;
    private LampState state;


    public static DataManager getInstance() {
        synchronized (DataManager.class) {
            if (inst == null) {
                inst = new DataManager();
                ConnectionsManager.getInstance().registerHigh(inst, false);
            }

            return inst;
        }
    }

    public void initAllModeNode() {
        readDataFromFile();
    }

    private DataManager() {
        this.readDataFromFile();
    }

    public void switchGroup() {
        readDataFromFile();
    }

    // 取得对应组的字符串表示
    public static String getGroupFlagStr() {
        return StringUtil.encodeToHexString(NetworkHelper.GetWifiName());
    }


    /**
     * 将数据保存到内存，并持久化到文件
     *
     * @param node
     * @param updateTime 保存新数据时需要更新时间，同步数据时不需要
     * @return
     */
    public boolean saveManualDataNode(ManualData node, boolean updateTime) {
        setManualData(node);
        if (PreferenceUtils.saveToFile(node, updateTime)) {
            return true;
        }
        return false;

    }

    /**
     * 将数据保存到内存，并持久化到文件
     *
     * @param node
     * @param updateTime 保存新数据时需要更新时间，同步数据时不需要
     * @return
     */
    public boolean saveMoonDataNode(MoonData node, boolean updateTime) {
        setMoonData(node);
        return true;
//        if (PreferenceUtils.saveToFile(node, updateTime)) {
//            return true;
//        }
//        return false;

    }


    public FlashData getFlashData() {
        return flashData;
    }

    public boolean saveFlashDataNode(FlashData flashData, boolean updateTime) {
//        if (PreferenceUtils.saveToFile(flashData, updateTime)) {
        this.flashData = flashData;
        return true;
//        }
//        return false;
    }

    /**
     * 完成从文件中读取数据到内存中，如DataManage初始化是调用
     *
     * @return
     */
    private boolean readDataFromFile() {
        manualData = loadDefaultManual();
        // 读 Auto
        clone = loadDefaultAuto(PackageId.Clone, Constants.DEFAULT_CLONE_TIMING, Constants.DEFAULT_CLONE);
        seedling = loadDefaultAuto(PackageId.Seedling, Constants.DEFAULT_SEEDLING_TIMING, Constants.DEFAULT_SEEDLING);
        vegetation = loadDefaultAuto(PackageId.Vegetation, Constants.DEFAULT_VEGETATION_TIMING, Constants.DEFAULT_VEGETATION);
        flowering = loadDefaultAuto(PackageId.Flowering, Constants.DEFAULT_FLOWERING_TIMING, Constants.DEFAULT_FLOWERING);
        fruiting = loadDefaultAuto(PackageId.Fruiting, Constants.DEFAULT_FRUITING_TIMING, Constants.DEFAULT_FRUITING);
        self = loadDefaultAuto(PackageId.Self, Constants.DEFAULT_SELF_TIMING, Constants.DEFAULT_SELF);


        // 读 flash
        flashData = loadDefaultFlash();

        // 读 moon
        moonData = loadDefaultMoon();

        return true;
    }


    public int getRegistrationStatus() {
        SharedPreferences sp = App.getInstance().getSharedPreferences(
                Constants.RegisterFileName, Activity.MODE_PRIVATE);
        int ret = sp.getInt("reg", 0);
        return ret;
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void onConnectStateChanged(ConnState connState, ConnectManager connectManager) {
        switch (connState) {
            case Connected:
                DataManager.getInstance().switchGroup();
                // showSyncDataDialog();
                break;
        }

    }

    @Override
    public boolean onReceive(Response response, ConnectManager connectManager) {
        // TODO 自动生成的方法存根
        return true;
    }


    public boolean saveDataNode(DataNode wn, boolean updateTime) {
        switch (wn.getDataType()) {
            case Curve:
                setCurveDataById2((CurveData) wn);
                return true;
            case Flash:
                return saveFlashDataNode((FlashData) wn, updateTime);
            case Instant:
                return saveManualDataNode((ManualData) wn, updateTime);
            case Moon:
                return saveMoonDataNode((MoonData) wn, updateTime);
            case Timing:
                setCurveDataById2((CurveData) wn);
                return true;
        }
        return false;
    }


    public void backDefaultAuto(int id2) {
        switch (id2) {
            case 0x00:
                seedling = loadDefaultAuto(PackageId.Seedling, Constants.DEFAULT_SEEDLING_TIMING, Constants.DEFAULT_SEEDLING);
                break;
            case 0x05:
                clone = loadDefaultAuto(PackageId.Clone, Constants.DEFAULT_CLONE_TIMING, Constants.DEFAULT_CLONE);
                break;
            case 0x07:
                vegetation = loadDefaultAuto(PackageId.Vegetation, Constants.DEFAULT_VEGETATION_TIMING, Constants.DEFAULT_VEGETATION);
                break;
            case 0x09:
                flowering = loadDefaultAuto(PackageId.Flowering, Constants.DEFAULT_FLOWERING_TIMING, Constants.DEFAULT_FLOWERING);
                break;
            case 0x0b:
                fruiting = loadDefaultAuto(PackageId.Fruiting, Constants.DEFAULT_FRUITING_TIMING, Constants.DEFAULT_FRUITING);
                break;
            case 0x0d:
                self = loadDefaultAuto(PackageId.Self, Constants.DEFAULT_SELF_TIMING, Constants.DEFAULT_SELF);
                break;
        }
    }


    public boolean backDefaultManual(DataType dataType) {
        manualData = DataManager.loadDefaultManual();
        return PreferenceUtils.saveToFile(manualData, false);
    }


    public boolean backDefaultFlash() {
        flashData = DataManager.loadDefaultFlash();
        return PreferenceUtils.saveToFile(flashData, false);
    }

    public boolean backDefaultMoon() {
        moonData = DataManager.loadDefaultMoon();
        return PreferenceUtils.saveToFile(moonData, false);
    }


    public static FlashData loadDefaultFlash() {
        FlashData node = new FlashData();
        int[][] times = Constants.DEFAULT_FLASH;
        node.setTime1(new TimeBucket(times[0][0], times[0][1]));
        node.setTime2(new TimeBucket(times[1][0], times[1][1]));
        node.setTime3(new TimeBucket(times[2][0], times[2][1]));

        return node;
    }

    public static MoonData loadDefaultMoon() {
        MoonData node = new MoonData();
        node.setTime(new TimeBucket(Constants.DEFAULT_MOON[0], Constants.DEFAULT_MOON[1]));
        return node;
    }

    public static CurveData loadDefaultAuto(byte[] ids, int[][] times, int[][] lamps) {
        CurveData autoNode = new CurveData(ids);
        for (int i = 0; i < times.length; i++) {
            int[] time = times[i];
            int[] lamp = lamps[i];
            autoNode.getPoints().add(new CurvePoint(time[0], time[1], new LampChannel(lamp[0], lamp[1], lamp[2], lamp[3], lamp[4])));
        }
        return autoNode;
    }


    public static ManualData loadDefaultManual() {
        ManualData node;
        int[] lamp = Constants.DEFAULT_MANUAL;
        node = new ManualData();
        LampChannel lampChannel2 = new LampChannel();
        for (int i = 0; i < ChanelType.values().length; i++) {
            lampChannel2.setData(ChanelType.values()[i], (byte) lamp[i]);
        }
        node.setChannel(lampChannel2);
        return node;
    }


    public void setCurveDataById2(CurveData curveData) {
        switch (curveData.getId2()) {
            case 0x00:
                seedling = curveData;
                break;
            case 0x05:
                clone = curveData;
                break;
            case 0x07:
                vegetation = curveData;
                break;
            case 0x09:
                flowering = curveData;
                break;
            case 0x0b:
                fruiting = curveData;
                break;
            case 0x0d:
                self = curveData;
                break;

            case 0x01:
                seedling = setTiming(seedling, curveData);
                break;
            case 0x06:
                clone = setTiming(clone, curveData);
                break;
            case 0x08:
                vegetation = setTiming(vegetation, curveData);
                break;
            case 0x0a:
                flowering = setTiming(flowering, curveData);
                break;
            case 0x0c:
                fruiting = setTiming(fruiting, curveData);
                break;
            case 0x0e:
                self = setTiming(self, curveData);
                break;
        }

    }


    private CurveData setTiming(CurveData curveData, CurveData curveTiming) {

        List<CurvePoint> points = curveTiming.getPoints();
        List<CurvePoint> dataPoints = curveData.getPoints();

        for (int i = 0; i < points.size(); i++) {
            CurvePoint point = points.get(i);
            try {
                point.setChannel(dataPoints.get(i).getChannel());
            } catch (IndexOutOfBoundsException e) {
                point.setChannel(new LampChannel());
            }
        }
        return curveTiming;
    }


    /**
     * 取对应包id2的时间曲线或数据曲线
     *
     * @param id2
     * @return
     */
    public CurveData getCurveDataById2(byte id2) {
        CurveData curveData = null;
        switch (id2) {
            case 0x00:
                curveData = seedling;
                break;
            case 0x05:
                curveData = clone;
                break;
            case 0x07:
                curveData = vegetation;
                break;
            case 0x09:
                curveData = flowering;
                break;
            case 0x0b:
                curveData = fruiting;
                break;
            case 0x0d:
                curveData = self;
                break;

            case 0x01:
                curveData = new CurveData(PackageId.Seedling_Timing[0], PackageId.Seedling_Timing[1]);
                curveData.setPoints(seedling.getPoints());
                break;
            case 0x06:
                curveData = new CurveData(PackageId.Clone_Timing[0], PackageId.Clone_Timing[1]);
                curveData.setPoints(clone.getPoints());
                break;
            case 0x08:
                curveData = new CurveData(PackageId.Vegetation_Timing[0], PackageId.Vegetation_Timing[1]);
                curveData.setPoints(vegetation.getPoints());
                break;
            case 0x0a:
                curveData = new CurveData(PackageId.Flowering_Timing[0], PackageId.Flowering_Timing[1]);
                curveData.setPoints(flowering.getPoints());
                break;
            case 0x0c:
                curveData = new CurveData(PackageId.Fruiting_Timing[0], PackageId.Fruiting_Timing[1]);
                curveData.setPoints(fruiting.getPoints());
                break;
            case 0x0e:
                curveData = new CurveData(PackageId.Self_Timing[0], PackageId.Self_Timing[1]);
                curveData.setPoints(self.getPoints());
                break;
        }
        return curveData;
    }

    public LampState getState() {
        return state;
    }

    public void setState(LampState state) {
        this.state = state;
    }

    public CurveData getSeedling() {
        return seedling;
    }

    public void setSeedling(CurveData seedling) {
        this.seedling = seedling;
    }

    public CurveData getClone() {
        return clone;
    }

    public void setClone(CurveData clone) {
        this.clone = clone;
    }

    public CurveData getVegetation() {
        return vegetation;
    }

    public void setVegetation(CurveData vegetation) {
        this.vegetation = vegetation;
    }

    public CurveData getFlowering() {
        return flowering;
    }

    public void setFlowering(CurveData flowering) {
        this.flowering = flowering;
    }

    public CurveData getFruiting() {
        return fruiting;
    }

    public void setFruiting(CurveData fruiting) {
        this.fruiting = fruiting;
    }

    public CurveData getSelf() {
        return self;
    }

    public void setSelf(CurveData self) {
        this.self = self;
    }

    public void setFlashData(FlashData flashData) {
        this.flashData = flashData;
    }

    public ManualData getManualData() {
        return manualData;

    }

    public void setManualData(ManualData node) {
        manualData = node;
    }

    public void setMoonData(MoonData node) {
        moonData = node;
    }

    public MoonData getMoonData() {
        return moonData;
    }

}
