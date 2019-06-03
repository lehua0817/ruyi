package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.entity.SleepClock;
import com.bt.elderbracelet.protocal.RemoteServiceCallback;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

/**
 * @author Administrator
 * 闹钟提醒详情类
 * <p>
 * 这个类具体执行过程：在ClockRemindActivity中点击某个闹钟条目，会跳转到ClockRemindDetailsActivity中
 * ClockRemindDetailsActivity的初始显示界面由 被点击中的闹钟条目 来决定
 * 首先：根据传递过来的闹钟事件实体对象(entity)，设置本界面中的每天闹钟时间(tvStartTime)，
 * 同时，根据clockEntity中的属性boolean[] isSetDay = {true,true,true,true,true,false,false};
 * 来设置本界面中7个复选框是否被选中
 * 如果我们要修改闹钟时间，点击tvStartTime，弹出TimePickerDialog，用传递过来的闹钟事件实体对象(entity)
 * 来初始化要显示的TimePickerDialog ，当你在TimePickerDialog 中选中修改时间以后，以新时间修改clockEntity；
 * 当你点击了"保存"按钮以后，要将新的闹钟事件保存在SharePreferencr中，同时向手环发送闹钟指令，手环会自动更新
 * 其内部的闹钟设置时间
 */
public class SleepRemindActivity extends Activity implements OnClickListener {
    private TitleView titleView;//title
    private TextView tvMidStartTime; //午睡开始时间
    private TextView tvMidEndTime; //午睡结束时间
    private TextView tvNightStartTime; //晚上入睡时间
    private TextView tvNightEndTime; //晚上早上时间
    private CheckBox cb_open;

    private SleepClock entity;

    TimePickerDialog.OnTimeSetListener midStartTimeListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            entity.midStartHour = hourOfDay;
            entity.midStartMinute = minute;
            tvMidStartTime.setText(BaseUtils.timeConversion(hourOfDay, minute));
        }
    };
    TimePickerDialog.OnTimeSetListener midEndTimeListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            entity.midEndHour = hourOfDay;
            entity.midEndMinute = minute;
            tvMidEndTime.setText(BaseUtils.timeConversion(hourOfDay, minute));
        }
    };

    TimePickerDialog.OnTimeSetListener nightStartTimeListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            entity.nightStartHour = hourOfDay;
            entity.nightStartMinute = minute;
            tvNightStartTime.setText(BaseUtils.timeConversion(hourOfDay, minute));
        }
    };
    TimePickerDialog.OnTimeSetListener nightEndTimeListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            entity.nightEndHour = hourOfDay;
            entity.nightEndMinute = minute;
            tvNightEndTime.setText(BaseUtils.timeConversion(hourOfDay, minute));
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                MethodUtils.showToast(SleepRemindActivity.this, "设置成功");
                //将新的睡眠提醒数据保存在SharePreference中
                SpHelp.saveObject(SpHelp.SLEEP_CLOCK, entity);

            } else {
                MethodUtils.showToast(SleepRemindActivity.this, "设置失败");
            }
        }
    };


    private IRemoteService mService;
    private IServiceCallback mServiceCallback = new RemoteServiceCallback() {
        @Override
        public void onSetSleepTime(int result) throws RemoteException {
            Message msg = Message.obtain();
            msg.what = result;
            mHandler.sendMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_sleep_remind);
        MyApplication.getInstance().addActivity(this);
        mService = MyApplication.remoteService;
        try {
            mService.registerCallback(mServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        initView();
        initListener();
        initData();
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.titleview);
        titleView.setTitle(R.string.sleep_remind_detail);
        titleView.setcolor("#a9d559");
        titleView.settextcolor("#ffffff");
        titleView.titleImg(R.drawable.sleep_icon);
        titleView.setBack(R.drawable.steps_back, new onBackLister() {
            @Override
            public void onClick(View button) {
                finish();
            }
        });
        titleView.right(R.string.save, new onSetLister() {

            @Override
            public void onClick(View button) {
                if (!MyApplication.isConnected) {
                    MethodUtils.showToast(getApplicationContext(), "请先连接蓝牙设备");
                    return;
                }

                MyApplication.cb_switch = cb_open.isChecked();
                entity.isOpen = cb_open.isChecked() ? 1 : 0;

                //发送设置闹钟指令
                callRemoteSetSleepTime(
                        entity.isOpen,
                        entity.midStartHour,
                        entity.midStartMinute,
                        entity.midEndHour,
                        entity.midEndMinute,
                        entity.nightStartHour,
                        entity.nightStartMinute,
                        entity.nightEndHour,
                        entity.nightEndMinute);
            }
        });

        tvMidStartTime = (TextView) findViewById(R.id.tv_mid_start_time);
        tvMidEndTime = (TextView) findViewById(R.id.tv_mid_end_time);
        tvNightStartTime = (TextView) findViewById(R.id.tv_night_start_time);
        tvNightEndTime = (TextView) findViewById(R.id.tv_night_end_time);

        cb_open = (CheckBox) findViewById(R.id.cb_open);
    }

    private void initListener() {
        tvMidStartTime.setOnClickListener(this);
        tvMidEndTime.setOnClickListener(this);
        tvNightStartTime.setOnClickListener(this);
        tvNightEndTime.setOnClickListener(this);
    }

    private void initData() {
        entity = (SleepClock) SpHelp.getObject(SpHelp.SLEEP_CLOCK);
        if (entity == null) {
            entity = new SleepClock();
        }
        tvMidStartTime.setText(BaseUtils.timeConversion(entity.midEndHour, entity.midStartMinute));
        tvMidEndTime.setText(BaseUtils.timeConversion(entity.midEndHour, entity.midEndMinute));
        tvNightStartTime.setText(BaseUtils.timeConversion(entity.nightStartHour, entity.nightStartMinute));
        tvNightEndTime.setText(BaseUtils.timeConversion(entity.nightEndHour, entity.nightEndMinute));

        cb_open.setChecked(entity.isOpen > 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_mid_start_time:
                showDialog(1);
                break;
            case R.id.tv_mid_end_time:
                showDialog(2);
                break;
            case R.id.tv_night_start_time:
                showDialog(3);
                break;
            case R.id.tv_night_end_time:
                showDialog(4);
                break;
            default:
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        TimePickerDialog dialog = null;
        if (id == 1) {
            dialog = new TimePickerDialog(this, midStartTimeListener,
                    entity.midStartHour, entity.midStartMinute, true); //true代表24小时制
        }
        if (id == 2) {
            dialog = new TimePickerDialog(this, midEndTimeListener,
                    entity.midEndHour, entity.midEndMinute, true);
        }
        if (id == 3) {
            dialog = new TimePickerDialog(this, nightStartTimeListener,
                    entity.nightStartHour, entity.nightStartMinute, true); //true代表24小时制
        }
        if (id == 4) {
            dialog = new TimePickerDialog(this, nightEndTimeListener,
                    entity.nightEndHour, entity.nightEndMinute, true);
        }
        return dialog;
    }

    private void callRemoteSetSleepTime(int isOpen,
                                        int midStartHour,
                                        int midStartMinute,
                                        int midEndHour,
                                        int midEndMinute,
                                        int nightStartHour,
                                        int nightStartMinute,
                                        int nightEndHour,
                                        int nightEndMinute) {
        if (mService != null) {
            try {
                mService.setSleepTime(midStartHour, midStartMinute, midEndHour, midEndMinute, nightStartHour,
                        nightStartMinute, nightEndHour, nightEndMinute);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
}
