package cn.com.lightech.led_g5w.entity;

/**
 * mode
 * <p/>
 * START_INDEX(0) 和MAX_INDEX(4)循环使用
 *
 * @author f
 */
public enum Mode {
    Auto(0), Manual(1), Flash(2), Moon(3), AutoTiming(4);

    private final int value;
    public final static int START_INDEX = 0;
    public final static int MAX_INDEX = 4;

    private Mode(int value) {
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

    public boolean outOfBound() {
        if (value > MAX_INDEX || value < START_INDEX)
            return true;
        else
            return false;
    }

    public static Mode valueOf(int index) {
        if (index < START_INDEX || index > MAX_INDEX)
            return null;
        else {
            return values()[index];
        }
    }

    public static Mode valueOfids(int id2) {
        switch (id2) {
            case 0x00:
                return Auto;
            case 0x01:
                return AutoTiming;
            case 0x02:
                return Flash;
            case 0x03:
                return Moon;
            case 0x04:
                return Manual;
        }
        return Auto;
    }

    /**
     * mode 顺序，对应UI上的模式顺序
     */
    public static int mode2Seq(Mode mode) {
        switch (mode) {
            case Auto:
                return 0;
            case Manual:
                return 1;
            default:
                return 0;
        }
    }

    /**
     * mode 顺序，对应UI上的模式顺序
     */
    public static Mode seq2Mode(int seq) {
        switch (seq) {
            case 0:
                return Auto;
            case 1:
                return Manual;
            default:
                return Auto;
        }
    }
}
