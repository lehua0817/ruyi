package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.bt.elderbracelet.tools.other.TasksCompletedSportTimeView;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;
import java.util.ArrayList;
import de.greenrobot.event.EventBus;

/**
 * 这个界面 是从Sport表中获取 SportTime数据
 */
public class SportTimeActivity extends Activity {
    TitleView titleview; //标题栏
    TasksCompletedSportTimeView tasksSportTimeview;  //任务完成视图
    TextView tvSportTime;  //运动时间
    int weight;
    private ModelDao model;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_sport_time);
        MyApplication.getInstance().addActivity(this);
        EventBus.getDefault().register(this);
        BleService.sendCommand(OrderData.getCommonOrder(OrderData.SYN_SPORT_ORDER));
        initView();
    }

    private void initView()
    {
        titleview = (TitleView) findViewById(R.id.titleview);
        titleview.setTitle(R.string.sport_time);
        titleview.setcolor("#BAB3D5");
        titleview.titleImg(R.drawable.sport_titleimg);
        titleview.settextcolor("#ffffff");

        tasksSportTimeview = (TasksCompletedSportTimeView) findViewById(R.id.tasks_sportTimeview);
        tvSportTime = (TextView) findViewById(R.id.tv_sportTime);
    }

    private void initData()
    {
        model = new ModelDao(getApplicationContext());
        ArrayList<Sport> sportList = model.queryAllSport();

        if (sportList.size() > 0) {
            for (int i = 0; i < sportList.size(); i++) {
                if (sportList.get(i).getDate().equals(BaseUtils.getTodayDate())) {
                    if (!TextUtils.isEmpty(sportList.get(i).getSportTime())) {
                        Sport sport = sportList.get(i);
                        int sportHour = Integer.parseInt(sport.getSportTime()) / 60;
                        int sportMinite = Integer.parseInt(sport.getSportTime()) % 60;
                        tvSportTime.setText(BaseUtils.timeConversion(sportHour, sportMinite));
                        int progress =  (Integer.parseInt(sport.getSportTime())*100) /120 ;
                        if(progress>=100){
                            tasksSportTimeview.setProgress(100);
                        }else {
                            tasksSportTimeview.setProgress(progress);
                        }
                    }
                    break;
                } else {
                    tvSportTime.setText(0 + "小时");
                    tasksSportTimeview.setProgress(0);
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
        titleview.right(R.string.history, new onSetLister() {

            @Override
            public void onClick(View button)
            {
                Intent intent = new Intent(getApplicationContext(), SportHistoryActivity.class);
                MyApplication.activityStuts = MyApplication.SPORT_TIME_HISTORY;
                startActivity(intent);
            }
        });
    }

    public void onEventMainThread(Event event) {
        if(event.update_sport_time){
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
