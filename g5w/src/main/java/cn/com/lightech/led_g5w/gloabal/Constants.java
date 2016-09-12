package cn.com.lightech.led_g5w.gloabal;


import cn.com.lightech.led_g5w.R;

public final class Constants {
    public final static int CHART_X_MAX = 24 * 60 + 1;
    public final static int HOUR_NUM = 24;
    public final static int LED_NUM = 5;
    public final static int LED_PORT = 8080;

    public final static String SHAREDPREFERENCES_WIFICONFIGURATION = "TempWifiConfig";

    public final static int EFFTCT_NUM = 3;
    public final static int MODE_NUM = 2;

    public final static String RegisterFileName = "LedRegister";
    public final static String PreferenFileName = "LedPreference";
    public final static String AppVersion = "AppVersion";
    public final static String DEFAULT_IP = "192.168.16.254";
    public final static int DEFAULT_PORT = 8080;

    public final static String LED_KEY_NAME = "LED";
    public static final String DATA_DELIMITER = ":";

    // Mode
    public static final int[] MODEL = new int[]{R.string.app_model_auto, R.string.app_model_manual};


    // 预设的 日程模式 及曲线数据
    public static final int[][] DEFAULT_AUTO_TIMING = {{0, 0}, {6, 0},
            {7, 0}, {9, 0}, {11, 0}, {16, 0}, {17, 0}, {19, 0},
            {20, 0}, {21, 0}};
    public static final int[][] DEFAULT_AUTO = {{0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}, {30, 15, 10, 8, 8}, {30, 15, 10, 8, 8},
            {80, 35, 20, 15, 15}, {80, 35, 20, 15, 15},
            {50, 25, 10, 8, 8}, {5, 3, 10, 3, 0}, {5, 3, 10, 3, 0},
            {0, 0, 0, 0, 0}};


    // 预设的 Manual model
    public static final int[] DEFAULT_MANUAL = {80, 50, 40, 20, 10};
    // 预设的 Flash
    public static final int[][] DEFAULT_FLASH = {{150, 205}, {500, 700}, {900, 1200}};
    //  预设的 Moon
    public static final int[] DEFAULT_MOON = {850, 205};
    // 预设的 造浪数据


    // 灯通道 Color
    public static final int[] CH_COLORl = new int[]{R.color.ch_color_1, R.color.ch_color_3,
            R.color.ch_color_2, R.color.ch_color_4, R.color.ch_color_5};

    public static final long DEFAULT_UNIX_TIME = 1;// 固定模式的unix时间


}
