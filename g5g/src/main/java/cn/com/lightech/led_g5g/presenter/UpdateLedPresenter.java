package cn.com.lightech.led_g5g.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.com.lightech.led_g5g.entity.data.UpdateData;
import cn.com.lightech.led_g5g.gloabal.IDataListener;
import cn.com.lightech.led_g5g.gloabal.LedProxy;
import cn.com.lightech.led_g5g.net.ConnectManager;
import cn.com.lightech.led_g5g.net.ConnectionsManager;
import cn.com.lightech.led_g5g.net.entity.ConnState;
import cn.com.lightech.led_g5g.net.entity.Response;
import cn.com.lightech.led_g5g.net.utils.Logger;
import cn.com.lightech.led_g5g.view.console.IUpdateLedView;

/**
 * Created by 明 on 2016/4/18.
 */
public class UpdateLedPresenter implements IDataListener {

    private final int LED_MAX_LENGTH = 0X4000;

    private final IUpdateLedView updateLedView;
    private Logger logger = Logger.getLogger(UpdateLedPresenter.class);
    private static final int TOTAL_PACKAGE = 0x80;
    private int id2 = 0x00;
    private Context mContext;
    private byte[] bytes;

    public UpdateLedPresenter(Context context, IUpdateLedView updateLedView) {
        this.mContext = context;
        this.updateLedView = updateLedView;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onConnectStateChanged(ConnState connState, ConnectManager connectManager) {

    }

    @Override
    public boolean onReceive(Response response, ConnectManager connectManager) {
        if (response == null)
            return false;
        switch (response.getCmdType()) {
            case SendDataToLED:
                if (!response.IsOK())
                    logger.e("SendDataToLED failed modeIndex:%02d", this.id2);
                syncNext();
                break;
            case ValidateSumFailed:
            case IDFormatError: // 出现错误就跳过继续下一个吧
                syncNext();
                if (!response.IsOK())
                    logger.e("ValidateSumFailed IDFormatError failed modeIndex:%d",
                            this.id2);
                break;

            default:
                return true;
        }
        Logger.getLogger().d(
                response.getCmdType().toString() + "   "
                        + response.getReplyCode());
        return false;

    }

    private void syncNext() {
        id2++;
        if (id2 > TOTAL_PACKAGE) {
            finish();
            return;
        }
        UpdateData updateData = genUpdateNode();
        LedProxy.sendToLed(updateData);
    }


    @NonNull
    private UpdateData genUpdateNode() {
        byte[] data;
        UpdateData updateData = new UpdateData((byte) id2);
        if (id2 == 0x80) {
            data = new byte[4];
            data[0] = 1;
            data[1] = 1;
            int sum = 0;
            for (byte item : bytes) {
                sum = (sum + (item & 0xff)) & 0xffff;
            }
            data[2] = (byte) ((sum & 0xffff) >> 8);
            data[3] = (byte) (sum & 0xff);
        } else {
            data = new byte[128];
            int begin = id2 * 128;
            for (int i = 0; i < data.length; i++) {
                data[i] = bytes[begin + i];
            }
        }
        updateData.setData(data);
        return updateData;
    }


    private void finish() {
        String txt = String.format("stop ; current: %d  ; total: %d  ;",
                this.id2, TOTAL_PACKAGE);
        logger.e(txt);
        updateLedView.stopUpdate();
    }

    public void register() {
        ConnectionsManager.getInstance().registerHigh(this, false);
    }

    public void unRegister() {
        ConnectionsManager.getInstance().unRegister(this);
    }


    public void starUpdate() {
        bytes = readLedData();
        if (bytes == null || bytes.length != LED_MAX_LENGTH) {
            updateLedView.stopUpdate();
            return;
        }
        LedProxy.sendToLed(genUpdateNode());

    }

    private byte[] readLedData() {
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            InputStream stream = mContext.getAssets().open("firmware_upgrade_package_grow.bin");
            int total = 0;
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = stream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
                total += rc;
            }
            if (total != LED_MAX_LENGTH) {
                byte[] buffer = new byte[]{(byte) 0xff};
                for (int i = total; i < LED_MAX_LENGTH; i++) {
                    swapStream.write(buffer, 0, 1);
                }
            }
            Log.i("readLedData", swapStream.size() + " / " + LED_MAX_LENGTH);
            return swapStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
