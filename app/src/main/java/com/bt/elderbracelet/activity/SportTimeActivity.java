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
import com.bt.elderbracelet.entity.Sport;
import com.bt.elderbracelet.protocal.RemoteServiceCallback;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.other.TasksCompletedSportTimeView;
import com.bt.elderbracelet.view.TitleView;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

import java.util.ArrayList;

/**
 * 这个界面 是从Sport表中获取 SportTime数据
 */
public class SportTimeActivity extends Activity {
    public static final String TAG = SportTimeActivity.class.getSimpleName();
    TitleView titleview; //标题栏
    TasksCompletedSportTimeView tasksSportTimeview;  //任务完成视图
    TextView tvSportTime;  //运动时间
    private ModelDao model;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            System.out.println("msg.what = " + msg.what);
            updateUI(msg.what);
        }
    };

    private IRemoteService mService;
    private IServiceCallback mServiceCallback = new RemoteServiceCallback() {

        @Override
        public void onGetCurSportData(int type, long timestamp, int step, int distance,
                                      int cal, int cursleeptime, int runningtime, int steptime) throws RemoteException {
            if (type == 1) {
                System.out.println("运动数据来了");

                Log.v(TAG, "runningtime = " + runningtime);
                Log.v(TAG, "steptime = " + steptime);

                // 运动时间 = 跑步时间+走路时间,并且将时间单位从秒化为分钟
                int sportTime = (runningtime + steptime) / 60;

                Sport sport = new Sport();

                boolean isExist = false;  //代表数据库中是否已经有今天的数据记录

                ArrayList<Sport> sportList = model.queryAllSport();
                if (sportList != null && sportList.size() > 0) {
                    for (int i = 0; i < sportList.size(); i++) {
                        if (sportList.get(i).getDate().equals(BaseUtils.getTodayDate())) {
                            isExist = true;
                            sport = sportList.get(i);
                            break;
                        }
                    }
                }
                sport.setSportTime(sportTime + "");
                if (isExist) {
                    model.updateSport(sport, BaseUtils.getTodayDate());
                } else {
                    sport.setDate(BaseUtils.getTodayDate());
                    model.insertSport(sport);
                }

                Message msg = Message.obtain();
                msg.what = sportTime;
                mHandler.sendMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_sport_time);
        MyApplication.getInstance().addActivity(this);
        model = new ModelDao(getApplicationContext());
        mService = MyApplication.remoteService;
        try {
            mService.registerCallback(mServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        initView();
    }

    private void initView() {
        titleview = (TitleView) findViewById(R.id.titleview);
        titleview.setTitle(R.string.sport_time);
        titleview.setcolor("#BAB3D5");
        titleview.titleImg(R.drawable.sport_titleimg);
        titleview.settextcolor("#ffffff");

        tasksSportTimeview = (TasksCompletedSportTimeView) findViewById(R.id.tasks_sportTimeview);
        tvSportTime = (TextView) findViewById(R.id.tv_sportTime);
        titleview.setBack(R.drawable.steps_back, new TitleView.onBackLister() {

            @Override
            public void onClick(View button) {
                finish();
            }
        });
        titleview.right(R.string.history, new TitleView.onSetLister() {

            @Override
            public void onClick(View button) {
                Intent intent = new Intent(getApplicationContext(), SportHistoryActivity.class);
                MyApplication.activityStuts = MyApplication.SPORT_TIME_HISTORY;
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        callgetCurSportData();
    }

    private void updateUI(int sportTime) {
        int sportHour = sportTime / 60;
        int sportMinite = sportTime % 60;
        tvSportTime.setText(BaseUtils.timeConversion(sportHour, sportMinite));
        int progress = (sportTime * 100) / 120;
        if (progress >= 100) {
            tasksSportTimeview.setProgress(100);
        } else {
            tasksSportTimeview.setProgress(progress);
        }
    }

    private void callgetCurSportData() {
        if (mService != null) {
            try {
                mService.getCurSportData();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

}
