package com.bt.elderbracelet.tools.other;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.bttow.elderbracelet.R;

/**
 * 数字选择器
 */
public class VNTNumberPickerPreference extends DialogPreference {
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;
    private static final boolean WRAP_SELECTOR_WHEEL = false;

    private int selectedValue;
    private final int minValue;
    private final int maxValue;
    private final boolean wrapSelectorWheel;
    private NumberPicker numberPicker;

    public VNTNumberPickerPreference(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.vnt_NumberPickerPreference);

        minValue = a.getInt(R.styleable.vnt_NumberPickerPreference_vnt_minValue, MIN_VALUE);
        maxValue = a.getInt(R.styleable.vnt_NumberPickerPreference_vnt_maxValue, MAX_VALUE);
        wrapSelectorWheel = a.getBoolean(R.styleable.vnt_NumberPickerPreference_vnt_setWrapSelectorWheel, WRAP_SELECTOR_WHEEL);
        a.recycle();
    }


    /**
     * 获取默认的值
     */
    @Override
    protected Object onGetDefaultValue(final TypedArray a, final int index)
    {
        return a.getInteger(index, 0);
    }

    /**
     * 设置初始值
     *
     * @param restoreValue
     * @param defaultValue
     */
    @Override
    protected void onSetInitialValue(final boolean restoreValue, final Object defaultValue)
    {
        final int intDefaultValue;
        if (defaultValue instanceof Integer) {    //如果你的默认值
            intDefaultValue = (int) defaultValue;
        } else {
            intDefaultValue = minValue;
        }

        if (restoreValue) {
            selectedValue = this.getPersistedInt(intDefaultValue);
        } else {
            selectedValue = intDefaultValue;
        }

        this.updateSummary();
    }

    @Override
    protected void onPrepareDialogBuilder(final Builder builder)
    {
        super.onPrepareDialogBuilder(builder);

        numberPicker = new NumberPicker(this.getContext());
        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setValue(selectedValue);
        numberPicker.setWrapSelectorWheel(wrapSelectorWheel);
        numberPicker.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        final LinearLayout linearLayout = new LinearLayout(this.getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(numberPicker);
        builder.setView(linearLayout);
    }

    //当对话框被关闭时调用
    @Override
    protected void onDialogClosed(final boolean positiveResult)
    {
        super.onDialogClosed(positiveResult);

        if (positiveResult && numberPicker != null) {
            final int newValue = numberPicker.getValue();

            if (this.callChangeListener(newValue)) {
                this.selectedValue = newValue;
                this.updateSummary();
                this.persistInt(this.selectedValue);
            }
        }
    }

    private void updateSummary()
    {
        this.setSummary(String.valueOf(selectedValue));
    }
}
