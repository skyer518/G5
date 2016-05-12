package cn.com.u2be.xbase.net;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 明 on 2016/2/26.
 */
class Multicast implements Runnable {

    public static final int WHAT_RECEIVER_DATAGRAMPACKET = 0xc1;
    public static final int WHAT_CLOSE_RECEIVER_THREAD = 0xc2;
    public static final int WHAT_START_RECEIVER_THREAD = 0xc2;

    public static final int BUFFER_SIZE = 4096;
    private int RECIVE_PORT = 18665;
    private String host;
    private int port;

    private MulticastSocket socket;
    private InetAddress inetAddress;

    private byte[] buffer = new byte[BUFFER_SIZE];
    private DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
    private DatagramPacket sendPacket = null;
    private boolean isRunning;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    private Handler handler;


    public Multicast(String host, int port, Handler recvHandler) {
        this.host = host;
        this.port = port;
        this.handler = recvHandler;

        try {
            socket = new MulticastSocket(RECIVE_PORT);
            inetAddress = InetAddress.getByName(this.host);
            InetSocketAddress address = new InetSocketAddress(inetAddress, this.port);
            sendPacket = new DatagramPacket(new byte[0], 0, address);

            new Thread(this).start();
            handler.sendEmptyMessage(WHAT_START_RECEIVER_THREAD);

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Multicast.class", "Can not Create Multicast Client , Prams is wrong!");
        }

    }

    /**
     * need run in new Thread
     *
     * @param data
     */
    public void send(final byte[] data) {

        try {
            sendPacket.setData(data);
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void run() {
        isRunning = true;
        try {
            while (isRunning) {
                socket.receive(receivePacket);
                receive(receivePacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
            handler.sendEmptyMessage(WHAT_CLOSE_RECEIVER_THREAD);
            Log.e("ii", "广播线程结束");
        }
    }


    public void close() {
        isRunning = false;
    }


    private void receive(DatagramPacket receivePacket) {
        Message msg = new Message();
        msg.what = WHAT_RECEIVER_DATAGRAMPACKET;
        msg.obj = receivePacket.getAddress().getHostAddress();
        Bundle data = new Bundle();
        data.putByteArray("data", receivePacket.getData());
        msg.setData(data);
        if (handler != null)
            handler.sendMessage(msg);
    }


}
