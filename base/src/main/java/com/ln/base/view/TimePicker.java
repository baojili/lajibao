package com.ln.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.ln.base.R;

import java.util.Calendar;

public class TimePicker extends LinearLayout implements WheelPicker.OnSelectLineChangeListener {

    private WheelPicker mHourWheel, mMinuteWheel;
    private OnTimeChangeListener mListener;
    private Calendar mCalendar;

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_time_picker, this);
        mHourWheel = findViewById(R.id.hour_wheel);
        mMinuteWheel = findViewById(R.id.minute_wheel);
        mHourWheel.setOnSelectLineChangeListener(this);
        mMinuteWheel.setOnSelectLineChangeListener(this);

        mCalendar = Calendar.getInstance();

        mHourWheel.setAdapter(new WheelPicker.NumberAdapter(0, 23, "%02d"), mCalendar.get(Calendar.HOUR_OF_DAY));
        mMinuteWheel.setAdapter(new WheelPicker.NumberAdapter(0, 59, "%02d"), mCalendar.get(Calendar.MINUTE));
    }

    @Override
    public void onSelectLineChange(WheelPicker wheelPicker, int selectLine) {
        if (wheelPicker == mHourWheel) {
            mCalendar.set(Calendar.HOUR_OF_DAY, mHourWheel.getSelectLine());
        } else {
            mCalendar.set(Calendar.MINUTE, mMinuteWheel.getSelectLine());
        }
        if (mListener != null) {
            mListener.onTimeChange(this, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
        }
    }

    public long getTime() {
        return mCalendar.getTimeInMillis();
    }

    public void setTime(long timeMillis) {
        mCalendar.setTimeInMillis(timeMillis);
        mHourWheel.setSelectLine(mCalendar.get(Calendar.HOUR_OF_DAY), true);
        mMinuteWheel.setSelectLine(mCalendar.get(Calendar.MINUTE), true);
    }

    public void setOnTimeChangeListener(OnTimeChangeListener listener) {
        mListener = listener;
    }

    public interface OnTimeChangeListener {
        void onTimeChange(TimePicker timePicker, int hourOfDay, int minute);
    }

}