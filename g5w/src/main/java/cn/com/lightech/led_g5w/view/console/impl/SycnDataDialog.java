package cn.com.lightech.led_g5w.view.console.impl;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.entity.DeviceGroup;
import cn.com.lightech.led_g5w.presenter.SycnLEDDataPresenter;
import cn.com.lightech.led_g5w.view.console.ISycnDataView;

public class SycnDataDialog extends Dialog implements ISycnDataView {


    @Bind(R.id.tv_msg)
    TextView tvMsg;
    private DeviceGroup group;


    private Context mContext;

    private SycnLEDDataPresenter sycnLEDDataPresenter;


    public SycnDataDialog(Context context, DeviceGroup group, String ip) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
        sycnLEDDataPresenter = new SycnLEDDataPresenter(context, this, ip);
        this.mContext = context;
        this.group = group;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_somthing);
        ButterKnife.bind(this);

        sycnLEDDataPresenter.register();
        sycnLEDDataPresenter.syncData();
    }


    @Override
    public void showMessage(String message) {
        this.tvMsg.setText(message);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sycnLEDDataPresenter.unRegister();
    }

    @Override
    public void stopSycn() {
        dismiss();
    }
}
