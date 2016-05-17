package cn.com.lightech.led_g5g.view.console.impl;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.entity.data.FlashData;
import cn.com.lightech.led_g5g.entity.LampState;
import cn.com.lightech.led_g5g.entity.data.MoonData;
import cn.com.lightech.led_g5g.gloabal.DataManager;
import cn.com.lightech.led_g5g.view.AppBaseActivity;
import cn.com.lightech.led_g5g.view.AppBaseFragment;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppBaseActivity {
    @Bind(R.id.fl_leftContainer)
    FrameLayout flLeftContainer;
    @Bind(R.id.fl_rightContainer)
    FrameLayout flRightContainer;


    @Override
    protected void initVariables(Bundle savedInstanceState) {

        SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = dsp.edit();
//        MoonData moonData = DataManager.getInstance().getMoonData();
//        edit.putString(getString(R.string.pref_key_moon_last_fullmoon_day), moonData.getLastFullMoonDay() + "")
//                .putInt(getString(R.string.pref_key_moon_time_start), moonData.getTime().getStart())
//                .putInt(getString(R.string.pref_key_moon_time_end), moonData.getTime().getEnd())
//                .commit();

        FlashData flashData = DataManager.getInstance().getFlashData();
        edit.putBoolean(getString(R.string.pref_key_flash_time1), flashData.getTime1().getStart() > 0 ? true : false)
                .putInt(getString(R.string.pref_key_flash_time1_start), flashData.getTime1().getStart())
                .putInt(getString(R.string.pref_key_flash_time1_end), flashData.getTime1().getEnd())
                .putBoolean(getString(R.string.pref_key_flash_time2), flashData.getTime2().getStart() > 0 ? true : false)
                .putInt(getString(R.string.pref_key_flash_time2_start), flashData.getTime2().getStart())
                .putInt(getString(R.string.pref_key_flash_time2_end), flashData.getTime2().getEnd())
                .putBoolean(getString(R.string.pref_key_flash_time3), flashData.getTime3().getStart() > 0 ? true : false)
                .putInt(getString(R.string.pref_key_flash_time3_start), flashData.getTime3().getStart())
                .putInt(getString(R.string.pref_key_flash_time3_end), flashData.getTime3().getEnd())
                .commit();

        LampState state = DataManager.getInstance().getState();
        edit.putBoolean(getString(R.string.pref_key_flash), state.lighting)
//                .putBoolean(getString(R.string.pref_key_moon), state.moon)
                .putBoolean(getString(R.string.pref_key_acclimation), state.acclimation)
                .commit();


    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_effect);
        setupActionBar();

        getFragmentManager().beginTransaction().replace(R.id.fl_leftContainer, new HeadFragment()).commit();

    }

    @Override
    protected void loadData() {
        SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(this);
        int mode = dsp.getInt(getString(R.string.pref_key_mode), 0);
        boolean flash = dsp.getBoolean(getString(R.string.pref_key_flash), false);
//        boolean moon = dsp.getBoolean(getString(R.string.pref_key_moon), false);
        boolean acclimation = dsp.getBoolean(getString(R.string.pref_key_acclimation), false);

//        int moonStart = dsp.getInt(getString(R.string.pref_key_moon_time_start), 0);
//        int moonEnd = dsp.getInt(getString(R.string.pref_key_moon_time_end), 0);

    }


    /**
     * Set up the {@link ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || AcclimationPreferenceFragment.class.getName().equals(fragmentName)
//                || MoonPreferenceFragment.class.getName().equals(fragmentName)
                || FlashPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class HeadFragment extends AppBaseFragment implements AdapterView.OnItemClickListener {


        @Bind(R.id.lv_effect)
        ListView lvEffect;


        String[] items;// = new String[]{"Moon", "Lightning", "Acclimation"};
        int selectedPositon = 0;
        ArrayAdapter<String> adapter;


        @Override
        protected void initVariables(Bundle savedInstanceState) {
            items = getActivity().getResources().getStringArray(R.array.effect);
        }


        ;

        @Override
        protected View initView(LayoutInflater inflater, ViewGroup container) {
            View rootView = inflater.inflate(R.layout.fragment_effect, null);
            ButterKnife.bind(this, rootView);
            lvEffect.setSelected(true);
            adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_effect, R.id.tv_name, items) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (position == selectedPositon) {
                        view.setBackgroundColor(getActivity().getResources().getColor(R.color.color_selected));
                    } else {
                        view.setBackgroundColor(getActivity().getResources().getColor(android.R.color.transparent));
                    }
                    return view;
                }
            };
            lvEffect.setAdapter(adapter);
            lvEffect.setOnItemClickListener(this);

            onItemClick(null, null, 0, 0);

            return rootView;
        }

        @Override
        protected void loadData() {

        }

        @Override
        public void onPause() {
            super.onPause();
//            SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//            LampState state = DataManager.getInstance().getState();
//            int dataType = state.dataType;
//            boolean flash = dsp.getBoolean(getString(R.string.pref_key_flash), false);
//            boolean moon = dsp.getBoolean(getString(R.string.pref_key_moon), false);
//            boolean acclimation = dsp.getBoolean(getString(R.string.pref_key_acclimation), false);
//            LedProxy.setState(dataType, flash, moon, acclimation);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            ButterKnife.unbind(this);
        }


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedPositon = position;
            adapter.notifyDataSetChanged();
            switch (position) {
                case 0:
                    getFragmentManager().beginTransaction().replace(R.id.fl_rightContainer, new FlashPreferenceFragment()).commit();
                    break;
                case 1:
                    getFragmentManager().beginTransaction().replace(R.id.fl_rightContainer, new AcclimationPreferenceFragment()).commit();
                    break;
//                case 2:
//                    getFragmentManager().beginTransaction().replace(R.id.fl_rightContainer, new MoonPreferenceFragment()).commit();
//                    break;
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
