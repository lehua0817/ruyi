package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.Sleep;
import com.bt.elderbracelet.protocal.RemoteServiceCallback;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.other.TasksCompletedSleepView;
import com.bt.elderbracelet.view.HorizontalProgressBarWithNumber;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

import java.util.ArrayList;

public class SleepActivity extends Activity {
    public static final String TAG = SleepActivity.class.getSimpleName();
    TitleView titleview;    //标题栏
    TasksCompletedSleepView sleepTaskView;   //睡眠分析比例视图
    TextView tvSleepDeep;   //深睡时间
    TextView tvSleepLow;   //浅睡时间
    TextView tvSleepTotal;    //睡眠总时间
    TextView tvRefreshSleep;   //刷新按钮

    private ModelDao modelDao;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateUI(msg.arg1, msg.arg2);
        }
    };


    private IRemoteService mService;
    private int totalSleepCount = 0; //  完整睡眠时间
    private int deepSleepCount = 0;  //  深度睡眠时间

    private IServiceCallback mServiceCallback = new RemoteServiceCallback() {
        @Override
        public void onGetDataByDay(int type, long timestamp, int sleepQuality, int heartrate)
                throws RemoteException {
            Log.v("onGetDataByDay", "睡眠数据来了");
            if (type == 2) {
                totalSleepCount++;
                if (sleepQuality >= 80) {
                    deepSleepCount++;
                }
            }
        }

        @Override
        public void onGetDataByDayEnd(int type, long timestamp) throws RemoteException {
            Log.v("onGetDataByDayEnd", "睡眠大数据同步成功");

            boolean isExist = false; //代表数据库中是否已经有昨天的睡眠数据
            ArrayList<Sleep> sleepList = modelDao.queryAllSleep();
            for (int i = 0; i < sleepList.size(); i++) {
                if (sleepList.get(i).getDate().equals(BaseUtils.getYesterdayDate())) {
                    isExist = true;
                    break;
                }
            }

            // 睡眠数据，不存在更新的问题，每次获取都一样
            if (!isExist) {
                Sleep sleep = new Sleep();
                sleep.setDate(BaseUtils.getYesterdayDate());
                sleep.setSleepTime(totalSleepCount + "");
                sleep.setSleepDeepTime(deepSleepCount + "");
                modelDao.insertSleep(sleep);
            }

            Message msg = Message.obtain();
            msg.arg1 = totalSleepCount;
            msg.arg2 = deepSleepCount;
            mHandler.sendMessage(msg);

            totalSleepCount = 0;
            deepSleepCount = 0;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_sleep);
        MyApplication.getInstance().addActivity(this);
        modelDao = new ModelDao(getApplicationContext());
        mService = MyApplication.remoteService;
        try {
            mService.registerCallback(mServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        initView();
        callRemoteGetSleepData();
        MethodUtils.showLoadingDialog(this);
    }


    private void initView() {
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
            public void onClick(View v) {
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
            public void onClick(View button) {
                finish();
            }
        });
        titleview.right(R.string.history, new onSetLister() {

            @Override
            public void onClick(View button) {
                Intent intent = new Intent(getApplicationContext(), SleepHistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateUI(int totalSleepCount, int deepSleepCount) {
        MethodUtils.cancelLoadingDialog();
        int totalSleep_hour = totalSleepCount / 60;
        int totalSleep_minute = totalSleepCount % 60;
        tvSleepTotal.setText(BaseUtils.timeConversion(totalSleep_hour, totalSleep_minute));

        int deepSleep_hour = deepSleepCount / 60;          //深睡了几个小时
        int deepSleep_minute = deepSleepCount % 60;        //深睡了多少分钟
        tvSleepDeep.setText(BaseUtils.timeConversion(deepSleep_hour, deepSleep_minute));

        int lowSleepCount = totalSleepCount - deepSleepCount;     //浅睡了多少分钟
        int lowSleep_hour = lowSleepCount / 60;
        int lowSleep_minute = lowSleepCount % 60;
        tvSleepLow.setText(BaseUtils.timeConversion(lowSleep_hour, lowSleep_minute));
    }

    private void callRemoteGetSleepData() {
        Log.i(TAG, "callRemoteGetData");
        if (mService != null) {
            try {
                // 1 代表获取睡眠数据
                // 0 代表获取今天0点到早上的睡眠数据，也就是我们习惯中的昨天的睡眠数据
                mService.getDataByDay(1, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MethodUtils.cancelLoadingDialog();
    }
}
