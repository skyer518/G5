package cn.com.lightech.led_g5w.view.console.impl;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.entity.LampState;
import cn.com.lightech.led_g5w.entity.MoonDataNode;
import cn.com.lightech.led_g5w.gloabal.DataManager;
import cn.com.lightech.led_g5w.gloabal.LedProxy;

/**
 * Created by 明 on 2016/4/17.
 */
public abstract class BasePreferenceFragment extends PreferenceFragment {


    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener enableValueChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            LampState state = DataManager.getInstance().getState();
            int mode = state.getMode();
            boolean flash = dsp.getBoolean(getString(R.string.pref_key_flash), false);
            boolean moon = dsp.getBoolean(getString(R.string.pref_key_moon), false);
            boolean acclimation = dsp.getBoolean(getString(R.string.pref_key_acclimation), false);
            Boolean boolValue = (Boolean) value;
            if (preference.getKey().equals(getString(R.string.pref_key_moon))) {
                moon = boolValue;
            } else if (preference.getKey().equals(getString(R.string.pref_key_flash))) {
                flash = boolValue;
            } else if (preference.getKey().equals(getString(R.string.pref_key_acclimation))) {
                acclimation = boolValue;
            }


            LedProxy.setState(mode, flash, moon, acclimation);
            state.setLighting(flash);
            state.setMoon(moon);
            state.setAcclimation(acclimation);

            return true;
        }
    };

    protected void bindStatePreference(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(enableValueChangeListener);

        // Trigger the listener immediately with the preference's
        // current value.
        enableValueChangeListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));
    }


}
