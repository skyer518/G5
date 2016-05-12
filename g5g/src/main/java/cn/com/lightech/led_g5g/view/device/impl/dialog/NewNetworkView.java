package cn.com.lightech.led_g5g.view.device.impl.dialog;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.com.u2be.alekwifilibrary.Wifi;

/**
 * Created by 明 on 2016/4/20.
 */
public class NewNetworkView extends BaseContentView {


    private boolean mIsOpenNetwork = false;

    public NewNetworkView(final WifiDialog dialog, final WifiManager wifiManager, ScanResult scanResult) {
        super(dialog, wifiManager, scanResult);

        mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Status).setVisibility(View.GONE);
        mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Speed).setVisibility(View.GONE);
        mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.IPAddress).setVisibility(View.GONE);
        if (Wifi.ConfigSec.isOpenNetwork(mScanResultSecurity)) {
            mIsOpenNetwork = true;
            mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Password).setVisibility(View.GONE);
        } else {
            ((TextView) mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Password_TextView)).setText(cn.com.u2be.alekwifilibrary.R.string.please_type_passphrase);
        }
    }

    private View.OnClickListener mConnectOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //           boolean connResult;
            String password = ((EditText) mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.Password_EditText)).getText().toString();
//            if (mIsOpenNetwork) {
//                connResult = Wifi.connectToNewNetwork(dialog.getContext(), mWifiManager, mScanResult, null, mNumOpenNetworksKept);
//            } else {
//                connResult = Wifi.connectToNewNetwork(dialog.getContext(), mWifiManager, mScanResult
//                        , password
//                        , mNumOpenNetworksKept);
//            }
            dialog.getListener().mConnectOnClick(dialog, mScanResult, password, false);
//            if (!connResult) {
//                Toast.makeText(dialog.getContext(), cn.com.u2be.alekwifilibrary.R.string.toastFailed, Toast.LENGTH_LONG).show();
//            }
//
//            dialog.dismiss();
        }
    };

    private View.OnClickListener mOnClickListeners[] = {mConnectOnClick, mCancelOnClick};

    @Override
    public int getButtonCount() {
        return 2;
    }

    @Override
    public View.OnClickListener getButtonOnClickListener(int index) {
        return mOnClickListeners[index];
    }


    @Override
    public CharSequence getButtonText(int index) {
        switch (index) {
            case 0:
                return dialog.getContext().getText(cn.com.u2be.alekwifilibrary.R.string.connect);
            case 1:
                return getCancelString();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getTitle() {
        return dialog.getContext().getString(cn.com.u2be.alekwifilibrary.R.string.wifi_connect_to, mScanResult.SSID);
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
