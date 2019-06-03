package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.Sleep;
import com.bt.elderbracelet.entity.others.Event;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.other.TasksCompletedSleepView;
import com.bt.elderbracelet.view.HorizontalProgressBarWithNumber;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class SleepActivity extends Activity {
    TitleView titleview;    //标题栏
    TasksCompletedSleepView sleepTaskView;   //睡眠分析比例视图
    TextView tvSleepDeep;   //深睡时间
    TextView tvSleepLow;   //浅睡时间
    TextView tvSleepTotal;    //睡眠总时间
    TextView tvRefreshSleep;   //刷新按钮

    int nSleepDeep_hour = 0;
    int nSleepDeep_minite = 0;
    int nSleepLow_hour = 0;
    int nSleepLow_minite = 0;
    int nSleepDeep, nSleepLow, nSleepTime;
    int nSleepTotal_hour = 0;
    int nSleepTotal_minite = 0;
    private ModelDao modelDao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_sleep);
        MyApplication.getInstance().addActivity(this);
        EventBus.getDefault().register(this);
        initView();
    }


    private void initView()
    {
        titleview = (TitleView) findViewById(R.id.titleview);
        titleview.setTitle(R.string.sleep);

        titleview.setcolor("#76C5F0");
        titleview.settextcolor("#ffffff");
        titleview.titleImg(R.drawable.sleeptitleimg);

        sleepTaskView = (TasksCompletedSleepView) findViewById(R.id.sleep_view);
        tvSleepDeep = (TextView) findViewById(R.id.tv_sleep_deep);
        tvSleepLow = (TextView) findViewById(R.id.tv_sleep_low);
        tvSleepTotal = (TextView) findViewById(R.id.tv_sleep_total);
        tvRefreshSleep = (TextView) findViewById(R.id.tv_refresh_sleep);

        HorizontalProgressBarWithNumber.setDEFAULT_TEXT_COLOR(0XFFfe4620);
        HorizontalProgressBarWithNumber.setDEFAULT_TEXT_COLOR2(0XFF1a211a);

        tvRefreshSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (MyApplication.isConnected) {
                    // TODO: 2019/6/1
//                    BleService.sendCommand(OrderData.getCommonOrder(OrderData.GET_SLEEP_BIG_DATA));   //手机向手环 获取当天睡眠数据 的指令  4bytes
                } else {
                    MethodUtils.showToast(getApplicationContext(), "请先连接蓝牙");
                }
            }
        });

        titleview.setBack(R.drawable.steps_back, new onBackLister() {

            @Override
            public void onClick(View button)
            {
                finish();
            }
        });
        titleview.right(R.string.history, new onSetLister() {

            @Override
            public void onClick(View button)
            {
                Intent intent = new Intent(getApplicationContext(), SleepHistoryActivity.class);
                startActivity(intent);
            }
        });
    }


    private void initData()
    {
        SystemClock.sleep(1000);
        if (modelDao == null) {
            modelDao = new ModelDao(getApplicationContext());
        }
        ArrayList<Sleep> sleepList = modelDao.queryAllSleep();


        if (sleepList.size() > 0) {
            for (int i = 0; i < sleepList.size(); i++) {
                if (sleepList.get(i).getDate().equals(BaseUtils.getYesterdayDate())) {
                    Sleep sleep = sleepList.get(i);

                    System.out.println("sleep  :" + sleep);

                    nSleepTime = Integer.valueOf(sleep.getSleepTime());      //注意：总睡眠时间
                    nSleepTotal_hour = nSleepTime / 60;
                    nSleepTotal_minite = nSleepTime % 60;
                    tvSleepTotal.setText(BaseUtils.timeConversion(nSleepTotal_hour, nSleepTotal_minite));

                    nSleepDeep = Integer.valueOf(sleep.getSleepDeepTime());    //深睡了 多少 分钟
                    nSleepDeep_hour = nSleepDeep / 60;          //深睡了几个小时
                    nSleepDeep_minite = nSleepDeep % 60;        //深睡了多少分钟
                    tvSleepDeep.setText(BaseUtils.timeConversion(nSleepDeep_hour, nSleepDeep_minite));

                    nSleepLow = nSleepTime - nSleepDeep;     //浅睡了多少分钟
                    nSleepLow_hour = nSleepLow / 60;
                    nSleepLow_minite = nSleepLow % 60;
                    tvSleepLow.setText(BaseUtils.timeConversion(nSleepLow_hour, nSleepLow_minite));
                    break;
                } else {
                    tvSleepDeep.setText("00:00");
                    tvSleepLow.setText("00:00");
                    tvSleepTotal.setText("00:00");
                }
            }
        }
    }

    public void onEventMainThread(Event Event)
    {
        if (Event.update_sleep) {
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
