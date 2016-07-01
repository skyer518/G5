package cn.com.lightech.led_g5g.presenter;

import android.content.Context;

import java.util.List;

import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.entity.LampChannel;
import cn.com.lightech.led_g5g.entity.data.CurveData;
import cn.com.lightech.led_g5g.entity.CurvePoint;
import cn.com.lightech.led_g5g.entity.DataNode;
import cn.com.lightech.led_g5g.gloabal.DataManager;
import cn.com.lightech.led_g5g.gloabal.IDataListener;
import cn.com.lightech.led_g5g.gloabal.LedProxy;
import cn.com.lightech.led_g5g.net.ConnectManager;
import cn.com.lightech.led_g5g.net.entity.ConnState;
import cn.com.lightech.led_g5g.net.entity.ReplyErrorCode;
import cn.com.lightech.led_g5g.net.entity.Response;
import cn.com.lightech.led_g5g.net.utils.Logger;
import cn.com.lightech.led_g5g.net.ConnectionsManager;
import cn.com.lightech.led_g5g.view.console.IAutoView;

/**
 * Created by 明 on 2016/3/15.
 */
public class ControlCurvePresenter implements IDataListener {

    private final Context mContext;
    private final IAutoView autoView;
    private final byte dataId;
    private final byte timingId;
    private int mCursor;

    private Logger logger = Logger.getLogger(getClass());


    public ControlCurvePresenter(Context context, IAutoView autoView, byte dataId, byte timingId) {
        this.mContext = context;
        this.autoView = autoView;
        this.dataId = dataId;
        this.timingId = timingId;

    }


    public void saveAuto() {
        LedProxy.sendToLed(DataManager.getInstance().getCurveDataById2(dataId));
        LedProxy.sendToLed(DataManager.getInstance().getCurveDataById2(timingId));

        logger.e("saveAuto curve: [%d,%d]", dataId, timingId);
    }


    public void previewAuto() {
        LedProxy.stopPreview();
        LedProxy.previewCurve();
    }

    public void previewChanel(LampChannel channel) {
        // LedProxy.stopPreview();
        LedProxy.preview(channel);
    }

    public void stopPreviewChanel() {
        LedProxy.stopPreview();
    }

    public void registerDataListener() {
        ConnectionsManager.getInstance().registerHigh(this, false);
    }

    public void unRegisterDataListener() {
        ConnectionsManager.getInstance().unRegister(this);
    }

    public void stopPreview() {
        LedProxy.stopPreview();
    }


    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public void onConnectStateChanged(ConnState connState, ConnectManager connectManager) {
        switch (connState) {
            case NoWifi:
            case DisConnected:
            case ParamError:
            case Connected:
                //loadWifiState();
                break;
        }
    }

    @Override
    public boolean onReceive(Response response, ConnectManager connectManager) {
        if (response == null)
            return true;
        switch (response.getCmdType()) {
            case SendDataToLED:
                if (response.getReplyCode() == ReplyErrorCode.OK) {
                    DataNode dataNode = response.getDataNode();
                    if (dataNode instanceof CurveData) {

                    }
                }
                break;
            case StopPreview:
                if (response.getReplyCode() == ReplyErrorCode.OK) {
                    this.autoView.stopPreview();
                }
                break;
            case PreViewCurve:
                if (response.getReplyCode() == ReplyErrorCode.OK) {
                    this.autoView.preview();
                }
                break;
            default:
                break;
        }
        logger.d(
                response.getCmdType().toString() + "   "
                        + response.getReplyCode());
        return true;
    }


    public void deletePoint() {
        if (mCursor != 0) {
            validCursor();
            DataManager.getInstance().getCurveDataById2(dataId).getPoints().get(mCursor);
            DataManager.getInstance().getCurveDataById2(dataId).getPoints().remove(mCursor);
            saveAuto();
            canAdd();
            mCursor--;
            loadCursor(mCursor);
            autoView.drawChart();
        }
    }

    private void validCursor() {
        if (mCursor >= DataManager.getInstance().getCurveDataById2(dataId).getPoints().size()) {
            mCursor = DataManager.getInstance().getCurveDataById2(dataId).getPoints().size() - 1;
        }
        if (mCursor < 0) {
            mCursor = 0;
        }
    }


    public void backDefault() {
        DataManager.getInstance().backDefaultAuto(dataId);
        saveAuto();
        canAdd();
        autoView.drawChart();
        mCursor = 0;
    }


    public void loadCursor(int spike) {
        mCursor += spike;
        validCursor();
        canDelete();
        CurvePoint point = DataManager.getInstance().getCurveDataById2(dataId).getPoints().get(mCursor);
        int time = point.getTime();
        autoView.spikToCurrent(time);
        canEdit(time);
    }

    public void addPoint(CurvePoint point) {
        final int index = DataManager.getInstance().getCurveDataById2(dataId).addPoint(point);
        if (index == -1) {
            autoView.showMessage(mContext.getString(R.string.error_points_too_match));
            return;
        }
        saveAuto();
        canAdd();
        if (mCursor < index)
            loadCursor(1);
        else
            loadCursor(0);
        autoView.drawChart();
    }

    public void spikeCursor(int time) {
        List<CurvePoint> points = DataManager.getInstance().getCurveDataById2(dataId).getPoints();
        if (points.get(mCursor).getTime() < time) {
            for (int i = mCursor; i < points.size(); i++) {
                if (points.get(i).getTime() == time) {
                    mCursor = i;
                    break;
                }
                if (points.get(i).getTime() > time) {
                    mCursor = i - 1;
                    break;
                }
                if (i == points.size() - 1) {
                    mCursor = i;
                }
            }
        } else if (points.get(mCursor).getTime() > time) {
            for (int i = mCursor; i >= 0; i--) {
                if (points.get(i).getTime() == time) {
                    mCursor = i;
                    break;
                }
                if (points.get(i).getTime() < time) {
                    mCursor = i + 1;
                    break;
                }
            }
        }
        canDelete();
        canEdit(time);

    }

    public void canDelete() {
        if (mCursor == 0) {
            autoView.enableDeleteButton(false);
        } else {
            autoView.enableDeleteButton(true);
        }
    }

    public void canEdit(int time) {
        List<CurvePoint> points = DataManager.getInstance().getCurveDataById2(dataId).getPoints();
        if (points.get(mCursor).getTime() == time) {
            autoView.enableEditButton(true);
        } else {
            autoView.enableEditButton(false);
        }
    }

    public void canAdd() {
        autoView.enableAddButton(!DataManager.getInstance().getCurveDataById2(dataId).isOutOfBounds());
    }
}
