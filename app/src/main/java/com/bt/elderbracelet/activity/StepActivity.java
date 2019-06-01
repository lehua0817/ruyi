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
import com.bt.elderbracelet.entity.Sport;
import com.bt.elderbracelet.entity.others.Event;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.other.TasksCompletedView;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 这个界面 是从Sport表中获取数据 step
 */
public class StepActivity extends Activity {
    TitleView titleview;   //标题栏
    TasksCompletedView tasksView; //任务完成圆形图标
    TextView tvTotalStep;   //今天完成总步数
    TextView tvCurrentStep;  //你没看错，这个也是今天完成总步数

    private ModelDao modelDao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_step);
        MyApplication.getInstance().addActivity(this);
        EventBus.getDefault().register(this);
        // TODO: 2019/6/1
//        BleService.sendCommand(OrderData.getCommonOrder(OrderData.SYN_SPORT_ORDER));

        initView();
    }

    private void initView()
    {
        titleview = (TitleView) findViewById(R.id.titleview);
        tasksView = (TasksCompletedView) findViewById(R.id.tasks_view);
        tvTotalStep = (TextView) findViewById(R.id.tv_total_step);
        tvCurrentStep = (TextView) findViewById(R.id.tv_current_step);
    }

    private void initData()
    {
        if (modelDao == null) {
            modelDao = new ModelDao(getApplicationContext());
        }
        ArrayList<Sport> sportList = modelDao.queryAllSport();

        if (sportList.size() > 0) {
            for (int i = 0; i < sportList.size(); i++) {
                if (sportList.get(i).getDate().equals(BaseUtils.getTodayDate())) {    //这里是从本地数据库获取数据，因为手环运动数据改变时，手环将数据传给手机
                    //手机先将数据保存到本地数据库
                    //从数据库中获取今天完成的总步数，只有一条最新的记录
                    //手环每天都在运动，自然而然要向是手机中传入很多次运动记录，但每一次上传时
                    //都会先检查一下 数据库中是否已经有今天的记录，如果有，则删除前一条记录，保存最先的记录
                    //这样就保证了数据库中每天的运动记录只有唯一的一条，而且是最新的记录
                    String strSteps = sportList.get(i).getStep();
                    tvCurrentStep.setText(strSteps + "步");
                    tvTotalStep.setText(strSteps);
                    if (!TextUtils.isEmpty(strSteps)) {
                        tasksView.setProgress((Integer.parseInt(strSteps) * 100 / 5000));
                    }
                    break;
                } else {
                    tvCurrentStep.setText(0 + "步");
                }
            }
        }else {
            System.out.println("Sport数据库中无数据");
        }
        titleview.setTitle(R.string.steps);
        titleview.setcolor("#85C226");
        titleview.settextcolor("#EBF8DB");
        titleview.titleImg(R.drawable.foot_icon);
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
                MyApplication.activityStuts = MyApplication.STEP_HISTORY;
                startActivity(intent);
            }
        });
    }

    /**
     * 只要一打开SportActivity 界面，就注册了事件监听器
     * 随时准备更新界面，接收最新运动数据
     */
    public void onEventMainThread(Event event)
    {
        if(event.update_step){
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
