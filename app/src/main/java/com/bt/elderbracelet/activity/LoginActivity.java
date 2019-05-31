package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.Register;
import com.bt.elderbracelet.okhttp.HttpRequest;
import com.bt.elderbracelet.okhttp.URLConstant;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView2;
import com.bttow.elderbracelet.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity implements View.OnClickListener {
    private TitleView2 titleView;
    private Button btn_sure;
    private EditText et_num, et_password;
    private String str_num, str_password;
    private ModelDao modelDao;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_login);
        MyApplication.getInstance().addActivity(this);
        init();
    }

    private void init() {
        modelDao = new ModelDao(getApplicationContext());
        titleView = (TitleView2) findViewById(R.id.titleview);
        titleView.setbg(R.drawable.register_titlebg);
        titleView.setTitle(R.string.login);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        et_num = (EditText) findViewById(R.id.et_num);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_sure.setOnClickListener(this);
        titleView.setBack(R.drawable.steps_back, new TitleView2.onBackLister() {
            @Override
            public void onClick(View button) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
//                str_num = et_num.getText().toString().trim();
//                if (!TextUtils.isEmpty(str_num) && str_num.length() == 11) {
//
//                    if (MethodUtils.is3G(getApplicationContext()) || MethodUtils.isWifi(getApplicationContext())) {
//                        attemptAlreadyRegister();
//                    } else {
//                        MethodUtils.showToast(getApplicationContext(), "请检查网络是否正常!");
//                    }
//                } else {
//                    MethodUtils.showToast(getApplicationContext(), "电话号码不存在");
//                }
                SpHelp.saveUserId("13368176003");
                MyApplication.getInstance().exit(); // 清空所有activity栈
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void attemptAlreadyRegister() {
        str_num = et_num.getText().toString().trim();
        str_password = et_password.getText().toString().trim();

        if (TextUtils.isEmpty(str_num) && str_num.length() == 11)
            MethodUtils.showToast(getApplicationContext(), "请检查并重新输入电话号码");

        if (TextUtils.isEmpty(str_password))
            MethodUtils.showToast(getApplicationContext(), "请输入密码");

        if (!MethodUtils.is3G(getApplicationContext()) && !MethodUtils.isWifi(getApplicationContext()))
            MethodUtils.showToast(getApplicationContext(), "请检查网络是否正常!");
        try {
            JSONObject object = new JSONObject();
            object.put("uId", str_num);
            object.put("password", str_password);
            HttpRequest.post(URLConstant.URL_ALREADY_REGISTER, object.toString(), new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optString("error").equals("0")) {
                        MethodUtils.showToast(getApplicationContext(), "验证成功");

                        JSONObject data = response.optJSONObject("data");
                        Register register = new Register();
                        register.setName(data.optString("name"));
                        register.setNum(data.optString("phone"));
                        register.setProvince(data.optString("province"));
                        register.setCity(data.optString("city"));
                        register.setArea(data.optString("area"));
                        register.setSex(data.optString("sex"));
                        register.setAge(data.optString("age"));
                        register.setHeight(data.optString("height"));
                        register.setWeight(data.optString("weight"));
                        register.setStepDistance(data.optString("stepDistance"));
                        register.setUrgentContactName(data.optString("urgentName"));
                        register.setUrgentContactPhone(data.optString("urgentPhone"));
                        register.setServiceId(data.optString("cId"));

                        modelDao.insertRegister(register);
                        SpHelp.saveUserId(data.optString("phone"));

                        MyApplication.getInstance().exit(); // 清空所有activity栈
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        MethodUtils.showToast(getApplicationContext(), "验证失败: " + response.optString("error_info"));
                    }
                }

                @Override
                public void onFailure() {
                    MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
