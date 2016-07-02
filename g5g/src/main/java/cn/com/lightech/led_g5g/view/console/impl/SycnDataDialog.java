package cn.com.lightech.led_g5g.view.console.impl;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.entity.DeviceGroup;
import cn.com.lightech.led_g5g.gloabal.App;
import cn.com.lightech.led_g5g.presenter.SycnDataPresenter;
import cn.com.lightech.led_g5g.view.console.ISycnDataView;

public class SycnDataDialog extends Dialog implements ISycnDataView {


    @Bind(R.id.tv_msg)
    TextView tvMsg;
    private DeviceGroup group;


    private Context mContext;

    private SycnDataPresenter sycnDataPresenter;


    public SycnDataDialog(Context context, DeviceGroup group, String ip) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
        sycnDataPresenter = new SycnDataPresenter(context, this, ip);
        this.mContext = context;
        this.group = group;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_somthing);
        ButterKnife.bind(this);

        sycnDataPresenter.register();
        sycnDataPresenter.syncData();
    }


    @Override
    public void showMessage(String message) {
        this.tvMsg.setText(message);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sycnDataPresenter.unRegister();
    }

    @Override
    public void stopSycn() {
        dismiss();
    }

    @Override
    public void finishSycn() {
        dismiss();
        Intent intent = new Intent();
        intent.setClass(App.getInstance(), ControlActivity.class);
        intent.putExtra(ControlActivity.ARGS_DEVICE_GROUP, group);
        mContext.startActivity(intent);
    }
}
