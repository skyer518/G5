package cn.com.lightech.led_g5w.gloabal;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.Arrays;

import cn.com.lightech.led_g5w.entity.AutoDataNode;
import cn.com.lightech.led_g5w.entity.CurvePoint;
import cn.com.lightech.led_g5w.entity.DataNode;
import cn.com.lightech.led_g5w.entity.FlashDataNode;
import cn.com.lightech.led_g5w.entity.LampState;
import cn.com.lightech.led_g5w.entity.ManualDataNode;
import cn.com.lightech.led_g5w.entity.Mode;
import cn.com.lightech.led_g5w.entity.MoonDataNode;
import cn.com.lightech.led_g5w.entity.TimeBucket;
import cn.com.lightech.led_g5w.net.ConnectManager;
import cn.com.lightech.led_g5w.net.entity.ChanelType;
import cn.com.lightech.led_g5w.net.entity.ConnState;
import cn.com.lightech.led_g5w.entity.LampChannel;
import cn.com.lightech.led_g5w.net.entity.Response;
import cn.com.lightech.led_g5w.net.socket.NetworkHelper;
import cn.com.lightech.led_g5w.net.utils.StringUtil;
import cn.com.lightech.led_g5w.net.ConnectionsManager;
import cn.com.lightech.led_g5w.utils.PreferenceUtils;

public class DataManager implements IDataListener {
    private static DataManager inst;

    private AutoDataNode autoDataNode;

    private ManualDataNode manualDataNode;

    private FlashDataNode flashDataNode;
    private MoonDataNode moonDataNode;
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
     * 获取模式的默认值
     */
    public int[][] getDefaultData() {
        int[][] array = new int[Constants.LED_NUM][Constants.HOUR_NUM];
        for (int i = 0; i < array.length; i++)
            Arrays.fill(array[i], 0);
        return array;
    }

    public ManualDataNode getManualDataNode() {
        return manualDataNode;

    }

    public void setManualDataNode(ManualDataNode node) {
        manualDataNode = node;
    }

    public void setMoonDataNode(MoonDataNode node) {
        moonDataNode = node;
    }

    public MoonDataNode getMoonDataNode() {
        return moonDataNode;
    }

    public DataNode getDataNode(Mode mode) {
        switch (mode) {
            case Auto:
                return getAutoDataNode();
            case Manual:
                return getManualDataNode();
            case Flash:
                return getFlashDataNode();
            case Moon:
                return getMoonDataNode();
            case AutoTiming:
                return getAutoTimingDataNode();
        }
        return null;
    }

    /**
     * 将数据保存到内存，并持久化到文件
     *
     * @param node
     * @param updateTime 保存新数据时需要更新时间，同步数据时不需要
     * @return
     */
    public boolean saveManualDataNode(ManualDataNode node, boolean updateTime) {
        setManualDataNode(node);
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
    public boolean saveMoonDataNode(MoonDataNode node, boolean updateTime) {
        setMoonDataNode(node);
        if (PreferenceUtils.saveToFile(node, updateTime)) {
            return true;
        }
        return false;

    }

    /**
     * 获得对应的AutoDataNode，可能为null
     *
     * @return 还未设置返回null
     */
    public AutoDataNode getAutoDataNode() {
        return autoDataNode;
    }

    /**
     * 将数据保存到内存，并持久化到文件
     *
     * @param node
     * @param updateTime 保存新数据时需要更新时间，同步数据时不需要
     * @return
     */
    public boolean saveAutoDataNode(AutoDataNode node, boolean updateTime) {
        setAutoDataNode(node);
        if (PreferenceUtils.saveToFile(node, updateTime)) {
            return true;
        }
        return false;

    }


    public FlashDataNode getFlashDataNode() {
        return flashDataNode;
    }

    public boolean saveFlashDataNode(FlashDataNode flashDataNode, boolean updateTime) {
        if (PreferenceUtils.saveToFile(flashDataNode, updateTime)) {
            this.flashDataNode = flashDataNode;
            return true;
        }
        return false;
    }

    /**
     * 完成从文件中读取数据到内存中，如DataManage初始化是调用
     *
     * @return
     */
    private boolean readDataFromFile() {
        // 读 Manual
        Mode scheduleMode = null;
        manualDataNode = PreferenceUtils.readManualDataFromFile();
        // 读 Auto

        autoDataNode = PreferenceUtils.readAutoDataFromFile();
        // 读 flash
        flashDataNode = PreferenceUtils.readFlashDataFromFile();

        // 读 moon
        moonDataNode = PreferenceUtils.readMoonDataFromFile();

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
        switch (wn.getScheduleMode()) {
            case Auto:
                return saveAutoDataNode((AutoDataNode) wn, updateTime);
            case Flash:
                return saveFlashDataNode((FlashDataNode) wn, updateTime);
            case Manual:
                return saveManualDataNode((ManualDataNode) wn, updateTime);
            case Moon:
                return saveMoonDataNode((MoonDataNode) wn, updateTime);
        }
        return false;
    }


    public boolean backDefaultAuto() {
        autoDataNode = DataManager.loadDefaultAuto();
        return PreferenceUtils.saveToFile(autoDataNode, false);
    }


    public boolean backDefaultManual(Mode mode) {
        manualDataNode = DataManager.loadDefaultManual();
        return PreferenceUtils.saveToFile(manualDataNode, false);
    }


    public boolean backDefaultFlash() {
        flashDataNode = DataManager.loadDefaultFlash();
        return PreferenceUtils.saveToFile(flashDataNode, false);
    }

    public boolean backDefaultMoon() {
        moonDataNode = DataManager.loadDefaultMoon();
        return PreferenceUtils.saveToFile(moonDataNode, false);
    }


    public void setAutoDataNode(AutoDataNode autoDataNode) {
        this.autoDataNode = autoDataNode;
    }


    public static FlashDataNode loadDefaultFlash() {
        FlashDataNode node = new FlashDataNode();
        int[][] times = Constants.DEFAULT_FLASH;
        node.setTime1(new TimeBucket(times[0][0], times[0][1]));
        node.setTime2(new TimeBucket(times[1][0], times[1][1]));
        node.setTime3(new TimeBucket(times[2][0], times[2][1]));

        return node;
    }

    public static MoonDataNode loadDefaultMoon() {
        MoonDataNode node = new MoonDataNode();
        node.setTime(new TimeBucket(Constants.DEFAULT_MOON[0], Constants.DEFAULT_MOON[1]));
        return node;
    }

    public static AutoDataNode loadDefaultAuto() {
        AutoDataNode autoNode = new AutoDataNode();
        int[][] times = Constants.DEFAULT_AUTO_TIMING;
        int[][] lamps = Constants.DEFAULT_AUTO;
        for (int i = 0; i < times.length; i++) {
            int[] time = times[i];
            int[] lamp = lamps[i];
            autoNode.getPoints().add(new CurvePoint(time[0], time[1], new LampChannel(lamp[0], lamp[1], lamp[2], lamp[3], lamp[4])));
        }
        return autoNode;
    }


    public static ManualDataNode loadDefaultManual() {
        ManualDataNode node;
        int[] lamp = Constants.DEFAULT_MANUAL;
        node = new ManualDataNode();
        LampChannel lampChannel2 = new LampChannel();
        for (int i = 0; i < ChanelType.values().length; i++) {
            lampChannel2.setData(ChanelType.values()[i], (byte) lamp[i]);
        }
        node.setChannel(lampChannel2);
        return node;
    }


    public AutoDataNode getAutoTimingDataNode() {
        AutoDataNode dataNode = new AutoDataNode(Mode.AutoTiming);
        dataNode.setPoints(autoDataNode.getPoints());
        return dataNode;
    }

    public LampState getState() {
        return state;
    }

    public void setState(LampState state) {
        this.state = state;
    }
}
