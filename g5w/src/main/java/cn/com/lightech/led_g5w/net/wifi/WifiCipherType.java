package cn.com.lightech.led_g5w.net.wifi;

/**
 * Created by 明 on 2016/3/8.
 * 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
 */
public enum WifiCipherType {

    WIFICIPHER_WEP("WIFICIPHER_WEP"),
    WIFICIPHER_WPA("WIFICIPHER_WPA"),
    WIFICIPHER_NOPASS("WIFICIPHER_NOPASS"),
    WIFICIPHER_INVALID("WIFICIPHER_INVALID");

    private final String name;

    WifiCipherType(String name) {
        this.name = name;
    }

    public String stringValue() {
        return name;
    }

}
