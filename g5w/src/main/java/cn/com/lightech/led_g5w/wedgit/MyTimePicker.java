package cn.com.lightech.led_g5w.wedgit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.lightech.led_g5w.R;
import cn.com.lightech.led_g5w.utils.DensityUtil;

public class MyTimePicker extends LinearLayout implements OnClickListener {

    // private static Dialog dialog;

    private PopupWindow popupTime;

    public static final int TYPE_H_AND_M = 1;
    public static final int TYPE_S_AND_MS = 2;
    public static final int TYPE_M_AND_S = 3;
    public static final int TYPE_S_AND_PMS = 4;

    public static NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
        final StringBuilder mBuilder = new StringBuilder();

        final java.util.Formatter mFmt = new java.util.Formatter(mBuilder,
                java.util.Locale.US);

        final Object[] mArgs = new Object[1];

        public String format(int value) {
            mArgs[0] = value;
            mBuilder.delete(0, mBuilder.length());
            mFmt.format("%02d", mArgs);
            return mFmt.toString();
        }
    };

    public static NumberPicker.Formatter formatter05 = new NumberPicker.Formatter() {

        public String format(int value) {
            return Integer.toString(value);
        }
    };
    /**
     * type 1 hour max
     */
    private static final int TYPE_H_AND_M_LEFT_MAX = 23;
    /**
     * type 1 minute max
     */
    private static final int TYPE_H_AND_M_RIGHT_MAX = 59;
    /**
     * type 2 秒 max
     */
    private static final int TYPE_S_AND_MS_LEFT_MAX = 9;
    /**
     * type 2 毫秒 max
     */
    private static final int TYPE_S_AND_MS_RIGHT_MAX = 13;
    /**
     * type 3 minute max
     */
    private static final int TYPE_M_AND_S_LEFT_MAX = 9;
    /**
     * type 3 秒 max
     */
    private static final int TYPE_M_AND_S_RIGHT_MAX = 59;
    /**
     * type 4 秒 max
     */
    private static final int TYPE_S_AND_PMS_LEFT_MAX = 9;
    /**
     * type 4 毫秒 max
     */
    private static final int TYPE_S_AND_PMS_RIGHT_MAX = 99;

    private static final int MIN = 0;

    private int left;
    private int right;

    @Bind(R.id.number_left)
    TextView numberLeft;

    @Bind(R.id.number_right)
    TextView numberRight;

    @Bind(R.id.text_left)
    TextView tv_left;

    @Bind(R.id.text_right)
    TextView tv_right;

    int type;

    private OnValueChangeListener onValueChangeListener;

    private TypedArray typeArray;

    private int textColor;

    public MyTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_mytimepicker, this);
        typeArray = context.obtainStyledAttributes(attrs,
                R.styleable.custTimePicker);
        ButterKnife.bind(this);
        init();
    }

    public MyTimePicker(Context context) {
        super(context);
        inflate(context, R.layout.popup_custtimepicker, this);
        init();
    }

    void init() {
        this.type = typeArray.getInt(R.styleable.custTimePicker_DisplayType, 1);
        this.textColor = typeArray.getColor(
                R.styleable.custTimePicker_textColor, 0);
        if (textColor == 0) {
            textColor = 2131165261;
        }
        String leftText = typeArray
                .getString(R.styleable.custTimePicker_centerText);
        String rightText = typeArray
                .getString(R.styleable.custTimePicker_rightText);

        setUnitText(tv_left, leftText);
        setUnitText(tv_right, rightText);

        this.setOnClickListener(this);
        setValue(0, 0);
        this.tv_left.setTextColor(textColor);
        this.tv_right.setTextColor(textColor);
        this.numberLeft.setTextColor(textColor);
        this.numberRight.setTextColor(textColor);
        // updateTextView();

    }

    private void setUnitText(TextView tv, String text) {
        if (TextUtils.isEmpty(text)) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setText(text);
            tv.setVisibility(View.VISIBLE);
        }
    }

    public void setValue(int left, int right) {
        left = left < 0 ? 0 : left;
        left = left > getMaxLeft() ? getMaxLeft() : left;
        this.left = left;

        right = right < 0 ? 0 : right;
        right = right > getMaxRight() ? getMaxRight() : right;
        this.right = right;
        notifyChange();

    }

    private void updateTextView() {
        this.numberLeft.setText(getFormatter(false).format(left));
        if (type == TYPE_S_AND_MS) {
            this.numberRight
                    .setText(getFormatter(true).format((right % 2) * 5));
        } else {
            this.numberRight.setText(getFormatter(true).format(right));
        }
    }

    public int getleftValue() {
        return left;
    }

    public int getRightValue() {
        if (type == TYPE_S_AND_MS)
            return (right % 2) * 5;
        else
            return right;

    }

    private int getMaxLeft() {
        switch (type) {
            case 1:
                return TYPE_H_AND_M_LEFT_MAX;
            case 2:
                return TYPE_S_AND_MS_LEFT_MAX;
            case 3:
                return TYPE_M_AND_S_LEFT_MAX;
            case 4:
                return TYPE_S_AND_PMS_LEFT_MAX;

            default:
                return 0;
        }

    }

    private int getMaxRight() {
        switch (type) {
            case 1:
                return TYPE_H_AND_M_RIGHT_MAX;
            case 2:
                return TYPE_S_AND_MS_RIGHT_MAX;
            case 3:
                return TYPE_M_AND_S_RIGHT_MAX;
            case 4:
                return TYPE_S_AND_PMS_RIGHT_MAX;

            default:
                return 0;
        }

    }

    private NumberPicker.Formatter getFormatter(boolean isRight) {
        if (isRight) {
            if (type == TYPE_S_AND_MS) {
                return formatter05;
            }
        }
        return formatter;
    }

    private int getMin() {
        return 0;
    }

    class OnValueChange implements
            android.widget.NumberPicker.OnValueChangeListener {

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            if (onValueChangeListener != null) {
                int left = getleftValue();
                int right = getRightValue();
                onValueChangeListener.onValueChange(left, right);
            }

        }
    }

    public interface OnValueChangeListener {
        void onValueChange(int left, int right);
    }

    public OnValueChangeListener getOnValueChangeListener() {
        return onValueChangeListener;
    }

    public void setOnValueChangeListener(
            OnValueChangeListener onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
    }

    private void initTimePickerType1(NumberPicker npLeft, NumberPicker npRight) {

        npLeft.setMaxValue(getMaxLeft());
        npLeft.setMinValue(getMin());
        npRight.setMaxValue(getMaxRight());
        npRight.setMinValue(getMin());
        npLeft.setDisplayedValues(getLabels(getMaxLeft()));
        npRight.setDisplayedValues(getLabels(getMaxRight()));
    }

    private void initTimePickerType2(NumberPicker npLeft, NumberPicker npRight) {

        // 变步长
        npRight.setDisplayedValues(new String[]{"0", "5", "0", "5", "0", "5",
                "0", "5", "0", "5", "0", "5", "0", "5"});
        npRight.setMaxValue(getMaxRight());
        npRight.setMinValue(getMin());

        npLeft.setDisplayedValues(getLabels(9));
        npLeft.setMaxValue(getMaxLeft());
        npLeft.setMinValue(getMin());
    }

    private void initTimePickerType3(NumberPicker npLeft, NumberPicker npRight) {
        npLeft.setMaxValue(getMaxLeft());
        npLeft.setMinValue(getMin());
        npRight.setMaxValue(getMaxRight());
        npRight.setMinValue(getMin());
        npLeft.setDisplayedValues(getLabels(getMaxLeft()));
        npRight.setDisplayedValues(getLabels(getMaxRight()));
    }

    private void initTimePickerType4(NumberPicker npLeft, NumberPicker npRight) {

        npLeft.setMaxValue(getMaxLeft());
        npLeft.setMinValue(getMin());
        npRight.setMaxValue(getMaxRight());
        npRight.setMinValue(getMin());
        npLeft.setDisplayedValues(getLabels(getMaxLeft()));
        npRight.setDisplayedValues(getLabels(getMaxRight()));
    }

    @Override
    public void onClick(View v) {

        if (popupTime == null) {
            popupTime = new PopupWindow(this);
            View popuView = LayoutInflater.from(getContext()).inflate(
                    R.layout.popup_custtimepicker, null);
            popupTime.setContentView(popuView);
            popupTime.setFocusable(true);
            popupTime.setBackgroundDrawable(new BitmapDrawable());
            TextView btnOK = (TextView) popuView
                    .findViewById(R.id.btn_dialog_timepicker_ok);

            final NumberPicker npLeft = (NumberPicker) popuView
                    .findViewById(R.id.np_time_hour);
            final NumberPicker npRight = (NumberPicker) popuView
                    .findViewById(R.id.np_time_minute);

            switch (type) {
                case TYPE_H_AND_M:
                    initTimePickerType1(npLeft, npRight);
                    break;
                case TYPE_S_AND_MS:

                    initTimePickerType2(npLeft, npRight);
                    break;
                case TYPE_M_AND_S:

                    initTimePickerType3(npLeft, npRight);
                    break;
                case TYPE_S_AND_PMS:

                    initTimePickerType4(npLeft, npRight);
                    break;
                default:
                    initTimePickerType1(npLeft, npRight);
                    break;
            }

            npRight.setValue(right);
            npLeft.setValue(left);
            npLeft.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            npRight.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

            TextView tvLeft = (TextView) popuView
                    .findViewById(R.id.tv_timepicker_center);
            TextView tvRight = (TextView) popuView
                    .findViewById(R.id.tv_timepicker_right);

            tvLeft.setText(typeArray
                    .getString(R.styleable.custTimePicker_centerText));
            tvRight.setText(typeArray
                    .getString(R.styleable.custTimePicker_rightText));
            btnOK.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    popupTime.dismiss();
                }
            });
            popupTime.setWidth(DensityUtil.dip2px(getContext(), 330));
            popupTime.setHeight(DensityUtil.dip2px(getContext(), 260));
            popupTime.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss() {
                    left = npLeft.getValue();
                    right = npRight.getValue();
                    setValue(left, right);
                    popupTime.dismiss();
                }
            });
        }
        if (popupTime.isShowing()) {
            popupTime.dismiss();
        } else {
            popupTime.showAtLocation(v, Gravity.CENTER, 0, 0);
        }

    }

    private void notifyChange() {
        updateTextView();
        if (onValueChangeListener != null) {
            onValueChangeListener.onValueChange(left, right);
        }
    }

    private String[] getLabels(int max) {
        String[] Labels = new String[max + 1];
        for (int i = 0; i <= max; i++) {
            Labels[i] = formatter.format(i);
        }
        return Labels;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        numberLeft.setEnabled(enabled);
        numberRight.setEnabled(enabled);
        tv_left.setEnabled(enabled);
        tv_right.setEnabled(enabled);
        if (enabled) {
            numberLeft.setTextColor(textColor);
            numberRight.setTextColor(textColor);
            tv_left.setTextColor(textColor);
            tv_right.setTextColor(textColor);
        } else {
            numberLeft.setTextColor(Color.GRAY);
            numberRight.setTextColor(Color.GRAY);
            tv_left.setTextColor(Color.GRAY);
            tv_right.setTextColor(Color.GRAY);
        }
    }

}
