package cn.com.lightech.led_g5w.view.device.impl.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.gloabal.Constants;
import cn.com.lightech.led_g5w.utils.PreferenceUtils;
import cn.com.u2be.alekwifilibrary.ConfiguredNetworkWifiDialogContent;
import cn.com.u2be.alekwifilibrary.CurrentNetworkWifiDialogContent;
import cn.com.u2be.alekwifilibrary.NewNetworkWifiDialogContent;
import cn.com.u2be.alekwifilibrary.Wifi;

/**
 * Created by 明 on 2016/4/20.
 */
public class WifiDialog extends Dialog {

    private OnButtonClickListener onButtonClickListener;
    private final ScanResult mScanResult;
    private final WifiManager mWifiManager;

    public WifiDialog(Context context, WifiManager wifiManager, ScanResult scanResult, OnButtonClickListener onButtonClickListener) {
        this(context, wifiManager, scanResult, onButtonClickListener, android.R.style.Theme_Holo_Light_Dialog);
    }

    public WifiDialog(Context context, WifiManager wifiManager, ScanResult scanResult, OnButtonClickListener onButtonClickListener, int themeResId) {
        super(context, themeResId);
        this.mScanResult = scanResult;
        this.mWifiManager = wifiManager;
        this.onButtonClickListener = onButtonClickListener;
    }


    private static final int[] BUTTONS = {cn.com.u2be.alekwifilibrary.R.id.button1, cn.com.u2be.alekwifilibrary.R.id.button2, cn.com.u2be.alekwifilibrary.R.id.button3, cn.com.u2be.alekwifilibrary.R.id.button4};


    private View mView;
    private ViewGroup mContentViewContainer;
    private WifiContent mWifiContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // It will not work if we setTheme here.
        // Please add android:theme="@android:style/Theme.Dialog" to any descendant class in AndroidManifest.xml!
        // See http://code.google.com/p/android/issues/detail?id=4394
        // setTheme(android.R.style.Theme_Dialog);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        mView = View.inflate(getContext(), cn.com.u2be.alekwifilibrary.R.layout.floating, null);
        final DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mView.setMinimumWidth(Math.min(dm.widthPixels, dm.heightPixels) - 20);
        setContentView(mView);

        mContentViewContainer = (ViewGroup) mView.findViewById(cn.com.u2be.alekwifilibrary.R.id.wifiDialogContent);

        createWifiContent();
    }

    private void setDialogContentView(final View contentView) {
        mContentViewContainer.removeAllViews();
        mContentViewContainer.addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setContent(WifiContent wifiContent) {
        mWifiContent = wifiContent;
        refreshContent();
    }

    public void refreshContent() {
        setDialogContentView(mWifiContent.getView());
        ((TextView) findViewById(cn.com.u2be.alekwifilibrary.R.id.title)).setText(mWifiContent.getTitle());

        final int btnCount = mWifiContent.getButtonCount();
        if (btnCount > BUTTONS.length) {
            throw new RuntimeException(String.format("%d exceeds maximum button count: %d!", btnCount, BUTTONS.length));
        }
        findViewById(cn.com.u2be.alekwifilibrary.R.id.buttons_view).setVisibility(btnCount > 0 ? View.VISIBLE : View.GONE);
        for (int buttonId : BUTTONS) {
            final Button btn = (Button) findViewById(buttonId);
            btn.setOnClickListener(null);
            btn.setVisibility(View.GONE);
        }

        for (int btnIndex = 0; btnIndex < btnCount; btnIndex++) {
            final Button btn = (Button) findViewById(BUTTONS[btnIndex]);
            btn.setText(mWifiContent.getButtonText(btnIndex));
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(mWifiContent.getButtonOnClickListener(btnIndex));
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (mWifiContent != null) {
            mWifiContent.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (mWifiContent != null) {
            return mWifiContent.onContextItemSelected(item);
        }
        return false;
    }

    public WindowManager getWindowManager() {
        return (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    }


    private boolean isAdHoc(final ScanResult scanResule) {
        return scanResule.capabilities.indexOf("IBSS") != -1;
    }

    private void createWifiContent() {

        if (mScanResult == null) {
            Toast.makeText(getContext(), cn.com.u2be.alekwifilibrary.R.string.scanresutl_is_null, Toast.LENGTH_LONG).show();
            dismiss();
            return;
        }

        if (isAdHoc(mScanResult)) {
            Toast.makeText(getContext(), cn.com.u2be.alekwifilibrary.R.string.adhoc_not_supported_yet, Toast.LENGTH_LONG).show();
            dismiss();
            return;
        }


        String password = PreferenceUtils.getWifiPassword(mScanResult.SSID);
        final String security = Wifi.ConfigSec.getScanResultSecurity(mScanResult);
        final WifiConfiguration config = Wifi.getWifiConfiguration(mWifiManager, mScanResult, security);


        if (config == null || password == null) {
            if (config != null) {
                mWifiManager.removeNetwork(config.networkId);
            }
            mWifiContent = new NewNetworkView(this, mWifiManager, mScanResult);
        } else {
            final boolean isCurrentNetwork_ConfigurationStatus = config.status == WifiConfiguration.Status.CURRENT;
            final WifiInfo info = mWifiManager.getConnectionInfo();
            final boolean isCurrentNetwork_WifiInfo = info != null
                    && android.text.TextUtils.equals(info.getSSID(), mScanResult.SSID)
                    && android.text.TextUtils.equals(info.getBSSID(), mScanResult.BSSID);
            if (isCurrentNetwork_ConfigurationStatus || isCurrentNetwork_WifiInfo) {
                mWifiContent = new CurrentNetworkView(this, mWifiManager, mScanResult, password);
            } else {
                mWifiContent = new ConfiguredNetworkView(this, mWifiManager, mScanResult, password);
            }


        }
        setContent(mWifiContent);
    }

    public OnButtonClickListener getListener() {
        return onButtonClickListener;
    }


    public interface WifiContent {
        CharSequence getTitle();

        View getView();

        int getButtonCount();

        CharSequence getButtonText(int index);

        View.OnClickListener getButtonOnClickListener(int index);

        void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo);

        boolean onContextItemSelected(MenuItem item);
    }


    public interface OnButtonClickListener {
        void mConnectOnClick(WifiDialog dialog, ScanResult mScanResult, String password, boolean isUpdate);
    }


}
