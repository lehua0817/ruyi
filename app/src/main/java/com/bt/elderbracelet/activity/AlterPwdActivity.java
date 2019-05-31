package com.bt.elderbracelet.activity;


import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.entity.others.Event;
import com.bt.elderbracelet.okhttp.HttpRequest;
import com.bt.elderbracelet.okhttp.URLConstant;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bttow.elderbracelet.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class AlterPwdActivity extends Activity implements View.OnClickListener {
    private TitleView titleView;
    private EditText etOldPwd, etNewPwd, etVerifyPwd, etVerifyCode;
    private Button btSend, btAlter;
    private ImageView imgHide;
    private final int INTERVAL_TIME = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alter_pwd);
        MyApplication.getInstance().addActivity(this);
        EventBus.getDefault().register(this);

        initView();
    }

    private void initView()
    {
        titleView = (TitleView) findViewById(R.id.titleview);
        titleView.setTitle(R.string.alter_password);
        titleView.setcolor("#495677");
        titleView.settextcolor("#ffffff");
        titleView.setBack(R.drawable.steps_back, new TitleView.onBackLister() {
            @Override
            public void onClick(View button)
            {
                finish();
            }
        });

        etOldPwd = (EditText) findViewById(R.id.et_old_pwd);
        etNewPwd = (EditText) findViewById(R.id.et_new_pwd);
        etVerifyPwd = (EditText) findViewById(R.id.et_verify_pwd);
        etVerifyCode = (EditText) findViewById(R.id.et_verifyCode);
        btSend = (Button) findViewById(R.id.bt_send);
        btSend.setOnClickListener(this);
        btAlter = (Button) findViewById(R.id.bt_alter);
        btAlter.setOnClickListener(this);
        imgHide = (ImageView) findViewById(R.id.img_hide);
        imgHide.setOnClickListener(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public int count = INTERVAL_TIME;
    public Timer timer = null;
    public TimerTask task = null;
    public Message msg = null;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            String content = (String) (msg.obj);
            btSend.setText(content);
            if (!btSend.isEnabled() && content.equals("重新获取")) {
                btSend.setEnabled(true);
                btSend.setBackgroundColor(getResources().getColor(R.color.bg_btn_press_color));
            }
        }
    };

    private boolean isHide = true;

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.img_hide:
                if (isHide) {
                    etNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); //显示密码
                    etVerifyPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); //显示密码
                    imgHide.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hide_pwd));
                    isHide = false;
                } else {
                    etNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance()); //隐藏密码
                    etVerifyPwd.setTransformationMethod(PasswordTransformationMethod.getInstance()); //隐藏密码
                    imgHide.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.show_pwd));
                    isHide = true;
                }
                break;
            case R.id.bt_send:

                btSend.setEnabled(false);
                btSend.setBackgroundColor(getResources().getColor(R.color.gray));
                /**
                 *  向服务器获取验证码
                 */
                timer = new Timer(true);
                task = new TimerTask() {
                    @Override
                    public void run()
                    {
                        msg = new Message();
                        msg.obj = count + "秒";
                        handler.sendMessage(msg);
                        if (count == 0) {
                            task.cancel();
                            task = null;
                            timer.cancel();
                            timer = null;
                            msg = new Message();
                            msg.obj = "重新获取";

                            count = INTERVAL_TIME;
                            handler.sendMessage(msg);
                        }
                        count--;
                    }
                };
                timer.schedule(task, 0, 1000);

                if (MethodUtils.isWifi(getApplicationContext())
                        || MethodUtils.is3G(getApplicationContext())) {
                     getVerifyCode();
                } else {
                    MethodUtils.showToast(getApplicationContext(), "请检查网络是否良好");
                }
                break;
            case R.id.bt_alter:
                String oldPwd = etOldPwd.getText().toString().trim();
                String newPwd = etNewPwd.getText().toString().trim();
                String verifyPwd = etVerifyPwd.getText().toString().trim();
                String verifyCode = etVerifyCode.getText().toString().trim();
                if (TextUtils.isEmpty(oldPwd)) {
                    MethodUtils.showToast(AlterPwdActivity.this, "旧密码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(newPwd)) {
                    MethodUtils.showToast(AlterPwdActivity.this, "新密码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(verifyPwd)) {
                    MethodUtils.showToast(AlterPwdActivity.this, "确认密码不能为空");
                    return;
                }
                if (!newPwd.equals(verifyPwd)) {
                    MethodUtils.showToast(AlterPwdActivity.this, "密码和确认密码不相同");
                    return;
                }
                if (TextUtils.isEmpty(verifyCode)) {
                    MethodUtils.showToast(AlterPwdActivity.this, "验证码不能为空");
                    return;
                }

                if (MethodUtils.isWifi(getApplicationContext())
                        || MethodUtils.is3G(getApplicationContext())) {
                    uploadNewPwd();
                } else {
                    MethodUtils.showToast(getApplicationContext(), "请检查网络是否良好");
                }

                break;
            default:
                break;
        }
    }


    public void getVerifyCode()
    {
        JSONObject object = new JSONObject();
        try {
            object.put("phone", SpHelp.getUserId());

            System.out.println("AlterPwdActivity  :  " + SpHelp.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest.post(URLConstant.URL_GET_VERIFY_CODE, object.toString(), new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response)
            {
                if (response.optString("error").equals("0")) {
                    MethodUtils.showToast(getApplicationContext(), "验证码获取成功");
                } else {
                    MethodUtils.showToast(getApplicationContext(), "验证码获取失败: " + response.optString("error_info"));
                }
            }

            @Override
            public void onFailure()
            {
                MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
            }
        });
    }

    public void uploadNewPwd()
    {
        JSONObject object = new JSONObject();
        try {
            object.put("phone", SpHelp.getUserId());
            object.put("oldPassword", etOldPwd.getText().toString().trim());
            object.put("newPassword", etNewPwd.getText().toString().trim());
            object.put("verifyCode", etVerifyCode.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest.post(URLConstant.URL_ALTER_PWD, object.toString(), new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response)
            {
                if (response.optString("error").equals("0")) {
                    MethodUtils.showToast(getApplicationContext(), "修改密码成功");
                } else {
                    MethodUtils.showToast(getApplicationContext(), "修改密码失败: " + response.optString("error_info"));
                }
            }

            @Override
            public void onFailure()
            {
                MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
            }
        });
    }

    public void onEventMainThread(Event event)
    {
        if (!TextUtils.isEmpty(event.msg)) {
            System.out.println(event.msg);
            if (event.msg.contains("验证码是："))
                etVerifyCode.setText(getCodeFromMsg(event.msg));
        }
    }

    public String getCodeFromMsg(String msg)
    {
        String[] strs = msg.split("验证码是：");
        return strs[1].substring(0, 4);
    }
}
