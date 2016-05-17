package cn.com.lightech.led_g5w.view.device.impl;

import android.app.ActionBar;
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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.adapter.ControllableDeviceAdapter;
import cn.com.lightech.led_g5w.entity.Device;
import cn.com.lightech.led_g5w.presenter.SprayListPresenter;
import cn.com.lightech.led_g5w.utils.UIHelper;
import cn.com.lightech.led_g5w.view.AppBaseStateFragment;
import cn.com.lightech.led_g5w.view.device.IDeviceView;


public class DeviceSprayFragment extends AppBaseStateFragment implements IDeviceView {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PRESENTER = "param3";

    @Bind(R.id.device_list)
    ListView elvDevices;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ControllableDeviceAdapter deviceAdapter;
    private SprayListPresenter presenter;
    private MenuItem scanMenu;

    public DeviceSprayFragment() {
        // Required empty public constructor
        System.out.print("DeviceLEDFragment ");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeviceLEDFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceSprayFragment newInstance(String param1, String param2) {
        DeviceSprayFragment fragment = new DeviceSprayFragment();
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
        presenter = new SprayListPresenter(getActivity(), this);
        Log.i("DeviceLEDFragment", "initVariables");
        System.out.print("DeviceLEDFragment savedInstanceState");
    }


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_device, container, false);
        ButterKnife.bind(this, rootView);

        ActionBar actionBar = getActivity().getActionBar();

        setHasOptionsMenu(true);
        this.deviceAdapter = new ControllableDeviceAdapter(getActivity()) {

            @Override
            public void OnControlButtonClick(String deviceIp) {
                presenter.gotoControl(deviceIp);
            }
        };
        elvDevices.setAdapter(deviceAdapter);
        elvDevices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = (Device) deviceAdapter.getItem(position);
                presenter.blinkLed(device);
                return true;
            }
        });

        elvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object tag_time = view.getTag(R.id.time_click);
                long time = System.currentTimeMillis();
                if (tag_time != null) {
                    time = (Long) tag_time;
                }
                view.setTag(R.id.time_click, System.currentTimeMillis());
                int count = 0;
                if (System.currentTimeMillis() - time < 1000) {
                    Object tag_count = view.getTag(R.id.count_click);
                    if (tag_count != null) {
                        count = (Integer) tag_count;
                    }
                    count++;
                    if (count == 7) {
                        UIHelper.showUpdataLedDialog(getActivity(), (Device) deviceAdapter.getItem(position));
                        count = 0;
                    }

                }
                view.setTag(R.id.count_click, count);

            }


        });


        Log.i("DeviceLEDFragment", "initView");
        System.out.print("DeviceLEDFragment initView");
        return rootView;
    }


    @Override
    protected void loadData() {
        //presenter.scanDevice();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.print("DeviceLEDFragment onAttach");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("DeviceLEDFragment", "onResume");
        System.out.print("DeviceLEDFragment onResume");
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
        Log.i("DeviceLEDFragment", "showDevices");
        ArrayList<Device> devices = presenter.getDevices();
        if (devices == null) {
            return;
        }
        deviceAdapter.setData(devices);
        deviceAdapter.notifyDataSetChanged();
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
        presenter.setDevices((ArrayList<Device>) savedInstanceState.getSerializable("Devices"));
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        outState.putSerializable("Devices", presenter.getDevices());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_actionbar_device_spray, menu);
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
    public void gotoDeleteGroupFragment() {

    }

    @Override
    public void gotoDeleteDeviceFragment() {

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

    public List<Device> getDevics() {
        return presenter.getDevices();
    }
}
