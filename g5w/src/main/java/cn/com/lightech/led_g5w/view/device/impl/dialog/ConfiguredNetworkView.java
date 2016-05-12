package cn.com.lightech.led_g5w.view.device.impl.dialog;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import cn.com.lightech.led_g5w.utils.PreferenceUtils;
import cn.com.u2be.alekwifilibrary.Wifi;

/**
 * Created by 明 on 2016/4/20.
 */
public class ConfiguredNetworkView extends BaseContentView implements CompoundButton.OnCheckedChangeListener {


    private final String mPassword;

    public ConfiguredNetworkView(WifiDialog WifiDialog, WifiManager wifiManager,
                                 ScanResult scanResult, String password) {
        super(WifiDialog, wifiManager, scanResult);

        this.mPassword = password;
        mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Status).setVisibility(View.GONE);
        mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Speed).setVisibility(View.GONE);
        mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.IPAddress).setVisibility(View.GONE);
        mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Password).setVisibility(View.GONE);
    }

    @Override
    public int getButtonCount() {
        if (mIsOpenNetwork)
            return 2;
        return 3;
    }

    @Override
    public View.OnClickListener getButtonOnClickListener(int index) {
        switch (index) {
            case 0:
                return mConnectOnClick;
            case 1:
                return mForgetOnClick;
            case 2:
                if (mIsOpenNetwork) {
                    return mCancelOnClick;
                } else {
                    return mChangeOnClick;
                }
            default:
                return null;
        }
    }

    @Override
    public CharSequence getButtonText(int index) {
        switch (index) {
            case 0:
                return dialog.getContext().getString(cn.com.u2be.alekwifilibrary.R.string.connect);
            case 1:
                return dialog.getContext().getString(cn.com.u2be.alekwifilibrary.R.string.forget_network);
            case 2:
                if (mIsOpenNetwork) {
                    return getCancelString();
                } else {
                    return dialog.getContext().getString(cn.com.u2be.alekwifilibrary.R.string.wifi_change_password);
                }
            case 3:
                return getCancelString();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getTitle() {
        return dialog.getContext().getString(cn.com.u2be.alekwifilibrary.R.string.wifi_connect_to, mScanResult.SSID);
    }

    private View.OnClickListener mChangeOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            changePassword();
        }
    };

    private View.OnClickListener mConnectOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            dialog.getListener().mConnectOnClick(dialog, mScanResult, mPassword, false);
        }
    };

    private View.OnClickListener mOpOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            dialog.registerForContextMenu(v);
            dialog.openContextMenu(v);
//            dialog.unregisterForContextMenu(v);
        }
    };

    private View.OnClickListener mForgetOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            forget();
        }
    };

    private void forget() {
        final WifiConfiguration config = Wifi.getWifiConfiguration(mWifiManager, mScanResult, mScanResultSecurity);
        boolean result = false;
        if (config != null) {
            result = mWifiManager.removeNetwork(config.networkId)
                    && mWifiManager.saveConfiguration();

        }
        if (!result) {
            Toast.makeText(dialog.getContext(), cn.com.u2be.alekwifilibrary.R.string.toastFailed, Toast.LENGTH_LONG).show();
        }
        PreferenceUtils.removeWifiConfig(config.SSID);
        dialog.dismiss();
    }

    private static final int MENU_FORGET = 0;
    private static final int MENU_CHANGE_PASSWORD = 1;


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_FORGET:
                forget();
                break;
            case MENU_CHANGE_PASSWORD:
                changePassword();
                break;
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, MENU_FORGET, Menu.NONE, cn.com.u2be.alekwifilibrary.R.string.forget_network);
        menu.add(Menu.NONE, MENU_CHANGE_PASSWORD, Menu.NONE, cn.com.u2be.alekwifilibrary.R.string.wifi_change_password);
    }

}
