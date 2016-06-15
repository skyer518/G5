package cn.com.lightech.led_g5w.view.console.impl;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.entity.CurvePoint;
import cn.com.lightech.led_g5w.entity.LampChannel;
import cn.com.lightech.led_g5w.gloabal.DataManager;
import cn.com.lightech.led_g5w.gloabal.LocalPhoneParms;
import cn.com.lightech.led_g5w.net.entity.ChanelType;
import cn.com.lightech.led_g5w.presenter.ControlAutoPresenter;
import cn.com.lightech.led_g5w.view.AppBaseFragment;
import cn.com.lightech.led_g5w.view.console.IAutoView;
import cn.com.lightech.led_g5w.wedgit.MyMarkerView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link AutoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AutoFragment extends AppBaseFragment implements OnChartValueSelectedListener, IAutoView {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.btn_add)
    ImageView btnAdd;
    @Bind(R.id.btn_del)
    ImageView btnDel;
    @Bind(R.id.btn_previous)
    ImageView btnPrevious;
    @Bind(R.id.btn_next)
    ImageView btnNext;
    @Bind(R.id.btn_defaul)
    TextView btnDefaul;
    @Bind(R.id.btn_edit)
    TextView btnEdit;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.btn_preview)
    ImageView btnPreview;
    @Bind(R.id.btn_stopPreview)
    ImageView btnStopPreview;
    @Bind(R.id.lc_chart)
    LineChart lcChart;

    private MyMarkerView mv;
    private int[] colors = {0XFFABCBED, 0XFF00D700};
    private boolean previewing = false;

    private float yAxisMax = Float.MIN_VALUE;
    private float yAxisMin = Float.MAX_VALUE;
    private float yAxisGap = 0.1f;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    static {
        intXLabel();
    }

    private List<LineDataSet> dataSets = new ArrayList<>();
    private int mCursor = 0;
    private ControlAutoPresenter autoPresenter;
    private CurvePoint currentCurvePoint;
    private CurvePoint tempCurvePoint;
    private Timer previewTimer = new Timer();

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            canPreview(true);
        }
    };
    private Dialog dialog;
    private ViewHolder holder;


    public AutoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AutoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AutoFragment newInstance(String param1, String param2) {
        AutoFragment fragment = new AutoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_auto, container, false);
        ButterKnife.bind(this, rootView);
        this.autoPresenter = new ControlAutoPresenter(getActivity(), this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        autoPresenter.registerDataListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        autoPresenter.unRegisterDataListener();
    }


    @Override
    protected void loadData() {
        addLineChartView();
        autoPresenter.loadCursor(0);
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

    @OnClick({R.id.btn_add, R.id.btn_del, R.id.btn_previous, R.id.btn_next, R.id.btn_defaul, R.id.btn_preview, R.id.btn_edit, R.id.btn_stopPreview})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                editChanelDialog(false);
                break;
            case R.id.btn_del:
                this.autoPresenter.deletePoint();
                break;
            case R.id.btn_previous:
                this.autoPresenter.loadCursor(-1);
                break;
            case R.id.btn_next:
                this.autoPresenter.loadCursor(1);
                break;
            case R.id.btn_defaul:
                this.autoPresenter.backDefault();
                break;
            case R.id.btn_preview:
                this.autoPresenter.previewAuto();
                break;
            case R.id.btn_stopPreview:
                this.autoPresenter.stopPreview();
                break;
            case R.id.btn_edit:
                editChanelDialog(true);
                break;
        }
    }


    private void editChanelDialog(boolean edit) {
        this.currentCurvePoint = new CurvePoint(tempCurvePoint.getTime(), tempCurvePoint.getChannel());
        if (dialog == null) {
            dialog = new Dialog(getActivity(), R.style.dialog);
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_channel, null);
            holder = new ViewHolder(view, dialog);
            dialog.setContentView(view);
        }
        dialog.show();
        if (edit) {
            holder.seekBarBlue.setProgress(currentCurvePoint.getChannel().getBlue());
            holder.seekBarWhite.setProgress(currentCurvePoint.getChannel().getWhite());
            holder.seekBarPurple.setProgress(currentCurvePoint.getChannel().getPurple());
            holder.seekBarGreen.setProgress(currentCurvePoint.getChannel().getGreen());
            holder.seekBarRed.setProgress(currentCurvePoint.getChannel().getRed());
        }
        autoPresenter.previewChanel(holder.getLampChannel());
        dialog.getWindow().setLayout(LocalPhoneParms.getPhoneWidth() / 4 * 3,
                LocalPhoneParms.getPhoneHeight() / 4 * 3);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                autoPresenter.stopPreviewChanel();
            }
        });
    }


    public void spikToCurrent(int time) {
        lcChart.highlightValue(time, 0);
        tvTime.setText(xVals.get(time));
        this.tempCurvePoint = new CurvePoint(time);
    }

    @Override
    public void enableDeleteButton(boolean b) {
        if (b) {
            this.btnDel.setImageResource(R.mipmap.ic_delete);
        } else {
            this.btnDel.setImageResource(R.mipmap.ic_delete_disabled);
        }
        this.btnDel.setEnabled(b);
    }

    @Override
    public void enableEditButton(boolean enable) {
        if (enable) {
            this.btnAdd.setVisibility(View.GONE);
            this.btnEdit.setVisibility(View.VISIBLE);
        } else {
            this.btnAdd.setVisibility(View.VISIBLE);
            this.btnEdit.setVisibility(View.GONE);
        }
    }

    @Override
    public void enableAddButton(boolean enable) {
        if (enable) {
            this.btnAdd.setImageResource(R.mipmap.ic_add);
        } else {
            this.btnAdd.setImageResource(R.mipmap.ic_add_disabled);
        }
        this.btnAdd.setEnabled(enable);
    }


    private void addLineChartView() {

        Resources resource = getResources();

        lcChart.setDescription("");
        lcChart.setScaleEnabled(true);
        lcChart.getAxisRight().setEnabled(true);
        lcChart.setDrawGridBackground(true);
        lcChart.setTouchEnabled(true);
        lcChart.setHardwareAccelerationEnabled(true);
        lcChart.setGridBackgroundColor(0X00EDF4FC);
        lcChart.setSpecialMarkerView(true);
        lcChart.setSpecialMarkerCircle(BitmapFactory
                .decodeResource(getResources(), R.mipmap.icon_point));
        lcChart.setHighlightAutoDismiss(true);
        lcChart.setDoubleTapToZoomEnabled(false);
        lcChart.setForceTouchEventEnable(true);
        lcChart.setPinchZoom(false);
        lcChart.setSpecialMarkerLineColor(Color.RED);

        lcChart.setScaleEnabled(false);//禁止缩放


        mv = new MyMarkerView(getActivity(), R.layout.custom_marker_assets_view);

        // set the marker to the chart
        lcChart.setMarkerView(mv);

        lcChart.setOnChartValueSelectedListener(this);

        // enable/disable highlight indicators (the lines that indicate the
        // highlighted Entry)
        lcChart.setHighLightIndicatorEnabled(true);

        Legend legend = lcChart.getLegend();
        legend.setEnabled(true);
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);


        /**
         * x轴样式设置
         */
        XAxis xAxis = lcChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);// 设置x轴在底部显示
        //xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED); // 设置x轴在顶部显示
        xAxis.setValues(xVals);
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(true);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setDrawAxisLine(true);
        xAxis.enableAxisDashedLine(10f, 10f, 0f);
        xAxis.setLabelsToSkip(11);


        /**
         * y轴样式设置
         */
        YAxis leftAxis = lcChart.getAxisLeft();
        leftAxis.setStartAtZero(true);
        leftAxis.setDrawLimitLinesBehindData(false);
        leftAxis.setDrawLabels(true);
//        leftAxis.setTextColor(resource.getColor(R.color.grey_low_txt));
//        leftAxis.setAxisLineColor(resource.getColor(R.color.color_dddddd));
        leftAxis.setDrawGridLines(false);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawAxisLine(true);
        leftAxis.enableAxisDashedLine(10f, 10f, 0f);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
//        leftAxis.setTextColor(resource.getColor(R.color.grey_low_txt));
//        leftAxis.setAxisLineColor(resource.getColor(R.color.color_dddddd));
        leftAxis.setDrawGridLines(false);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawAxisLine(true);
        leftAxis.enableAxisDashedLine(10f, 10f, 0f);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

//        leftAxis.setDrawAxisLine(false);
//        leftAxis.setEnabled(false);
//        leftAxis.setGridColor(resource.getColor(R.color.color_dddddd));
        leftAxis.setAxisMaxValue(120); // 设置Y轴最大值
        leftAxis.setAxisMinValue(0);// 设置Y轴最小值
        leftAxis.setDrawAxisLine(true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "";
            }
        });
//        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = lcChart.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawAxisLine(true);
        rightAxis.setAxisLineColor(0xffdddddd);

        drawChart();
    }

    public void drawChart() {
        List<CurvePoint> points = DataManager.getInstance().getAutoDataNode().genData4PreviewChart();
        dataSets = genPreviewLedData(points);
        LineData data = new LineData(xVals, dataSets);
        lcChart.setData(data);
        lcChart.invalidate();
    }


    private LineDataSet getLineDataSet(ArrayList<Entry> yRawData, String lable, int color) {
        /**
         * 曲线样式设置
         */
        LineDataSet set = new LineDataSet(yRawData, lable);
        set.setLineWidth(dip2px(getActivity(), 0.5f));
        set.setCircleSize(dip2px(getActivity(), 0.5f));
        set.setColor(color);
        set.setCircleColor(color);
        set.setDrawCircles(false);
        return set;
    }

    private List<LineDataSet> genPreviewLedData(List<CurvePoint> curvePoints) {
        ArrayList<Entry> values1 = new ArrayList<Entry>();
        ArrayList<Entry> values2 = new ArrayList<Entry>();
        ArrayList<Entry> values3 = new ArrayList<Entry>();
        ArrayList<Entry> values4 = new ArrayList<Entry>();
        ArrayList<Entry> values5 = new ArrayList<Entry>();
        for (int i = 0; i < curvePoints.size(); i++) {
            CurvePoint point = curvePoints.get(i);
            values1.add(new Entry(point.getChannel().getTempBlue(), point.getTime()));
            values2.add(new Entry(point.getChannel().getTempWhite(), point.getTime()));
            values3.add(new Entry(point.getChannel().getTempPurple(), point.getTime()));
            values4.add(new Entry(point.getChannel().getTempGreen(), point.getTime()));
            values5.add(new Entry(point.getChannel().getTempRed(), point.getTime()));
        }

        LineDataSet setDataBlue = getLineDataSet(values1, getString(R.string.ch_B),
                getResources().getColor(R.color.ch_color_blue));
        LineDataSet setDataWhite = getLineDataSet(values2, getString(R.string.ch_W),
                getResources().getColor(R.color.ch_color_white));
        LineDataSet setDataPurple = getLineDataSet(values3, getString(R.string.ch_P),
                getResources().getColor(R.color.ch_color_purple));
        LineDataSet setDataGreen = getLineDataSet(values4, getString(R.string.ch_G),
                getResources().getColor(R.color.ch_color_green));
        LineDataSet setDataRed = getLineDataSet(values5, getString(R.string.ch_R),
                getResources().getColor(R.color.ch_color_red));

        List<LineDataSet> datas = new ArrayList<LineDataSet>(5);
        datas.add(setDataBlue);
        datas.add(setDataWhite);
        datas.add(setDataPurple);
        datas.add(setDataGreen);
        datas.add(setDataRed);
        return datas;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取最大最小年化率
     *
     * @param currentNum
     */
    private void initMaxMin(float currentNum) {
        if (currentNum >= yAxisMax) {
            yAxisMax = currentNum;
        }

        if (currentNum < yAxisMin) {
            yAxisMin = currentNum;
        }
    }


    private static ArrayList<String> xVals;

    private static void intXLabel() {
        String fommater = "%02d:%2d0";
        int xCount = 24 * 6 + 1;
        xVals = new ArrayList<String>();
        for (int i = 0; i < xCount; i++) {
            xVals.add(String.format(fommater, (i / 6) % 24, i % 6));
        }
    }


    @Override
    public void onValueSelected(List<Entry> e, int dataSetIndex, Highlight h) {
        if (e != null && e.size() > 0) {
            int time = e.get(0).getXIndex();
            String textTime = xVals.get(time);
            tvTime.setText(textTime);
            LampChannel lc = new LampChannel();
            lc.setBlue(e.get(0).getVal());
            lc.setWhite(e.get(1).getVal());
            lc.setPurple(e.get(2).getVal());
            lc.setGreen(e.get(3).getVal());
            lc.setRed(e.get(4).getVal());
            this.tempCurvePoint = new CurvePoint(time, lc);
            autoPresenter.spikeCursor(time);

        }
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void stopPreview() {
        // lcChart.getAnimator().
        this.lcChart.animateXStop();
        previewing = false;
        canPreview(true);
    }


    public void preview() {
        if (!previewing) {
            this.lcChart.animateX(25 * 1000);
            previewTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            }, new Date(System.currentTimeMillis() + 25 * 1000));
            previewing = true;
            canPreview(false);
        }
    }

    void canPreview(boolean can) {
        if (can) {
            if (btnPreview != null)
                btnPreview.setVisibility(View.VISIBLE);
            if (btnStopPreview != null)
                btnStopPreview.setVisibility(View.GONE);
        } else {
            if (btnPreview != null)
                btnPreview.setVisibility(View.GONE);
            if (btnStopPreview != null)
                btnStopPreview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG)
                .show();
    }

    class ViewHolder implements SeekBar.OnSeekBarChangeListener {
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
        @Bind(R.id.seekBar_green)
        SeekBar seekBarGreen;
        @Bind(R.id.tv_value_sb_green)
        TextView tvValueSbGreen;
        @Bind(R.id.seekBar_red)
        SeekBar seekBarRed;
        @Bind(R.id.tv_value_sb_red)
        TextView tvValueSbRed;
        @Bind(R.id.tv_btn_ok)
        Button tvBtnOk;
        @Bind(R.id.tv_btn_cancel)
        Button tvBtnCancel;


        private Dialog dialog;

        ViewHolder(View view, Dialog dialog) {
            ButterKnife.bind(this, view);
            this.dialog = dialog;
            seekBarBlue.setOnSeekBarChangeListener(this);
            seekBarWhite.setOnSeekBarChangeListener(this);
            seekBarPurple.setOnSeekBarChangeListener(this);
            seekBarRed.setOnSeekBarChangeListener(this);
            seekBarGreen.setOnSeekBarChangeListener(this);

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser)
                previewChannel(progress, false);
            switch (seekBar.getId()) {
                case R.id.seekBar_blue:
                    this.tvValueSbBlue.setText("" + progress);
                    currentCurvePoint.getChannel().setData(ChanelType.Bule, progress);
                    break;
                case R.id.seekBar_white:
                    this.tvValueSbWhite.setText("" + progress);
                    currentCurvePoint.getChannel().setData(ChanelType.White, progress);
                    break;
                case R.id.seekBar_purple:
                    this.tvValueSbPurple.setText("" + progress);
                    currentCurvePoint.getChannel().setData(ChanelType.PurPle, progress);
                    break;
                case R.id.seekBar_red:
                    this.tvValueSbRed.setText("" + progress);
                    currentCurvePoint.getChannel().setData(ChanelType.Red, progress);
                    break;
                case R.id.seekBar_green:
                    this.tvValueSbGreen.setText("" + progress);
                    currentCurvePoint.getChannel().setData(ChanelType.Green, progress);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            previewChannel(progress, true);
        }

        private void previewChannel(int progress, boolean isLast) {
            if (!isLast) {
                if (progress % 10 == 0)
                    autoPresenter.previewChanel(getLampChannel());
            } else {
                autoPresenter.previewChanel(getLampChannel());
            }
        }


        @NonNull
        private LampChannel getLampChannel() {
            LampChannel channel = new LampChannel();
            channel.setData(ChanelType.Bule, seekBarBlue.getProgress());
            channel.setData(ChanelType.White, seekBarWhite.getProgress());
            channel.setData(ChanelType.PurPle, seekBarPurple.getProgress());
            channel.setData(ChanelType.Green, seekBarGreen.getProgress());
            channel.setData(ChanelType.Red, seekBarRed.getProgress());
            return channel;
        }

        @OnClick(R.id.tv_btn_ok)
        void onBtnOKClick() {
            LampChannel channel = getLampChannel();

            CurvePoint point = new CurvePoint(currentCurvePoint.getTime(), channel);
            autoPresenter.addPoint(point);
            dialog.dismiss();
        }

        @OnClick(R.id.tv_btn_cancel)
        void onCancelClick() {
            dialog.dismiss();
        }
    }

}
