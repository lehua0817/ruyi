package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.adapter.PushAdapter;
import com.bt.elderbracelet.entity.others.PushMessage;
import com.bt.elderbracelet.okhttp.HttpRequest;
import com.bt.elderbracelet.okhttp.URLConstant;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PushListActivity extends Activity {
    private TitleView titleView;      //标题栏
    private ListView pushList;           //专家建议ListView下一页
    private Button btnPrePage, btnNextPage;   //两个按钮，上一页，

    private ArrayList<PushMessage> messageList;
    private PushAdapter pushAdapter;

    private int page = 1;
    private boolean isFavorite = false;    //代表当前页面是在历史记录页面还是 收藏界面
    private boolean isEmpty = true;        //获取当期页面后，会返回一个 数据has_next，代表获取该页面后是否还有下一个页面
    private static final boolean GET_HISTORY_MESSAGE = true;
    private static final boolean GET_FAVORITE_MESSAGE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_advice_list);
        MyApplication.getInstance().addActivity(this);
        initView();
        initListener();
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.titleview);
        titleView.setcolor("#F19ABD");
        titleView.titleImg(R.drawable.advice_icon);
        titleView.setBack(R.drawable.steps_back, new onBackLister() {
            @Override
            public void onClick(View button) {
                if (isFavorite) {
                    initPushHistory();
                } else {
                    finish();
                }
            }
        });

        titleView.right(R.string.my_collect, new onSetLister() {
            @Override
            public void onClick(View view) {
                initPushFavorite();
            }
        });

        pushList = (ListView) findViewById(R.id.lv_advice);
        btnPrePage = (Button) findViewById(R.id.btn_pre_page);
        btnNextPage = (Button) findViewById(R.id.btn_next_page);
    }

    private void initListener() {
        btnPrePage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                page--;
                if (page == 0) {
                    page = 1;
                    MethodUtils.showToast(getApplicationContext(), "已到最前页");
                } else {
                    messageList.clear();
                    pushAdapter.notifyDataSetChanged();
                    getPushMessage(page, !isFavorite);
                }
            }
        });
        btnNextPage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEmpty) {
                    messageList.clear();
                    pushAdapter.notifyDataSetChanged();
                    page++;
                    getPushMessage(page, !isFavorite);
                } else {
                    MethodUtils.showToast(getApplicationContext(), "已到最后一页");
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void initData() {
        messageList = new ArrayList<>();
        pushAdapter = new PushAdapter(messageList, PushListActivity.this);
        pushList.setAdapter(pushAdapter);
        initPushHistory();
    }

    private void initPushHistory() {
        isFavorite = false;
        titleView.setTitle(R.string.healthyAdvice);   //健康建议
        titleView.hideRight();
        messageList.clear();
        pushAdapter.notifyDataSetChanged();
        page = 1;
        getPushMessage(page, GET_HISTORY_MESSAGE);
    }

    public void initPushFavorite() {
        isFavorite = true;
        titleView.setTitle(R.string.healthyAdviceCollect);
        messageList.clear();
        pushAdapter.notifyDataSetChanged();
        page = 1;
        getPushMessage(page, GET_FAVORITE_MESSAGE);
    }


    //如果参数为True，则向 URLConstant.URL_PUSH_HISTORY 获取推送消息
    //如果参数为false，则向 URL_FAVORITE_HISTORY 获取推送消息

    /**
     * @param page             查看第几页的历史消息
     * @param whichKindMessage true 代表查看历史消息
     *                         false 代表查看用户收藏过的历史消息
     */
    private void getPushMessage(int page, boolean whichKindMessage) {
        if (whichKindMessage == GET_HISTORY_MESSAGE) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", SpHelp.getUserId());
            params.put("page", page);
            params.put("limit", 7);
            HttpRequest.get(URLConstant.URL_PUSH_HISTORY, null, params, new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    String error = response.optString("error");
                    if (error.equals("0")) {
                        isEmpty = !response.optJSONObject("data").optBoolean("has_next");
                        JSONArray list = response.optJSONObject("data").optJSONArray("data");
                        for (int i = 0; i < list.length(); i++) {
                            PushMessage pushMessage = new PushMessage();
                            JSONObject item = list.optJSONObject(i);
                            pushMessage.setId(item.optString("id"));
                            pushMessage.setTitleColor(item.optString("titleColor"));
                            pushMessage.setTitleStrong(item.optInt("titleStrong"));

                            System.out.println(item.optString("titleColor"));
                            System.out.println(item.optInt("titleStrong"));

                            pushMessage.setBrief(item.optString("brief"));
                            pushMessage.setTitle(item.optString("title"));
                            pushMessage.setTime(item.optString("time"));
                            pushMessage.setLooked(item.optBoolean("read"));
                            pushMessage.setCollected(item.optBoolean("favorite"));
                            messageList.add(pushMessage);
                        }
                        pushAdapter.notifyDataSetChanged();
                    } else {
                        MethodUtils.showToast(getApplicationContext(), response.optString("error_info"));
                    }
                }

                @Override
                public void onFailure() {
                    MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
                }
            });
        }
        if (whichKindMessage == GET_FAVORITE_MESSAGE) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", SpHelp.getUserId());
            params.put("page", page);
            params.put("limit", 7);
            HttpRequest.get(URLConstant.URL_FAVORITE_HISTORY, null, params, new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    String error = response.optString("error");
                    if (error.equals("0")) {
                        isEmpty = !response.optJSONObject("data").optBoolean("has_next");
                        JSONArray list = response.optJSONObject("data").optJSONArray("data");
                        for (int i = 0; i < list.length(); i++) {
                            PushMessage pushMessage = new PushMessage();
                            JSONObject item = list.optJSONObject(i);
                            pushMessage.setId(item.optString("id"));
                            pushMessage.setBrief(item.optString("brief"));
                            pushMessage.setTitle(item.optString("title"));
                            pushMessage.setTime(item.optString("time"));
                            pushMessage.setLooked(item.optBoolean("read"));
                            pushMessage.setCollected(true);
                            messageList.add(pushMessage);
                        }
                        pushAdapter.notifyDataSetChanged();
                    } else {
                        MethodUtils.showToast(getApplicationContext(), response.optString("error_info"));
                    }
                }

                @Override
                public void onFailure() {
                    MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
/*   le 0907     if (pushAdapter != null)
            pushAdapter.notifyDataSetChanged();*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isFavorite) {
            initPushHistory();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }
}
