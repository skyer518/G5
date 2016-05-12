package cn.com.lightech.led_g5g.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.lightech.led_g5g.gloabal.IDataListener;
import cn.com.lightech.led_g5g.net.entity.CmdType;
import cn.com.lightech.led_g5g.net.entity.ConnState;
import cn.com.lightech.led_g5g.net.entity.Request;
import cn.com.lightech.led_g5g.net.entity.Response;
import cn.com.lightech.led_g5g.presenter.SycnDataPresenter;

/**
 * Created by 明 on 2016/3/16.
 */
public class ConnectionsManager implements IDataListener {


    private static ConnectionsManager instance;

    public static ConnectionsManager getInstance() {
        synchronized (ConnectionsManager.class) {
            if (instance == null) {
                instance = new ConnectionsManager();
            }
            return instance;
        }
    }


    private Map<String, ConnectManager> allConnections = new HashMap<>(0);

    private Map<String, ConnectManager> hightConnections = new HashMap<>(0);


    public synchronized void priorityConnect(String ip, int port) {
        connect(ip, port);
        Iterator<Map.Entry<String, ConnectManager>> iterator = allConnections.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ConnectManager> entry = iterator.next();
            String key = entry.getKey();
            if (key == ip) {
                hightConnections.put(entry.getKey(), entry.getValue());
            }
        }
    }


    public synchronized void clearPriorityConnections() {
        hightConnections.clear();

    }


    public synchronized void connect(List<String> ip, int port) {
        for (String host : ip) {
            ConnectManager connectManager = allConnections.get(host);
            if (connectManager != null)
                continue;
            ConnectManager manager = new ConnectManager();
            manager.Connect(host, port);
            allConnections.put(host, manager);
        }
    }

    public synchronized void connect(String ip, int port) {
        ConnectManager connectManager = allConnections.get(ip);
        if (connectManager != null) {
            if (connectManager.IsConnected() && connectManager.IsLedReady())
                return;
            connectManager.closeConnection();
            allConnections.remove(connectManager.getHost());
        }
        ConnectManager manager = new ConnectManager();
        manager.Connect(ip, port);
        allConnections.put(ip, manager);

    }


    public boolean isConnected(boolean allSend) {
        Map<String, ConnectManager> tempConnections = this.hightConnections;
        if (allSend) {
            tempConnections = allConnections;
        }
        for (String host : tempConnections.keySet()) {
            ConnectManager connectManager = tempConnections.get(host);
            if (!connectManager.IsConnected()) {
                return false;
            }
        }
        return true;
    }


    public boolean check(boolean showTips) {
        for (String host : allConnections.keySet()) {
            ConnectManager connectManager = allConnections.get(host);
            if (!connectManager.Check(showTips)) {
                return false;
            }
        }
        return true;
    }


    public void checkLedReady(boolean allSend) {
        Map<String, ConnectManager> tempConnections = this.hightConnections;
        if (allSend) {
            tempConnections = allConnections;
        }
        for (String host : tempConnections.keySet()) {
            ConnectManager connectManager = tempConnections.get(host);
            connectManager.checkLedReady();
        }
    }

    public void checkLedReady(String host) {
        ConnectManager connectManager = allConnections.get(host);
        if (connectManager != null) {
            connectManager.checkLedReady();
        }
    }


    public boolean isLedReady(boolean allSend) {
        Map<String, ConnectManager> tempConnections = this.hightConnections;
        if (allSend) {
            tempConnections = allConnections;
        }
        for (String host : tempConnections.keySet()) {
            ConnectManager connectManager = tempConnections.get(host);
            if (!connectManager.IsLedReady()) {
                return false;
            }
        }
        return true;
    }


    public void register(IDataListener listener, boolean allSend) {
        Map<String, ConnectManager> tempConnections = this.hightConnections;
        if (allSend) {
            tempConnections = allConnections;
        }
        for (String host : tempConnections.keySet()) {
            ConnectManager connectManager = tempConnections.get(host);
            connectManager.Register(listener);
        }
    }

    public void registerHigh(IDataListener listener, String ip, boolean allSend) {
        Map<String, ConnectManager> tempConnections = this.hightConnections;
        if (allSend) {
            tempConnections = allConnections;
        }
        if (tempConnections.containsKey(ip)) {
            ConnectManager connectManager = tempConnections.get(ip);
            connectManager.RegisterHigh(listener);
        }
    }


    public void registerHigh(IDataListener listener, boolean allSend) {
        Map<String, ConnectManager> tempConnections = this.hightConnections;
        if (allSend) {
            tempConnections = allConnections;
        }
        for (String host : tempConnections.keySet()) {
            ConnectManager connectManager = tempConnections.get(host);
            connectManager.RegisterHigh(listener);
        }
    }


    public void send2Led(Request request, boolean allSend) {
        Map<String, ConnectManager> tempConnections = this.hightConnections;
        if (allSend) {
            tempConnections = allConnections;
        }
        for (String host : tempConnections.keySet()) {
            ConnectManager connectManager = tempConnections.get(host);
            connectManager.SendToLed(request);
        }
    }


    public void sendToLed(byte[] cmd, boolean allSend) {
        Map<String, ConnectManager> tempConnections = this.hightConnections;
        if (allSend) {
            tempConnections = allConnections;
        }
        for (String host : tempConnections.keySet()) {
            ConnectManager connectManager = tempConnections.get(host);
            connectManager.SendToLed(cmd);
        }
    }


    public void sendToLed(Request request, boolean allSend) {
        Map<String, ConnectManager> tempConnections = this.hightConnections;
        if (allSend) {
            tempConnections = allConnections;
        }
        for (String host : tempConnections.keySet()) {
            ConnectManager connectManager = tempConnections.get(host);
            connectManager.SendToLed(request);
        }
    }


    public void unRegister(IDataListener listener) {
        for (String host : allConnections.keySet()) {
            ConnectManager connectManager = allConnections.get(host);
            connectManager.UnRegister(listener);
        }
    }


    public void closeConnections() {
        for (String host : allConnections.keySet()) {
            ConnectManager connectManager = allConnections.get(host);
            connectManager.closeConnection();
        }
        allConnections.clear();
    }


    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onConnectStateChanged(ConnState connState, ConnectManager connectManager) {
        if (connState == ConnState.DisConnected) {
            if (allConnections.containsKey(connectManager.getHost())) {
                allConnections.remove(connectManager.getHost());
                connectManager.closeConnection();
            }
            if (hightConnections.containsKey(connectManager.getHost())) {
                hightConnections.remove(connectManager.getHost());
                connectManager.closeConnection();
            }
        }
    }

    @Override
    public boolean onReceive(Response response, ConnectManager connectManager) {
        return false;
    }

    public void sendaToHost(Request request, String ip) {
        if (allConnections.containsKey(ip)) {
            ConnectManager connectManager = allConnections.get(ip);
            connectManager.SendToLed(request);
        }
    }


}
