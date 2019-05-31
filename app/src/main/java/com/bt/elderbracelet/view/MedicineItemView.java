package com.bt.elderbracelet.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bttow.elderbracelet.R;

import butterknife.Bind;

/**
 * Created by Administrator on 2016/8/6.
 */
public class MedicineItemView extends LinearLayout implements View.OnClickListener{
    @Bind(R.id.cb_is_repeat)
    CheckBox cbIsRepeat;
    @Bind(R.id.layout_senven)
    LinearLayout layoutSenven;
    @Bind(R.id.layout_one)
    LinearLayout layoutOne;
    @Bind(R.id.layout_two)
    LinearLayout layoutTwo;
    @Bind(R.id.layout_three)
    LinearLayout layoutThree;
    @Bind(R.id.layout_four)
    LinearLayout layoutFour;
    @Bind(R.id.layout_five)
    LinearLayout layoutFive;
    @Bind(R.id.layout_six)
    LinearLayout layoutSix;
    @Bind(R.id.tv_musci_name)
    TextView tvMusciName;
    @Bind(R.id.cb_shock)
    CheckBox cbShock;

    public MedicineItemView(Context context) {
        super(context);
        init(context);
    }

    public MedicineItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MedicineItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.bracelet_medicine_view, null);
        addView(view);
        cbIsRepeat.setOnClickListener(this);
        layoutSenven.setOnClickListener(this);
        layoutOne.setOnClickListener(this);
        layoutTwo.setOnClickListener(this);
        layoutThree.setOnClickListener(this);
        layoutFour.setOnClickListener(this);
        layoutFive.setOnClickListener(this);
        layoutSix.setOnClickListener(this);
        tvMusciName.setOnClickListener(this);
        cbShock.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cb_is_repeat:
                break;

            case R.id.cb_shock:
                break;

            case R.id.layout_senven:
                break;

            case R.id.layout_one:
                break;

            case R.id.layout_two:
                break;

            case R.id.layout_three:
                break;

            case R.id.layout_four:
                break;

            case R.id.layout_five:
                break;

            case R.id.layout_six:
                break;


        }
    }
}
