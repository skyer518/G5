package cn.com.lightech.led_g5w.view.device.impl.dialog;

import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.lightech.led_g5w.utils.PreferenceUtils;
import cn.com.u2be.alekwifilibrary.Wifi;

/**
 * Created by 明 on 2016/4/20.
 */
public class CurrentNetworkView extends BaseContentView {

    private final String mPassword;

    public CurrentNetworkView(WifiDialog dialog, WifiManager wifiManager,
                              ScanResult scanResult, String password) {
        super(dialog, wifiManager, scanResult);
        this.mPassword = password;
        mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Status).setVisibility(View.GONE);
        mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Speed).setVisibility(View.GONE);
        mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.IPAddress).setVisibility(View.GONE);
        mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Password).setVisibility(View.GONE);

        final WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            Toast.makeText(dialog.getContext(), cn.com.u2be.alekwifilibrary.R.string.toastFailed, Toast.LENGTH_LONG).show();
        } else {
            final SupplicantState state = wifiInfo.getSupplicantState();
            final NetworkInfo.DetailedState detailedState = WifiInfo.getDetailedStateOf(state);
            if (detailedState == NetworkInfo.DetailedState.CONNECTED
                    || (detailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR && wifiInfo.getIpAddress() != 0)) {
                mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Status).setVisibility(View.VISIBLE);
                mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Speed).setVisibility(View.VISIBLE);
                mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.IPAddress).setVisibility(View.VISIBLE);

                ((TextView) mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Status_TextView)).setText(cn.com.u2be.alekwifilibrary.R.string.status_connected);
                ((TextView) mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.LinkSpeed_TextView)).setText(wifiInfo.getLinkSpeed() + " " + WifiInfo.LINK_SPEED_UNITS);
                ((TextView) mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.IPAddress_TextView)).setText(getIPAddress(wifiInfo.getIpAddress()));
            } else if (detailedState == NetworkInfo.DetailedState.AUTHENTICATING
                    || detailedState == NetworkInfo.DetailedState.CONNECTING
                    || detailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Status).setVisibility(View.VISIBLE);
                ((TextView) mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Status_TextView)).setText(cn.com.u2be.alekwifilibrary.R.string.status_connecting);
            }
        }
    }

    @Override
    public int getButtonCount() {
        // No Modify button for open network.
        return mIsOpenNetwork ? 2 : 3;
    }

    private View.OnClickListener mConfirmOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            dialog.getListener().mConnectOnClick(dialog, mScanResult, mPassword, false);
        }
    };

    private View.OnClickListener mForgetOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
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
    };
    private View.OnClickListener mOnClickListeners[] = {mConfirmOnClick, mForgetOnClick, mChangePasswordOnClick};

    @Override
    public View.OnClickListener getButtonOnClickListener(int index) {
        if (mIsOpenNetwork && index == 2) {
            // No Modify button for open network.
            // index 1 is Cancel(index 2).
            return mOnClickListeners[3];
        }
        return mOnClickListeners[index];
    }

    @Override
    public CharSequence getButtonText(int index) {
        switch (index) {
            case 0:
                return dialog.getContext().getString(cn.com.u2be.alekwifilibrary.R.string.wifi_confirm);
            case 1:
                return dialog.getContext().getString(cn.com.u2be.alekwifilibrary.R.string.forget_network);
            case 2:
                if (mIsOpenNetwork) {
                    // No Modify button for open network.
                    // index 1 is Cancel.
                    return getCancelString();
                }
                return dialog.getContext().getString(cn.com.u2be.alekwifilibrary.R.string.button_change_password);
            case 3:
                return getCancelString();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getTitle() {
        return mScanResult.SSID;
    }


    private String getIPAddress(int address) {
        StringBuilder sb = new StringBuilder();
        sb.append(address & 0x000000FF).append(".")
                .append((address & 0x0000FF00) >> 8).append(".")
                .append((address & 0x00FF0000) >> 16).append(".")
                .append((address & 0xFF000000L) >> 24);
        return sb.toString();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
    }

}
