package cn.com.lightech.led_g5w.view.console.impl;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.presenter.UpdataLedPresenter;
import cn.com.lightech.led_g5w.view.console.IUpdataLedView;

public class UpdataLedDialog extends Dialog implements IUpdataLedView {


    @Bind(R.id.tv_msg)
    TextView tvMsg;

    private Context mContext;
    private UpdataLedPresenter updataLedPresenter;


    public UpdataLedDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
        updataLedPresenter = new UpdataLedPresenter(context, this);
        this.mContext = context;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_somthing);
        ButterKnife.bind(this);
        showMessage(R.string.msg_updataLed);
        updataLedPresenter.register();
        updataLedPresenter.starUpdata();
    }


    @Override
    public void showMessage(String message) {
        this.tvMsg.setText(message);
    }

    public void showMessage(int message) {
        this.tvMsg.setText(message);
    }

    @Override
    protected void onStop() {
        super.onStop();
        updataLedPresenter.unRegister();
    }

    @Override
    public void stopUpdata() {
        dismiss();
    }


}
