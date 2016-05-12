package cn.com.lightech.led_g5w.net.socket;

import android.os.Handler;
import android.os.Message;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;

import cn.com.lightech.led_g5w.net.entity.ConnMsgType;
import cn.com.lightech.led_g5w.net.entity.ConnState;
import cn.com.lightech.led_g5w.net.entity.DataMessage;
import cn.com.lightech.led_g5w.net.utils.Logger;
import cn.com.lightech.led_g5w.net.utils.StringUtil;
import cn.com.lightech.led_g5w.gloabal.CmdParser;

/**
 * 客户端Socket线程
 */
public class ClientSocket implements Runnable {

    public static final int SOCKET_DELAY = 3000;
    // Sokcet
    private Socket tcpSocket;
    // 接受到socket消息后，向UI线程发送消息的Handler对象
    private Handler recvHandler;
    // UI线程向Socket发送Msg的Handler对象
    private Handler sendHandler;
    // Socket所对应的输入流
    private DataInputStream reader = null;
    // Socket所对应的输出流
    private OutputStream writer = null;
    private String IPAddress;
    private int Port;
    // 接收子线程是否在运行
    private boolean IsRuning = false;
    // 线程是否可用标志位
    private boolean threadflag = false;
    private boolean IsParamOk = false;

    private Logger logger = Logger.getLogger(ClientSocket.class);

    public ClientSocket(final String ip, int port, Handler recvHandler) {

        this.recvHandler = recvHandler;

        if (ip.trim().equals("") || port > 65565) {
            logger.e("IP或端口有误");
            OnReceive(ConnMsgType.Conn, ConnState.ParamError, "Please check setting");
            return;
        }

        this.IPAddress = ip;
        this.Port = port;

        // 创建接收Handler对象
        sendHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (tcpSocket == null || !tcpSocket.isConnected()
                        || reader == null || writer == null) {
                    logger.e("连接不可用");
                    return;
                }
                // 将数据写入网络
                try {
                    byte[] byteData = (byte[]) msg.obj;
                    if (byteData == null || byteData.length == 0) {
                        return;
                    }
                    writer.write(byteData);
                    writer.flush();
                    logger.i("IP:" + tcpSocket.getInetAddress().toString() + " ; send data:" + Arrays.toString(byteData));
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e);
                }
            }

        };

        IsParamOk = true;
    }

    public boolean IsEnable() {
        return threadflag && IsRuning && tcpSocket != null
                && tcpSocket.isConnected();
    }

    public void Send(Message msg) {
        if (sendHandler == null)
            return;
        sendHandler.sendMessage(msg);
    }

    private void OnReceive(ConnMsgType msgType, ConnState connState, String text) {
        DataMessage data = new DataMessage();
        data.setMsgType(msgType);
        data.setConnState(connState);
        data.setText(text);
        OnReceive(data);
    }

    private void OnReceive(ConnMsgType msgType, String text) {
        DataMessage data = new DataMessage();
        data.setMsgType(msgType);
        data.setText(text);
        OnReceive(data);
    }

    private void OnReceive(ConnMsgType msgType, byte[] content) {
        DataMessage data = new DataMessage();
        data.setMsgType(msgType);
        data.setByteArray(content);
        OnReceive(data);
    }

    private void OnReceive(DataMessage data) {
        Message msg = new Message();
        msg.obj = data;
        recvHandler.sendMessage(msg);
    }

    public void Close() {
        threadflag = false;
        IsRuning = false;
        try {
            if (tcpSocket != null) {
                if (!tcpSocket.isClosed())
                    tcpSocket.close();
                tcpSocket = null;
            }
            if (reader != null) {
                reader.close();
                reader = null;
            }
            if (writer != null) {
                writer.close();
                writer = null;
            }

        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    @Override
    public void run() {
        if (!IsParamOk)
            return;
        if (!NetworkHelper.CheckWifi()) {
            logger.i("WIFI不可用");
            OnReceive(ConnMsgType.Conn, ConnState.NoWifi, "请先连接WIFI！");
            return;
        }

        threadflag = true;
        tcpSocket = new Socket();
        try {
            tcpSocket.connect(new InetSocketAddress(IPAddress, Port), 5000);

            if (!tcpSocket.isConnected()) {
                OnReceive(ConnMsgType.Conn, ConnState.DisConnected, "连接失败！");
                tcpSocket.close();
                return;
            }
            reader = new DataInputStream(
                    tcpSocket.getInputStream());
            writer = tcpSocket.getOutputStream();
            logger.i("TCP连接成功!,localPort:" + tcpSocket.getLocalPort() + " inetAddress:" + tcpSocket.getInetAddress().toString() + ":" + tcpSocket.getPort());

            OnReceive(ConnMsgType.Conn, ConnState.Connected, "连接成功！");

            // 启动一条子线程来读取服务器相应的数据
            new Thread() {

                @Override
                public void run() {
                    IsRuning = true;
                    try {
                        // 不断的读取Socket输入流的内容
                        byte[] recvData = new byte[150];

                        int nCount = -1;
                        long lastMS = 0;
                        long currentMS = System.currentTimeMillis();
                        ArrayList<byte[]> rspBuffer = new ArrayList<byte[]>();

                        while (threadflag && (nCount = reader.read(recvData)) != -1) {
                            lastMS = currentMS;
                            currentMS = System.currentTimeMillis();
                            byte[] byteData = Arrays.copyOf(recvData, nCount);
                            if (rspBuffer.size() > 0) {
                                if (currentMS - lastMS < SOCKET_DELAY)//500ms以内
                                {
                                    //合并两次接收的数据包
                                    byte[] newData = Arrays.copyOf(rspBuffer.get(0), rspBuffer.get(0).length + byteData.length);
                                    System.arraycopy(byteData, 0, newData, rspBuffer.get(0).length, byteData.length);
                                    byteData = newData;
                                } else
                                    logger.e("时间太长丢包");
                                rspBuffer.clear();
                            } else {
                                if (CmdParser.needMoreData(byteData)) {
                                    logger.d("need more data");
                                    rspBuffer.add(byteData);
                                    continue; //需要更多数据，继续接收
                                }
                            }

                            OnReceive(ConnMsgType.Data, byteData);
                            logger.d("recv:" + StringUtil.Bytes2HexString(byteData));

                        }
                    } catch (IOException ioe) {
                        logger.error(ioe);
                        ioe.printStackTrace();
                    }finally {
                        IsRuning = false;
                        logger.i("Socket读线程退出");
                        OnReceive(ConnMsgType.Conn, ConnState.DisConnected, "LED连接结束");
                    }

                }

            }.start();
            // 为当前线程初始化Looper
            // Looper.prepare();

            // 启动Looper
            // Looper.loop();

        } catch (SocketTimeoutException e) {

            logger.error(e);
            OnReceive(ConnMsgType.Conn, ConnState.DisConnected, "网络连接超时！");
        } catch (IOException io) {
            logger.error(io);
            io.printStackTrace();
            OnReceive(ConnMsgType.Conn, ConnState.DisConnected, "网络IO错误！");
        }

    }

    /**
     * 判断是否断开连接，断开返回true,没有返回false
     *
     * @param socket
     * @return
     */
    public Boolean isServerClose(Socket socket) {
        try {
            socket.sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        } catch (Exception se) {
            return true;
        }
    }
}
