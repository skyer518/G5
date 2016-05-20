package cn.com.lightech.led_g5w.entity;

/**
 * 模式数据包ID的定义
 */
public class PackageId {

    public static final byte[] Auto = {0x00, 0x00};
    public static final byte[] AutoTiming = {0x00, 0x01};
    public static final byte[] Flash = {0x00, 0x02};
    public static final byte[] Moon = {0x00, 0x03};
    public static final byte[] Manual = {0x00, 0x04};

    public static final byte[] Wave = {0x02, 0x00};


    public static Mode getMode(byte[] ids) {
        if (ids == null || ids.length != 2 || ids[1] < 0 || ids[1] > 0x0f)
            return Mode.Auto;
        if (ids[0] == 0) {
            return Mode.valueOfids(ids[1]);
        }
        return Mode.Auto;

    }

    public static byte[] getModePackageId(int schedule) {
        Mode sm = Mode.valueOf(schedule);
        if (sm == null)
            return null;
        return getModePackageId(sm);
    }

    public static byte[] getModePackageId(Mode sm) {
        switch (sm) {
            case Auto:
                return PackageId.Auto;
            case AutoTiming:
                return PackageId.AutoTiming;
            case Manual:
                return PackageId.Manual;
            case Flash:
                return PackageId.Flash;
            case Moon:
                return PackageId.Moon;
            default:
                break;
        }

        return PackageId.Auto;
    }

    public static byte[] getWavePackageId() {
        return PackageId.Wave;
    }
}
