package cn.com.u2be.xbase.utils;

import java.util.Arrays;

/**
 * Created by 明 on 2016/2/29.
 */
public class ArrayUtil {

    public static final <T> T[] merge(T[] before, T[] after) {
        T[] tempDate = Arrays.copyOf(before, before.length + after.length);
        System.arraycopy(after, 0, tempDate, before.length, after.length);
        return tempDate;
    }


    public static byte[] merge(byte[] before, byte[] after) {
        byte[] tempDate = Arrays.copyOf(before, before.length + after.length);
        System.arraycopy(after, 0, tempDate, before.length, after.length);
        return tempDate;
    }


}
