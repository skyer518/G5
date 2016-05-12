package cn.com.lightech.led_g5g.entity;

import java.util.UnknownFormatConversionException;

/**
 * Created by 明 on 2016/4/22.
 */
public enum DeviceType {
    Led(0x5A), Unknown(0x00);

    private final int value;

    private DeviceType(int value) {
        this.value = value;
    }

    public static DeviceType parseInt(int value) {
        switch (value) {
            case 0x5A:
                return Led;
        }
        return Unknown;
    }


}
