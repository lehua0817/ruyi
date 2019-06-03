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
import com.bt.elderbracelet.tools.other.TasksCompletedDistanceView;
import com.bt.elderbracelet.view.TitleView;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;

//这个界面也是从 Sport表中获取 数据
//运动距离 直接获取Sport表的sportDistance数据
//而任务完成量 根据Sport的步距，除于目标总步数 来获取
public class DistanceActivity extends Activity {
    private static final String TAG = DistanceActivity.class.getSimpleName();

    TitleView titleview;   //标题栏
    TasksCompletedDistanceView tasksView;   //任务完成视图
    TextView tvTotalDistance;    //今天总运动距离
    TextView tv_current_distance;  //也是今天总运动距离
    private ModelDao modelDao;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateUI(msg.what);
        }
    };


    private IRemoteService mService;
    private IServiceCallback mServiceCallback = new RemoteServiceCallback() {

        @Override
        public void onGetCurSportData(int type, long timestamp, int step, int distance,
                                      int cal, int cursleeptime, int totalrunningtime, int steptime) throws RemoteException {
            if (type == 0) {
                System.out.println("运动数据来了");

                Log.v(TAG, "step = " + step);
                Log.v(TAG, "distance = " + distance);
                Log.v(TAG, "cal = " + cal);
                Log.v(TAG, "cursleeptime = " + cursleeptime);
                Log.v(TAG, "totalrunningtime = " + totalrunningtime);
                Log.v(TAG, "steptime = " + steptime);

                int _step = step;
                int _distance = distance;
                int _calories = cal;
                int _sportTime = 100;

                Sport sport = new Sport();
                sport.setDate(BaseUtils.getTodayDate());
                sport.setStep(String.valueOf(_step));
                sport.setDistance(String.valueOf(_distance));
                sport.setCalorie(String.valueOf(_calories));
                sport.setSportTime(String.valueOf(_sportTime));

                boolean isExist = false;  //代表数据库中是否已经有今天的数据记录

                ArrayList<Sport> sportList = modelDao.queryAllSport();
                if (sportList != null && sportList.size() > 0) {
                    for (int i = 0; i < sportList.size(); i++) {
                        if (sportList.get(i).getDate().equals(BaseUtils.getTodayDate())) {
                            sport.setId(sportList.get(i).getId());
                            isExist = true;
                            break;
                        }
                    }
                }
                if (isExist) {
                    modelDao.updateSport(sport, BaseUtils.getTodayDate());
                } else {
                    modelDao.insertSport(sport);
                }
                Message msg = Message.obtain();
                msg.what = distance;
                mHandler.sendMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_distance);
        MyApplication.getInstance().addActivity(this);
        modelDao = new ModelDao(getApplicationContext());
        mService = MyApplication.remoteService;
        try {
            mService.registerCallback(mServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        initView();
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

    private void initView() {
        titleview = (TitleView) findViewById(R.id.titleview);
        tasksView = (TasksCompletedDistanceView) findViewById(R.id.tasks_view);
        tvTotalDistance = (TextView) findViewById(R.id.tv_total_distance);
        tv_current_distance = (TextView) findViewById(R.id.tv_current_distance);
        titleview.setTitle(R.string.sport_distance);
        titleview.setcolor("#0093DE");
        titleview.titleImg(R.drawable.distance_icon);
        titleview.settextcolor("#EBF8DB");
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
                MyApplication.activityStuts = MyApplication.DISTANCE_HISTORY;
                startActivity(intent);
            }
        });
    }


    private void updateUI(int distance) {
        // 手环显示距离时，采用向下取整，比如37步，手环显示0.03
        // 这里显示要保持同步
        distance = distance - distance % 10;
        DecimalFormat df = new DecimalFormat("0.00");
        double distanceKm = (double) distance / 1000;
        tvTotalDistance.setText(df.format(distanceKm) + " km");
        tv_current_distance.setText(df.format(distanceKm) + " km");
        tasksView.setProgress(distance * 100 / 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        callgetCurSportData();
    }

}
