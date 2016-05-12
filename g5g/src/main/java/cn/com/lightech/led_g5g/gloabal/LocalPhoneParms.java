package cn.com.lightech.led_g5g.gloabal;

/**
 * Created by 明 on 2016/3/31.
 */
public class LocalPhoneParms {

    private static int tableTitleHeight = -1;   //static变量
    private static int pageTitleHeight = -1;
    private static int mainTabHeight = -1;
    private static int leftHeightForTable = -1;

    private static int phoneHeight = -1;
    private static int phoneWidth = -1;

    public static int getPhoneHeight() {
        return phoneHeight;
    }

    public static void setPhoneHeight(int phoneHeight) {
        LocalPhoneParms.phoneHeight = phoneHeight;
    }

    public static int getPageTitleHeight() {
        return pageTitleHeight;
    }

    public static void setPageTitleHeight(int pageTitleHeight) {
        LocalPhoneParms.pageTitleHeight = pageTitleHeight;
    }

    public static int getTableTitleHeight() {
        return tableTitleHeight;
    }

    public static void setTableTitleHeight(int tableTitleHeight) {
        LocalPhoneParms.tableTitleHeight = tableTitleHeight;
    }

    public static int getMainTabHeight() {
        return mainTabHeight;
    }

    public static void setMainTabHeight(int mainTabHeight) {
        LocalPhoneParms.mainTabHeight = mainTabHeight;
    }

    public static int getLeftHeightForTable() {
        leftHeightForTable = phoneHeight - mainTabHeight - pageTitleHeight - tableTitleHeight;
        return leftHeightForTable;
    }

    public static int getPhoneWidth() {
        return phoneWidth;
    }

    public static void setPhoneWidth(int phoneWidth) {
        LocalPhoneParms.phoneWidth = phoneWidth;
    }


}