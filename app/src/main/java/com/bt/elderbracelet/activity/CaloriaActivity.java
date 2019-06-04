package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.Sport;
import com.bt.elderbracelet.protocal.RemoteServiceCallback;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.other.TasksCompletedCaloriaView;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

import java.util.ArrayList;

/**
 * 这个界面 从Sport表中获取数据 koloria
 */
public class CaloriaActivity extends Activity {
    private String TAG = CaloriaActivity.class.getSimpleName();
    private TitleView titleView;    //标题栏
    private TextView tvCaloria;    //消耗的卡路里
    private ImageView iv_center;
    private ModelDao modelDao;
    private TasksCompletedCaloriaView caloriaTaskView;    //任务完成视图
    private Bitmap bitmap;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateUI(msg.arg1, msg.arg2);
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
                msg.arg1 = step;
                msg.arg2 = cal;
                mHandler.sendMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_kaloria);
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

    public void initView() {
        titleView = (TitleView) findViewById(R.id.titleview);
        tvCaloria = (TextView) findViewById(R.id.tv_caloria);
        caloriaTaskView = (TasksCompletedCaloriaView) findViewById(R.id.tasks_caloria_view);
        iv_center = (ImageView) findViewById(R.id.iv_center);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.caloria_center_icon);

        titleView.setTitle(R.string.kaloria2);
        titleView.setcolor("#E77843");
        titleView.titleImg(R.drawable.kalor);
        titleView.settextcolor("#ffffff");
        titleView.setBack(R.drawable.steps_back, new onBackLister() {

            @Override
            public void onClick(View button) {
                finish();
            }
        });
        titleView.right(R.string.history, new onSetLister() {

            @Override
            public void onClick(View button) {
                Intent intent = new Intent(getApplicationContext(), SportHistoryActivity.class);
                MyApplication.activityStuts = 2;
                startActivity(intent);
            }
        });
    }


    private void updateUI(int step, int cal) {

        tvCaloria.setText(cal + "卡");
        int progress = (step * 100 / 5000);    //这个完成百分比 就是 步数完成百分比
        caloriaTaskView.setProgress(progress);

        //获取手机屏幕宽度:单位像素px
        DisplayMetrics dm = getResources().getDisplayMetrics();
        LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(0, 0);
        para.leftMargin = 35;
        para.height = bitmap.getHeight();
        float current_progress = ((float) progress / 100) * dm.widthPixels;//总长度 1440
        if (current_progress > dm.widthPixels) {
            current_progress = dm.widthPixels - 90;
        }
        para.width = (int) current_progress;
        if (current_progress == 0) {
            //	iv_center.setLayoutParams(para);
            iv_center.setVisibility(View.INVISIBLE);
        } else {
            iv_center.setLayoutParams(para);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        callgetCurSportData();
    }

}
