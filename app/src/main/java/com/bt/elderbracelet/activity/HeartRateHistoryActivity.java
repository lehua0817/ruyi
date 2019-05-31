package com.bt.elderbracelet.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.HeartRate;
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

public class HeartRateHistoryActivity extends DemoBase {
    protected LineChart mLineChart;
    private TitleView titleView;
    private Button btn_pre_page, btn_next_page;
    private ArrayList<HeartRate> heartRateList;
    private ModelDao modelDao;
    LineDataSet lineDataSet = null;
    LineData lineData = null;

    private int page = 1;
    private boolean hasNext = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_heartrate_history);
        MyApplication.getInstance().addActivity(this);
        initView();
        initListener();
        initData();
    }

    private void initView()
    {
        titleView = (TitleView) findViewById(R.id.titleview);
        titleView.setTitle(R.string.heart_rate_his);
        titleView.setcolor("#F09BA0");
        titleView.titleImg(R.drawable.heart_rate_top_icon);
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

        mLineChart = (LineChart) findViewById(R.id.sportHisLineChart);

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
        xAxis.setLabelsToSkip(1);

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(20, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(40f);  //设置图表中最高值的顶部间距
        leftAxis.setStartAtZero(false);
        leftAxis.setAxisMinValue(50.0f);
        leftAxis.setAxisMaxValue(120.0f);
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
                    MethodUtils.showToast(HeartRateHistoryActivity.this, "请勿点击太快，服务器表示很难过");
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
                    getHeartRate(page, 7);
                }
            }
        });

        btn_next_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if ((System.currentTimeMillis() - clickTime) < 500) {
                    MethodUtils.showToast(HeartRateHistoryActivity.this, "请勿点击太快，服务器表示很难过");
                    clickTime = System.currentTimeMillis();
                    return;
                }
                clickTime = System.currentTimeMillis();

                if (hasNext) {
                    page++;
                    getHeartRate(page, 7);
                    btn_pre_page.setBackgroundResource(R.color.heartRate_bg);
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
        heartRateList = new ArrayList<>();
        getHeartRate(1, 7);
    }

    private void initLineChart(List<HeartRate> heartRateList)
    {
        ArrayList<Entry> yValueList = new ArrayList<Entry>();
        ArrayList<String> xValueList = new ArrayList<>();
        for (int i = 0; i < heartRateList.size(); i++) {
            HeartRate rate = heartRateList.get(i);

            xValueList.add(rate.getPreciseDate().substring(5));
            float yValue = Float.valueOf(rate.getHeartRate());

            yValueList.add(new Entry(yValue, i));
        }
        lineDataSet = new LineDataSet(yValueList, "心率次数 ");

        lineDataSet.setValueTextSize(20);
        lineDataSet.setValueTextColor(R.color.colorPrimaryDark);
        lineDataSet.setCircleSize(5);
        lineDataSet.setCircleColor(R.color.red);
        lineDataSet.setLineWidth(4);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setHighLightColor(R.color.fuchsia);
        lineDataSet.setHighlightLineWidth(2);
        lineDataSet.setDrawFilled(false);
        lineDataSet.setColor(Color.parseColor("#F09BA0"));

        lineData = new LineData(xValueList, lineDataSet);
        mLineChart.setData(lineData);
        mLineChart.invalidate();
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mLineChart != null) {
            mLineChart = null;
        }
    }

    private void getHeartRate(int page, final int limit)
    {
        if (heartRateList != null) {
            heartRateList.clear();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("id", SpHelp.getUserId());
        params.put("page", page);
        params.put("limit", limit);
        HttpRequest.get(URLConstant.URL_GET_HEART_RATE, null, params, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response)
            {
                if (response.optString("error").equals("0")) {
                    System.out.println("HeartRate--0000");
                    System.out.println("error ：" + response.optString("error"));
                    hasNext = response.optJSONObject("data").optBoolean("hasNext");

                    System.out.println("hasNext :" + hasNext);
                    if (!hasNext) {
                        btn_next_page.setBackgroundResource(R.color.gray);
                    } else {
                        btn_next_page.setBackgroundResource(R.color.heartRate_bg);
                    }
                    JSONArray list = response.optJSONObject("data").optJSONArray("data");
                    System.out.println("list.length() :" + list.length());
                    for (int i = 0; i < list.length(); i++) {
                        HeartRate rate = new HeartRate();
                        JSONObject item = list.optJSONObject(i);

                        rate.setHeartRate(item.optString("heartRate"));
                        rate.setPreciseDate(item.optString("date"));

                        System.out.println(rate);

                        heartRateList.add(rate);
                    }

                    initLineChart(heartRateList);
                } else {
                    MethodUtils.showToast(getApplicationContext(), response.optString("error_info"));
                }
            }

            @Override
            public void onFailure()
            {
                MethodUtils.showToast(getApplicationContext(), "为了查询更多历史记录，请检查网络是否异常");
                heartRateList = modelDao.queryAllHeartRate();
                if (heartRateList == null) {
                    heartRateList = new ArrayList<HeartRate>();
                }
                initLineChart(heartRateList);
            }
        });
    }
}
