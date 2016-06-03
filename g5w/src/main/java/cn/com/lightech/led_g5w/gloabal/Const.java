package cn.com.lightech.led_g5w.gloabal;

/**
 * Created by 明 on 2016/3/9.
 */
public class Const {

    private static Const instance;

    public static Const getInstance() {
        if (instance == null) {
            instance = new Const();
        }
        return instance;
    }

    public byte[] UUID;

    public byte[] getUUID() {
        return UUID;
    }

    public void setUUID(byte[] UUID) {
        this.UUID = UUID;
    }


}
