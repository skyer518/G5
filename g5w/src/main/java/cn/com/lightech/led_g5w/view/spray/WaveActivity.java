package cn.com.lightech.led_g5w.view.spray;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.gloabal.DataManager;
import cn.com.lightech.led_g5w.gloabal.IDataListener;
import cn.com.lightech.led_g5w.net.ConnectManager;
import cn.com.lightech.led_g5w.net.ConnectionsManager;
import cn.com.lightech.led_g5w.net.entity.ConnState;
import cn.com.lightech.led_g5w.net.entity.ReplyErrorCode;
import cn.com.lightech.led_g5w.net.entity.Response;
import cn.com.lightech.led_g5w.view.AppBaseActivity;
import cn.com.lightech.led_g5w.view.spray.entity.WaveNode;
import cn.com.lightech.led_g5w.wedgit.MyTimePicker;

public class WaveActivity extends AppBaseActivity implements OnClickListener,
        IDataListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {


    @Bind(R.id.tv_motorSpeend)
    TextView tvMotorSpeend;
    @Bind(R.id.tv_M1)
    TextView tvM1;
    @Bind(R.id.tv_M2)
    TextView tvM2;
    @Bind(R.id.tv_M3)
    TextView tvM3;
    @Bind(R.id.tv_M4)
    TextView tvM4;
    @Bind(R.id.tv_lastMoonDay)
    TextView tvLastMoonDay;

    private ActionBar actionBar;


    @Bind(R.id.sp_function)
    Spinner sp_function;
    @Bind(R.id.sp_effect)
    Spinner sp_effect;

    @Bind(R.id.hsb_Channel)
    SeekBar seekbar_channel;
    @Bind(R.id.hsb_motorSpeend)
    SeekBar seekbar_speed;
    @Bind(R.id.hsb_lastMoonDay)
    SeekBar seekbar_moonDay;

    @Bind(R.id.hsb_M1)
    SeekBar seekbar_M1;
    @Bind(R.id.hsb_M2)
    SeekBar seekbar_M2;
    @Bind(R.id.hsb_M3)
    SeekBar seekbar_M3;
    @Bind(R.id.hsb_M4)
    SeekBar seekbar_M4;

    @Bind(R.id.sw_dayOrNight)
    Switch sw_dayOrNight;
    @Bind(R.id.sw_feed)
    Switch sw_feed;
    @Bind(R.id.sw_autoWave)
    Switch sw_autoWave;
    @Bind(R.id.cust_timepicker_pause)
    MyTimePicker tv_pause;

    private WaveNode waveNode;

    private ArrayAdapter<String> functionAdapter;

    private String[] functionItem;

    private String[] effectItem;

    private ArrayAdapter<String> effectAdapter;


    final OnItemSelectedListener effectListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (position != 3) {
                seekbar_moonDay.setEnabled(false);
            } else {
                seekbar_moonDay.setEnabled(true);
            }
            onValueChanged();

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub

        }

    };

    final OnItemSelectedListener functionListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            if (position == 3) {
                seekbar_M1.setEnabled(true);
                seekbar_M2.setEnabled(true);
                seekbar_M3.setEnabled(true);
                seekbar_M4.setEnabled(true);

            } else {
                seekbar_M1.setEnabled(false);
                seekbar_M2.setEnabled(false);
                seekbar_M3.setEnabled(false);
                seekbar_M4.setEnabled(false);
            }
            if (position < 2) {
                sw_autoWave.setEnabled(true);
            } else {
                sw_autoWave.setEnabled(false);
            }
            onValueChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        waveNode = DataManager.getInstance().getWaveNode();
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_wave);
        ButterKnife.bind(this);
        if (actionBar == null) {
            initActionBar();
        }

        sp_effect.setOnItemSelectedListener(effectListener);

        sp_function.setOnItemSelectedListener(functionListener);

        //seekbar_channel.setOnSeekBarChangeListener(this);
        seekbar_M1.setOnSeekBarChangeListener(this);
        seekbar_M1.setTag(tvM1);
        seekbar_M2.setOnSeekBarChangeListener(this);
        seekbar_M2.setTag(tvM2);
        seekbar_M3.setOnSeekBarChangeListener(this);
        seekbar_M3.setTag(tvM3);
        seekbar_M4.setOnSeekBarChangeListener(this);
        seekbar_M4.setTag(tvM4);
        seekbar_moonDay.setOnSeekBarChangeListener(this);
        seekbar_moonDay.setTag(tvLastMoonDay);
        seekbar_speed.setOnSeekBarChangeListener(this);
        seekbar_speed.setTag(tvMotorSpeend);

        sw_autoWave.setOnCheckedChangeListener(this);
        sw_dayOrNight.setOnCheckedChangeListener(this);
        sw_feed.setOnCheckedChangeListener(this);

        tv_pause.setOnValueChangeListener(new MyTimePicker.OnValueChangeListener() {
            @Override
            public void onValueChange(int left, int right) {
                onValueChanged();
            }
        });

    }


    @Override
    protected void loadData() {

        initData();
    }


    void onValueChanged() {
        sendDataToLed();
    }

    private void initData() {
        if (waveNode != null) {

            seekbar_channel.setProgress(waveNode.getChannel());
            seekbar_speed.setProgress(waveNode.getPower() / 10);
            sw_autoWave.setChecked(waveNode.isAutoWave());
            sw_dayOrNight.setChecked(waveNode.isDayOrNight());
            sw_feed.setChecked(waveNode.isFeed());

            //tp_pause.setValue(waveNode.getPulseS(), waveNode.getPulseMs());
            seekbar_M1.setProgress(waveNode.getM1());
            seekbar_M2.setProgress(waveNode.getM2());
            seekbar_M3.setProgress(waveNode.getM3());
            seekbar_M4.setProgress(waveNode.getM4());

            seekbar_moonDay.setProgress(waveNode.getDaysAgo());
            if (waveNode.getDaysAgo() < 32) {
                functionItem = getResources().getStringArray(
                        R.array.array_wave_function_all);
                effectItem = getResources().getStringArray(
                        R.array.array_wave_effect_all);
            } else {
                functionItem = getResources().getStringArray(
                        R.array.array_wave_function);
                effectItem = getResources().getStringArray(
                        R.array.array_wave_effect);
            }
            functionAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, functionItem);
            effectAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, effectItem);
            sp_function.setAdapter(functionAdapter);
            sp_effect.setAdapter(effectAdapter);

            sp_function.setSelection(waveNode.getFunction() - 1);
            sp_effect.setSelection(waveNode.getEffect() - 1);
        }
    }

    private void initActionBar() {
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }


    @Override
    protected void onStart() {
        super.onStart();
        ConnectionsManager.getInstance().register(this, false);
    }


    @Override
    protected void onStop() {
        super.onStop();
        ConnectionsManager.getInstance().unRegister(this);

    }


    @Override
    public boolean onReceive(Response response, ConnectManager connectManager) {


        if (response == null)
            return true;
        switch (response.getCmdType()) {
//            case RecvDataFromLED:
//                WaveNode waveNode = response.getWaveNode();
//                if (response.getReplyErrorCode() == ReplyErrorCode.OK) {
//                    this.waveNode = waveNode;
//                    LoadLedData();
//                }

            case SendDataToLED:
                if (response.getReplyErrorCode() == ReplyErrorCode.OK) {
                }
                break;
            default:
                break;
        }

        return true;
    }


    private void sendDataToLed() {

        WaveNode node = new WaveNode();

        node.setFunction((byte) (sp_function.getSelectedItemPosition() + 1));
        node.setEffect((byte) (sp_effect.getSelectedItemPosition() + 1));
        node.setPower((byte) ((byte) seekbar_speed.getProgress() * 10));
        node.setChannel(seekbar_channel.getProgress());
        node.setAutoWave(sw_autoWave.isChecked());
        node.setDayOrNight(sw_dayOrNight.isChecked());
        node.setDaysAgo((byte) seekbar_moonDay.getProgress());
        node.setFeed(sw_feed.isChecked());

        //实际时间
        Date date = new Date(System.currentTimeMillis());
        node.setTime(new Timing(date.getHours(), date.getMinutes()));
        // Pulse
        node.setPulseS((byte) tv_pause.getleftValue());
        node.setPulseMs((byte) tv_pause.getRightValue());
        int time_1 = seekbar_M1.getProgress();
        node.setM1((byte) time_1);

        int time_2 = seekbar_M2.getProgress();
        node.setM2((byte) time_2);

        int time_3 = seekbar_M3.getProgress();
        node.setM3((byte) time_3);

        int time_4 = seekbar_M4.getProgress();
        node.setM4((byte) time_4);

        DataManager.getInstance().saveWaveNode(node, true);
        // node.setID2((byte) node.getChannel());
        SparyProxy.sendWaveToDevice(node);

    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onConnectStateChanged(ConnState connState, ConnectManager connectManager) {

    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // TODO
        final TextView tvTag = (TextView) seekBar.getTag();
        tvTag.setText(progress + "");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        onValueChanged();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        seekbar_M1.setOnSeekBarChangeListener(this);
    }


}
