package cn.com.u2be.myutils;

import java.io.ByteArrayOutputStream;

public class StringUtil {
    private final static String hexString = "0123456789ABCDEF";

    /* 计算较验和 */
    public static char checkSum(byte[] buffer, int length) {
        char cksum = 0;
        int k = 0;
        while (length > 1) {
            if (k > (length / 2))
                break;
            cksum += (char) ((buffer[k * 2] << 8) + buffer[k * 2 + 1]);
            length -= 2;
            k += 1;
        }
        if (length > 0)
            cksum += (char) (buffer[k * 2] << 8);
        cksum = (char) ((cksum >> 16) + (cksum & 0xffff));
        cksum += (cksum >> 16);

        // cksum=(char)Integer.toBinaryString(~cksum);
        cksum = (char) ~cksum;
        return cksum;
    }

	/*
     * public long calcAdler32CheckSum(String name, ByteString body) { Adler32
	 * checksum = new Adler32(); checksum.update(name.getBytes());
	 * checksum.update(body.toByteArray()); return checksum.getValue(); }
	 */

    public static String stringToHexString(String strPart) {
        String hexString = "";
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString = hexString + strHex;
        }
        return hexString;
    }

    /*
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    public static String encodeToHexString(String str) {
        // 根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /*
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     */
    public static String decodeHexString(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                bytes.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

    private static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}))
                .byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}))
                .byteValue();
        byte ret = (byte) (_b0 | _b1);
        return ret;
    }

	/*
     * public static byte[] HexString2Bytes(String src) { byte[] ret = new
	 * byte[6]; byte[] tmp = src.getBytes(); for(int i=0; i<6; ++i ) { ret[i] =
	 * uniteBytes(tmp[i*2], tmp[i*2+1]); } return ret; }
	 */

    private static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    // 从字节数组到十六进制字符串转换
    public static String Bytes2HexString(byte[] b) {
        byte[] hex = hexString.getBytes();
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }

    // 从十六进制字符串到字节数组转换
    public static byte[] HexString2Bytes(String hexstr) {
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    public static String int2OrdinalNumeral(int number) {
        String[] suffixs = {"th", "st", "nd", "rd", "th"};
        int temp = number % 10;
        if (temp > 4) {
            return number + suffixs[4];
        }
        return number + suffixs[temp];
    }
}
