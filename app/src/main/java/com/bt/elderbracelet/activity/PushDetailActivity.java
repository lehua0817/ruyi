package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.entity.others.PushMessage;
import com.bt.elderbracelet.okhttp.HttpRequest;
import com.bt.elderbracelet.okhttp.URLConstant;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bttow.elderbracelet.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;


public class PushDetailActivity extends Activity {
    private TitleView titleView;
    private View optionView;
    private PopupWindow optionWindow;
    private AlertDialog deleteDialog;
    TextView tvMsgCollect;
    TextView tvMsgDelete;
    TextView tvShare;

    private FrameLayout frame_webView;
    private WebView webView;
    private PushMessage pushMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_push_detial);
        MyApplication.getInstance().addActivity(this);
        initView();
        initPopubWindow();
    }

    private void initView()
    {
        titleView = (TitleView) findViewById(R.id.titleview);
        titleView.setTitle(R.string.healthyAdvice);
        titleView.setcolor("#F19ABD");
        titleView.titleImg(R.drawable.advice_icon);
        titleView.setBack(R.drawable.steps_back, new TitleView.onBackLister() {
            @Override
            public void onClick(View button)
            {
                finish();
            }
        });
        titleView.right(R.string.operate, new TitleView.onSetLister() {
            @Override
            public void onClick(View view)
            {
                if (webView.getProgress() == 100)
                    optionWindow.showAsDropDown(view, -3, 0);
                else
                    MethodUtils.showToast(getApplicationContext(), "没有数据");
            }
        });

        frame_webView = (FrameLayout) findViewById(R.id.frame_webView);

        webView = new WebView(PushDetailActivity.this);
        webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        WebSettings webSettings = webView.getSettings();
        //设置 webView的缓存模式
        if (MethodUtils.is3G(PushDetailActivity.this) || MethodUtils.isWifi(PushDetailActivity.this)) {
            webSettings.setCacheMode(WebSettings.LOAD_NORMAL);
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        //设置AppCache缓存
        String appCachePath = getFilesDir() + "/appCache";
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAppCacheEnabled(true);

        //打开JavaScript
        webSettings.setJavaScriptEnabled(true);
        //打开 DomStorage 缓存
        webSettings.setDomStorageEnabled(true);
        //打开 Databases 缓存
        webSettings.setDatabaseEnabled(true);

        webSettings.setDefaultTextEncodingName("UTF-8");

        //设置不用系统浏览器打开,直接显示在当前Webview
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                MethodUtils.showToast(PushDetailActivity.this, "加载进度为：" + newProgress + "%");
            }
        });

        frame_webView.addView(webView);

    }


    private void initPopubWindow()
    {
        optionView = this.getLayoutInflater().inflate(R.layout.bracelet_advice_operate, null);
        tvMsgCollect = (TextView) optionView.findViewById(R.id.tv_message_collect);

        tvMsgDelete = (TextView) optionView.findViewById(R.id.tv_message_delete);
        tvShare = (TextView) optionView.findViewById(R.id.tv_share);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels / 3;//宽度
        optionWindow = new PopupWindow(optionView, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        optionWindow.setOutsideTouchable(true);
        optionWindow.setBackgroundDrawable(new BitmapDrawable());

        tvMsgCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Map<String, Object> parmas = new HashMap<>();
                parmas.put("uid", SpHelp.getUserId());
                parmas.put("mid", pushMessage.getId());
                HttpRequest.get(URLConstant.URL_FAVORITE, null, parmas, new HttpRequest.HttpRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response)
                    {
                        if (response.optString("error").equals("0")) {
                            if (!pushMessage.isCollected()) {
                                pushMessage.setCollected(true);
                                tvMsgCollect.setText("取消收藏");
                                MethodUtils.showToast(getApplicationContext(), "收藏成功");
                            } else {
                                pushMessage.setCollected(false);
                                tvMsgCollect.setText("收藏");
                                MethodUtils.showToast(getApplicationContext(), "取消收藏成功");
                            }
                        } else {
                            MethodUtils.showToast(getApplicationContext(), "操作失败:　" + response.optString("error_info"));
                        }
                    }

                    @Override
                    public void onFailure()
                    {
                        MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
                    }
                });

                if (null != optionWindow)
                    optionWindow.dismiss();
            }
        });

        tvMsgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showDeleteDialog();
            }
        });
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                try {
                    showShare();
                } catch (Exception e) {
                    Log.e("ERROR", "分享异常***************");
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initData();
    }

    private void initData()
    {
        pushMessage = (PushMessage) getIntent().getSerializableExtra("pushMessage");
        if (pushMessage.isCollected())
            tvMsgCollect.setText("取消收藏");
        if (!TextUtils.isEmpty(pushMessage.getId())) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", pushMessage.getId());
            HttpRequest.get(URLConstant.URL_PUSH_DETAIL, null, params, new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response)
                {
                    if (response.optString("error").equals("0")) {
                        String htmlText = response.optString("data");
                        Log.e("HTTP", htmlText);
                        webView.loadDataWithBaseURL(null, htmlText, "text/html", "utf-8", null);
                    } else {
                        MethodUtils.showToast(getApplicationContext(), "获取消息详情失败,原因和连接服务器相关：" + response.optString("error_info"));
                    }
                }

                @Override
                public void onFailure()
                {
                    MethodUtils.showToast(getApplicationContext(), "获取消息详情失败,请检查网络是否异常");
                }
            });
        } else {
            System.out.println("消息Id为空");
        }
    }



    private void showDeleteDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(PushDetailActivity.this);
        deleteDialog = builder.setMessage("                   删除后不可恢复")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        JSONObject params = new JSONObject();
                        try {
                            params.put("uId", SpHelp.getUserId())
                                    .put("msgId", pushMessage.getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        HttpRequest.post(URLConstant.URL_DELETE_HISTORY, params.toString(), new HttpRequest.HttpRequestCallback() {
                            @Override
                            public void onSuccess(JSONObject response)
                            {
                                if (response.optString("error").equals("0")) {
                                    MethodUtils.showToast(getApplicationContext(), "删除成功");
                                    deleteDialog.dismiss();
                                    optionWindow.dismiss();
                                    finish();
                                } else {
                                    MethodUtils.showToast(getApplicationContext(), "删除失败: " + response.optString("error_info"));
                                }
                            }

                            @Override
                            public void onFailure()
                            {
                                MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
                            }
                        });
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        deleteDialog.dismiss();
                        optionWindow.dismiss();
                    }
                }).create();

        deleteDialog.show();
    }

    private void showShare()
    {
        ShareSDK.initSDK(PushDetailActivity.this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        oks.setTitle("健康推送");
        String url = MyApplication.Ip_Address + URLConstant.URL_SHARE + "?msgId=" + pushMessage.getId();
        Log.e("ID", pushMessage.getId());
        oks.setText(url);
        oks.setImageUrl(MyApplication.Ip_Address + "images/android/ruyi.jpg");
        // 启动分享GUI
        oks.show(PushDetailActivity.this);
        if (null != optionWindow)
            optionWindow.dismiss();
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }

    }
}
