package com.bt.elderbracelet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.BloodSugar;
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

public class SugarHistoryActivity extends DemoBase {
    protected LineChart mLineChart;
    private TitleView titleView;
    private ArrayList<BloodSugar> sugarList;
    private ModelDao modelDao;
    private Button btn_pre_page, btn_next_page;

    LineDataSet lineDataSet_1 = null;
    LineDataSet lineDataSet_2 = null;
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
        titleView.setTitle(R.string.sugar_his);
        titleView.setcolor("#FB3A67");
        titleView.titleImg(R.drawable.sugar_icon);
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
        xAxis.setLabelsToSkip(1);

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(20, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(40f);  //设置图表中最高值的顶部间距
        leftAxis.setAxisMinValue(0.0f);
        leftAxis.setAxisMaxValue(10.0f);
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
                    MethodUtils.showToast(SugarHistoryActivity.this, "请勿点击太快，服务器表示很难过");
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
                    getBloodSugar(page, 7);
                }
            }
        });

        btn_next_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if ((System.currentTimeMillis() - clickTime) < 500) {
                    MethodUtils.showToast(SugarHistoryActivity.this, "请勿点击太快，服务器表示很难过");
                    clickTime = System.currentTimeMillis();
                    return;
                }
                clickTime = System.currentTimeMillis();

                if (hasNext) {
                    page++;
                    getBloodSugar(page, 7);
                    btn_pre_page.setBackgroundResource(R.color.sugar_bg);
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
        sugarList = new ArrayList<>();
        getBloodSugar(1, 7);
    }


    private void initLineChart(List<BloodSugar> sugarList)
    {

        ArrayList<Entry> value_1 = new ArrayList<Entry>();
        ArrayList<Entry> value_2 = new ArrayList<Entry>();
        ArrayList<String> xValueList = new ArrayList<String>();

        for (int i = 0; i < sugarList.size(); i++) {
            BloodSugar sugar = sugarList.get(i);
            xValueList.add(sugar.getPreciseDate().substring(5, sugar.getPreciseDate().length()));

            float sugarBefore = Float.valueOf(sugar.getBloodSugarBefore());
            float sugarAfter = Float.valueOf(sugar.getBloodSugarAfter());

            value_1.add(new Entry(sugarBefore, i));
            value_2.add(new Entry(sugarAfter, i));
        }

        lineDataSet_1 = new LineDataSet(value_1, "餐前血糖 单位：mmol/L");
        lineDataSet_2 = new LineDataSet(value_2, "餐后血糖 单位：mmol/L");

        lineDataSet_1.setValueTextSize(20);
        lineDataSet_1.setValueTextColor(getResources().getColor(R.color.blue));
        lineDataSet_1.setCircleSize(5);
        lineDataSet_1.setCircleColor(getResources().getColor(R.color.blue));
        lineDataSet_1.setLineWidth(4);
        lineDataSet_1.setColor(getResources().getColor(R.color.blue));
        lineDataSet_1.setHighlightEnabled(true);
        lineDataSet_1.setHighLightColor(R.color.fuchsia);
        lineDataSet_1.setHighlightLineWidth(2);

        lineDataSet_2.setValueTextSize(20);
        lineDataSet_2.setValueTextColor(getResources().getColor(R.color.red));
        lineDataSet_2.setCircleSize(5);
        lineDataSet_2.setCircleColor(getResources().getColor(R.color.red));
        lineDataSet_2.setLineWidth(4);
        lineDataSet_2.setColor(getResources().getColor(R.color.red));
        lineDataSet_2.setHighlightEnabled(true);
        lineDataSet_2.setHighLightColor(R.color.fuchsia);
        lineDataSet_2.setHighlightLineWidth(2);

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet_1);
        dataSets.add(lineDataSet_2);

        lineData = new LineData(xValueList, dataSets);
        mLineChart.setData(lineData);
        mLineChart.invalidate();
    }

    private void getBloodSugar(int page, final int limit)
    {
        if (sugarList != null) {
            sugarList.clear();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("id", SpHelp.getUserId());
        params.put("page", page);
        params.put("limit", limit);
        HttpRequest.get(URLConstant.URL_GET_BLOODSUGAR, null, params, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response)
            {
                if (response.optString("error").equals("0")) {
                    System.out.println("Sugar--0000");
                    System.out.println("error ：" + response.optString("error"));
                    hasNext = response.optJSONObject("data").optBoolean("hasNext");

                    System.out.println("Sugar hasNext :" + hasNext);

                    if (!hasNext) {
                        btn_next_page.setBackgroundResource(R.color.gray);
                    } else {
                        btn_next_page.setBackgroundResource(R.color.sugar_bg);
                    }
                    JSONArray list = response.optJSONObject("data").optJSONArray("data");
                    System.out.println("list.length() :" + list.length());

                    for (int i = 0; i < list.length(); i++) {

                        BloodSugar sugar = new BloodSugar();
                        JSONObject item = list.optJSONObject(i);

                        sugar.setBloodSugarBefore(item.optString("beforeMealSugar"));
                        sugar.setBloodSugarAfter(item.optString("afterMealSugar"));
                        sugar.setPreciseDate(item.optString("date"));

                        System.out.println(sugar);

                        sugarList.add(sugar);
                    }
                    initLineChart(sugarList);
                } else {
                    MethodUtils.showToast(getApplicationContext(), response.optString("error_info"));
                }
            }

            @Override
            public void onFailure()
            {
                MethodUtils.showToast(getApplicationContext(), "为了查询更多历史记录，请检查网络是否异常");
                sugarList = modelDao.queryAllBloodSugar();
                if (sugarList == null) {
                    sugarList = new ArrayList<BloodSugar>();
                }
                initLineChart(sugarList);
            }
        });
    }
}
