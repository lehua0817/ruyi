package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.BloodPressure;
import com.bt.elderbracelet.entity.others.Event;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;

import java.util.List;

import de.greenrobot.event.EventBus;


public class BloodPressureActivity extends Activity {
    TitleView titleview;
    TextView tvdate;
    TextView tvHighPressure;
    TextView tvLowPressure;

    private ModelDao modelDao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_pressure);
        MyApplication.getInstance().addActivity(this);
        EventBus.getDefault().register(this);
        initView();

    }

    private void initView()
    {
        titleview = (TitleView) findViewById(R.id.titleview);
        titleview.setcolor("#F8C301");
        titleview.titleImg(R.drawable.pressure_icon);
        titleview.setTitle(R.string.pressure);
        titleview.right(R.string.history, new onSetLister() {
            @Override
            public void onClick(View button)
            {
                Intent intent = new Intent(getApplicationContext(), PressureHistoryActivity.class);
                intent.putExtra("dataType", MyApplication.PRESSURE_TYPE);
                startActivity(intent);
            }
        });

        tvdate = (TextView) findViewById(R.id.tv_date);

        tvHighPressure = (TextView) findViewById(R.id.tv_high_pressure);
        tvLowPressure = (TextView) findViewById(R.id.tv_low_pressure);
        tvHighPressure.setText("0.0");
        tvLowPressure.setText("0.0");

    }

    private void initData()
    {
        if (null == modelDao) {
            modelDao = new ModelDao(getApplicationContext());
        }
        List<BloodPressure> pressureList = modelDao.queryAllBloodPressure();
        //删除大于10条的数据
        if (pressureList.size() > 20) {
            for (int i = 0; i < pressureList.size() - 20; i++) {
                modelDao.deleteBloodPressure(pressureList.get(i).getId());
                pressureList.remove(i);
            }
        }
        if (pressureList.size() > 0) {
            BloodPressure lastPressure = pressureList.get(pressureList.size() - 1);
            String lastDate = lastPressure.getPreciseDate();
            if (!TextUtils.isEmpty(lastDate)) {
                //如果数据库中最后一条记录 保存的是当天的记录，则显示数据库中数据
                //如果 不是当天的记录，说明那天还没有设置过 血压数据，则显示默认值
                if (lastDate.substring(0, 10).equals(BaseUtils.getTodayDate())) {
                    tvHighPressure.setText(lastPressure.getBloodPressureHigh());
                    tvLowPressure.setText(lastPressure.getBloodPressureLow());
                    tvdate.setText(lastPressure.getPreciseDate());
                }
            }
        }
        titleview.setBack(R.drawable.steps_back, new onBackLister() {

            @Override
            public void onClick(View button)
            {
                finish();
            }
        });
    }

    public void onEventMainThread(Event event)
    {
        if (event.pressure != null) {
            initData();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initData();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
