package cn.com.lightech.led_g5g.gloabal;


import cn.com.lightech.led_g5g.R;

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

//    // DataType
//    public static final int[] MODEL = new int[]{R.string.app_model_auto, R.string.app_model_manual};


    // 预设的 日程模式 及曲线数据
    public static final int[][] DEFAULT_SEEDLING_TIMING = {{0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0},
            {7, 0}, {8, 0}, {9, 0}, {10, 0}, {11, 0}, {12, 0}, {13, 0}, {14, 0}, {15, 0}, {16, 0}, {17, 0}, {18, 0}, {19, 0},
            {20, 0}, {21, 0}, {22, 0}, {23, 0}};
    public static final int[][] DEFAULT_SEEDLING = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {5, 10, 5, 50, 0}, {10, 15, 10, 100, 0}, {15, 20, 10, 100, 0}, {20, 25, 10, 100, 0}, {20, 35, 10, 100, 0}, {25, 45, 10, 100, 0}, {35, 55, 15, 100, 0}, {35, 65, 15, 85, 0}, {40, 75, 15, 70, 0}, {50, 85, 20, 55, 0}, {85, 100, 20, 40, 0}, {85, 100, 30, 25, 10}, {85, 100, 35, 10, 25}, {85, 100, 30, 0, 40}, {75, 100, 25, 0, 55}, {65, 100, 25, 0, 70}, {55, 95, 25, 0, 85}, {45, 75, 20, 0, 100}, {35, 55, 15, 0, 100}, {25, 35, 15, 0, 100}, {15, 20, 10, 0, 100}, {0, 0, 0, 0, 100}};


    // 预设的 日程模式 及曲线数据
    public static final int[][] DEFAULT_CLONE_TIMING = {{0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0},
            {7, 0}, {8, 0}, {9, 0}, {10, 0}, {11, 0}, {12, 0}, {13, 0}, {14, 0}, {15, 0}, {16, 0}, {17, 0}, {18, 0}, {19, 0},
            {20, 0}, {21, 0}, {22, 0}, {23, 0}};
    public static final int[][] DEFAULT_CLONE = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {5, 10, 5, 50, 0}, {10, 15, 10, 100, 0}, {15, 20, 10, 100, 0}, {20, 25, 10, 100, 0}, {20, 35, 10, 100, 0}, {25, 45, 10, 100, 0}, {35, 55, 15, 100, 0}, {35, 65, 15, 85, 0}, {40, 75, 15, 70, 0}, {50, 85, 20, 55, 0}, {85, 100, 20, 40, 0}, {85, 100, 30, 25, 10}, {85, 100, 35, 10, 25}, {85, 100, 30, 0, 40}, {75, 100, 25, 0, 55}, {65, 100, 25, 0, 70}, {55, 95, 25, 0, 85}, {45, 75, 20, 0, 100}, {35, 55, 15, 0, 100}, {25, 35, 15, 0, 100}, {15, 20, 10, 0, 100}, {0, 0, 0, 0, 100}};


    // 预设的 日程模式 及曲线数据
    public static final int[][] DEFAULT_VEGETATION_TIMING = {{0, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0},
            {7, 0}, {8, 0}, {9, 0}, {10, 0}, {11, 0}, {12, 0}, {13, 0}, {14, 0}, {15, 0}, {16, 0}, {17, 0}, {18, 0}, {19, 0},
            {20, 0}, {21, 0}, {22, 0}, {23, 0}};
    public static final int[][] DEFAULT_VEGETATION = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {20, 35, 15, 50, 0}, {35, 50, 25, 100, 0}, {60, 65, 35, 100, 0}, {85, 80, 45, 100, 0}, {100, 100, 65, 100, 0}, {100, 100, 85, 100, 0}, {100, 100, 100, 100, 0}, {100, 100, 100, 100, 10}, {100, 100, 100, 100, 25}, {100, 100, 100, 85, 40}, {100, 100, 100, 70, 55}, {100, 100, 100, 55, 70}, {100, 100, 80, 40, 85}, {85, 95, 60, 25, 100}, {60, 65, 50, 5, 100}, {35, 35, 40, 0, 100}, {20, 30, 30, 0, 100}, {10, 20, 20, 0, 100}, {0, 0, 0, 0, 100}, {0, 0, 0, 0, 0}};

    // 预设的 日程模式 及曲线数据
    public static final int[][] DEFAULT_FLOWERING_TIMING = {{0, 0}, {5, 0}, {6, 0},
            {7, 0}, {8, 0}, {9, 0}, {10, 0}, {11, 0}, {12, 0}, {13, 0}, {14, 0}, {15, 0}, {16, 0}, {17, 0}, {18, 0}, {19, 0}};
    public static final int[][] DEFAULT_FLOWERING = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 50, 0}, {55, 35, 75, 100, 0}, {95, 65, 100, 100, 0},
            {100, 95, 100, 100, 15}, {100, 95, 100, 100, 30}, {100, 100, 100, 100, 45}, {100, 100, 100, 100, 60}, {100, 100, 100, 100, 75}, {100, 95, 100, 100, 90},
            {100, 90, 100, 80, 100}, {100, 85, 100, 60, 100}, {100, 75, 100, 40, 100}, {65, 45, 85, 20, 100}, {0, 0, 0, 0, 0}};


    // 预设的 日程模式 及曲线数据
    public static final int[][] DEFAULT_FRUITING_TIMING = {{0, 0}, {5, 0}, {6, 0},
            {7, 0}, {8, 0}, {9, 0}, {10, 0}, {11, 0}, {12, 0}, {13, 0}, {14, 0}, {15, 0}, {16, 0}, {17, 0}, {18, 0}, {19, 0}};
    public static final int[][] DEFAULT_FRUITING = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {0, 0, 0, 50, 0}, {55, 35, 75, 100, 0}, {95, 65, 100, 100, 0}, {100, 95, 100, 100, 15},
            {100, 95, 100, 100, 30}, {100, 100, 100, 100, 45}, {100, 100, 100, 100, 60}, {100, 100, 100, 100, 75}, {100, 95, 100, 100, 90}, {100, 90, 100, 80, 100},
            {100, 85, 100, 60, 100}, {100, 75, 100, 40, 100}, {65, 45, 85, 20, 100}, {0, 0, 0, 0, 0}};


    // 预设的 日程模式 及曲线数据
    public static final int[][] DEFAULT_SELF_TIMING = {{0, 0}, {6, 0},
            {7, 0}, {9, 0}, {11, 0}, {16, 0}, {17, 0}, {19, 0},
            {20, 0}, {21, 0}};
    public static final int[][] DEFAULT_SELF = {{0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}, {30, 15, 10, 8, 8}, {30, 15, 10, 8, 8},
            {80, 35, 20, 15, 15}, {80, 35, 20, 15, 15},
            {50, 25, 10, 8, 8}, {5, 3, 10, 3, 0}, {5, 3, 10, 3, 0},
            {0, 0, 0, 0, 0}};


    // 预设的 Instant model
    public static final int[] DEFAULT_MANUAL = {80, 50, 40, 20, 10};
    // 预设的 Flash
    public static final int[][] DEFAULT_FLASH = {{150, 205}, {500, 700}, {900, 1200}};
    //  预设的 Moon
    public static final int[] DEFAULT_MOON = {850, 205};
    // 预设的 造浪数据


    // 灯通道 Color
    public static final int[] CH_COLORl = new int[]{R.color.ch_color_blue,
            R.color.ch_color_white, R.color.ch_color_purple,
            R.color.ch_color_green, R.color.ch_color_red};

    public static final long DEFAULT_UNIX_TIME = 1;// 固定模式的unix时间


}
