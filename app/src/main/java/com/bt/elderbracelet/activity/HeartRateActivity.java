package com.bt.elderbracelet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.HeartRate;
import com.bt.elderbracelet.protocal.RemoteServiceCallback;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

import java.util.ArrayList;
import java.util.List;

import mpchart.notimportant.DemoBase;


public class HeartRateActivity extends DemoBase {
    public static final String TAG = HeartRateActivity.class.getSimpleName();

    private TitleView titleView;
    private FrameLayout frameGraph;
    private TextView tvheartRate, tvheartRateTime;

    private List<Entry> sourceData;
    private LineData lineData = null;
    private LineDataSet lineDataSet = null;
    private LineChart mLineChart = null;
    private List<Integer> allHeartRateNum;  //记录一次测心率的过程中变化的所有数据

    private ModelDao modelDao;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (allHeartRateNum == null) {
                allHeartRateNum = new ArrayList<Integer>();
            }

            allHeartRateNum.add(msg.what);
            updateUI(msg.what);
        }
    };


    private IRemoteService mService;
    private IServiceCallback mServiceCallback = new RemoteServiceCallback() {

        @Override
        public void onReceiveSensorData(final int heartrate, int pressureHigh, int pressureLow, int oxygen,
                                        int tired) throws RemoteException {
            // 心率, 高血压, 低血压, 血氧, 疲劳值);
            Log.v("onReceiveSensorData", "result:" + heartrate + " , " + pressureHigh + " , " + pressureLow + " , " + oxygen + " , " + tired);

            if (heartrate != 0) {
                Log.v(TAG, "APP接收到心率数据：" + heartrate);
                Message msg = Message.obtain();
                msg.what = heartrate;
                mHandler.sendMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_heart_rate);
        MyApplication.getInstance().addActivity(this);
        modelDao = new ModelDao(getApplicationContext());
        mService = MyApplication.remoteService;
        try {
            mService.registerCallback(mServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        initView();
        initChartData();
        callRemoteOpenBlood(true);
    }

    private void initView() {
        //初始化标题栏
        titleView = (TitleView) findViewById(R.id.titleview);       //初始化标题栏
        titleView.setTitle(R.string.heart_rate);
        titleView.setcolor("#F09BA0");
        titleView.settextcolor("#ffffff");
        titleView.titleImg(R.drawable.heart_rate_top_icon);
        titleView.setBack(R.drawable.steps_back, new onBackLister() {
            @Override
            public void onClick(View button) {
                finish();
            }
        });
        titleView.right(R.string.history, new onSetLister() {
            @Override
            public void onClick(View button) {
                Intent intent = new Intent(getApplicationContext(), HeartRateHistoryActivity.class);
                startActivity(intent);
            }
        });

        //初始化 图表视图
        frameGraph = (FrameLayout) findViewById(R.id.frame_graph);

        mLineChart = new LineChart(HeartRateActivity.this);
        mLineChart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mLineChart.setDescription("");
        mLineChart.setMaxVisibleValueCount(20);
        mLineChart.setTouchEnabled(true);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);
        mLineChart.setPinchZoom(true);
        mLineChart.setDrawGridBackground(true);
        mLineChart.setBorderColor(getResources().getColor(R.color.step_color));

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(5);
        xAxis.setTextColor(R.color.step_color);
        xAxis.setTextSize(15);
        xAxis.setLabelsToSkip(0);

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(40f);  //设置图表中最高值的顶部间距
        leftAxis.setAxisMinValue(50f);
        leftAxis.setAxisMaxValue(120f);
        leftAxis.setStartAtZero(false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setTextColor(R.color.step_color);
        leftAxis.setTextSize(15);

        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend l = mLineChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(12f);
        l.setTextSize(15f);
        l.setXEntrySpace(6f);

        mLineChart.animateX(2500);

        frameGraph.addView(mLineChart);

        //初始化 心率数值和时间
        tvheartRate = (TextView) findViewById(R.id.tv_heartRate);              //心率数值
        tvheartRateTime = (TextView) findViewById(R.id.tv_time);         //测试心率的时间

    }

    private void initChartData() {

        if (sourceData == null) {
            sourceData = new ArrayList<>();
        }
        lineDataSet = new LineDataSet(sourceData, "心率数据表");
        lineDataSet.setDrawValues(false);
        lineDataSet.setCircleSize(3);
        lineDataSet.setCircleColor(getResources().getColor(R.color.heartRate_bg));
        lineDataSet.setLineWidth(3);
        lineDataSet.setColor(getResources().getColor(R.color.heartRate_bg));

        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setHighLightColor(R.color.fuchsia);
        lineDataSet.setHighlightLineWidth(2);
        lineDataSet.setDrawFilled(false);

        ArrayList<String> xValues = new ArrayList<>();
        lineData = new LineData(xValues, lineDataSet);
        mLineChart.setData(lineData);
    }


    @Override
    protected void onStop() {
        super.onStop();
        //下面是计算本次心率变化数据的平均值，然后保存到数据库中
        int amount = 0;
        if (allHeartRateNum != null) {
            for (int i = 0; i < allHeartRateNum.size(); i++) {
                amount += allHeartRateNum.get(i);
            }
            int averageRate = amount / allHeartRateNum.size();
            HeartRate heartRate = new HeartRate();
            heartRate.setHeartRate(String.valueOf(averageRate));
            heartRate.setPreciseDate(BaseUtils.getPreciseDate());
            modelDao.insertHeartRate(heartRate);
        }
    }

    private void updateUI(int heartRate) {
        Log.v(TAG, "APP接收到心率数据：" + heartRate);
        tvheartRate.setText(heartRate + "");
//        tvheartRateTime.setText(rate.getPreciseDate());

        lineData = mLineChart.getData();
        lineDataSet = lineData.getDataSetByIndex(0);
        int count = lineDataSet.getEntryCount();
        Entry entry = new Entry(heartRate, count);
        lineData.addXValue("");
        lineData.addEntry(entry, 0);

        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();
    }

    private void callRemoteOpenBlood(boolean enable) {
        if (mService != null) {
            try {
                mService.setBloodPressureMode(enable);
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
        callRemoteOpenBlood(false);
        if (mLineChart != null) {
            mLineChart = null;
        }
    }

}
