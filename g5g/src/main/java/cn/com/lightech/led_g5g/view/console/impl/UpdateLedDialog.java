package cn.com.lightech.led_g5g.view.console.impl;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.presenter.UpdateLedPresenter;
import cn.com.lightech.led_g5g.view.console.IUpdateLedView;

public class UpdateLedDialog extends Dialog implements IUpdateLedView {


    @Bind(R.id.tv_msg)
    TextView tvMsg;

    private Context mContext;
    private UpdateLedPresenter updateLedPresenter;


    public UpdateLedDialog(Context context) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
        updateLedPresenter = new UpdateLedPresenter(context, this);
        this.mContext = context;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_somthing);
        ButterKnife.bind(this);
        showMessage(R.string.msg_updateLed);
        updateLedPresenter.register();
        updateLedPresenter.starUpdate();
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
        updateLedPresenter.unRegister();
    }

    @Override
    public void stopUpdate() {
        dismiss();
    }


}
