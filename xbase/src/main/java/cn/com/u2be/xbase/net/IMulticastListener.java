package cn.com.u2be.xbase.net;

public interface IMulticastListener {
    /**
     * 权重
     *
     * @return
     */
    int getPriority();

    /**
     * 开始接收
     */

    void onStartReciverThread();

    /**
     * 停止接收
     */
    void onStopReciverThread();

    /**
     * 消息回调
     */
    boolean onReceive(byte[] data, String host);
}
