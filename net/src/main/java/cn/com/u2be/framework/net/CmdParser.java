package cn.com.u2be.framework.net;

/**
 * Created by alek on 2016/5/13.
 */
public interface CmdParser {
    boolean needMoreData(byte[] content);
}
