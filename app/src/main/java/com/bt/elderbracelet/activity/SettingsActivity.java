package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bonten.ble.application.MyApplication;
import com.bonten.ble.servise.BleService;
import com.bt.elderbracelet.entity.others.Event;
import com.bt.elderbracelet.protocal.OrderData;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bttow.elderbracelet.R;

import de.greenrobot.event.EventBus;

public class SettingsActivity extends Activity implements View.OnClickListener {

    TitleView titleView;
    TextView textMac;
    LinearLayout ll_restart, ll_resetPwd, ll_unlock, ll_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        MyApplication.getInstance().addActivity(this);
        EventBus.getDefault().register(this);
        initView();
        initListener();
        initData();
    }

    private void initView()
    {
        titleView = (TitleView) findViewById(R.id.titleview);
        titleView.setTitle(R.string.display_show);
        titleView.setcolor("#495677");
        titleView.settextcolor("#ffffff");

        titleView.setBack(R.drawable.steps_back, new TitleView.onBackLister() {
            @Override
            public void onClick(View button)
            {
                finish();
            }
        });

        textMac = (TextView) findViewById(R.id.text_mac);
        textMac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                BleService.sendCommand(OrderData.getCommonOrder(OrderData.GET_DEVICE_MAC_ORDER));
            }
        });

        ll_restart = (LinearLayout) findViewById(R.id.ll_restart);
        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
        ll_logout = (LinearLayout) findViewById(R.id.ll_logout);
        ll_resetPwd = (LinearLayout) findViewById(R.id.ll_resetPwd);
    }

    private void initListener()
    {
        ll_restart.setOnClickListener(this);
        ll_unlock.setOnClickListener(this);
        ll_logout.setOnClickListener(this);
        ll_resetPwd.setOnClickListener(this);
    }

    private void initData()
    {
        if (!MyApplication.isConndevice) {
            MethodUtils.showToast(SettingsActivity.this, "尚未连接手环");
            finish();
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
            return;
        }
        BleService.sendCommand(OrderData.getCommonOrder(OrderData.GET_DEVICE_MAC_ORDER));
    }

    AlertDialog.Builder builder = null;
    AlertDialog dialog = null;

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.ll_restart:
                if (!MyApplication.isConndevice) {
                    MethodUtils.showToast(SettingsActivity.this, "尚未连接手环");
                    finish();
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
                }
                builder = new AlertDialog.Builder(this);
                builder.setTitle("请确定是否要重启蓝牙设备？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        BleService.sendCommand(OrderData.getCommonOrder(OrderData.RESTRAT_DEVICE_ORDER));
                        SystemClock.sleep(2000);
                        finish();
                        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;
            case R.id.ll_unlock:

                builder = new AlertDialog.Builder(this);
                builder.setTitle("请确定是否要取消设备绑定？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        SpHelp.saveDeviceMac("");
                        MyApplication.getInstance().exit();
                        //finish();
                        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();

                break;
            case R.id.ll_logout:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("请确定是否要注销登入？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        SpHelp.saveUserId("");
                        SpHelp.saveDeviceMac("");
                        SpHelp.saveUserPhoto(null);
                        SpHelp.savePersonalDetailOne(null);
                        SpHelp.savePersonalDetailTwo(null);
                        SpHelp.savePersonalDetailThree(null);

                        BleService.blueToothServiceclose();   //断开手环和手机相连接

                        MyApplication.getInstance().exit();
                        //finish();
                        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;

            case R.id.ll_resetPwd:
                startActivity(new Intent(SettingsActivity.this, AlterPwdActivity.class));
                finish();
            default:
                break;
        }

    }

    public void onEventMainThread(Event event)
    {
        if (!TextUtils.isEmpty(event.mac)) {
            textMac.setText(event.mac);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}