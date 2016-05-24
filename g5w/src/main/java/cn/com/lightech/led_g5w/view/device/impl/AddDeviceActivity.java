package cn.com.lightech.led_g5w.view.device.impl;

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
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.adapter.WifiAdapter;
import cn.com.lightech.led_g5w.entity.DeviceType;
import cn.com.lightech.led_g5w.presenter.ScanDevicePresenter;
import cn.com.lightech.led_g5w.view.AppBaseActivity;
import cn.com.lightech.led_g5w.view.device.IAddDeviceView;

public class AddDeviceActivity extends AppBaseActivity implements IAddDeviceView {


    @Bind(R.id.lv_new_devices)
    ListView lvNewDevices;
    @Bind(R.id.tv_noItemsInfo)
    TextView tvNoItemsInfo;

    private ScanDevicePresenter addDevicePresenter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private WifiManager mWifiManager;
    private MenuItem scanMenu;
    private DeviceType type;


    @Override
    protected void initVariables(Bundle savedInstanceState) {
        this.addDevicePresenter = new ScanDevicePresenter(this, this);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_new_device);
        ButterKnife.bind(this);
        ActionBar supportActionBar = getActionBar();
        supportActionBar.setTitle(getString(R.string.device_newdevice_title));
        supportActionBar.setDisplayShowHomeEnabled(false);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        lvNewDevices.setAdapter(new WifiAdapter(this, new ArrayList<ScanResult>(0)));
        lvNewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScanResult scaResult = (ScanResult) parent.getAdapter().getItem(position);
                addDevicePresenter.connectDevice(scaResult);
            }
        });

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

    }


    @Override
    protected void loadData() {
        WifiAdapter adapter = (WifiAdapter) lvNewDevices.getAdapter();
        adapter.setScanResult(new ArrayList<ScanResult>(0));
        adapter.notifyDataSetChanged();
        scanWifi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar_newdevice, menu);
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


    @Override
    public void showWifiScanResult(List<ScanResult> scanResults) {
        if (scanResults == null || scanResults.size() > 0) {
            tvNoItemsInfo.setVisibility(View.GONE);
        } else {
            tvNoItemsInfo.setVisibility(View.VISIBLE);
        }
        WifiAdapter adapter = (WifiAdapter) lvNewDevices.getAdapter();
        adapter.setScanResult(scanResults);
        adapter.notifyDataSetChanged();
        //ProgressUtil.closeDialog();
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
                showWifiScanResult(addDevicePresenter.getWifiScanResult());
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

}
