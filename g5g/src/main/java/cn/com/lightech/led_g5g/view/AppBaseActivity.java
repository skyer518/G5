package cn.com.lightech.led_g5g.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.util.Log;

import cn.com.lightech.led_g5g.gloabal.LocalPhoneParms;
import cn.com.lightech.led_g5g.utils.ProgressUtil;
import cn.com.u2be.xbase.activity.BaseActivity;

/**
 * Created by 明 on 2016/3/4.
 */
public abstract class AppBaseActivity extends BaseActivity implements IBaseView {
    AlertDialog msgDilog;

    @Override
    public void showMessage(String message) {
        msgDilog = new AlertDialog.Builder(this)
                .setTitle("Error").setMessage(message).create();
        msgDilog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ProgressUtil.closeDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadDisplayMetrics();
        super.onCreate(savedInstanceState);
    }

    private void loadDisplayMetrics() {
        // others
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        LocalPhoneParms
                .setPhoneHeight(dm.widthPixels > dm.heightPixels ? dm.heightPixels
                        : dm.widthPixels); // 保存手机的长宽到LocalPhoneParams类
        LocalPhoneParms
                .setPhoneWidth(dm.widthPixels > dm.heightPixels ? dm.widthPixels
                        : dm.heightPixels);
        Log.i("AppBaseActivity", "height:" + LocalPhoneParms.getPhoneHeight() + " width:"
                + LocalPhoneParms.getPhoneWidth());
    }

}
