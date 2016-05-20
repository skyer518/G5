package cn.com.lightech.led_g5g.view.device.impl;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.adapter.WifiAdapter;
import cn.com.lightech.led_g5g.presenter.ScanWifiPresenter;
import cn.com.lightech.led_g5g.utils.ProgressUtil;
import cn.com.lightech.led_g5g.view.AppBaseActivity;
import cn.com.lightech.led_g5g.view.device.IWifiView;
import cn.com.lightech.led_g5g.view.device.impl.dialog.WifiDialog;

public class WifiActivity extends AppBaseActivity implements IWifiView, WifiDialog.OnButtonClickListener {


    @Bind(R.id.lv_new_devices)
    ListView lvWifi;
    @Bind(R.id.tv_noItemsInfo)
    TextView tvNoItemsInfo;

    private ScanWifiPresenter wifiPresenter;

    private ScanResult ledWifi;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private WifiManager mWifiManager;
    private ScanResult mScanResult;
    private MenuItem scanMenu;

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        Intent intent = getIntent();
        ledWifi = intent.getParcelableExtra(getString(R.string.intent_key_ledwifi));
        this.wifiPresenter = new ScanWifiPresenter(this, this, ledWifi);


    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_new_device);
        ButterKnife.bind(this);
        ActionBar supportActionBar = getActionBar();
        supportActionBar.setTitle(getString(R.string.device_wifi_title));
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(false);
        lvWifi.setAdapter(new WifiAdapter(this, new ArrayList<ScanResult>(0)));
        lvWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mScanResult = (ScanResult) parent.getAdapter().getItem(position);

                openWifiConnectorDialog(mScanResult);

            }


        });


        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

    }

    private void openWifiConnectorDialog(ScanResult mScanResult) {
        WifiDialog dialog = new WifiDialog(this, mWifiManager, mScanResult, this);
        dialog.show();

    }


    @Override
    protected void loadData() {
        WifiAdapter adapter = (WifiAdapter) lvWifi.getAdapter();
        adapter.setScanResult(new ArrayList<ScanResult>(0));
        adapter.notifyDataSetChanged();
        scanWifi();
//        ProgressUtil.showPogress(this, getString(R.string.device_wifi_scanning));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar_wifi, menu);
        scanMenu = menu.findItem(R.id.action_btn_newdevice_scanning);
        scanLoading(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_btn_newdevice_scanning:
                scanWifi();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void scanWifi() {
        scanLoading(true);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanLoading(false);
            }
        }, 5000);
        mWifiManager.startScan();
    }


    @Override
    public void showWifiScanResult(List<ScanResult> scanResults) {
        if (scanResults == null || scanResults.size() > 0) {
            tvNoItemsInfo.setVisibility(View.GONE);
        } else {
            tvNoItemsInfo.setVisibility(View.VISIBLE);
        }
        WifiAdapter adapter = (WifiAdapter) lvWifi.getAdapter();
        adapter.setScanResult(scanResults);
        adapter.notifyDataSetChanged();

        // ProgressUtil.closeDialog();
    }


    @Override
    public void successSetting() {
        onBackPressed();
    }


    @Override
    public void mConnectOnClick(WifiDialog dialog, ScanResult mScanResult, String password, boolean isUpdate) {
        dialog.dismiss();
        ProgressUtil.showPogress(this, getString(R.string.device_wifi_connecting), false);
        wifiPresenter.connectWifi(mScanResult, password, isUpdate);
    }


    @Override
    public void onResume() {
        super.onResume();
        final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mReceiver, filter);
        scanWifi();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                showWifiScanResult(wifiPresenter.getWifiScanResult());
            }

        }
    };

    public void scanLoading(boolean loading) {
        if (scanMenu != null) {
            if (loading) {
                scanMenu.setActionView(R.layout.actionbar_indeterminate_progress);
                scanMenu.setEnabled(false);
            } else {
                scanMenu.setActionView(null);
                scanMenu.setEnabled(true);
            }
        }
    }
}
