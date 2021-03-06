package cn.com.lightech.led_g5g.view.console.impl;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;

import cn.com.lightech.led_g5g.R;

/**
 * This fragment shows general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AcclimationPreferenceFragment extends BasePreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_acclimation);
        bindStatePreference(findPreference(getString(R.string.pref_key_acclimation)));
    }


}
