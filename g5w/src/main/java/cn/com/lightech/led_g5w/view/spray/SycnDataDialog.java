package cn.com.lightech.led_g5w.view.spray;

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
import cn.com.lightech.led_g5w.presenter.SycnWaveDataPresenter;
import cn.com.lightech.led_g5w.view.console.ISycnDataView;

public class SycnDataDialog extends Dialog implements ISycnDataView {


    @Bind(R.id.tv_msg)
    TextView tvMsg;

    private Context mContext;

    private SycnWaveDataPresenter sycnWaveDataPresenter;


    public SycnDataDialog(Context context, String ip) {
        super(context, android.R.style.Theme_Holo_Light_Dialog);
        sycnWaveDataPresenter = new SycnWaveDataPresenter(context, this, ip);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_somthing);
        ButterKnife.bind(this);

        sycnWaveDataPresenter.register();
        sycnWaveDataPresenter.syncData();
    }


    @Override
    public void showMessage(String message) {
        this.tvMsg.setText(message);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sycnWaveDataPresenter.unRegister();
    }

    @Override
    public void stopSycn() {
        dismiss();
    }
}
