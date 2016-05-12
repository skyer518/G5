package cn.com.lightech.led_g5g.view.device.impl;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5g.R;
import cn.com.lightech.led_g5g.adapter.DeviceAdapter;
import cn.com.lightech.led_g5g.entity.Device;
import cn.com.lightech.led_g5g.entity.DeviceGroup;
import cn.com.lightech.led_g5g.presenter.DeviceAddGroupPresenter;
import cn.com.lightech.led_g5g.view.AppBaseActivity;
import cn.com.lightech.led_g5g.view.device.IGroupView;

public class EditGroupActivity extends AppBaseActivity implements IGroupView {

    public static final String ARGS_DEFAULT_DEVICE_GROUP = "params1";
    public static final String ARGS_NEW_GROUP_NUMBER = "params2";

    @Bind(R.id.lv_defaultGroup_device)
    ListView lvDefaultGroupDevice;
    @Bind(R.id.lv_newGroup_device)
    ListView lvNewGroupDevice;

    private DeviceGroup defaultDeviceGroup;
    private DeviceGroup tempDeviceGroup;
    private int newGroupNumber;
    private DeviceAddGroupPresenter groupPresenter;
    private MenuItem menuSave;

    public EditGroupActivity() {
    }


    @Override
    protected void initVariables(Bundle savedInstanceState) {

        defaultDeviceGroup = (DeviceGroup) getIntent().getExtras().getSerializable(ARGS_DEFAULT_DEVICE_GROUP);
        newGroupNumber = getIntent().getExtras().getInt(ARGS_NEW_GROUP_NUMBER, 0);
        tempDeviceGroup = new DeviceGroup(newGroupNumber);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.fragment_edit_group);
        ButterKnife.bind(this);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.device_menu_addGroup);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        groupPresenter = new DeviceAddGroupPresenter(this, this);

        final DeviceAdapter defaultDeviceAdapter = new DeviceAdapter(this, defaultDeviceGroup.getDevices());
        lvDefaultGroupDevice.setAdapter(defaultDeviceAdapter);
        final DeviceAdapter tempDeviceAdapter = new DeviceAdapter(this, tempDeviceGroup.getDevices());
        lvNewGroupDevice.setAdapter(tempDeviceAdapter);


        lvDefaultGroupDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = defaultDeviceGroup.getDevices().remove(position);
                tempDeviceGroup.getDevices().add(device);
                tempDeviceAdapter.notifyDataSetChanged();
                defaultDeviceAdapter.notifyDataSetChanged();
                toggleSaveButton();

            }
        });
        lvNewGroupDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = tempDeviceGroup.getDevices().remove(position);
                defaultDeviceGroup.getDevices().add(device);
                tempDeviceAdapter.notifyDataSetChanged();
                defaultDeviceAdapter.notifyDataSetChanged();
                toggleSaveButton();

            }
        });


        lvDefaultGroupDevice.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = defaultDeviceGroup.getDevices().get(position);
                groupPresenter.blinkLed(device);
                return true;
            }
        });
        lvNewGroupDevice.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = tempDeviceGroup.getDevices().get(position);
                groupPresenter.blinkLed(device);
                return true;
            }
        });
    }


    @Override
    protected void loadData() {
        toggleSaveButton();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar_edit_group, menu);
        this.menuSave = menu.findItem(R.id.action_btn_save);
        toggleSaveButton();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_btn_save:
                groupPresenter.saveGroup(tempDeviceGroup);
                finish();
                return true;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void toggleSaveButton() {
        if (menuSave != null) {
            if (defaultDeviceGroup.getDevices().size() < 1 || tempDeviceGroup.getDevices().size() < 1) {
                menuSave.setEnabled(false);
            } else {
                menuSave.setEnabled(true);
            }
        }
    }
}
