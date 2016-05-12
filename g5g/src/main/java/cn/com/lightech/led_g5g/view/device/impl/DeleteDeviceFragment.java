package cn.com.lightech.led_g5g.view.device.impl;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.adapter.ExpDeleteDeviceAdapter;
import cn.com.lightech.led_g5g.entity.Device;
import cn.com.lightech.led_g5g.entity.DeviceGroup;
import cn.com.lightech.led_g5g.presenter.DeviceDeleteLedPresenter;
import cn.com.lightech.led_g5g.view.AppBaseFragment;
import cn.com.lightech.led_g5g.view.device.IDeleteDeviceView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link DeleteDeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeleteDeviceFragment extends AppBaseFragment implements IDeleteDeviceView {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "deviceGroup";

    @Bind(R.id.elv_can_delete_devices)
    ExpandableListView elvCanDeleteDevices;
    @Bind(R.id.tv_btn_ok)
    Button tvBtnOk;
    @Bind(R.id.tv_btn_cancel)
    Button tvBtnCancel;

    private DeviceDeleteLedPresenter deleteDevicePresenter;
    private List<DeviceGroup> deviceGroups;
    private ExpDeleteDeviceAdapter adapter;

    public DeleteDeviceFragment() {
        // Required empty public constructor
    }


    public static DeleteDeviceFragment newInstance(List<DeviceGroup> deviceGroup) {
        DeleteDeviceFragment fragment = new DeleteDeviceFragment();
        Bundle args = new Bundle();
        ArrayList<DeviceGroup> groups = new ArrayList<>();
        groups.addAll(deviceGroup);
        args.putSerializable(ARG_PARAM1, groups);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initVariables(Bundle savedInstanceState) {
        this.deviceGroups = (List<DeviceGroup>) getArguments().getSerializable(ARG_PARAM1);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_delete_device, container, false);
        ButterKnife.bind(this, view);
        deleteDevicePresenter = new DeviceDeleteLedPresenter(getActivity(), this);
        this.adapter = new ExpDeleteDeviceAdapter(getActivity(), deviceGroups) {
            @Override
            protected void onChecked(Map<Integer, Boolean> checkedMap) {
                if (checkedMap.size() > 0) {
                    tvBtnOk.setEnabled(true);
                } else {
                    tvBtnOk.setEnabled(false);
                }
            }
        };
        elvCanDeleteDevices.setAdapter(adapter);
        elvCanDeleteDevices.expandGroup(0);
//        elvCanDeleteDevices.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                Integer key = groupPosition * 1000 + childPosition;
//                Boolean aBoolean = adapter.getCheckedMap().get(key);
//                if (aBoolean != null && aBoolean) {
//                    adapter.getCheckedMap().put(key, false);
//                }
//                adapter.getCheckedMap().put(key, true);
//                adapter.notifyDataSetChanged();
//                return false;
//            }
//        });

        elvCanDeleteDevices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Integer groupPosition = (Integer) view.getTag(R.id.expend_listview_group_tag);
                Integer childPosition = (Integer) view.getTag(R.id.expend_listview_child_tag);
                if (childPosition != -1) {
                    Device device = (Device) adapter.getChild(groupPosition, childPosition);
                    deleteDevicePresenter.blinkLed(device);
                } else {
                    DeviceGroup group = (DeviceGroup) adapter.getGroup(groupPosition);
                    deleteDevicePresenter.blinkLed(group);
                }
                return true;
            }
        });
        return view;
    }

    @Override
    protected void loadData() {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.tv_btn_ok, R.id.tv_btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_btn_ok:
                List<Device> devices = new ArrayList<>(0);
                Map<Integer, Boolean> checkedMap = adapter.getCheckedMap();
                for (Integer key : checkedMap.keySet()) {
                    int groupPosition = key / 1000;
                    int childPosition = key % 1000;
                    devices.add(this.deviceGroups.get(groupPosition).getDevices().get(childPosition));
                }
                this.deleteDevicePresenter.deleteDevice(devices);
                break;
            case R.id.tv_btn_cancel:
                closeView();
                break;
        }
    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void closeView() {
        getActivity().onBackPressed();
    }

    @Override
    public void remove(String ip) {
        for (int j = 0; j < deviceGroups.size(); j++) {
            DeviceGroup group = deviceGroups.get(j);
            for (int i = 0; i < group.getDevices().size(); i++) {
                Device device = group.getDevices().get(i);
                if (ip.equals(device.getIp())) {
                    group.getDevices().remove(i);
                    if (deviceGroups.size() > 1 && group.getDevices().size() == 0)
                        deviceGroups.remove(group);
                    break;
                }
            }
        }
        adapter.notifyDataSetChanged();
        tvBtnOk.setEnabled(false);
    }
}
