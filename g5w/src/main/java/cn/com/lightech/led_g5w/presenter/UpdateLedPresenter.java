package cn.com.lightech.led_g5w.presenter;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.com.lightech.led_g5w.entity.UpdateNode;
import cn.com.lightech.led_g5w.gloabal.IDataListener;
import cn.com.lightech.led_g5w.gloabal.LedProxy;
import cn.com.lightech.led_g5w.net.ConnectManager;
import cn.com.lightech.led_g5w.net.ConnectionsManager;
import cn.com.lightech.led_g5w.net.entity.ConnState;
import cn.com.lightech.led_g5w.net.entity.Response;
import cn.com.lightech.led_g5w.net.utils.Logger;
import cn.com.lightech.led_g5w.view.console.IUpdateLedView;

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
        UpdateNode updateNode = genUpdateNode();
        LedProxy.sendToLed(updateNode);
    }


    @NonNull
    private UpdateNode genUpdateNode() {
        byte[] data;
        UpdateNode updateNode = new UpdateNode((byte) id2);
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
        updateNode.setData(data);
        return updateNode;
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
        InputStream stream = null;
        final File sdRootPath = Environment.getExternalStorageDirectory();

        File file = new File(sdRootPath + File.separator + "firmware_upgrade_package_water.bin");
        try {
            if (file.exists()) {
                stream = new FileInputStream(file);
            } else {
                stream = mContext.getAssets().open("firmware_upgrade_package_water.bin");
            }
        } catch (FileNotFoundException e) {
            logger.i("SD card root directory of the upgrade file not found...");
        } catch (IOException e) {
            logger.i("The default upgrade file read error...");
        }


        bytes = readUpgradeFirmware(stream);
        if (bytes == null || bytes.length != LED_MAX_LENGTH) {
            updateLedView.stopUpdate();
            return;
        }
        LedProxy.sendToLed(genUpdateNode());

    }

    private byte[] readUpgradeFirmware(InputStream stream) {
        ByteArrayOutputStream swapStream = null;
        try {
            // stream = mContext.getAssets().open("firmware_upgrade_package_water.bin");
            swapStream = new ByteArrayOutputStream();
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
            Log.i("readUpgradeFirmware", swapStream.size() + " / " + LED_MAX_LENGTH);
            return swapStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (swapStream != null) {
                    swapStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
