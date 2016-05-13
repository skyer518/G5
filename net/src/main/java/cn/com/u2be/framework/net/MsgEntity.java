package cn.com.u2be.framework.net;

import android.os.Handler;

/**
 * Created by alek on 2016/5/13.
 */
public class MsgEntity {

    private Handler handler;

    private byte[] data;

    public MsgEntity(byte[] data, Handler handler) {
        this.data = data;
        this.handler = handler;
    }


    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
