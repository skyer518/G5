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

/**
 * Created by 明 on 2016/2/26.
 */
class MulticastClient implements Runnable {

    public static final int BUFFER_SIZE = 4096;
    private String host = "224.0.0.1";
    private int RECIVE_PORT = 18665;
    private int port = 988;

    private MulticastSocket socket;
    private InetAddress inetAddress;

    private byte[] buffer = new byte[BUFFER_SIZE];
    private DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
    private DatagramPacket sendPacket = null;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    private Handler handler;


    public MulticastClient(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            socket = new MulticastSocket(RECIVE_PORT);
            inetAddress = InetAddress.getByName(this.host);
            InetSocketAddress address = new InetSocketAddress(inetAddress, this.port);
            sendPacket = new DatagramPacket(new byte[0], 0, address);

            new Thread(this).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void send(final byte[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendPacket.setData(data);
                    socket.send(sendPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    @Override
    public void run() {
        try {
            long last = System.currentTimeMillis();
            long current = System.currentTimeMillis();
            while (current - last < 3 * 1000) {
                last = current;
                socket.receive(receivePacket);
                receive(receivePacket);
                current = System.currentTimeMillis();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
            Log.e("ii", "广播线程结束");
        }
    }


    private void receive(DatagramPacket receivePacket) {
        Message msg = new Message();
        msg.what = UDPManager.WHAT_RECEIVER_DATAGRAMPACKET;
        msg.obj = receivePacket.getAddress().getHostAddress();
        Bundle data = new Bundle();
        data.putByteArray("data",
                receivePacket.getData());
        msg.setData(data);
        if (handler != null)
            handler.sendMessage(msg);
    }


}
