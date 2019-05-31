package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bonten.ble.application.MyApplication;
import com.bonten.ble.servise.BleService;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.Sport;
import com.bt.elderbracelet.entity.others.Event;
import com.bt.elderbracelet.protocal.OrderData;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.other.TasksCompletedDistanceView;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_distance);
        MyApplication.getInstance().addActivity(this);
        EventBus.getDefault().register(this);
        BleService.sendCommand(OrderData.getCommonOrder(OrderData.SYN_SPORT_ORDER));
        initView();
    }

    private void initView() {
        titleview = (TitleView) findViewById(R.id.titleview);
        tasksView = (TasksCompletedDistanceView) findViewById(R.id.tasks_view);
        tvTotalDistance = (TextView) findViewById(R.id.tv_total_distance);
        tv_current_distance = (TextView) findViewById(R.id.tv_current_distance);
    }

    private void initData() {
        if (modelDao == null) {
            modelDao = new ModelDao(getApplicationContext());
        }
        ArrayList<Sport> sportList = modelDao.queryAllSport();
        if (sportList.size() > 0) {
            for (int i = 0; i < sportList.size(); i++) {
                if (sportList.get(i).getDate().equals(BaseUtils.getTodayDate())) {
                    // 手环显示距离时，采用向下取整，比如37步，手环显示0.03
                    // 这里显示要保持同步
                    String _distance = sportList.get(i).getDistance();
                    if (!TextUtils.isEmpty(_distance)) {
                        int distance = Integer.valueOf(_distance);
                        distance = distance - distance % 10;
                        Log.v(TAG, "distance = " + distance);

                        DecimalFormat df = new DecimalFormat("0.00");
                        double distanceKm = (double) distance / 1000;
                        tvTotalDistance.setText(df.format(distanceKm) + " km");
                        tv_current_distance.setText(df.format(distanceKm) + " km");
                        tasksView.setProgress(distance * 100 / 5000);
                    } else {
                        tv_current_distance.setText(0 + " km");   //这是代表有那天记录，但是记录栏为空，则设置为0
                    }
                    break;
                }
            }
        }
        titleview.setTitle(R.string.sport_distance);
        titleview.setcolor("#0093DE");
        titleview.titleImg(R.drawable.distance_icon);
        titleview.settextcolor("#EBF8DB");
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
                MyApplication.activityStuts = MyApplication.DISTANCE_HISTORY;
                startActivity(intent);
            }
        });
    }


    public void onEventMainThread(Event event) {
        if (event.update_distance) {
            initData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
