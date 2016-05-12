package cn.com.u2be.xbase.net;

import android.os.Handler;

/**
 * Created by 明 on 2016/2/26.
 */
public class UDPManager implements IUdp {


    public static final String HOST_MULTI = "224.0.0.1";
    public static final int HLK_PORT = 988;
    public static final int CTL_PORT = 987;
    public static final int RECIVE_PORT = 14987;
    public static final int WHAT_RECEIVER_DATAGRAMPACKET = 0xa0;


    public void getInstance() {
    }


    /**
     * 发送组播 并 通过 handler 接收响应
     *
     * @param ip
     * @param port
     * @param handler
     */
    public void sendMulticast(String ip, int port, byte[] data, Handler handler) {
        MulticastClient client = new MulticastClient(ip, port);
        client.setHandler(handler);
        client.send(data);
    }

    /**
     * 发送组播 不响应
     *
     * @param ip
     * @param port
     */
    public void sendMulticast(String ip, int port, byte[] data) {
        MulticastClient client = new MulticastClient(ip, port);
        client.send(data);
    }

    /**
     * 发送单播 并 通过 handler 接收响应
     *
     * @param ip
     * @param port
     * @param handler
     */
    public void sendUnicast(String ip, int port, byte[] data, Handler handler) {
        UnicastClient client = new UnicastClient(ip, port);
        client.setHandler(handler);
        client.send(data);
    }

    /**
     * 发送单播 不响应
     *
     * @param ip
     * @param port
     */
    public void sendUnicast(String ip, int port, byte[] data) {
        UnicastClient client = new UnicastClient(ip, port);
        client.send(data);
    }

}
