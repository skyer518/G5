package cn.com.lightech.led_g5w.net.utils;

/**
 * Created by 明 on 2016/3/8.
 */
public class PostParamUtil {

    private static final String host = "192.168.16.254";

    private static final String url = "http://%1s/goform/ser2netconfigAT";

    private final static String FORMAT_PARAM_SAVE_WIFI = "netmode=2&wifi_conf=%1$s,%2$s,%3$s&dhcpc=1&net_commit=1&reboot=1";

    private final static String FORMAT_PARAM_RESET_WIFI = "default=1&reboot=1";

    private final static String FORMAT_PARAM_REBOOT_WIFI = "reboot=1";

    private final static String FORMAT_PARAM_SCAN_WIFI = "wifi_scan=?";

    private final static String FORMAT_PARAM_GET_MAC = "Get_MAC=?";

    public static String getSaveWifiParams(String wifi, String securityType, String password) {
        return String.format(FORMAT_PARAM_SAVE_WIFI, wifi, securityType, password);
    }

    public static String getRebootWifiParams() {
        return FORMAT_PARAM_REBOOT_WIFI;
    }


    public static String getResetWifiParams() {
        return FORMAT_PARAM_RESET_WIFI;
    }

    public static String getGetMacParams() {
        return FORMAT_PARAM_GET_MAC;
    }

    public static String getPostUrl(String hostIp) {
        if (hostIp == null)
            hostIp = PostParamUtil.host;
        return String.format(url, hostIp);
    }

}
