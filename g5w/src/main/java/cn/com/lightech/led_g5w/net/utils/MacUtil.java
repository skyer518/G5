package cn.com.lightech.led_g5w.net.utils;

/**
 * Created by alek on 2016/5/9.
 */
public class MacUtil {

    /**
     * 20:F4:1B:79:FB:79
     *
     * @param macStr
     * @return
     */
    public static byte[] convertMac(String macStr) {
        byte[] mac = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        if (macStr.contains(":")) {
            try {
                final String[] split = macStr.split(":");
                mac = new byte[6];
                mac[0] = uniteBytes(split[0].getBytes()[0], split[0].getBytes()[1]);
                mac[1] = uniteBytes(split[1].getBytes()[0], split[1].getBytes()[1]);
                mac[2] = uniteBytes(split[2].getBytes()[0], split[2].getBytes()[1]);
                mac[3] = uniteBytes(split[3].getBytes()[0], split[3].getBytes()[1]);
                mac[4] = uniteBytes(split[4].getBytes()[0], split[4].getBytes()[1]);
                mac[5] = uniteBytes(split[5].getBytes()[0], split[5].getBytes()[1]);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                return mac;
            }
        } else {
            return HexString2Bytes(macStr);
        }
    }


    public static String convertMac(byte[] mac) {
        StringBuffer sb = new StringBuffer();
        for (byte i : mac) {
            final String temp = Integer.toHexString(i & 0xff);
            if (temp.length() < 2)
                sb.append("0");
            sb.append(temp);
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 将两个ASCII字符合成一个字节；
     * 如："EF"--> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式
     * 如："20F41B79FB79" --> byte[]{0x2B, 0x44, 0xEF, 0xD9}
     *
     * @param src String
     * @return byte[]
     */
    public static byte[] HexString2Bytes(String src) {
        byte[] ret = new byte[6];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < 6; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

}
