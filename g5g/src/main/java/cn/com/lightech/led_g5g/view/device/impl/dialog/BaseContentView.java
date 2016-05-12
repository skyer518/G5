package cn.com.lightech.led_g5g.view.device.impl.dialog;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import cn.com.u2be.alekwifilibrary.ChangePasswordWifiDialogContent;
import cn.com.u2be.alekwifilibrary.Wifi;

/**
 * Created by 明 on 2016/4/20.
 */
public abstract class BaseContentView implements WifiDialog.WifiContent, CompoundButton.OnCheckedChangeListener {

    protected static final int[] SIGNAL_LEVEL = {cn.com.u2be.alekwifilibrary.R.string.wifi_signal_0, cn.com.u2be.alekwifilibrary.R.string.wifi_signal_1,
            cn.com.u2be.alekwifilibrary.R.string.wifi_signal_2, cn.com.u2be.alekwifilibrary.R.string.wifi_signal_3};


    protected final WifiManager mWifiManager;
    protected final WifiDialog dialog;
    protected final ScanResult mScanResult;
    protected final String mScanResultSecurity;
    protected final boolean mIsOpenNetwork;

    protected int mNumOpenNetworksKept;

    protected View mView;

    protected View.OnClickListener mCancelOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

    protected String getCancelString() {
        return dialog.getContext().getString(android.R.string.cancel);
    }

    public BaseContentView(final WifiDialog dialog, final WifiManager wifiManager, final ScanResult scanResult) {
        super();
        mWifiManager = wifiManager;
        this.dialog = dialog;
        mScanResult = scanResult;
        mScanResultSecurity = Wifi.ConfigSec.getScanResultSecurity(mScanResult);
        mIsOpenNetwork = Wifi.ConfigSec.isOpenNetwork(mScanResultSecurity);

        mView = View.inflate(this.dialog.getContext(), cn.com.u2be.alekwifilibrary.R.layout.base_content, null);


        ((TextView) mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.SignalStrength_TextView)).setText(SIGNAL_LEVEL[WifiManager.calculateSignalLevel(mScanResult.level, SIGNAL_LEVEL.length)]);
        final String rawSecurity = Wifi.ConfigSec.getDisplaySecirityString(mScanResult);
        final String readableSecurity = Wifi.ConfigSec.isOpenNetwork(rawSecurity) ? this.dialog.getContext().getString(cn.com.u2be.alekwifilibrary.R.string.wifi_security_open) : rawSecurity;
        ((TextView) mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Security_TextView)).setText(readableSecurity);
        ((CheckBox) mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.ShowPassword_CheckBox)).setOnCheckedChangeListener(this);

        mNumOpenNetworksKept = Settings.Secure.getInt(dialog.getContext().getContentResolver(),
                Settings.Secure.WIFI_NUM_OPEN_NETWORKS_KEPT, 10);
    }


    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ((EditText) mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Password_EditText)).setInputType(
                InputType.TYPE_CLASS_TEXT |
                        (isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                : InputType.TYPE_TEXT_VARIATION_PASSWORD));
    }

    public View.OnClickListener mChangePasswordOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            changePassword();
        }

    };

    public void changePassword() {
        dialog.setContent(new ChangePasswordView(dialog, mWifiManager, mScanResult));
    }

}
