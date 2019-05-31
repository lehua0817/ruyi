package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bonten.ble.application.MyApplication;
import com.bonten.ble.servise.BleService;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.Sport;
import com.bt.elderbracelet.entity.others.Event;
import com.bt.elderbracelet.protocal.OrderData;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.other.TasksCompletedCaloriaView;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_kaloria);
        MyApplication.getInstance().addActivity(this);
        EventBus.getDefault().register(this);

        BleService.sendCommand(OrderData.getCommonOrder(OrderData.SYN_SPORT_ORDER));
        initView();
    }

    public void initView() {
        modelDao = new ModelDao(getApplicationContext());
        titleView = (TitleView) findViewById(R.id.titleview);
        tvCaloria = (TextView) findViewById(R.id.tv_caloria);
        caloriaTaskView = (TasksCompletedCaloriaView) findViewById(R.id.tasks_caloria_view);
        iv_center = (ImageView) findViewById(R.id.iv_center);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.caloria_center_icon);

    }

    /**
     * 这个方法的作用是 初始化显示界面数据 所有的界面数据 也是从数据库中直接获取
     */
    private void initData() {
        ArrayList<Sport> sportList = modelDao.queryAllSport();
        if (sportList.size() > 0) {
            for (int i = 0; i < sportList.size(); i++) {
                if (sportList.get(i).getDate().equals(BaseUtils.getTodayDate())) {
                    if (!TextUtils.isEmpty(sportList.get(i).getCalorie())) {

                        tvCaloria.setText(sportList.get(i).getCalorie() + "卡");
                        String step = sportList.get(i).getStep();
                        if (!TextUtils.isEmpty(step)) {
                            int progress = (Integer.parseInt(step) * 100 / 5000);    //这个完成百分比 就是 步数完成百分比
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
                            Log.e(TAG, "界面宽度*************" + dm.widthPixels + "实际顶部进度条**********" + (progress / 100) * dm.widthPixels);
                            para.width = (int) current_progress;
                            if (current_progress == 0) {
                                //	iv_center.setLayoutParams(para);
                                iv_center.setVisibility(View.INVISIBLE);
                            } else {
                                iv_center.setLayoutParams(para);
                            }
                        }
                    }
                    break;
                } else {
                    tvCaloria.setText(0 + "卡");
                }
            }

        }
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

    /**
     * 随时准备 接收手环传递过来的数据 更新界面
     */
    public void onEventMainThread(Event event) {
        if(event.update_caloria){
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
