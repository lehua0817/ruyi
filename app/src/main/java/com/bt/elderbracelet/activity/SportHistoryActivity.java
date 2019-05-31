package com.bt.elderbracelet.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.Sport;
import com.bt.elderbracelet.okhttp.HttpRequest;
import com.bt.elderbracelet.okhttp.URLConstant;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bttow.elderbracelet.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mpchart.notimportant.DemoBase;

public class SportHistoryActivity extends DemoBase {
    private static final String TAG = SportHistoryActivity.class.getSimpleName();
    protected LineChart mLineChart;
    private TitleView titleView;
    private ModelDao modelDao;
    private Button btn_pre_page, btn_next_page;
    private ArrayList<Sport> sportList;
    LineDataSet lineDataSet = null;
    LineData lineData = null;

    private int page = 1;
    private boolean hasNext = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sport_history);
        MyApplication.getInstance().addActivity(this);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.titleview);
        btn_next_page = (Button) findViewById(R.id.btn_next_page);
        btn_pre_page = (Button) findViewById(R.id.btn_pre_page);
        btn_next_page.setBackgroundResource(R.color.gray);
        btn_pre_page.setBackgroundResource(R.color.gray);
        if (MyApplication.activityStuts == MyApplication.STEP_HISTORY) {
            titleView.setTitle(R.string.step_his);
            titleView.setcolor("#85C226");
            titleView.titleImg(R.drawable.foot_icon);
        } else if (MyApplication.activityStuts == MyApplication.CALORIA_HISTORY) {
            titleView.setTitle(R.string.caloria_his);
            titleView.setcolor("#E77843");
            titleView.titleImg(R.drawable.kalor);
        } else if (MyApplication.activityStuts == MyApplication.SPORT_TIME_HISTORY) {
            titleView.setTitle(R.string.sport_time_his);
            titleView.setcolor("#BAB3D5");
            titleView.titleImg(R.drawable.sport_titleimg);
        } else if (MyApplication.activityStuts == MyApplication.DISTANCE_HISTORY) {
            titleView.setTitle(R.string.distance_his);
            titleView.setcolor("#0093DE");
            titleView.titleImg(R.drawable.distance_icon);
        }

        titleView.setBack(R.drawable.steps_back, new TitleView.onBackLister() {
            @Override
            public void onClick(View button) {
                finish();
            }
        });

        mLineChart = (LineChart) findViewById(R.id.sportHisLineChart);

        mLineChart.setDescription("");

        mLineChart.setMaxVisibleValueCount(10);

        mLineChart.setTouchEnabled(true);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);
        mLineChart.setPinchZoom(true);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setBorderColor(getResources().getColor(R.color.step_color));

        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(true);
        xAxis.setSpaceBetweenLabels(5);
        xAxis.setLabelsToSkip(0);
        xAxis.setTextColor(R.color.step_color);
        xAxis.setTextSize(15);


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

    private void initListener() {
        btn_pre_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((System.currentTimeMillis() - clickTime) < 500) {
                    MethodUtils.showToast(SportHistoryActivity.this, "请勿点击太快，服务器表示很难过");
                    clickTime = System.currentTimeMillis();
                    return;
                }
                clickTime = System.currentTimeMillis();
                page--;
                if (page == 0) {
                    page = 1;
                    MethodUtils.showToast(getApplicationContext(), "已到最前页");
                    return;
                }
                if (page == 1) {
                    btn_pre_page.setBackgroundResource(R.color.gray);
                }
                getSportData(page, 7);
            }
        });

        btn_next_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((System.currentTimeMillis() - clickTime) < 500) {
                    MethodUtils.showToast(SportHistoryActivity.this, "请勿点击太快，服务器表示很难过");
                    clickTime = System.currentTimeMillis();
                    return;
                }
                clickTime = System.currentTimeMillis();

                if (hasNext) {
                    page++;
                    getSportData(page, 7);

                    if (MyApplication.activityStuts == MyApplication.STEP_HISTORY) {
                        btn_pre_page.setBackgroundResource(R.color.step_color);
                    } else if (MyApplication.activityStuts == MyApplication.CALORIA_HISTORY) {
                        btn_pre_page.setBackgroundResource(R.color.caloria_color);
                    } else if (MyApplication.activityStuts == MyApplication.SPORT_TIME_HISTORY) {
                        btn_pre_page.setBackgroundResource(R.color.sport_time_color);
                    } else if (MyApplication.activityStuts == MyApplication.DISTANCE_HISTORY) {
                        btn_pre_page.setBackgroundResource(R.color.distance_color);
                    }

                } else {
                    MethodUtils.showToast(getApplicationContext(), "已到最后一页");
                }
            }
        });
    }

    /**
     * 界面初始化时，先从服务器获取数据，如果服务器都没有数据的话，则加载本地数据
     * <p>
     * 问题1：为什么初始化时，界面第一页的数据就在本地啊，不用本地的数据呢？
     * 因为我们必须考虑到，当用户换了账号登陆时，本地数据清空了，但是服务器中还有数据
     * <p>
     * 问题2： 为什么不是先从本地获取数据，如果没有，再从服务器去获取数据呢？
     * 因为 我们点击下一页，获取第二页的数据时，点击之前要知道 “has_next”字段的值
     * 而该字段的值，在获取第一页的返回数据当中，所以我们必须从服务器加载第一页的数据
     */
    private void initData() {
        if (modelDao == null) {
            modelDao = new ModelDao(this);
        }

        sportList = new ArrayList<>();
        getSportData(1, 7);
    }

    private void getSportData(int page, final int limit) {
        if (sportList != null) {
            sportList.clear();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("id", SpHelp.getUserId());
        Log.v(TAG, "id = " + SpHelp.getUserId());
        params.put("page", page);
        params.put("limit", limit);

        HttpRequest.get(URLConstant.URL_GET_SPORT, null, params, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response.optString("error").equals("0")) {
                    hasNext = response.optJSONObject("data").optBoolean("hasNext");
                    if (!hasNext) {
                        btn_next_page.setBackgroundResource(R.color.gray);
                    } else {
                        if (MyApplication.activityStuts == MyApplication.STEP_HISTORY) {
                            btn_next_page.setBackgroundResource(R.color.step_color);
                        } else if (MyApplication.activityStuts == MyApplication.CALORIA_HISTORY) {
                            btn_next_page.setBackgroundResource(R.color.caloria_color);
                        } else if (MyApplication.activityStuts == MyApplication.SPORT_TIME_HISTORY) {
                            btn_next_page.setBackgroundResource(R.color.sport_time_color);
                        } else if (MyApplication.activityStuts == MyApplication.DISTANCE_HISTORY) {
                            btn_next_page.setBackgroundResource(R.color.distance_color);
                        }
                    }
                    JSONArray list = response.optJSONObject("data").optJSONArray("data");
                    for (int i = 0; i < list.length(); i++) {
                        Sport sport = new Sport();
                        JSONObject item = list.optJSONObject(i);

                        sport.setStep(item.optString("step"));
                        sport.setDistance(item.optString("distance"));
                        sport.setCalorie(item.optString("calorie"));
                        sport.setSportTime(item.optString("sportTime"));
                        sport.setDate(item.optString("date"));

                        System.out.println(sport);

                        sportList.add(sport);
                    }
                    initLineChart(sportList);
                } else {
                    MethodUtils.showToast(getApplicationContext(), "查询运动数据失败，原因和连接服务器相关：" + response.optString("error_info"));
                }
            }

            @Override
            public void onFailure() {
                MethodUtils.showToast(getApplicationContext(), "为了查询更多历史记录，请检查网络是否异常");
                sportList = modelDao.queryAllSport();
                if (sportList == null) {
                    sportList = new ArrayList<Sport>();
                }
                initLineChart(sportList);
            }
        });
    }

    private void initLineChart(List<Sport> sportList) {
        if (mLineChart.getData() != null) {
            mLineChart.setData(new LineData());
        }
        ArrayList<Entry> yValueList = new ArrayList<Entry>();
        ArrayList<String> xValueList = new ArrayList<>();

        for (int i = 0; i < sportList.size(); i++) {
            Sport sport = sportList.get(i);
            int month = Integer.valueOf(sport.getDate().substring(5, 7));
            int day = Integer.valueOf(sport.getDate().substring(8, 10));

            if (sport.getDate().equals(BaseUtils.getTodayDate())) {
                xValueList.add("今天");
            } else if (sport.getDate().equals(BaseUtils.getYesterdayDate())) {
                xValueList.add("昨天");
            } else {
                xValueList.add(String.format("%02d", month) + "/" + String.format("%02d", day));
            }

            float yValue = 0.0f;
            if (MyApplication.activityStuts == MyApplication.STEP_HISTORY) {
                yValue = Float.valueOf(sport.getStep());
            } else if (MyApplication.activityStuts == MyApplication.CALORIA_HISTORY) {
                yValue = Float.valueOf(sport.getCalorie());
            } else if (MyApplication.activityStuts == MyApplication.SPORT_TIME_HISTORY) {
                yValue = Float.valueOf(sport.getSportTime());
            } else if (MyApplication.activityStuts == MyApplication.DISTANCE_HISTORY) {
                yValue = Float.valueOf(sport.getDistance());
            }
            yValueList.add(new Entry(yValue, i));
        }
        if (MyApplication.activityStuts == MyApplication.STEP_HISTORY) {
            lineDataSet = new LineDataSet(yValueList, "运动步数 单位：步");
        } else if (MyApplication.activityStuts == MyApplication.CALORIA_HISTORY) {
            lineDataSet = new LineDataSet(yValueList, "运动消耗能量 单位：卡路里");
        } else if (MyApplication.activityStuts == MyApplication.SPORT_TIME_HISTORY) {
            lineDataSet = new LineDataSet(yValueList, "运动时间 单位：分钟");
        } else if (MyApplication.activityStuts == MyApplication.DISTANCE_HISTORY) {
            lineDataSet = new LineDataSet(yValueList, "运动距离 单位：米");
        }

        lineDataSet.setValueTextSize(20);
        lineDataSet.setValueTextColor(R.color.colorPrimaryDark);
        lineDataSet.setCircleSize(5);
        lineDataSet.setCircleColor(R.color.red);
        lineDataSet.setLineWidth(4);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setHighLightColor(R.color.fuchsia);
        lineDataSet.setHighlightLineWidth(2);
        lineDataSet.setDrawFilled(false);
        if (MyApplication.activityStuts == MyApplication.STEP_HISTORY) {
            lineDataSet.setColor(getResources().getColor(R.color.step_color));
        } else if (MyApplication.activityStuts == MyApplication.CALORIA_HISTORY) {
            lineDataSet.setColor(getResources().getColor(R.color.caloria_color));
        } else if (MyApplication.activityStuts == MyApplication.SPORT_TIME_HISTORY) {
            lineDataSet.setColor(getResources().getColor(R.color.sport_time_color));
        } else if (MyApplication.activityStuts == MyApplication.DISTANCE_HISTORY) {
            lineDataSet.setColor(getResources().getColor(R.color.distance_color));
        }

        lineData = new LineData(xValueList, lineDataSet);

        Log.v(TAG, "xValueList.size = " + xValueList.size());

        mLineChart.setData(lineData);
        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLineChart != null) {
            mLineChart = null;
        }
    }
}



