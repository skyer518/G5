package cn.com.u2be.xbase.net;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 明 on 2016/4/19.
 */
public class MulticastManager {


    private static MulticastManager instance;

    public static MulticastManager getInstance() {
        if (instance == null) {
            instance = new MulticastManager();
        }
        return instance;
    }

    private Handler recvHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Multicast.WHAT_RECEIVER_DATAGRAMPACKET) {
                for (int i = 0; i < multicastListenerList.size(); i++) {
                    byte[] data = msg.getData().getByteArray("data");
                    if (multicastListenerList.get(i).onReceive(data, (String) msg.obj)) {
                        return;
                    }
                }

            } else if (msg.what == Multicast.WHAT_CLOSE_RECEIVER_THREAD) {
                for (int i = 0; i < multicastListenerList.size(); i++) {
                    multicastListenerList.get(i).onStopReciverThread();
                }
            } else if (msg.what == Multicast.WHAT_START_RECEIVER_THREAD) {
                for (int i = 0; i < multicastListenerList.size(); i++) {
                    multicastListenerList.get(i).onStartReciverThread();
                }
            }
        }
    };


    private MulticastManager() {
    }


    private int port = 988;
    private String host = "224.0.0.1";
    private Multicast multicast;

    private List<IMulticastListener> multicastListenerList = Collections.synchronizedList(new ArrayList<IMulticastListener>(0));

    public void connect() {
        multicast = new Multicast(host, port, recvHandler);
    }

    public void registListener(IMulticastListener listener) {
        multicastListenerList.add(listener);
    }

    public void unRegistListener(IMulticastListener listener) {
        multicastListenerList.remove(listener);
    }

    public void send(byte[] data) {
        multicast.send(data);
    }
}
