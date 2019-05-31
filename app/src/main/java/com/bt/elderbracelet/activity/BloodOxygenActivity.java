package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.BloodOxygen;
import com.bt.elderbracelet.entity.others.Event;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.other.TasksCompletedOxygenView;
import com.bt.elderbracelet.view.TitleView;
import com.bttow.elderbracelet.R;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class BloodOxygenActivity extends Activity {
    TitleView titleview;
    TextView tvOxygen;
    TextView tvTime;
    TasksCompletedOxygenView taskViewOxygen;
    BloodOxygen oxygen;
    private List<Integer> allOxygenNum;  //记录一次测血氧数据的过程中变化的所有数据

    private ModelDao modelDao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_oxygen);
        MyApplication.getInstance().addActivity(this);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    private void initView()
    {
        titleview = (TitleView) findViewById(R.id.titleview);
        titleview.setcolor("#B7278F");
        titleview.titleImg(R.drawable.oxygen_icon);
        titleview.setTitle(R.string.oxygen);
        titleview.right(R.string.history, new TitleView.onSetLister() {
            @Override
            public void onClick(View button)
            {
                Intent intent = new Intent(getApplicationContext(), OxygenHistoryActivity.class);
                startActivity(intent);
            }
        });
        titleview.setBack(R.drawable.steps_back, new TitleView.onBackLister() {
            @Override
            public void onClick(View button)
            {
                finish();
            }
        });

        taskViewOxygen = (TasksCompletedOxygenView) findViewById(R.id.task_view_oxygen);
        tvOxygen = (TextView) findViewById(R.id.tv_oxygen);
        tvTime = (TextView) findViewById(R.id.tv_time);
    }

    private void initData()
    {
        if (null == modelDao) {
            modelDao = new ModelDao(getApplicationContext());
        }
        if (oxygen == null) {
            oxygen = modelDao.getLastOxygen();
        }
        if(oxygen == null){
            tvOxygen.setText(String.valueOf(0));
            taskViewOxygen.setProgress(0);
            tvTime.setText("");
        }else {
            tvOxygen.setText(oxygen.getBloodOxygen());
            taskViewOxygen.setProgress(Integer.valueOf(oxygen.getBloodOxygen()));
            tvTime.setText(oxygen.getPreciseDate());
        }

    }

    public void onEventMainThread(Event event)
    {
        if (event.oxygen != null) {
            oxygen = event.oxygen;

            System.out.println(oxygen);

            int oxygenValue = Integer.valueOf(oxygen.getBloodOxygen());

            System.out.println();

            if (allOxygenNum == null) {
                allOxygenNum = new ArrayList<Integer>();
            }
            allOxygenNum.add(oxygenValue);

            initData();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //下面是计算本次血氧变化数据的平均值，然后保存到数据库中
        int amount = 0;
        if (allOxygenNum != null) {
            for (int i = 0; i < allOxygenNum.size(); i++) {
                amount += allOxygenNum.get(i);
            }
            int averageOxygen = amount / allOxygenNum.size();
            BloodOxygen oxygen = new BloodOxygen();
            oxygen.setBloodOxygen(String.valueOf(averageOxygen));
            oxygen.setPreciseDate(BaseUtils.getPreciseDate());
            modelDao.insertBloodOxygen(oxygen);

            MethodUtils.uploadBloodOxygen(BloodOxygenActivity.this, oxygen);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
