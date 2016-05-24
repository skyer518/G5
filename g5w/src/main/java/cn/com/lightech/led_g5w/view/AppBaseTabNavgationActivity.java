package cn.com.lightech.led_g5w.view;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import cn.com.lightech.led_g5w.wedgit.CustViewPager;

public abstract class AppBaseTabNavgationActivity extends AppBaseActivity implements ActionBar.TabListener {

    public static final String ARGS_DEVICE_GROUP = "PARAM_DEVICE_GROUP";

    private FragmentPagerAdapter mSectionsPagerAdapter;

    CustViewPager mViewPager;


    public AppBaseTabNavgationActivity() {


    }


    @Override
    protected void loadData() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
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
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    public void setmSectionsPagerAdapter(FragmentPagerAdapter mSectionsPagerAdapter) {
        this.mSectionsPagerAdapter = mSectionsPagerAdapter;
    }

    public FragmentPagerAdapter getmSectionsPagerAdapter() {
        return mSectionsPagerAdapter;
    }

    public void setmViewPager(CustViewPager mViewPager) {
        this.mViewPager = mViewPager;
    }
}