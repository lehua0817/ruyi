package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.okhttp.HttpRequest;
import com.bt.elderbracelet.tools.DateTimeFormatter;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView;
import com.bttow.elderbracelet.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.bt.elderbracelet.okhttp.URLConstant.URL_CONTACT_SERVICE;

/**
 * 提醒类
 *
 * @author Administrator
 */
public class ContactServicesActivity extends Activity implements OnClickListener {
    @Bind(R.id.titleview)        //标题栏
            TitleView titleview;
    @Bind(R.id.tv_revice_call)  //您的呼叫已受理 请保持通话畅通
            TextView tvReviceCall;
    @Bind(R.id.lin_call)        //拨打电话
            LinearLayout linCall;
    @Bind(R.id.lin_text_bg)     //也是拨打电话
            LinearLayout linTextBg;
    @Bind(R.id.et_input_advice)   //编辑留言框
            EditText etInputAdvice;
    @Bind(R.id.btn_advice_send)   //发送留言
            Button btnAdviceSend;

    private String strInputWords;
    private Intent intent;
    private String uid_project = "";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_contact_services);
        MyApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        intent = getIntent();                          //只有MainActivity 会跳转到这个界面，同时携带了数据  uid_project
        uid_project = intent.getStringExtra("uid");

        titleview = (TitleView) findViewById(R.id.titleview);
        titleview.setTitle(R.string.services_call);
        titleview.titleImg(R.drawable.services_icon);
        titleview.setcolor("#85C226");
        titleview.settextcolor("#ffffff");
        titleview.setBack(R.drawable.steps_back, new TitleView.onBackLister() {

            @Override
            public void onClick(View button)
            {
                finish();
            }
        });
        linCall.setOnClickListener(this);            //给拨打电话按钮设置监听器
        btnAdviceSend.setOnClickListener(this);     //给 发送留言 按钮设置监听器
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        ButterKnife.unbind(this);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.lin_call:    //问题：这个触发时事件应该是拨打客服电话，但实际上也是发送留言到服务器
                strInputWords = etInputAdvice.getText().toString();
                contactService(uid_project, null);
                break;
            case R.id.btn_advice_send:
                strInputWords = etInputAdvice.getText().toString();
                if (TextUtils.isEmpty(strInputWords)) {
                    MethodUtils.showToast(getApplicationContext(), "请把留言填写完整");
                }
                contactService(uid_project, strInputWords);
                break;
        }
    }

    private void contactService(String uId, String message)
    {
        try {
            JSONObject object = new JSONObject();
            object.put("uId", uId)
                    .put("date", DateTimeFormatter.YMDHM.format(new Date()))
                    .put("message", message);

            final String noti = TextUtils.isEmpty(message) ? "已受理您的留言,我们马上处理！" : "已受理您的留言,我们马上处理！";

            HttpRequest.post(URL_CONTACT_SERVICE, object.toString(), new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response)
                {
                    if (response.optString("error").equals("0")) {
                        Toast.makeText(ContactServicesActivity.this, noti, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ContactServicesActivity.this, "请求失败: " + response.optString("error_info"), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure()
                {
                    Toast.makeText(ContactServicesActivity.this, "请求异常, 请稍后再试", Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //版本名
    public static String getVersionName(Context context)
    {
        return getPackageInfo(context).versionName;
    }

    private static PackageInfo getPackageInfo(Context context)
    {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
}
