package cn.com.lightech.led_g5g.wedgit;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import cn.com.lightech.led_g5g.R;

/**
 * Created by 明 on 2016/3/23.
 */
public class TimePreference extends DialogPreference {


    private TimePicker timePicker;
    private int time;

    private TextView tvValue;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.timePicker = new TimePicker(context, attrs);
        this.timePicker.setEnabled(true);
        this.timePicker.setId(R.id.timepreferenct);
        //this.timePicker.setIs24HourView(true);
    }

    public TimePreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        this.tvValue = (TextView) view.findViewById(R.id.tv_value);
        setTextValue();
        this.tvValue.setEnabled(isEnabled());

    }

    @Override
    protected View onCreateDialogView() {
        ViewParent oldParent = timePicker.getParent();
        if (oldParent != null) {
            ((ViewGroup) oldParent).removeView(timePicker);
        }
        return timePicker;
    }


    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.callChangeListener(this.time);
        if (positiveResult) {
            this.timePicker.setIs24HourView(true);
            int hour = this.timePicker.getCurrentHour();
            int minute = this.timePicker.getCurrentMinute();
            this.timePicker.setIs24HourView(false);
            this.time = hour * 60 + minute;
            if (callChangeListener(time)) {
                setTime(time);
            }
            setTextValue();
        }

    }

    public void setTime(int time) {
        final boolean wasBlocking = shouldDisableDependents();

        this.time = time;

        persistInt(time);

        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }
    }

    private void setTextValue() {
        if (this.tvValue != null)
            tvValue.setText(String.format("%02d:%02d", this.time / 60, this.time % 60));
    }


    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setTime(restoreValue ? getPersistedInt(time) : (int) defaultValue);
    }

}
