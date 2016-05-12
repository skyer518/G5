package cn.com.u2be.xbase.net;


import android.os.Handler;

/**
 * Created by 明 on 2016/3/4.
 */
public interface IUdp {


    /**
     * 发送组播
     *
     * @param ip
     * @param port
     */
    void sendMulticast(String ip, int port, byte[] data);

    /**
     * 发送组播
     *
     * @param ip
     * @param port
     * @param handler
     */
    void sendMulticast(String ip, int port, byte[] data, Handler handler);


    /**
     * 发送单播
     *
     * @param ip
     * @param port
     */
    void sendUnicast(String ip, int port, byte[] data);


    /**
     * 发送单播
     *
     * @param ip
     * @param port
     * @param handler
     */
    void sendUnicast(String ip, int port, byte[] data, Handler handler);


}
