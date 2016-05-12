package cn.com.u2be.xbase.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * UdpHelper帮助类
 *
 * @author 陈喆榕
 */
public class UdpClient implements Runnable {
    private static int localPort = 8903;
    ;
    public Boolean IsThreadDisable = false;//指示监听线程是否终止
    private WifiManager.MulticastLock lock;
    InetAddress mInetAddress;
    private DatagramSocket datagramSocket;

//    // UDP服务器监听的端口
//    Integer localPort = 8903;

    public UdpClient(WifiManager manager) {
        this.lock = manager.createMulticastLock("UDPwifi");
    }

    public void StartListen() {
        // 接收的字节大小，客户端发送的数据不能超过这个大小
        byte[] message = new byte[100];
        try {
            // 建立Socket连接
            this.datagramSocket = new DatagramSocket(localPort);
            datagramSocket.setBroadcast(true);
            DatagramPacket datagramPacket = new DatagramPacket(message,
                    message.length);
            try {
                while (!IsThreadDisable) {
                    // 准备接收数据
                    Log.d("UDP Demo", "准备接受");
                    this.lock.acquire();

                    datagramSocket.receive(datagramPacket);
                    byte[] data = datagramPacket.getData();
                    Log.d("UDP recv", datagramPacket.getAddress()
                            .getHostAddress().toString()
                            + ":" + Arrays.toString(data));
                    this.lock.release();
                }
            } catch (IOException e) {//IOException
                e.printStackTrace();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }


    public void send(final byte[] message, final String ip, final int prot) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendData(message, ip, prot);
            }
        }).start();
    }

    private void sendData(byte[] message, String ip, int prot) {
        try {
            DatagramPacket p = new DatagramPacket(message, message.length, InetAddress.getByName(ip), prot);
            datagramSocket.send(p);
            Log.d("UDP send", ip + ":" + Arrays.toString(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        StartListen();
    }
}