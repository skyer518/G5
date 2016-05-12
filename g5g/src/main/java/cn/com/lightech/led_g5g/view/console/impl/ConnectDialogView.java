package cn.com.lightech.led_g5g.view.console.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.adapter.DeviceConnectAdapter;
import cn.com.lightech.led_g5g.entity.Device;
import cn.com.lightech.led_g5g.entity.DeviceGroup;
import cn.com.lightech.led_g5g.net.ConnectionsManager;
import cn.com.lightech.led_g5g.net.utils.TimerHelper;
import cn.com.lightech.led_g5g.presenter.DeviceConnectLedPresenter;
import cn.com.lightech.led_g5g.utils.UIHelper;
import cn.com.lightech.led_g5g.view.console.IConnectView;

public class ConnectDialogView extends LinearLayout implements IConnectView {

    public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private static final int MSG_WIFI_STATE_CHANGE = 1;
    private DeviceGroup group;

    @Bind(R.id.lv_deviceConnect)
    ListView lvDeviceConnect;
    @Bind(R.id.tv_btn_ok)
    Button tvBtnOk;
    @Bind(R.id.tv_btn_cancel)
    Button tvBtnCancel;

    private Context mContext;

    private DeviceConnectLedPresenter connectPresenter;

    private TimerHelper connectStateTimer = null;
    private Handler timerHandler = null;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WIFI_STATE_CHANGE:
                    break;

                default:
                    break;
            }
        }

        ;
    };
    private DeviceConnectAdapter adapter;

    public ConnectDialogView(Context context, DeviceGroup group) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.dialog_connect, this);
        ButterKnife.bind(this);

        this.mContext = context;
        this.group = group;
        this.connectPresenter = new DeviceConnectLedPresenter(mContext, this, group);
        init();
    }


    private void init() {
        enableButoonOK(false);

        adapter = new DeviceConnectAdapter(mContext, group.getDevices());
        lvDeviceConnect.setAdapter(adapter);
        lvDeviceConnect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setCurrentPosition(position);
                adapter.notifyDataSetChanged();
            }
        });
        lvDeviceConnect.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                connectPresenter.blinkLed((Device) adapter.getItem(position));
                return true;
            }
        });
        connectPresenter.priorityConnect();
        connectPresenter.registDataListener();
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        connectPresenter.unRegistDataListener();
    }


    @OnClick({R.id.tv_btn_ok, R.id.tv_btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_btn_ok:
                if (!adapter.isAllConnect()) {
                    new AlertDialog.Builder(mContext)
                            .setMessage("有设备连接成功，是否要继续？")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    connectPresenter.gotoControl(adapter.getCurrentPosition());
                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                } else {
                    connectPresenter.gotoControl(adapter.getCurrentPosition());
                }
                //gotoControl();
                break;
            case R.id.tv_btn_cancel:
                ConnectionsManager.getInstance().clearPriorityConnections();
                UIHelper.getInstance().closeConnectDialog();
                break;
        }
    }

    public void enableButoonOK(boolean enable) {
        tvBtnOk.setEnabled(enable);
    }


    public void updateItem(String host) {
        int position = getPosition(host);
        if (position != -1)
            adapter.addConnect(position);
        if (!tvBtnOk.isEnabled()) {
            enableButoonOK(true);
            adapter.setCurrentPosition(position);
        }
        adapter.notifyDataSetChanged();
    }

    private int getPosition(String host) {
        for (int i = 0; i < group.getDevices().size(); i++) {
            if (host.equals(group.getDevices().get(i).getIp()))
                return i;
        }
        return -1;
    }


}
