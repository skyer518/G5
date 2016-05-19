package cn.com.lightech.led_g5w.view.spray;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.gloabal.IDataListener;
import cn.com.lightech.led_g5w.net.ConnectManager;
import cn.com.lightech.led_g5w.net.ConnectionsManager;
import cn.com.lightech.led_g5w.net.entity.ConnState;
import cn.com.lightech.led_g5w.net.entity.ReplyErrorCode;
import cn.com.lightech.led_g5w.net.entity.Response;
import cn.com.lightech.led_g5w.view.AppBaseActivity;
import cn.com.lightech.led_g5w.view.spray.entity.WaveNode;

public class WaveActivity extends AppBaseActivity implements OnClickListener,
        IDataListener {

    private ActionBar actionBar;

    private ImageButton ib_back;

    private ImageButton ib_refresh;

    private ImageButton ib_send;

    @Bind(R.id.sp_function)
    Spinner sp_function;
    @Bind(R.id.sp_effect)
    Spinner sp_effect;

    @Bind(R.id.hsb_Channel)
    SeekBar hsb_channel;
    @Bind(R.id.hsb_motorSpeend)
    SeekBar hsb_speed;
    @Bind(R.id.hsb_lastMoonDay)
    SeekBar hsb_moonDay;

    @Bind(R.id.hsb_M1)
    SeekBar hsb_M1;
    @Bind(R.id.hsb_M2)
    SeekBar hsb_M2;
    @Bind(R.id.hsb_M3)
    SeekBar hsb_M3;
    @Bind(R.id.hsb_M4)
    SeekBar hsb_M4;

    @Bind(R.id.sw_dayOrNight)
    Switch sw_dayOrNight;
    @Bind(R.id.sw_feed)
    Switch sw_feed;
    @Bind(R.id.sw_autoWave)
    Switch sw_autoWave;
    @Bind(R.id.tv_time_pause)
    TextView tv_pause;

    private WaveNode waveNode;

    private ArrayAdapter<String> functionAdapter;

    private String[] functionItem;

    private String[] effectItem;

    private ArrayAdapter<String> effectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave);
        ButterKnife.bind(this);
        if (actionBar == null) {
            initActionBar();
        }
        // hsb_speed.setStep(10);
        sp_effect.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position != 3) {
                    hsb_moonDay.setEnabled(false);
                } else {
                    hsb_moonDay.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }

        });
        sp_function.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == 3) {
                    hsb_M1.setEnabled(true);
                    hsb_M2.setEnabled(true);
                    hsb_M3.setEnabled(true);
                    hsb_M4.setEnabled(true);

                } else {
                    hsb_M1.setEnabled(false);
                    hsb_M2.setEnabled(false);
                    hsb_M3.setEnabled(false);
                    hsb_M4.setEnabled(false);
                }
                if (position < 2) {
                    sw_autoWave.setEnabled(true);
                } else {
                    sw_autoWave.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        tv_pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void loadData() {

    }

    private void init() {
        if (waveNode != null) {

            hsb_channel.setProgress(waveNode.getChannel());
            hsb_speed.setProgress(waveNode.getPower() / 10);
            sw_autoWave.setChecked(waveNode.isAutoWave());
            sw_dayOrNight.setChecked(waveNode.isDayOrNight());
            sw_feed.setChecked(waveNode.isFeed());

            //tp_pause.setValue(waveNode.getPulseS(), waveNode.getPulseMs());
            hsb_M1.setProgress(waveNode.getM1());
            hsb_M2.setProgress(waveNode.getM2());
            hsb_M3.setProgress(waveNode.getM3());
            hsb_M4.setProgress(waveNode.getM4());

            hsb_moonDay.setProgress(waveNode.getDaysAgo());
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
//        View custBarView = LayoutInflater.from(this).inflate(
//                R.layout.actionbar_wave, null);
//        ib_back = (ImageButton) custBarView.findViewById(R.id.bar_ib_wave_back);
//        ib_refresh = (ImageButton) custBarView
//                .findViewById(R.id.bar_ib_wave_refresh);
//        ib_send = (ImageButton) custBarView.findViewById(R.id.bar_ib_wave_send);
//
//        ib_back.setOnClickListener(this);
//        ib_refresh.setOnClickListener(this);
//        ib_send.setOnClickListener(this);
//        actionBar.setCustomView(custBarView);
    }


    @Override
    protected void onStart() {
        super.onStart();
        ConnectionsManager.getInstance().register(this, false);
    }

//    private void queryWave() {
//        waveNode = DataManager.getInstance().getWaveNode();
//        byte[] schedulePackageId = PackageId.getSchedulePackageId(Mode.WAVE);
//        schedulePackageId[1] = (byte) hsb_channel.getValue();
//        LedProxy.QueryData(schedulePackageId);
//    }

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
            case RecvDataFromLED:
                WaveNode waveNode = response.getWaveNode();
                if (response.getReplyCode() == ReplyErrorCode.OK) {
                    this.waveNode = waveNode;
                    LoadLedData();
                }

            case SendDataToLED:
                if (response.getReplyCode() == ReplyErrorCode.OK) {
                }
                break;
            default:
                break;
        }

        return true;
    }

    private void LoadLedData() {
        init();
    }

//    private void sendDataToLed() {
//
//        WaveNode node = new WaveNode();
//
//        node.setFunction((byte) (sp_function.getSelectedItemPosition() + 1));
//        node.setEffect((byte) (sp_effect.getSelectedItemPosition() + 1));
//        node.setPower((byte) ((byte) hsb_speed.getValue() * 10));
//        node.setChannel(hsb_channel.getValue());
//        node.setAutoWave(sw_autoWave.isChecked());
//        node.setDayOrNight(sw_dayOrNight.isChecked());
//        node.setDaysAgo((byte) hsb_moonDay.getValue());
//        node.setFeed(sw_feed.isChecked());
//
//        // ʵ��ʱ��
//        Date date = new Date(System.currentTimeMillis());
//        node.setTime(new Timing(date.getHours(), date.getMinutes()));
//        // Pulse
//        node.setPulseS((byte) tp_pause.getleftValue());
//        node.setPulseMs((byte) tp_pause.getRightValue());
//        int time_1 = hsb_M1.getValue();
//        node.setM1((byte) time_1);
//
//        int time_2 = hsb_M2.getValue();
//        node.setM2((byte) time_2);
//
//        int time_3 = hsb_M3.getValue();
//        node.setM3((byte) time_3);
//
//        int time_4 = hsb_M4.getValue();
//        node.setM4((byte) time_4);
//
//        DataManager.getInstance().saveWaveNode(node, true);
//        node.setID2((byte) node.getChannel());
//        LedProxy.SendToLed(node);
//        // node.set
//
//    }

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
}
