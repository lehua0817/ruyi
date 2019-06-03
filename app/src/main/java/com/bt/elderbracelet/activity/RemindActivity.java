package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.protocal.RemoteServiceCallback;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

/**
 * 提醒类
 *
 * @author Administrator
 */
public class RemindActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
    private TitleView titleView;//标题栏
    private CheckBox cb_phone_call_remind;//来电提醒
    private CheckBox cb_phone_msg_remind;//短信提醒
    private LinearLayout ll_clock_remind;//闹钟提醒
    private LinearLayout ll_medicine_remind;//吃药提醒
    private LinearLayout ll_fd_remind;//防丢提醒
    private LinearLayout ll_sedentary_remind;//久坐提醒
    private LinearLayout ll_sleep_remind;//睡眠提醒
    private LinearLayout ll_set_device_img;//功能设置.
    private AlertDialog.Builder builder;
    private int index = 0;
    private TextView tv_version_name;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                MethodUtils.showToast(RemindActivity.this, "设置成功");
                //将新的睡眠提醒数据保存在SharePreference中
                if (index == 0) {
                    SpHelp.saveFdRemind(SpHelp.FD_REMIND_CLOSE);
                } else if (index == 1) {
                    SpHelp.saveFdRemind(SpHelp.FD_REMIND_OPEN);
                }
            } else {
                MethodUtils.showToast(RemindActivity.this, "设置失败");
            }
        }
    };


    private IRemoteService mService;
    private IServiceCallback mServiceCallback = new RemoteServiceCallback() {

        @Override
        public void onSetAntiLost(int result) throws RemoteException {
            Message msg = Message.obtain();
            msg.what = result;
            mHandler.sendMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_remind);
        MyApplication.getInstance().addActivity(this);
        mService = MyApplication.remoteService;
        try {
            mService.registerCallback(mServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        initView();
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.titleview);
        titleView.setTitle(R.string.remind_title);
        titleView.setcolor("#495677");
        titleView.settextcolor("#ffffff");
        //		titleView.titleImg(R.drawable.heart_titleimg);
        titleView.setBack(R.drawable.steps_back, new onBackLister() {
            @Override
            public void onClick(View button) {
                finish();
            }
        });

        cb_phone_call_remind = (CheckBox) findViewById(R.id.cb_phone_call_remind);
        cb_phone_call_remind.setOnCheckedChangeListener(this);
        boolean callEnable = SpHelp.getPhoneCallRemind();
        //这里是根据系统SharePreference中保存的TELEPHONE_REMIND，来初始化复选框应该显示的界面
        if (callEnable) {
            cb_phone_call_remind.setChecked(true);
        } else {
            cb_phone_call_remind.setChecked(false);
        }

        cb_phone_msg_remind = (CheckBox) findViewById(R.id.cb_phone_msg_remind);
        cb_phone_msg_remind.setOnCheckedChangeListener(this);
        boolean msgEnable = SpHelp.getPhoneMsgRemind();
        if (msgEnable) {
            cb_phone_msg_remind.setChecked(true);
        } else {
            cb_phone_msg_remind.setChecked(false);
        }


        tv_version_name = (TextView) findViewById(R.id.tv_vertion_name);
        tv_version_name.setText(getVersionName(this));      //设置软件版本号

        ll_clock_remind = (LinearLayout) findViewById(R.id.ll_clock_remind);
        ll_medicine_remind = (LinearLayout) findViewById(R.id.ll_medicine_remind);
        ll_fd_remind = (LinearLayout) findViewById(R.id.ll_fd_remind);
        ll_sedentary_remind = (LinearLayout) findViewById(R.id.ll_sedentary_remind);
        ll_sleep_remind = (LinearLayout) findViewById(R.id.ll_sleep_remind);
        ll_set_device_img = (LinearLayout) findViewById(R.id.ll_set_device_img);

        ll_clock_remind.setOnClickListener(this);
        ll_medicine_remind.setOnClickListener(this);
        ll_fd_remind.setOnClickListener(this);
        ll_sedentary_remind.setOnClickListener(this);
        ll_sleep_remind.setOnClickListener(this);
        ll_set_device_img.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_phone_call_remind://来电提醒
                SpHelp.savePhoneCallRemind(isChecked);
                break;
            case R.id.cb_phone_msg_remind://短信提醒
                SpHelp.savePhoneMsgRemind(isChecked);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.ll_clock_remind://进入闹钟提醒设置界面
                intent = new Intent(getApplicationContext(), ClockRemindActivity.class);
                startActivity(intent);
                break;
//            case R.id.ll_medicine_remind://进入吃药提醒设置界面
//                intent = new Intent(getApplicationContext(), MedicineRemindActivity.class);
//                startActivity(intent);
//                break;
            case R.id.ll_sedentary_remind: //进入久坐提醒设置界面
                intent = new Intent(getApplicationContext(), SitRemindActivity.class);
                startActivity(intent);
                break;

            case R.id.ll_sleep_remind:  //进入睡眠提醒设置界面
                intent = new Intent(getApplicationContext(), SleepRemindActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_set_device_img:  //进入功能设置界面
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_fd_remind://注意：防丢提醒设置并没有进入新的Activity，而是弹出一个AlertDialog
                //弹出单选框
                setAlertDialog();
                /**
                 * 防丢提醒工作原理： 用户点击 R.id.ll_fd_remind，弹出AlertDialog，其中有单选框，
                 * 系统根据SharePreference中保存的 SharePreference.FD_REMIND，来设置该单选框的初始选中项，
                 * 当用户选中 打开防丢提醒后，则手机会向手环发送 开启防丢提醒的指令，手环收到指令后，打开自己的防丢提醒设置，
                 * 当成功开启后，手环会向手机发送 设置成功的反馈指令，当手机收到该指令后，才算是真正地开启了 防丢提醒，
                 * 然后 再次设置 SharePreference中SystemData.FD_REMIND的值，并保存。
                 */
                break;
            default:
                break;
        }
    }

    /**
     * 弹出单选框
     */
    public void setAlertDialog() {
        String[] stringArray = new String[]{"关闭防丢提醒", "开启防丢提醒"};
        if (builder == null) {
            builder = new AlertDialog.Builder(RemindActivity.this);
            builder.setTitle(getResources().getString(R.string.fd_remind));
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!MyApplication.isConnected) {
                        MethodUtils.showToast(getApplicationContext(), "请先连接蓝牙设备");
                        finish();
                    } else {
                        callRemoteSetAntiLost(index > 0);
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.setSingleChoiceItems(stringArray, SpHelp.getFdRemind(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = which;              //即 index = 0;关闭防丢提醒,index = 1，开启防丢提醒
                    }
                });
        builder.show();
    }

    //版本名
    public static String getVersionName(Context context) {    //获取软件版本号
        return getPackageInfo(context).versionName;
    }

    private static PackageInfo getPackageInfo(Context context) {      //PackageInfo 是系统自定义的类，表示 AndroidManifest中所有数据信息
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

    private void callRemoteSetAntiLost(boolean openAntiLost) {
        if (mService != null) {
            try {
                mService.setAntiLost(openAntiLost);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

}
