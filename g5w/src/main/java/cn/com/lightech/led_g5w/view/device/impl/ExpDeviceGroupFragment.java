package cn.com.lightech.led_g5w.view.device.impl;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.adapter.ExpDeviceAdapter;
import cn.com.lightech.led_g5w.entity.Device;
import cn.com.lightech.led_g5w.entity.DeviceGroup;
import cn.com.lightech.led_g5w.presenter.LedListPresenter;
import cn.com.lightech.led_g5w.utils.UIHelper;
import cn.com.lightech.led_g5w.view.AppBaseStateFragment;
import cn.com.lightech.led_g5w.view.device.IDeviceView;


public class ExpDeviceGroupFragment extends AppBaseStateFragment implements IDeviceView {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PRESENTER = "param3";

    @Bind(R.id.explv_device_list)
    ExpandableListView elvDevices;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ExpDeviceAdapter deviceAdapter;
    private LedListPresenter presenter;
    private MenuItem scanMenu;

    public ExpDeviceGroupFragment() {
        // Required empty public constructor
        System.out.print("ExpDeviceGroupFragment ");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpDeviceGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpDeviceGroupFragment newInstance(String param1, String param2) {
        ExpDeviceGroupFragment fragment = new ExpDeviceGroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        presenter = new LedListPresenter(getActivity(), this);
        Log.i("ExpDeviceGroupFragment", "initVariables");
        System.out.print("ExpDeviceGroupFragment savedInstanceState");
    }


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_device_group_explv, container, false);
        ButterKnife.bind(this, rootView);

        ActionBar actionBar = getActivity().getActionBar();

        setHasOptionsMenu(true);
        this.deviceAdapter = new ExpDeviceAdapter(getActivity()) {

            @Override
            public void OnControlButtonClick(int groupNo) {
                presenter.gotoControl(groupNo);
            }
        };
        elvDevices.setAdapter(deviceAdapter);
        elvDevices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Integer groupPosition = (Integer) view.getTag(R.id.expend_listview_group_tag);
                Integer childPosition = (Integer) view.getTag(R.id.expend_listview_child_tag);
                if (childPosition != -1) {
                    Device device = (Device) deviceAdapter.getChild(groupPosition, childPosition);
                    presenter.blinkLed(device);
                } else {
                    DeviceGroup group = (DeviceGroup) deviceAdapter.getGroup(groupPosition);
                    presenter.blinkLed(group);
                }
                return true;
            }
        });

        elvDevices.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Object tag_time = v.getTag(R.id.time_click);
                long time = System.currentTimeMillis();
                if (tag_time != null) {
                    time = (Long) tag_time;
                }
                v.setTag(R.id.time_click, System.currentTimeMillis());
                int count = 0;
                if (System.currentTimeMillis() - time < 1000) {
                    Object tag_count = v.getTag(R.id.count_click);
                    if (tag_count != null) {
                        count = (Integer) tag_count;
                    }
                    count++;
                    if (count == 7) {
                        UIHelper.showUpdataLedDialog(getActivity(),(Device) deviceAdapter.getChild(groupPosition, childPosition));
                        count = 0;
                    }

                }
                v.setTag(R.id.count_click, count);


                return false;
            }
        });


        Log.i("ExpDeviceGroupFragment", "initView");
        System.out.print("ExpDeviceGroupFragment initView");
        return rootView;
    }



    @Override
    protected void loadData() {
        Log.i("ExpDeviceGroupFragment", "loadData");
        System.out.print("ExpDeviceGroupFragment loadData");
        //presenter.scanDevice();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.print("ExpDeviceGroupFragment onAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("ExpDeviceGroupFragment", "onResume");
        System.out.print("ExpDeviceGroupFragment onResume");
        presenter.start();
    }


    @Override
    public void onStop() {
        super.onStop();
        presenter.stop();
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void showDevices() {
        Log.i("ExpDeviceGroupFragment", "showDevices");
        ArrayList<DeviceGroup> deviceGroups = presenter.getDeviceGroups();
        if (deviceGroups == null) {
            return;
        }
        deviceAdapter.setData(deviceGroups);
        deviceAdapter.notifyDataSetChanged();
        if (elvDevices != null && deviceGroups.size() > 0)
            elvDevices.expandGroup(0);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onFirstTimeLaunched() {
        super.onFirstTimeLaunched();
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        presenter.setDeviceGroups((ArrayList<DeviceGroup>) savedInstanceState.getSerializable("DeviceGroups"));
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        outState.putSerializable("DeviceGroups", presenter.getDeviceGroups());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_actionbar_device_device, menu);
        scanMenu = menu.findItem(R.id.action_btn_device_scanning);
        scanLoading(true);
        super.onCreateOptionsMenu(menu, inflater);

    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_btn_device_scanning:
                presenter.scanDevice();
                scanLoading(true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public List<DeviceGroup> getDeviceGroups() {
        return presenter.getDeviceGroups();
    }
}
