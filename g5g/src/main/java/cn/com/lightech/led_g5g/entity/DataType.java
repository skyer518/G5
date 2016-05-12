package cn.com.lightech.led_g5g.entity;

/**
 * dataType
 * <p>
 * START_INDEX(0) 和MAX_INDEX(4)循环使用
 *
 * @author f
 */
public enum DataType {
    UnKown(0),
    Curve(1),
    Instant(2),
    Flash(3),
    Moon(4),
    Timing(5),
    Update(6);


    private final int value;
    public final static int START_INDEX = 0;
    public final static int MAX_INDEX = 15;

    DataType(int value) {
        this.value = value;
    }

    /**
     * 尽量不使用，value的值可能会发生改变
     *
     * @return
     */
    public int toInt() {
        return value;
    }

    public boolean isOutOfBound() {
        if (value > MAX_INDEX || value < START_INDEX)
            return true;
        else
            return false;
    }

    public static DataType valueOf(int index) {
        if (index < START_INDEX || index > MAX_INDEX)
            return null;
        else {
            return values()[index];
        }
    }

    /**
     * id2 --> dataType
     *
     * @param id2
     * @return
     */
    public static DataType valueOfIds(int id1, int id2) {
        if (id1 == 0x00) {
            switch (id2) {
                case 0x00:
                case 0x05:
                case 0x07:
                case 0x09:
                case 0x0b:
                case 0x0d:
                    return Curve;
                case 0x01:
                case 0x06:
                case 0x08:
                case 0x0a:
                case 0x0c:
                case 0x0e:
                    return Timing;
                case 0x02:
                    return Flash;
                case 0x03:
                    return Moon;
                case 0x04:
                    return Instant;
            }
            return UnKown;
        } else if (id1 == 0x01) {
            return UnKown;
        } else if (id1 == 0x02) {
            return UnKown;
        } else if (id1 == 0x03) {
            return UnKown;
        } else if (id1 == 0x04) {
            return Update;
        } else {
            return UnKown;
        }
    }

//    /**
//     * dataType 顺序，对应UI上的模式顺序
//     */
//    public static int mode2Seq(DataType dataType) {
//        switch (dataType) {
//            case Auto:
//                return 0;
//            case Manual:
//                return 1;
//            default:
//                return 0;
//        }
//    }
//
//    /**
//     * dataType 顺序，对应UI上的模式顺序
//     */
//    public static DataType seq2Mode(int seq) {
//        switch (seq) {
//            case 0:
//                return Auto;
//            case 1:
//                return Manual;
//            default:
//                return Auto;
//        }
//    }
}
