package cn.com.lightech.led_g5g.entity;

/**
 * Created by 明 on 2016/4/22.
 */
public enum DeviceType {
    Led(0xA6), Unknown(0x00);

    private final int value;

    DeviceType(int value) {
        this.value = value;
    }

    public int getIntValue() {
        return value;
    }

    public static DeviceType parseInt(int value) {
        switch (value) {
            case 0xA6:
                return Led;
        }
        return Unknown;
    }


}
