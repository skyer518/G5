package cn.com.lightech.led_g5w.view.device.impl;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.adapter.DeleteGroupAdapter;
import cn.com.lightech.led_g5w.entity.Device;
import cn.com.lightech.led_g5w.entity.DeviceGroup;
import cn.com.lightech.led_g5w.presenter.DeviceDeleteGroupPresenter;
import cn.com.lightech.led_g5w.view.AppBaseFragment;
import cn.com.lightech.led_g5w.view.device.IDeleteGroupView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link DeleteGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeleteGroupFragment extends AppBaseFragment implements IDeleteGroupView {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "deviceGroup";


    @Bind(R.id.tv_btn_ok)
    Button tvBtnOk;
    @Bind(R.id.tv_btn_cancel)
    Button tvBtnCancel;
    @Bind(R.id.elv_can_delete_group)
    ListView elvCanDeleteGroup;

    private DeviceDeleteGroupPresenter deleteGroupPresenter;
    private List<DeviceGroup> deviceGroups;
    private DeleteGroupAdapter adapter;

    public DeleteGroupFragment() {
        // Required empty public constructor
    }


    public static DeleteGroupFragment newInstance(List<DeviceGroup> deviceGroup) {
        DeleteGroupFragment fragment = new DeleteGroupFragment();
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
        View view = inflater.inflate(R.layout.fragment_delete_group, container, false);
        ButterKnife.bind(this, view);
        deleteGroupPresenter = new DeviceDeleteGroupPresenter(getActivity(), this);
        this.adapter = new DeleteGroupAdapter(getActivity(), deviceGroups) {
            @Override
            protected void onChecked(Map<Integer, Boolean> checkedMap) {
                if (checkedMap.size() > 0) {
                    tvBtnOk.setEnabled(true);
                } else {
                    tvBtnOk.setEnabled(false);
                }
            }
        };
        elvCanDeleteGroup.setAdapter(adapter);
        //elvCanDeleteGroup.setAdapter(adapter);
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
        return view;
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        deleteGroupPresenter.regist();
    }

    @Override
    public void onStop() {
        super.onStop();
        deleteGroupPresenter.unRegist();
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
                    devices.addAll(this.deviceGroups.get(key).getDevices());
                }
                this.deleteGroupPresenter.deleteGroup(devices);
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


}
