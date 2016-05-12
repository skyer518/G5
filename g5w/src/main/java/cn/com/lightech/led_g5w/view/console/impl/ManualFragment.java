package cn.com.lightech.led_g5w.view.console.impl;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.net.entity.ChanelType;
import cn.com.lightech.led_g5w.entity.LampChannel;
import cn.com.lightech.led_g5w.presenter.ControlManualPresenter;
import cn.com.lightech.led_g5w.view.AppBaseFragment;
import cn.com.lightech.led_g5w.view.console.IManualView;

/**
 * A simple {@link AppBaseFragment} subclass.
 */
public class ManualFragment extends AppBaseFragment implements SeekBar.OnSeekBarChangeListener, IManualView {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.seekBar_blue)
    SeekBar seekBarBlue;
    @Bind(R.id.tv_value_sb_blue)
    TextView tvValueSbBlue;
    @Bind(R.id.seekBar_white)
    SeekBar seekBarWhite;
    @Bind(R.id.tv_value_sb_white)
    TextView tvValueSbWhite;
    @Bind(R.id.seekBar_purple)
    SeekBar seekBarPurple;
    @Bind(R.id.tv_value_sb_purple)
    TextView tvValueSbPurple;
    @Bind(R.id.seekBar_red)
    SeekBar seekBarRed;
    @Bind(R.id.tv_value_sb_red)
    TextView tvValueSbRed;
    @Bind(R.id.seekBar_green)
    SeekBar seekBarGreen;
    @Bind(R.id.tv_value_sb_green)
    TextView tvValueSbGreen;
    private ControlManualPresenter manualPresenter;
    boolean isReady = false;

    public ManualFragment() {
        // Required empty public constructor
    }


    public static ManualFragment getInstance(String param1, String param2) {
        ManualFragment fragment = new ManualFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_manual, container, false);
        ButterKnife.bind(this, view);
        this.manualPresenter = new ControlManualPresenter(getActivity(), this);
        seekBarBlue.setOnSeekBarChangeListener(this);
        seekBarWhite.setOnSeekBarChangeListener(this);
        seekBarPurple.setOnSeekBarChangeListener(this);
        seekBarRed.setOnSeekBarChangeListener(this);
        seekBarGreen.setOnSeekBarChangeListener(this);
        return view;
    }

    @Override
    protected void loadData() {
        manualPresenter.loadManual();

    }

    @Override
    public void onResume() {
        super.onResume();
        manualPresenter.registerDataListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        manualPresenter.stopPreview();
        manualPresenter.unRegisterDataListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
            onProgressChage(seekBar, progress, false);

    }

    private void sendData(ChanelType type, int progress, boolean isLast) {
        if (!isLast) {
            if (progress % 10 == 0)
                this.manualPresenter.preview(type, progress);
        } else {
            this.manualPresenter.saveChannel(type, progress);
            this.manualPresenter.preview(type, progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        onProgressChage(seekBar, progress, true);
    }

    public void onProgressChage(SeekBar seekBar, int progress, boolean isLast) {
        switch (seekBar.getId()) {
            case R.id.seekBar_blue:
                this.tvValueSbBlue.setText("" + progress);
                sendData(ChanelType.Bule, progress, isLast);
                break;
            case R.id.seekBar_white:
                this.tvValueSbWhite.setText("" + progress);
                sendData(ChanelType.White, progress, isLast);
                break;
            case R.id.seekBar_purple:
                this.tvValueSbPurple.setText("" + progress);
                sendData(ChanelType.PurPle, progress, isLast);
                break;
            case R.id.seekBar_red:
                this.tvValueSbRed.setText("" + progress);
                sendData(ChanelType.Red, progress, isLast);
                break;
            case R.id.seekBar_green:
                this.tvValueSbGreen.setText("" + progress);
                sendData(ChanelType.Green, progress, isLast);
                break;
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void showManual(LampChannel channel) {
        seekBarBlue.setProgress(channel.getBlue());
        tvValueSbBlue.setText("" + channel.getBlue());
        seekBarWhite.setProgress(channel.getWhite());
        tvValueSbWhite.setText("" + channel.getWhite());
        seekBarPurple.setProgress(channel.getPurple());
        tvValueSbPurple.setText("" + channel.getPurple());
        seekBarRed.setProgress(channel.getRed());
        tvValueSbRed.setText("" + channel.getRed());
        seekBarGreen.setProgress(channel.getGreen());
        tvValueSbGreen.setText("" + channel.getGreen());
    }
}
