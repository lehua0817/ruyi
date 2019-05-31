package com.bt.elderbracelet.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.BloodOxygen;
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

public class OxygenHistoryActivity extends DemoBase {
    protected LineChart mLineChart;
    private TitleView titleView;
    private Button btn_pre_page, btn_next_page;
    private ArrayList<BloodOxygen> oxygenList;
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
        setContentView(R.layout.activity_oxygen_history);
        MyApplication.getInstance().addActivity(this);
        initView();
        initListener();
        initData();
    }

    private void initView()
    {
        titleView = (TitleView) findViewById(R.id.titleview);
        titleView.setTitle(R.string.oxygen_his);
        titleView.setcolor("#B7278F");
        titleView.titleImg(R.drawable.oxygen_icon);
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

        mLineChart = (LineChart) findViewById(R.id.oxygenHisLineChart);

        mLineChart.setNoDataText("");
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
        xAxis.setTextSize(15);
        xAxis.setLabelsToSkip(1);

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(20, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(40f);  //设置图表中最高值的顶部间距
        leftAxis.setStartAtZero(false);
        leftAxis.setAxisMinValue(90.0f);
        leftAxis.setAxisMaxValue(100.0f);
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
                    MethodUtils.showToast(OxygenHistoryActivity.this, "请勿点击太快，服务器表示很难过");
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
                    getBloodOxygen(page, 7);
                }
            }
        });

        btn_next_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if ((System.currentTimeMillis() - clickTime) < 500) {
                    MethodUtils.showToast(OxygenHistoryActivity.this, "请勿点击太快，服务器表示很难过");
                    clickTime = System.currentTimeMillis();
                    return;
                }
                clickTime = System.currentTimeMillis();

                if (hasNext) {
                    page++;
                    getBloodOxygen(page, 7);
                    btn_pre_page.setBackgroundResource(R.color.oxygen_color);
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
        oxygenList = new ArrayList<>();
        getBloodOxygen(1, 7);
        System.out.println("hasNext :" + hasNext);
    }


    private void initLineChart(List<BloodOxygen> oxygenList)
    {
        ArrayList<Entry> yValueList = new ArrayList<Entry>();
        ArrayList<String> xValueList = new ArrayList<>();
        for (int i = 0; i < oxygenList.size(); i++) {
            BloodOxygen oxygen = oxygenList.get(i);

            xValueList.add(oxygen.getPreciseDate().substring(5, oxygen.getPreciseDate().length()));

            float yValue = Float.valueOf(oxygen.getBloodOxygen());
            yValueList.add(new Entry(yValue, i));
        }
        lineDataSet = new LineDataSet(yValueList, "血氧数据 单位：sp02");
        lineDataSet.setValueTextSize(20);
        lineDataSet.setValueTextColor(getResources().getColor(R.color.oxygen_color));
        lineDataSet.setCircleSize(5);
        lineDataSet.setCircleColor(getResources().getColor(R.color.oxygen_color));
        lineDataSet.setLineWidth(4);
        lineDataSet.setColor(getResources().getColor(R.color.oxygen_color));
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setHighLightColor(R.color.fuchsia);
        lineDataSet.setHighlightLineWidth(2);
        lineDataSet.setDrawFilled(false);

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

    private void getBloodOxygen(int page, final int limit)
    {
        if (oxygenList != null) {
            oxygenList.clear();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("id", SpHelp.getUserId());
        params.put("page", page);
        params.put("limit", limit);
        HttpRequest.get(URLConstant.URL_GET_BLOODOXYGEN, null, params, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response)
            {
                if (response.optString("error").equals("0")) {
                    hasNext = response.optJSONObject("data").optBoolean("hasNext");

                    if (!hasNext) {
                        btn_next_page.setBackgroundResource(R.color.gray);
                    } else {
                        btn_next_page.setBackgroundResource(R.color.oxygen_color);
                    }
                    JSONArray list = response.optJSONObject("data").optJSONArray("data");
                    for (int i = 0; i < list.length(); i++) {
                        BloodOxygen oxygen = new BloodOxygen();
                        JSONObject item = list.optJSONObject(i);

                        oxygen.setBloodOxygen(item.optString("oxygen"));
                        oxygen.setPreciseDate(item.optString("date"));

                        System.out.println(oxygen);

                        oxygenList.add(oxygen);
                    }
                    initLineChart(oxygenList);
                } else {
                    MethodUtils.showToast(getApplicationContext(), "查询血氧数据失败，原因和连接服务器相关："+ response.optString("error_info"));
                }
            }

            @Override
            public void onFailure()
            {
                MethodUtils.showToast(getApplicationContext(), "为了查询更多血氧历史记录，请检查网络是否异常");
                oxygenList = modelDao.queryAllBloodOxygen();
                if (oxygenList == null) {
                    oxygenList = new ArrayList<BloodOxygen>();
                }
                initLineChart(oxygenList);
            }
        });
    }

}
