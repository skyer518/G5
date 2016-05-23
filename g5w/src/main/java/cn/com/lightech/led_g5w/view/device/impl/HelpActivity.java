package cn.com.lightech.led_g5w.view.device.impl;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.view.AppBaseActivity;

/**
 * Created by 明 on 2016/3/4.
 */
public class HelpActivity extends AppBaseActivity {


    @Bind(R.id.wv_webview)
    WebView webView;


    public HelpActivity() {
        super();
        System.out.print("MainDeviceActivity ");
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

        System.out.print("MainDeviceActivity initVariables");
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
        ActionBar supportActionBar = getActionBar();
        supportActionBar.setTitle(getString(R.string.device_menu_help));
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        String able = getResources().getConfiguration().locale.getCountry();
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        if (able.equals("CN"))
            webView.loadUrl("file:///android_asset/help/android_cn.htm");
        else if (able.equals("TW")) {
            webView.loadUrl("file:///android_asset/help/android_cn_tw.htm");
        } else {
            webView.loadUrl("file:///android_asset/help/android_en.htm");

        }
    }

    @Override
    protected void loadData() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
