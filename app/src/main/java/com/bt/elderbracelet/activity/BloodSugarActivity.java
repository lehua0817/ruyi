package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.TextView;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.BloodSugar;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.other.DecimalScaleRulerView;
import com.bt.elderbracelet.tools.other.DrawUtil;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;


public class BloodSugarActivity extends Activity {
    TitleView titleview;
    TextView tvdate;
    TextView tvSugarPre;
    DecimalScaleRulerView rulerSuagarPre;
    TextView tvSugarAfter;
    DecimalScaleRulerView rulerSugarAfter;
    TextView tvsure;
    Chronometer ch;

    private float mSugar_pre = 4.5f;//餐前血糖初始值
    private float mSugar_after = 6.0f;
    private String sugarBefore, sugarAfter;
    private ModelDao modelDao;
    private BloodSugar sugar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_sugar);
        MyApplication.getInstance().addActivity(this);
        initView();
        init();
    }

    private void initView()
    {
        titleview = (TitleView) findViewById(R.id.titleview);
        titleview.setcolor("#FB3A67");
        titleview.titleImg(R.drawable.sugar_icon);
        titleview.setTitle(R.string.sugar);
        titleview.right(R.string.history, new onSetLister() {
            @Override
            public void onClick(View button)
            {
                Intent intent = new Intent(getApplicationContext(), SugarHistoryActivity.class);
                intent.putExtra("dataType", MyApplication.SUGAR_TYPE);
                startActivity(intent);
            }
        });
        tvdate = (TextView) findViewById(R.id.tvdate);
        tvSugarPre = (TextView) findViewById(R.id.tv_sugar_pre);
        tvSugarAfter = (TextView) findViewById(R.id.tv_sugar_after);
        rulerSuagarPre = (DecimalScaleRulerView) findViewById(R.id.ruler_suagar_pre);
        rulerSugarAfter = (DecimalScaleRulerView) findViewById(R.id.ruler_sugar_after);
        tvsure = (TextView) findViewById(R.id.tvsure);
        ch = (Chronometer) findViewById(R.id.ch);
        ch.setVisibility(View.GONE);
        ch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer)
            {
                if ((SystemClock.elapsedRealtime() - ch.getBase()) > 10 * 1000) {
                    ch.stop();
                    tvsure.setClickable(true);
                    tvsure.setBackgroundResource(R.color.sugar_bg);
                    ch.setVisibility(View.GONE);
                }
            }
        });
    }

    private void init()
    {
        if (modelDao == null) {
            modelDao = new ModelDao(getApplicationContext());
        }

        //这里其实还是初始化界面，给餐前餐后血糖控件赋值
        //从 HealthState 数据库中取数据，如果有今天的数据，则显示出来，如果没有，则显示系统默认数值
        sugar = modelDao.getLastBloodSuger();
        if (sugar != null) {
            String lastDate = sugar.getPreciseDate();
            if (lastDate.substring(0, 10).equals(BaseUtils.getTodayDate())) {
                tvSugarPre.setText(sugar.getBloodSugarBefore());
                tvSugarAfter.setText(sugar.getBloodSugarAfter());
                tvdate.setText(sugar.getPreciseDate());
            }
        } else {
            tvSugarPre.setText(mSugar_pre + "");           //初始化界面时，人为地设置餐前餐后血糖值
            tvSugarAfter.setText(mSugar_after + "");
        }

        tvsure.setOnClickListener(new OnClickListener() {   //点击确定按钮，更新本地数据库HealthState 的同时
            //也将数据上传到服务器
            @Override
            public void onClick(View v)
            {
                sugarBefore = tvSugarPre.getText().toString();
                sugarAfter = tvSugarAfter.getText().toString();

                sugar = new BloodSugar();
                sugar.setBloodSugarBefore(sugarBefore);
                sugar.setBloodSugarAfter(sugarAfter);
                sugar.setPreciseDate(BaseUtils.getPreciseDate());

                modelDao.insertBloodSugar(sugar);
                MethodUtils.uploadBloodSugar(BloodSugarActivity.this, sugar);

                tvsure.setClickable(false);
                tvsure.setBackgroundResource(R.color.gray);
                ch.setBase(SystemClock.elapsedRealtime());
                ch.start();
                tvsure.setEnabled(false);

            }
        });
        titleview.setBack(R.drawable.steps_back, new onBackLister() {

            @Override
            public void onClick(View button)
            {
                finish();
            }
        });

        /**
         *餐前血糖类初始化，一开始用系统默认值来设置 数值选择器，当你在数值选择器中选择了数据以后
         * 则 用选择的新数据来 设置tvSugarPre(血糖值)
         */
        rulerSuagarPre.setParam(DrawUtil.dip2px(40), DrawUtil.dip2px(85), DrawUtil.dip2px(75),
                DrawUtil.dip2px(65), DrawUtil.dip2px(50), DrawUtil.dip2px(50));
        rulerSuagarPre.initViewParam(mSugar_pre, 2.0f, 20.0f, 1);//餐前 血糖
        rulerSuagarPre.setValueChangeListener(new DecimalScaleRulerView.OnValueChangeListener() {
            //数值选择器的 监听器
            @Override
            public void onValueChange(float value)
            {
                tvSugarPre.setText(value + "");
                mSugar_pre = value;
            }
        });

        /**
         *餐后血糖类初始化
         */
        rulerSugarAfter.setParam(DrawUtil.dip2px(40), DrawUtil.dip2px(85), DrawUtil.dip2px(75),
                DrawUtil.dip2px(65), DrawUtil.dip2px(50), DrawUtil.dip2px(50));
        rulerSugarAfter.initViewParam(mSugar_after, 4.0f, 30.0f, 1);//餐前 血糖
        rulerSugarAfter.setValueChangeListener(new DecimalScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value)
            {
                tvSugarAfter.setText(value + "");
                mSugar_after = value;
            }
        });

    }


}
