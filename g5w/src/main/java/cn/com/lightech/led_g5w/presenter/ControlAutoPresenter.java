package cn.com.lightech.led_g5w.presenter;

import android.content.Context;

import java.util.List;

import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.entity.AutoDataNode;
import cn.com.lightech.led_g5w.entity.CurvePoint;
import cn.com.lightech.led_g5w.entity.DataNode;
import cn.com.lightech.led_g5w.entity.LampChannel;
import cn.com.lightech.led_g5w.gloabal.DataManager;
import cn.com.lightech.led_g5w.gloabal.IDataListener;
import cn.com.lightech.led_g5w.gloabal.LedProxy;
import cn.com.lightech.led_g5w.net.ConnectManager;
import cn.com.lightech.led_g5w.net.entity.ConnState;
import cn.com.lightech.led_g5w.net.entity.ReplyErrorCode;
import cn.com.lightech.led_g5w.net.entity.Response;
import cn.com.lightech.led_g5w.net.utils.Logger;
import cn.com.lightech.led_g5w.net.ConnectionsManager;
import cn.com.lightech.led_g5w.view.console.IAutoView;

/**
 * Created by 明 on 2016/3/15.
 */
public class ControlAutoPresenter implements IDataListener {

    private final Context mContext;
    private final IAutoView autoView;

    private int mCursor;


    public ControlAutoPresenter(Context context, IAutoView autoView) {
        this.mContext = context;
        this.autoView = autoView;
    }


    public void saveAuto() {
        LedProxy.sendToLed(DataManager.getInstance().getAutoDataNode());
        LedProxy.sendToLed(DataManager.getInstance().getAutoTimingDataNode());
    }


    public void previewAuto() {
        LedProxy.stopPreview();
        LedProxy.previewCurve();
    }

    public void previewChanel(LampChannel channel) {
        //LedProxy.stopPreview();
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
                    if (dataNode instanceof AutoDataNode) {

                    }
                }
                break;
            case StopPreview:
                if (response.getReplyCode() == ReplyErrorCode.OK) {
                    this.autoView.stopPreview();
                }
                break;
            case PreViewCurve:
                if (response.getReplyCode() != ReplyErrorCode.OK) {
                    this.autoView.stopPreview();
                } else {
                    this.autoView.preview();
                }
                break;
            default:
                break;
        }
        Logger.getLogger().d(
                response.getCmdType().toString() + "   "
                        + response.getReplyCode());
        return true;
    }


    public void deletePoint() {
        validCursor();
        DataManager.getInstance().getAutoDataNode().getPoints().get(mCursor);
        DataManager.getInstance().getAutoDataNode().getPoints().remove(mCursor);
        saveAuto();

        canAdd();
        mCursor--;
        loadCursor(mCursor);
        autoView.drawChart();
    }

    private void validCursor() {
        if (mCursor >= DataManager.getInstance().getAutoDataNode().getPoints().size()) {
            mCursor = DataManager.getInstance().getAutoDataNode().getPoints().size() - 1;
        }
        if (mCursor < 0) {
            mCursor = 0;
        }
    }


    public void backDefault() {
        DataManager.getInstance().backDefaultAuto();
        saveAuto();
        canAdd();
        autoView.drawChart();
        mCursor = 0;
    }


    public void loadCursor(int spike) {
        mCursor += spike;
        validCursor();
        canDelete();
        CurvePoint point = DataManager.getInstance().getAutoDataNode().getPoints().get(mCursor);
        int time = point.getTime();
        autoView.spikToCurrent(time);
        canEdit(time);
    }

    public void addPoint(CurvePoint point) {
        int index = DataManager.getInstance().getAutoDataNode().addPoint(point);
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
        List<CurvePoint> points = DataManager.getInstance().getAutoDataNode().getPoints();
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
        List<CurvePoint> points = DataManager.getInstance().getAutoDataNode().getPoints();
        if (points.get(mCursor).getTime() == time) {
            autoView.enableEditButton(true);
        } else {
            autoView.enableEditButton(false);
        }
    }

    public void canAdd() {
        autoView.enableAddButton(!DataManager.getInstance().getAutoDataNode().isOutOfBounds());
    }
}
