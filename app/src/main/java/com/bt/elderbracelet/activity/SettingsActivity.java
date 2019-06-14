package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;

public class SettingsActivity extends Activity implements View.OnClickListener {

    TitleView titleView;
    TextView textMac;
    TextView textName;
    LinearLayout ll_restart, ll_resetPwd, ll_unlock, ll_logout;
    private IRemoteService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        MyApplication.getInstance().addActivity(this);
        mService = MyApplication.remoteService;

        initView();
        initListener();
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.titleview);
        titleView.setTitle(R.string.display_show);
        titleView.setcolor("#495677");
        titleView.settextcolor("#ffffff");

        titleView.setBack(R.drawable.steps_back, new TitleView.onBackLister() {
            @Override
            public void onClick(View button) {
                finish();
            }
        });

        textMac = (TextView) findViewById(R.id.text_mac);
        textName = (TextView) findViewById(R.id.text_name);
        textMac.setText(SpHelp.getDeviceMac());
        textName.setText(SpHelp.getDeviceName());

        ll_restart = (LinearLayout) findViewById(R.id.ll_restart);
        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
        ll_logout = (LinearLayout) findViewById(R.id.ll_logout);
        ll_resetPwd = (LinearLayout) findViewById(R.id.ll_resetPwd);
    }

    private void initListener() {
        ll_restart.setOnClickListener(this);
        ll_unlock.setOnClickListener(this);
        ll_logout.setOnClickListener(this);
        ll_resetPwd.setOnClickListener(this);
    }

    AlertDialog.Builder builder = null;
    AlertDialog dialog = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.ll_restart:
//                if (!MyApplication.isConnected) {
//                    MethodUtils.showToast(SettingsActivity.this, "尚未连接手环");
//                    finish();
//                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
//                }
//                builder = new AlertDialog.Builder(this);
//                builder.setTitle("请确定是否要重启蓝牙设备？");
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        dialog.dismiss();
//                        BleService.sendCommand(OrderData.getCommonOrder(OrderData.RESTRAT_DEVICE_ORDER));
//                        SystemClock.sleep(2000);
//                        finish();
//                        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
//                        startActivity(intent);
//                    }
//                });
//                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        dialog.dismiss();
//                    }
//                });
//                dialog = builder.create();
//                dialog.show();
//                break;
            case R.id.ll_unlock:

                builder = new AlertDialog.Builder(this);
                builder.setTitle("请确定是否要取消设备绑定？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SpHelp.saveDeviceMac("");
                        MyApplication.getInstance().exit();
                        callRemoteDisconnect();
                        Intent intent = new Intent(SettingsActivity.this, BindActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SpHelp.saveUserId("");
                        SpHelp.saveDeviceMac("");
                        SpHelp.saveUserPhoto(null);
                        SpHelp.saveObject(SpHelp.REGISTER_INFO, null);

                        callRemoteDisconnect();   //断开手环和手机相连接

                        MyApplication.getInstance().exit();
                        //finish();
                        Intent intent = new Intent(SettingsActivity.this, BindActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

    private void callRemoteDisconnect() {

        if (mService != null) {
            try {
                mService.disconnectBt(true);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }


}