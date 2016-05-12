/*
 * Wifi Connecter
 * 
 * Copyright (c) 2011 Kevin Yuan (farproc@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 **/

package cn.com.u2be.alekwifilibrary;


import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

/**
 * A dialog-like floating activity
 *
 * @author Kevin Yuan
 */
public class Floating extends Activity {

    private static final int[] BUTTONS = {R.id.button1, R.id.button2, R.id.button3};


    private View mView;
    private ViewGroup mContentViewContainer;
    private WifiDialogContent mWifiDialogContent;
    private String password = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // It will not work if we setTheme here.
        // Please add android:theme="@android:style/Theme.Dialog" to any descendant class in AndroidManifest.xml!
        // See http://code.google.com/p/android/issues/detail?id=4394
        // setTheme(android.R.style.Theme_Dialog);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        mView = View.inflate(this, R.layout.floating, null);
        final DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mView.setMinimumWidth(Math.min(dm.widthPixels, dm.heightPixels) - 20);
        setContentView(mView);

        mContentViewContainer = (ViewGroup) mView.findViewById(R.id.wifiDialogContent);
    }

    private void setDialogContentView(final View contentView) {
        mContentViewContainer.removeAllViews();
        mContentViewContainer.addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public void setContent(WifiDialogContent wifiDialogContent) {
        mWifiDialogContent = wifiDialogContent;
        refreshContent();
    }

    public void refreshContent() {
        setDialogContentView(mWifiDialogContent.getView());
        ((TextView) findViewById(R.id.title)).setText(mWifiDialogContent.getTitle());

        final int btnCount = mWifiDialogContent.getButtonCount();
        if (btnCount > BUTTONS.length) {
            throw new RuntimeException(String.format("%d exceeds maximum button count: %d!", btnCount, BUTTONS.length));
        }
        findViewById(R.id.buttons_view).setVisibility(btnCount > 0 ? View.VISIBLE : View.GONE);
        for (int buttonId : BUTTONS) {
            final Button btn = (Button) findViewById(buttonId);
            btn.setOnClickListener(null);
            btn.setVisibility(View.GONE);
        }

        for (int btnIndex = 0; btnIndex < btnCount; btnIndex++) {
            final Button btn = (Button) findViewById(BUTTONS[btnIndex]);
            btn.setText(mWifiDialogContent.getButtonText(btnIndex));
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(mWifiDialogContent.getButtonOnClickListener(btnIndex));
        }
    }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (mWifiDialogContent != null) {
            mWifiDialogContent.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (mWifiDialogContent != null) {
            return mWifiDialogContent.onContextItemSelected(item);
        }
        return false;
    }


    public interface WifiDialogContent {
        CharSequence getTitle();

        View getView();

        int getButtonCount();

        CharSequence getButtonText(int index);

        OnClickListener getButtonOnClickListener(int index);

        void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo);

        boolean onContextItemSelected(MenuItem item);
    }
}
