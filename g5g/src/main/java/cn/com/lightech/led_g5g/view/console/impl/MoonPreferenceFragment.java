package cn.com.lightech.led_g5g.view.console.impl;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.entity.data.MoonData;
import cn.com.lightech.led_g5g.entity.TimeBucket;
import cn.com.lightech.led_g5g.gloabal.DataManager;
import cn.com.lightech.led_g5g.gloabal.LedProxy;

/**
 * This fragment shows notification preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MoonPreferenceFragment extends BasePreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_moon);
        setHasOptionsMenu(false);

        bindStatePreference(findPreference(getString(R.string.pref_key_moon)));
        bindPreferenceStringValue(findPreference(getString(R.string.pref_key_moon_last_fullmoon_day)));
//        bindPreferenceIntValue(findPreference(getString(R.string.pref_key_moon_time_start)));
//        bindPreferenceIntValue(findPreference(getString(R.string.pref_key_moon_time_end)));
    }


    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceIntValueChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            if (preference instanceof ListPreference) {
                String stringValue = value.toString();
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            }
//
//            MoonData data = getData();
//            if (preference.getKey().equals(getString(R.string.pref_key_moon_last_fullmoon_day))) {
//                data.setLastFullMoonDay(Integer.parseInt((String) value));
//            } else if (preference.getKey().equals(getString(R.string.pref_key_moon_time_start))) {
//                data.getTime().setStart((Integer) value);
//            } else if (preference.getKey().equals(getString(R.string.pref_key_moon_time_end))) {
//                data.getTime().setEnd((Integer) value);
//            }
//            LedProxy.sendToLed(data);
            return true;
        }
    };


    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceIntValueChangeListener
     */
    private void bindPreferenceIntValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceIntValueChangeListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceIntValueChangeListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getInt(preference.getKey(), 0));
    }

    private void bindPreferenceStringValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceIntValueChangeListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceIntValueChangeListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public void onPause() {
        super.onPause();
        MoonData data = getData();
        LedProxy.sendToLed(data);
        DataManager.getInstance().saveMoonDataNode(data, true);
    }

    public MoonData getData() {
        SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        MoonData moonData = new MoonData();
        int moonLastFullMoonDay = Integer.parseInt(dsp.getString(getString(R.string.pref_key_moon_last_fullmoon_day), "1"));
        int moonStart = dsp.getInt(getString(R.string.pref_key_moon_time_start), 0);
        int moonEnd = dsp.getInt(getString(R.string.pref_key_moon_time_end), 0);
        moonData.setLastFullMoonDay(moonLastFullMoonDay);
        moonData.setTime(new TimeBucket(moonStart, moonEnd));
        return moonData;
    }
}