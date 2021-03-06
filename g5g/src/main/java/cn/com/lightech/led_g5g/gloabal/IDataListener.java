package cn.com.lightech.led_g5g.gloabal;

import java.util.Comparator;

import cn.com.lightech.led_g5g.net.ConnectManager;
import cn.com.lightech.led_g5g.net.entity.ConnState;
import cn.com.lightech.led_g5g.net.entity.Response;


public interface IDataListener {
    /**
     * 权重
     *
     * @return
     */
    int getPriority();

    /**
     * 连接状态变化
     */
    void onConnectStateChanged(ConnState connState, ConnectManager connectManager);

    /**
     * 消息回调 ,返回 true 继续，返回false 停止
     */
    boolean onReceive(Response response, ConnectManager connectManager);
}
