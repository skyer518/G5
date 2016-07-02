package cn.com.lightech.led_g5g.entity;

/**
 * 模式数据包ID的定义
 */
public class PackageId {

    public static final byte[] Seedling = {0x00, 0x00};
    public static final byte[] Seedling_Timing = {0x00, 0x01};

    public static final byte[] Flash = {0x00, 0x02};
    public static final byte[] Moon = {0x00, 0x03};
    public static final byte[] Instant = {0x00, 0x04};

    public static final byte[] Clone = {0x00, 0x05};
    public static final byte[] Vegetation = {0x00, 0x07};
    public static final byte[] Flowering = {0x00, 0x09};
    public static final byte[] Fruiting = {0x00, 0x0b};
    public static final byte[] Self = {0x00, 0x0d};

    public static final byte[] Clone_Timing = {0x00, 0x06};
    public static final byte[] Vegetation_Timing = {0x00, 0x08};
    public static final byte[] Flowering_Timing = {0x00, 0x0a};
    public static final byte[] Fruiting_Timing = {0x00, 0x0c};
    public static final byte[] Self_Timing = {0x00, 0x0e};

    public static byte[] getPackageIdByIndex(int dataIndex) {
        switch (dataIndex) {
            case 0:
                return Seedling;
            case 1:
                return Clone;
            case 2:
                return Vegetation;
            case 3:
                return Flowering;
            case 4:
                return Fruiting;
            case 5:
                return Self;
            case 6:
                return Flash;
            case 7:
                return Instant;
            case 8:
                return Self_Timing;
            case 9:
                return Clone_Timing;
            case 10:
                return Vegetation_Timing;
            case 11:
                return Seedling_Timing;
            case 12:
                return Flowering_Timing;
            case 13:
                return Fruiting_Timing;
//            case 14:
//                return Moon;
        }
        return null;

    }


//    public static DataType getDataType(byte[] ids) {
//        if (ids == null || ids.length != 2 || ids[1] < 0 || ids[1] > 0x0f)
//            return DataType.Auto;
//        if (ids[0] == 0) {
//            return DataType.valueOfIds(ids[1]);
//        }
//        return DataType.Auto;
//
//    }

//    public static byte[] getModePackageId(int schedule) {
//        DataType sm = DataType.valueOf(schedule);
//        if (sm == null)
//            return null;
//        return getModePackageId(sm);
//    }
//
//    public static byte[] getModePackageId(DataType sm) {
//        switch (sm) {
//            case Auto:
//                return PackageId.Auto;
//            case AutoTiming:
//                return PackageId.AutoTiming;
//            case PackageId.Instant:
//                return PackageId.Instant;
//            case Flash:
//                return PackageId.Flash;
//            case Moon:
//                return PackageId.Moon;
//            default:
//                break;
//        }
//
//        return PackageId.Auto;
//    }
}
