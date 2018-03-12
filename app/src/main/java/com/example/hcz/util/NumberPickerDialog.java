package com.example.hcz.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.hcz.hechangzhi.R;

/**
 * 使用NumberPicker获取数值的对话框
 */
public class NumberPickerDialog extends AlertDialog implements OnClickListener, NumberPicker.OnValueChangeListener {

    private final String maxValue = "最大值";
    private final String minValue = "最小值";
    private final String currentValue = "当前值";

    private final NumberPicker mNumberPicker;
    private final NumberPicker.OnValueChangeListener mCallback;

    private int newVal;
    private int oldVal;

    private String[] strings;
    private TextView title;

    public NumberPickerDialog(Context context, NumberPicker.OnValueChangeListener mCallback, String[] strings, String string) {
        super(context);
        this.mCallback = mCallback;
        this.strings = strings;

        setIcon(0);

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, "确定", this);
        setButton(BUTTON_NEGATIVE, "取消", this);

        LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_picker, null);
        setView(view);
        mNumberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);

        mNumberPicker.setDisplayedValues(strings);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setMaxValue(strings.length - 1);
        mNumberPicker.setOnValueChangedListener(this);
        title = (TextView) view.findViewById(R.id.title);
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].equals(string)){
                title.setText(strings[i]);
                mNumberPicker.setValue(i);
            }
        }
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(maxValue, mNumberPicker.getMaxValue());
        state.putInt(minValue, mNumberPicker.getMinValue());
        state.putInt(currentValue, mNumberPicker.getValue());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int max = savedInstanceState.getInt(maxValue);
        int min = savedInstanceState.getInt(minValue);
        int cur = savedInstanceState.getInt(currentValue);
        mNumberPicker.setMaxValue(max);
        mNumberPicker.setMinValue(min);
        mNumberPicker.setValue(cur);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        this.oldVal = oldVal;
        this.newVal = newVal;
        title.setText(strings[newVal]);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                mCallback.onValueChange(mNumberPicker, oldVal, newVal);
                break;
        }
    }

    /**
     * <b>功能</b>: setCurrentValue，设置NumberPicker的当前值<br/>
     *
     * @return
     * @author : weiyou.com <br/>
     */
    public NumberPickerDialog setCurrentValue(int value) {

        mNumberPicker.setValue(value);

        return this;
    }
}