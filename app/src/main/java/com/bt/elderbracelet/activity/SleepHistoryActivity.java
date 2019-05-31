package com.bt.elderbracelet.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.Sleep;
import com.bt.elderbracelet.okhttp.HttpRequest;
import com.bt.elderbracelet.okhttp.URLConstant;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bttow.elderbracelet.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mpchart.notimportant.DemoBase;

public class SleepHistoryActivity extends DemoBase {
    protected LineChart mLineChart;
    private TitleView titleView;
    private ArrayList<Sleep> sleepList;
    private ModelDao modelDao;
    private Button btn_pre_page, btn_next_page;


    LineDataSet lineDataSet_SleepTime = null;
    LineDataSet lineDataSet_SleepDeepTime = null;
    LineData lineData = null;
    private int page = 1;
    private boolean hasNext = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sleep_linechart);
        MyApplication.getInstance().addActivity(this);
        initView();
        initListener();
        initData();
    }

    private void initView()
    {
        titleView = (TitleView) findViewById(R.id.titleview);
        titleView.setcolor("#76C5F0");
        titleView.titleImg(R.drawable.sleep_icon);
        titleView.setTitle(R.string.sleep_his);
        titleView.setBack(R.drawable.steps_back, new TitleView.onBackLister() {
            @Override
            public void onClick(View button)
            {
                finish();
            }
        });
        btn_next_page = (Button) findViewById(R.id.btn_next_page);
        btn_pre_page = (Button) findViewById(R.id.btn_pre_page);
        btn_next_page.setBackgroundResource(R.color.gray);
        btn_pre_page.setBackgroundResource(R.color.gray);

        mLineChart = (LineChart) findViewById(R.id.sleepHisLineChart);

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
        xAxis.setDrawGridLines(true);
        xAxis.setSpaceBetweenLabels(5);
        xAxis.setTextColor(R.color.step_color);
        xAxis.setTextSize(15);
        xAxis.setLabelsToSkip(0);

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(40f);  //设置图表中最高值的顶部间距
        leftAxis.setAxisMinValue(0f);
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
    }

    private long clickTime = 0;

    private void initListener()
    {
        btn_pre_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if ((System.currentTimeMillis() - clickTime) < 500) {
                    MethodUtils.showToast(SleepHistoryActivity.this, "请勿点击太快，服务器表示很难过");
                    clickTime = System.currentTimeMillis();
                    return;
                }
                clickTime = System.currentTimeMillis();

                page--;
                if (page == 1) {
                    btn_pre_page.setBackgroundResource(R.color.gray);
                } else if (page == 0) {
                    page = 1;
                    MethodUtils.showToast(getApplicationContext(), "已到最前页");
                } else {
                    getSleep(page, 7);
                }
            }
        });

        btn_next_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if ((System.currentTimeMillis() - clickTime) < 500) {
                    MethodUtils.showToast(SleepHistoryActivity.this, "请勿点击太快，服务器表示很难过");
                    clickTime = System.currentTimeMillis();
                    return;
                }
                clickTime = System.currentTimeMillis();

                if (hasNext) {
                    page++;
                    getSleep(page, 7);
                    btn_pre_page.setBackgroundResource(R.color.pressure_bg);
                } else {
                    MethodUtils.showToast(getApplicationContext(), "已到最后一页");
                }
            }
        });
    }

    private void initData()
    {
        if (modelDao == null) {
            modelDao = new ModelDao(this);
        }
        sleepList = new ArrayList<>();
        getSleep(1, 7);

    }

    private void initLineChart(List<Sleep> sleepList)
    {
        System.out.println("sleepList.size() :" + sleepList.size());
        ArrayList<Entry> sleepTimeValues = new ArrayList<Entry>();
        ArrayList<Entry> sleepDeepTimeValues = new ArrayList<Entry>();
        ArrayList<String> xValueList = new ArrayList<String>();

        for (int i = 0; i < sleepList.size(); i++) {
            Sleep sleep = sleepList.get(i);
            int month = Integer.valueOf(sleep.getDate().substring(5, 7));
            int day = Integer.valueOf(sleep.getDate().substring(8, 10));

            if (i == (sleepList.size() - 1)) {
                xValueList.add("今天");
            } else if (i == (sleepList.size() - 2)) {
                xValueList.add("昨天");
            } else {
                xValueList.add(String.format("%02d", month) + "/" + String.format("%02d", day));
            }

            System.out.println(sleep.getSleepTime() + ":::" + sleep.getSleepDeepTime());
            float sleepTime = Float.valueOf(sleep.getSleepTime());
            float sleepDeepTime = Float.valueOf(sleep.getSleepDeepTime());

            sleepTimeValues.add(new Entry(sleepTime, i));
            sleepDeepTimeValues.add(new Entry(sleepDeepTime, i));
        }

        lineDataSet_SleepTime = new LineDataSet(sleepTimeValues, "总睡眠时间 单位：分钟");
        lineDataSet_SleepDeepTime = new LineDataSet(sleepDeepTimeValues, "深睡时间 单位：分钟");

        lineDataSet_SleepTime.setValueTextSize(20);
        lineDataSet_SleepTime.setValueTextColor(R.color.colorPrimaryDark);
        lineDataSet_SleepTime.setCircleSize(5);
        lineDataSet_SleepTime.setCircleColor(R.color.red);
        lineDataSet_SleepTime.setLineWidth(4);
        lineDataSet_SleepTime.setColor(getResources().getColor(R.color.red));
        lineDataSet_SleepTime.setHighlightEnabled(true);
        lineDataSet_SleepTime.setHighLightColor(R.color.fuchsia);
        lineDataSet_SleepTime.setHighlightLineWidth(2);

        lineDataSet_SleepDeepTime.setValueTextSize(20);
        lineDataSet_SleepDeepTime.setValueTextColor(R.color.red);
        lineDataSet_SleepDeepTime.setCircleSize(5);
        lineDataSet_SleepDeepTime.setCircleColor(R.color.red);
        lineDataSet_SleepDeepTime.setLineWidth(4);
        lineDataSet_SleepDeepTime.setColor(getResources().getColor(R.color.blue));
        lineDataSet_SleepDeepTime.setHighlightEnabled(true);
        lineDataSet_SleepDeepTime.setHighLightColor(R.color.fuchsia);
        lineDataSet_SleepDeepTime.setHighlightLineWidth(2);

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet_SleepTime);
        dataSets.add(lineDataSet_SleepDeepTime);

        lineData = new LineData(xValueList, dataSets);
        mLineChart.setData(lineData);
        mLineChart.invalidate();

    }

    private void getSleep(int page, final int limit)
    {
        if (sleepList != null) {
            sleepList.clear();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("id", SpHelp.getUserId());
        params.put("page", page);
        params.put("limit", limit);
        HttpRequest.get(URLConstant.URL_GET_SLEEP, null, params, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response)
            {
                if (response.optString("error").equals("0")) {
                    System.out.println("Pressure--1111");
                    System.out.println("error ：" + response.optString("error"));

                    hasNext = response.optJSONObject("data").optBoolean("hasNext");
                    System.out.println("heartRate hasNext :" + hasNext);

                    if (!hasNext) {
                        btn_next_page.setBackgroundResource(R.color.gray);
                    } else {
                        btn_next_page.setBackgroundResource(R.color.pressure_bg);
                    }
                    JSONArray list = response.optJSONObject("data").optJSONArray("data");
                    System.out.println("list.length() :" + list.length());
                    for (int i = 0; i < list.length(); i++) {
                        Sleep sleep = new Sleep();
                        JSONObject item = list.optJSONObject(i);

                        sleep.setSleepTime(item.optString("sleepTime"));
                        sleep.setSleepDeepTime(item.optString("sleepDeepTime"));
                        sleep.setDate(item.optString("date"));
                        System.out.println(sleep);
                        sleepList.add(sleep);
                    }
                    initLineChart(sleepList);
                } else {
                    MethodUtils.showToast(getApplicationContext(), response.optString("error_info"));
                }
            }

            @Override
            public void onFailure()
            {
                MethodUtils.showToast(getApplicationContext(), "为了查询更多睡眠历史记录，请检查网络是否异常");
                sleepList = modelDao.queryAllSleep();
                if (sleepList == null) {
                    sleepList = new ArrayList<Sleep>();
                }
                initLineChart(sleepList);
            }
        });

    }

}
