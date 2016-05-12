package cn.com.lightech.led_g5g.view.console.impl;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.entity.data.FlashData;
import cn.com.lightech.led_g5g.entity.TimeBucket;
import cn.com.lightech.led_g5g.gloabal.DataManager;
import cn.com.lightech.led_g5g.gloabal.LedProxy;

/**
 * This fragment shows data and sync preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FlashPreferenceFragment extends BasePreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_flash);
        setHasOptionsMenu(false);

        bindStatePreference(findPreference(getString(R.string.pref_key_flash)));
//        bindPreferenceBooleanValue(findPreference(getString(R.string.pref_key_flash_time1)));
//        bindPreferenceBooleanValue(findPreference(getString(R.string.pref_key_flash_time2)));
//        bindPreferenceBooleanValue(findPreference(getString(R.string.pref_key_flash_time3)));
//        bindPreferenceIntValue(findPreference(getString(R.string.pref_key_flash_time1_end)));
//        bindPreferenceIntValue(findPreference(getString(R.string.pref_key_flash_time2_end)));
//        bindPreferenceIntValue(findPreference(getString(R.string.pref_key_flash_time3_end)));
//        bindPreferenceIntValue(findPreference(getString(R.string.pref_key_flash_time1_start)));
//        bindPreferenceIntValue(findPreference(getString(R.string.pref_key_flash_time2_start)));
//        bindPreferenceIntValue(findPreference(getString(R.string.pref_key_flash_time3_start)));

    }

    private boolean enableFlash() {
        SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return dsp.getBoolean(getString(R.string.pref_key_flash), false);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceIntAndBooleanValueChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            FlashData data = getData();

            if (preference.getKey().equals(getString(R.string.pref_key_flash_time1))) {
                Boolean value1 = (Boolean) value;
                if (!value1) {
                    data.setTime1(new TimeBucket(0, 0));
                    data.setTime2(new TimeBucket(0, 0));
                    data.setTime3(new TimeBucket(0, 0));
                }
            } else if (preference.getKey().equals(getString(R.string.pref_key_flash_time2))) {
                Boolean value1 = (Boolean) value;
                if (!value1) {
                    data.setTime2(new TimeBucket(0, 0));
                    data.setTime3(new TimeBucket(0, 0));
                }
            } else if (preference.getKey().equals(getString(R.string.pref_key_flash_time3))) {
                Boolean value1 = (Boolean) value;
                if (!value1) {
                    data.setTime3(new TimeBucket(0, 0));
                }
            } else if (preference.getKey().equals(getString(R.string.pref_key_flash_time1_end))) {
                Integer value1 = (Integer) value;
                data.getTime1().setEnd(value1);
            } else if (preference.getKey().equals(getString(R.string.pref_key_flash_time2_end))) {
                Integer value1 = (Integer) value;
                data.getTime2().setEnd(value1);
            } else if (preference.getKey().equals(getString(R.string.pref_key_flash_time3_end))) {
                Integer value1 = (Integer) value;
                data.getTime3().setEnd(value1);
            } else if (preference.getKey().equals(getString(R.string.pref_key_flash_time1_start))) {
                Integer value1 = (Integer) value;
                data.getTime1().setStart(value1);
            } else if (preference.getKey().equals(getString(R.string.pref_key_flash_time2_start))) {
                Integer value1 = (Integer) value;
                data.getTime2().setStart(value1);
            } else if (preference.getKey().equals(getString(R.string.pref_key_flash_time3_start))) {
                Integer value1 = (Integer) value;
                data.getTime3().setStart(value1);
            }


            LedProxy.sendToLed(data);


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
     * @see #sBindPreferenceIntAndBooleanValueChangeListener
     */
    private void bindPreferenceIntValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceIntAndBooleanValueChangeListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceIntAndBooleanValueChangeListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getInt(preference.getKey(), 0));
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceIntAndBooleanValueChangeListener
     */
    private void bindPreferenceBooleanValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceIntAndBooleanValueChangeListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceIntAndBooleanValueChangeListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));
    }


    public FlashData getData() {
        SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        boolean flash = dsp.getBoolean(getString(R.string.pref_key_flash), false);

        FlashData flashData = new FlashData();
        boolean flashTime1 = dsp.getBoolean(getString(R.string.pref_key_flash_time1), false);
        int flashTime1Start = 0xff;
        int flashTime1End = 0xff;
        if (flashTime1) {
            flashTime1Start = dsp.getInt(getString(R.string.pref_key_flash_time1_start), 0);
            flashTime1End = dsp.getInt(getString(R.string.pref_key_flash_time1_end), 0);
        }
        boolean flashTime2 = dsp.getBoolean(getString(R.string.pref_key_flash_time2), false);
        int flashTime2Start = 0xff;
        int flashTime2End = 0xff;
        if (flashTime2) {
            flashTime2Start = dsp.getInt(getString(R.string.pref_key_flash_time2_start), 0);
            flashTime2End = dsp.getInt(getString(R.string.pref_key_flash_time2_end), 0);
        }
        boolean flashTime3 = dsp.getBoolean(getString(R.string.pref_key_flash_time3), false);
        int flashTime3Start = 0xff;
        int flashTime3End = 0xff;
        if (flashTime3) {
            flashTime3Start = dsp.getInt(getString(R.string.pref_key_flash_time3_start), 0);
            flashTime3End = dsp.getInt(getString(R.string.pref_key_flash_time3_end), 0);
        }
        flashData.setTime1(new TimeBucket(flashTime1Start, flashTime1End));
        flashData.setTime2(new TimeBucket(flashTime2Start, flashTime2End));
        flashData.setTime3(new TimeBucket(flashTime3Start, flashTime3End));
        return flashData;
    }


    @Override
    public void onPause() {
        super.onPause();
        FlashData data = getData();
        LedProxy.sendToLed(data);
        DataManager.getInstance().saveFlashDataNode(data, true);
    }

}