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
import com.bt.elderbracelet.tools.other.TasksCompletedView;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

import java.util.ArrayList;

/**
 * 这个界面 是从Sport表中获取数据 step
 */
public class StepActivity extends Activity {
    public static final String TAG = StepActivity.class.getSimpleName();
    TitleView titleview;   //标题栏
    TasksCompletedView tasksView; //任务完成圆形图标
    TextView tvTotalStep;   //今天完成总步数
    TextView tvCurrentStep;  //你没看错，这个也是今天完成总步数

    private ModelDao modelDao;

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
                                      int cal, int cursleeptime, int totalrunningtime, int steptime) throws RemoteException {
            if (type == 0) {
                System.out.println("运动数据来了");

                /**
                 * 这里跟如医X10Pro手环处理数据很不同
                 * 这里处理数据的逻辑是：
                 *     先判断数据库中有没有今天的数据记录，
                 *     如果有，则取出今天的数据记录，
                 *          只更新其中的step、distance和cal数据，然后将此Sport更新到原数据库
                 *          不能新建Sport,再给其step、distance和cal，然后更新到数据库，因为这样
                 *          今天的睡眠时间数据就会被覆盖掉。
                 *     如果数据库中没有今天的数据，则新建Sport,给step、distance和cal赋值，别忘了给
                 *          Date赋值，然后插入到数据库中。
                 */

                Log.v(TAG, "step = " + step);
                Log.v(TAG, "distance = " + distance);
                Log.v(TAG, "cal = " + cal);


                Sport sport = new Sport();
                boolean isExist = false;  //代表数据库中是否已经有今天的数据记录

                ArrayList<Sport> sportList = modelDao.queryAllSport();
                if (sportList != null && sportList.size() > 0) {
                    for (int i = 0; i < sportList.size(); i++) {
                        if (sportList.get(i).getDate().equals(BaseUtils.getTodayDate())) {
                            isExist = true;
                            sport = sportList.get(i);
                            break;
                        }
                    }
                }
                sport.setStep(step + "");
                sport.setDistance(distance + "");
                sport.setCalorie(cal + "");
                if (isExist) {
                    modelDao.updateSport(sport, BaseUtils.getTodayDate());
                } else {
                    sport.setDate(BaseUtils.getTodayDate());
                    modelDao.insertSport(sport);
                }

                Message msg = Message.obtain();
                msg.what = step;
                mHandler.sendMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_step);
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
        tasksView = (TasksCompletedView) findViewById(R.id.tasks_view);
        tvTotalStep = (TextView) findViewById(R.id.tv_total_step);
        tvCurrentStep = (TextView) findViewById(R.id.tv_current_step);
        titleview.setTitle(R.string.steps);
        titleview.setcolor("#85C226");
        titleview.settextcolor("#EBF8DB");
        titleview.titleImg(R.drawable.foot_icon);
        titleview.setBack(R.drawable.steps_back, new onBackLister() {
            @Override
            public void onClick(View button) {
                finish();
            }
        });
        titleview.right(R.string.history, new onSetLister() {

            @Override
            public void onClick(View button) {
                Intent intent = new Intent(getApplicationContext(), SportHistoryActivity.class);
                MyApplication.activityStuts = MyApplication.STEP_HISTORY;
                startActivity(intent);
            }
        });
    }

    private void updateUI(int step) {
        tvCurrentStep.setText(step + "步");
        tvTotalStep.setText(step + "");
        tasksView.setProgress(step * 100 / 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        callgetCurSportData();
    }


}
