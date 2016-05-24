package cn.com.lightech.led_g5w.view.console.impl;


import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.entity.DeviceGroup;
import cn.com.lightech.led_g5w.entity.LampState;
import cn.com.lightech.led_g5w.gloabal.DataManager;
import cn.com.lightech.led_g5w.net.ConnectionsManager;
import cn.com.lightech.led_g5w.presenter.ControlMainPresenter;
import cn.com.lightech.led_g5w.view.AppBaseActivity;
import cn.com.lightech.led_g5w.view.console.IControlView;
import cn.com.lightech.led_g5w.wedgit.CustViewPager;

public class ControlActivity extends AppBaseActivity implements ActionBar.TabListener, IControlView {

    public static final String ARGS_DEVICE_GROUP = "PARAM_DEVICE_GROUP";

    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Bind(R.id.container)
    CustViewPager mViewPager;

    private String[] workModel;

    private ControlMainPresenter controlPresenter;
    DeviceGroup deviceGroup;

    boolean isReady = false;

    public ControlActivity() {


    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        deviceGroup = (DeviceGroup) getIntent().getSerializableExtra(ARGS_DEVICE_GROUP);
        workModel = getResources().getStringArray(R.array.work_model);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_control);
        ButterKnife.bind(this);
        controlPresenter = new ControlMainPresenter(this, this);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);


        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });


        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }

    @Override
    protected void loadData() {

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu_effect) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        int position = tab.getPosition();
        mViewPager.setCurrentItem(position);
        if (isReady) {
            if (DataManager.getInstance().getState() != null) {
                int workMode = position == 0 ? position : 1;
                controlPresenter.setLedState(workMode);
                saveTempState((byte) workMode);
            }
        }
    }

    public void saveTempState(byte workMode) {
        LampState state = DataManager.getInstance().getState();
        if (state != null) {
            state.mode = workMode;
            DataManager.getInstance().setState(state);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void switchMode(int workMode) {
        isReady = true;
        getActionBar().setSelectedNavigationItem(workMode);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return AutoFragment.newInstance("", "");
                case 1:
                    return ManualFragment.getInstance("", "");
            }
            return AutoFragment.newInstance("", "");
        }

        @Override
        public int getCount() {
            return workModel.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return workModel[position];
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        controlPresenter.registerDataListener();
        controlPresenter.initData();
    }

    @Override
    public void onPause() {
        super.onPause();
        controlPresenter.unRegisterDataListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectionsManager.getInstance().clearPriorityConnections();
    }
}
