package cn.com.lightech.led_g5g.net;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import cn.com.lightech.led_g5g.gloabal.IDataListener;
import cn.com.lightech.led_g5g.net.entity.CmdType;
import cn.com.lightech.led_g5g.net.entity.ConnState;
import cn.com.lightech.led_g5g.net.entity.DataMessage;
import cn.com.lightech.led_g5g.net.entity.Request;
import cn.com.lightech.led_g5g.net.entity.Response;
import cn.com.lightech.led_g5g.net.socket.ClientSocket;
import cn.com.lightech.led_g5g.gloabal.CmdBuilder;
import cn.com.lightech.led_g5g.gloabal.CmdParser;
import cn.com.lightech.led_g5g.net.utils.Logger;
import cn.com.lightech.led_g5g.net.utils.MessageUtil;
import cn.com.lightech.led_g5g.net.utils.TimerHelper;
import cn.com.lightech.led_g5g.net.utils.TimerProcessor;
import cn.com.lightech.led_g5g.gloabal.ConfigString;

enum MsgPriority {
    Normal, High
}

public class ConnectManager {
    // extends BroadcastReceiver

    private Queue<Message> reqQueue = new LinkedBlockingQueue<Message>(
            100);
    private long sendTime = 1;

    private boolean isSuccess;
    private Logger logger = Logger.getLogger(ConnectManager.class);
    private ClientSocket clientSocket = null;
    // 数据回调Listener队列
    private List<IDataListener> dataListenerList = new CopyOnWriteArrayList<IDataListener>();
    // 优先数据回调Listener队列，如果此队列不为空，其它Listener将收不到回调，直到此队列清空
    private List<IDataListener> dataListenerList_High = new CopyOnWriteArrayList<IDataListener>();
    private boolean bLedReady = false;// led是否就绪
    private TimerHelper checkLedTimer = null;
    final int checkInterval = 1000;// 检查led时间间隔
    private Thread processor;

    private Handler mhHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            MessageUtil.showToast(msg.obj.toString());
        }

    };
    private String ip;
    private int port;


    private Comparator<IDataListener> comparator = new Comparator<IDataListener>() {
        @Override
        public int compare(IDataListener lhs, IDataListener rhs) {
            return lhs.getPriority() - rhs.getPriority();
        }
    };

    public void setMac(String mac) {
        this.mac = mac;
    }

    private String mac;
/*
    private static ConnectManager inst;


    public static ConnectManager getInstance() {
        synchronized (ConnectManager.class) {
            if (inst == null)
                inst = new ConnectManager();
        }
        return inst;
    }
*/

    /**
     * 无参数构造函数 注意：不能在类外调用此函数实例化，必须使用单例。这里加这个函数，是为了BroadcastReceiver
     */
    public ConnectManager() {

        processor = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    if (IsConnected() && IsLedReady()) {
                        long delay = System.currentTimeMillis() - sendTime;
                        if (isSuccess || delay > ClientSocket.SOCKET_DELAY) {
                            Message msg = reqQueue.poll();
                            if (msg != null) {
                                logger.i("ConnectManager" + getHost(), "send:" + msg.toString());
                                clientSocket.Send(msg);
                                sendTime = System.currentTimeMillis();
                                isSuccess = false;
                            }
                        }
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        processor.start();
    }

    // 接受socket回调
    private Handler recvHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DataMessage dataMsg = (DataMessage) msg.obj;
            isSuccess = true;
            switch (dataMsg.getMsgType()) {
                case Conn:
                    ConnState connState = dataMsg.getConnState();
                    if (connState == ConnState.Connected)
                        StartCheckLed(); // 检查灯是否就绪
                    else {
                        bLedReady = false;
                        StopCheckLed();
                    }

                    for (IDataListener listener : dataListenerList) {
                        listener.onConnectStateChanged(connState, ConnectManager.this);
                    }
                    break;
                case Data:
                    Response rsp = CmdParser.Parse(dataMsg.getByteArray());
                    if (rsp != null && rsp.getCmdType() == CmdType.CheckReady) {
                        bLedReady = rsp.IsOK();
                        if (bLedReady)
                            StopCheckLed();
                    }

                    // 只要优先队列不为空，则只给优先队列回调
                    if (!dataListenerList_High.isEmpty()) {
                        for (IDataListener listener : dataListenerList_High) {
                            if (!listener.onReceive(rsp, ConnectManager.this)) {
                                return;
                            }
                        }
                        return;
                    }

                    for (IDataListener listener : dataListenerList) {
                        if (!listener.onReceive(rsp, ConnectManager.this)) {
                            return;
                        }
                    }
                    break;
                case Log:
                    logger.d(dataMsg.getText());
                    break;
            }
        }

    };

    /**
     * 注册一个数据监听
     */
    public void Register(IDataListener listener) {
        if (listener == null)
            return;
        if (dataListenerList.contains(listener))
            return;
        this.dataListenerList.add(listener);
        Logger.getLogger().d(
                "registerHigh :" + listener.getClass().getSimpleName().toString());
//        List<IDataListener> temp = new ArrayList<>(dataListenerList);
//        Collections.sort(temp, comparator);
//        temp=null;
    }

    /**
     * 注册一个优先Listener，其它Listener将收不到回调，直到此Listener反注册
     */
    public void RegisterHigh(IDataListener listener) {

        if (listener == null)
            return;
        if (dataListenerList_High.contains(listener))
            return;
        this.dataListenerList_High.add(listener);

        Logger.getLogger().d(
                "registerHigh :"
                        + listener.getClass().getSimpleName().toString());
//        Collections.sort(dataListenerList, comparator);
    }

    /**
     * 取消一个数据监听
     */
    @SuppressWarnings("unused")
    public void UnRegister(IDataListener listener) {
        Logger.getLogger()
                .d("unRegister :"
                        + listener.getClass().getSimpleName().toString());
        if (listener == null)
            return;
        if (this.dataListenerList.contains(listener))
            this.dataListenerList.remove(listener);
        if (this.dataListenerList_High.contains(listener))
            this.dataListenerList_High.remove(listener);

    }

    /**
     * 检查是否就绪
     */
    public boolean Check(boolean showTips) {
        if (!IsConnected()) {
            if (showTips) {
                Message msg = new Message();
                msg.obj = ConfigString.P_CONN_LED;
                mhHandler.sendMessage(msg);
            }

            return false;
        }
        if (!IsLedReady()) {
            if (showTips) {
                Message msg = new Message();
                msg.obj = ConfigString.LED_NOT_READY;
                mhHandler.sendMessage(msg);
            }
            return false;
        }
        return true;
    }

    /**
     * 是否连接LED
     */
    public boolean IsConnected() {
        return clientSocket != null && clientSocket.IsEnable();
    }

    /**
     * 开灯后，led会有一分钟左右的自检，然后才就绪
     */
    public boolean IsLedReady() {
        return bLedReady;
    }

    /**
     * 连接LED
     */
    public synchronized void Connect(String ip, int port) {
        if (IsConnected()) {
            return;
        }
        clientSocket = new ClientSocket(ip, port, recvHandler);
        Thread socketThread = new Thread(clientSocket);// 启动线程 向服务器发送和接收信息
        socketThread.setName(ip);
        socketThread.start();
        this.ip = ip;
        this.port = port;
    }

    /**
     * 向LED发送数据
     *
     * @param request CmdBuilder构造的数据包
     */
    public void SendToLed(Request request) {
        Message msg = new Message();
        byte[] cmd = CmdBuilder.Build(request);
        msg.obj = cmd;
        // 发送消息 修改UI线程中的组件
        reqQueue.offer(msg);

    }

    private void Send2Led(Request request) {
        if (!IsConnected())
            return;
        Message msg = new Message();
        byte[] cmd = CmdBuilder.Build(request);
        msg.obj = cmd;
        clientSocket.Send(msg);

    }

    /**
     * 向LED发送数据
     *
     * @param cmd CmdBuilder构造的数据包
     */
    public void SendToLed(byte[] cmd) {
        if (!IsConnected())
            return;
        Message msg = new Message();
        // msg.what = 0x11;
        msg.obj = cmd;
        // 发送消息 修改UI线程中的组件
        reqQueue.offer(msg);
    }

    /**
     * 开始检查Led
     */
    private void StartCheckLed() {
        if (checkLedTimer == null) {
            checkLedTimer = new TimerHelper(500, checkInterval,
                    new TimerProcessor() {

                        @Override
                        public void process() {
                            Log.i(ConnectManager.this.getHost(), " process");
                            checkLedReady();
                        }

                        @Override
                        public void stop() {
                            Log.i(ConnectManager.this.getHost(), " stop");
                            // TODO Auto-generated method stub

                        }
                    });
        }
        checkLedTimer.startTimer();
    }

    /**
     * 停止检查led
     */
    private void StopCheckLed() {
        if (checkLedTimer != null) {
            checkLedTimer.stopTimer();
            checkLedTimer = null;
        }
    }

    /**
     * 连接成功后，检查led是否就绪状态
     */
    public void checkLedReady() {
        Request req = new Request();
        req.setCmdType(CmdType.CheckReady);
        Send2Led(req);
    }

    public void closeConnection() {
        if (clientSocket != null) {
            clientSocket.Close();
        }

    }

    public String getHost() {
        return ip;
    }


    public String getMac() {
        return mac;
    }
}
