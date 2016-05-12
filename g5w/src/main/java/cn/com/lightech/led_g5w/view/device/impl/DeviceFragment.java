package cn.com.lightech.led_g5w.view.device.impl;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.adapter.DeviceAdapter;
import cn.com.lightech.led_g5w.entity.DeviceGroup;
import cn.com.lightech.led_g5w.view.AppBaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link DeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceFragment extends AppBaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "deviceGroup";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.lv_defaultGroup_device)
    ListView lvDefaultGroupDevice;
    @Bind(R.id.lv_newGroup_device)
    ListView lvNewGroupDevice;


    private DeviceGroup defaultGroup;
    private int groupNumber;

    public DeviceFragment() {
        // Required empty public constructor
    }


    public static DeviceFragment newInstance(DeviceGroup deviceGroup, int GroupNumber) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, deviceGroup);
        args.putInt(ARG_PARAM2, GroupNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initVariables(Bundle savedInstanceState) {
        this.defaultGroup = (DeviceGroup) getArguments().get(ARG_PARAM1);
        this.groupNumber = getArguments().getInt(ARG_PARAM2);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_edit_group, container, false);
        ButterKnife.bind(this, view);
        lvDefaultGroupDevice.setAdapter(new DeviceAdapter(getActivity(), defaultGroup.getDevices()));
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


}
