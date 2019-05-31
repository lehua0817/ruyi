package com.bt.elderbracelet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.HeartRate;
import com.bt.elderbracelet.entity.others.Event;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
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

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import mpchart.notimportant.DemoBase;


public class HeartRateActivity extends DemoBase {

    private TitleView titleView;
    private FrameLayout frameGraph;
    private TextView tvheartRate, tvheartRateTime;

    private ModelDao modelDao;
    private List<Entry> sourceData;
    private LineData lineData = null;
    private LineDataSet lineDataSet = null;
    private LineChart mLineChart = null;
    private HeartRate rate = null;
    private List<Integer> allHeartRateNum;  //记录一次测心率的过程中变化的所有数据


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_heart_rate);
        MyApplication.getInstance().addActivity(this);
        EventBus.getDefault().register(this);
        initView();
        initChartData();
        initData();
    }

    private void initView()
    {
        //初始化标题栏
        titleView = (TitleView) findViewById(R.id.titleview);       //初始化标题栏
        titleView.setTitle(R.string.heart_rate);
        titleView.setcolor("#F09BA0");
        titleView.settextcolor("#ffffff");
        titleView.titleImg(R.drawable.heart_rate_top_icon);
        titleView.setBack(R.drawable.steps_back, new onBackLister() {
            @Override
            public void onClick(View button)
            {
                finish();
            }
        });
        titleView.right(R.string.history, new onSetLister() {
            @Override
            public void onClick(View button)
            {
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

    private void initChartData()
    {

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

    public void initData()
    {
        if (modelDao == null) {
            modelDao = new ModelDao(HeartRateActivity.this);
        }
        if (rate == null) {   //初始化心率界面时，首先从数据库中读取历史数据
            rate = modelDao.getLastHeartRate();
        }

        if(rate == null){   //如果从数据库中读取到的数据还是null
            tvheartRate.setText("0");
            tvheartRateTime.setText("");
        }else {
            tvheartRate.setText(rate.getHeartRate());
            tvheartRateTime.setText(rate.getPreciseDate());

            lineData = mLineChart.getData();
            lineDataSet = lineData.getDataSetByIndex(0);
            int count = lineDataSet.getEntryCount();
            Entry entry = new Entry(Integer.valueOf(rate.getHeartRate()), count);
            lineData.addXValue("");
            lineData.addEntry(entry, 0);

            mLineChart.notifyDataSetChanged();
            mLineChart.invalidate();
        }
    }

    /**
     * le: 随时准备接收传递过来的心率数据
     */
    public void onEventMainThread(Event event)
    {
        if (event.heartRate != null) {
            rate = event.heartRate;
            int heartRateValue = Integer.valueOf(rate.getHeartRate()); //获取心率数值
            if (allHeartRateNum == null) {
                allHeartRateNum = new ArrayList<Integer>();
            }
            allHeartRateNum.add(heartRateValue);

            initData();
        }

    }

    @Override
    protected void onStop()
    {
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

            MethodUtils.uploadHeartRate(HeartRateActivity.this, heartRate);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mLineChart != null) {
            mLineChart = null;
        }
    }


}
