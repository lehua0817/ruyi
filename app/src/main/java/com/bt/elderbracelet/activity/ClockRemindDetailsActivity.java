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
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.entity.ClockEntity;
import com.bt.elderbracelet.protocal.RemoteServiceCallback;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.AlarmInfoItem;
import com.sxr.sdk.ble.keepfit.aidl.BleClientOption;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

import java.util.ArrayList;

/**
 * @author Administrator
 * 闹钟提醒详情类
 * <p>
 * 这个类具体执行过程：在ClockRemindActivity中点击某个闹钟条目，会跳转到ClockRemindDetailsActivity中
 * ClockRemindDetailsActivity的初始显示界面由 被点击中的闹钟条目 来决定
 * 首先：根据传递过来的闹钟事件实体对象(entity)，设置本界面中的每天闹钟时间(tv_time)，
 * 同时，根据entity中的属性boolean[] isSetDay = {true,true,true,true,true,false,false};
 * 来设置本界面中7个复选框是否被选中
 * 如果我们要修改闹钟时间，点击tv_time，弹出TimePickerDialog，用传递过来的闹钟事件实体对象(entity)
 * 来初始化要显示的TimePickerDialog ，当你在TimePickerDialog 中选中修改时间以后，以新时间修改entity；
 * 当你点击了"保存"按钮以后，要将新的闹钟事件保存在SharePreferencr中，同时向手环发送闹钟指令，手环会自动更新
 * 其内部的闹钟设置时间
 */
public class ClockRemindDetailsActivity extends Activity implements OnClickListener, CompoundButton.OnCheckedChangeListener {
    private TitleView titleView;//title
    private TextView tv_time; //提醒时间
    private CheckBox cb_open;
    private CheckBox cb_shock;
    private CheckBox cb_music;
    private CheckBox cb_one;
    private CheckBox cb_two;
    private CheckBox cb_three;
    private CheckBox cb_four;
    private CheckBox cb_five;
    private CheckBox cb_six;
    private CheckBox cb_seven;
    private CheckBox cb_repeat_once;

    private ClockEntity entity;

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            entity.hour = hourOfDay;
            entity.minute = minute;
            tv_time.setText(BaseUtils.timeConversion(hourOfDay, minute));
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                MethodUtils.showToast(ClockRemindDetailsActivity.this, "闹钟设置成功");

                //将新的闹钟数据保存在SharePreference中
                SpHelp.saveObject(SpHelp.getClockKey(entity.id), entity);
                finish();
            } else {
                MethodUtils.showToast(ClockRemindDetailsActivity.this, "闹钟设置失败");
            }
        }
    };


    private IRemoteService mService;
    private IServiceCallback mServiceCallback = new RemoteServiceCallback() {
        @Override
        public void onSetAlarm(int result) throws RemoteException {
            Message msg = Message.obtain();
            if (result == 1) {
                msg.what = 1;
            } else {
                msg.what = 0;
            }
            mHandler.sendMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_clock_remind_details);
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
        titleView.setTitle(R.string.clock_remind_details);
        titleView.setcolor("#a9d559");
        titleView.settextcolor("#ffffff");
        titleView.titleImg(R.drawable.clock_icon);
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

                entity.enableMonday = cb_one.isChecked() ? 1 : 0;
                entity.enableTuesday = cb_two.isChecked() ? 1 : 0;
                entity.enableWednesday = cb_three.isChecked() ? 1 : 0;
                entity.enableThursday = cb_four.isChecked() ? 1 : 0;
                entity.enableFriday = cb_five.isChecked() ? 1 : 0;
                entity.enableSaturday = cb_six.isChecked() ? 1 : 0;
                entity.enableSunday = cb_seven.isChecked() ? 1 : 0;

                entity.isSingle = cb_repeat_once.isChecked();
//                entity.isMusic = cb_music.isChecked();
//                entity.isShock = cb_shock.isChecked();

                //发送设置闹钟指令
                callSetAlarm(entity.id,
                        entity.isOpen,
                        entity.hour,
                        entity.minute,
                        entity.enableMonday,
                        entity.enableTuesday,
                        entity.enableWednesday,
                        entity.enableThursday,
                        entity.enableFriday,
                        entity.enableSaturday,
                        entity.enableSunday,
                        "",
                        entity.isSingle
                );
            }
        });

        tv_time = (TextView) findViewById(R.id.tv_time);

        cb_open = (CheckBox) findViewById(R.id.cb_open);
        cb_music = (CheckBox) findViewById(R.id.cb_music);
        cb_shock = (CheckBox) findViewById(R.id.cb_shock);

        cb_one = (CheckBox) findViewById(R.id.cb_one);
        cb_two = (CheckBox) findViewById(R.id.cb_two);
        cb_three = (CheckBox) findViewById(R.id.cb_three);
        cb_four = (CheckBox) findViewById(R.id.cb_four);
        cb_five = (CheckBox) findViewById(R.id.cb_five);
        cb_six = (CheckBox) findViewById(R.id.cb_six);
        cb_seven = (CheckBox) findViewById(R.id.cb_seven);
        cb_repeat_once = (CheckBox) findViewById(R.id.cb_repeat_once);

    }

    private void initListener() {
        cb_open.setOnCheckedChangeListener(this);
        cb_music.setOnCheckedChangeListener(this);
        cb_shock.setOnCheckedChangeListener(this);
        cb_one.setOnCheckedChangeListener(this);
        cb_two.setOnCheckedChangeListener(this);
        cb_three.setOnCheckedChangeListener(this);
        cb_four.setOnCheckedChangeListener(this);
        cb_five.setOnCheckedChangeListener(this);
        cb_six.setOnCheckedChangeListener(this);
        cb_seven.setOnCheckedChangeListener(this);
        cb_repeat_once.setOnCheckedChangeListener(this);

        tv_time.setOnClickListener(this);

    }

    private void initData() {
        entity = (ClockEntity) getIntent().getSerializableExtra("clock");
        if (entity == null) {
            return;
        }

        tv_time.setText(BaseUtils.timeConversion(entity.hour, entity.minute));

        cb_open.setChecked(entity.isOpen > 0);
//        cb_music.setChecked(entity.isMusic);
//        cb_shock.setChecked(entity.isShock);

        cb_one.setChecked(entity.enableMonday > 0);
        cb_two.setChecked(entity.enableTuesday > 0);
        cb_three.setChecked(entity.enableWednesday > 0);
        cb_four.setChecked(entity.enableThursday > 0);
        cb_five.setChecked(entity.enableFriday > 0);
        cb_six.setChecked(entity.enableSaturday > 0);
        cb_seven.setChecked(entity.enableSunday > 0);
        cb_repeat_once.setChecked(entity.isSingle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_time:
                showDialog(1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_one:
            case R.id.cb_two:
            case R.id.cb_three:
            case R.id.cb_four:
            case R.id.cb_five:
            case R.id.cb_six:
            case R.id.cb_seven:
                if (isChecked) {
                    if (cb_repeat_once.isChecked()) {
                        cb_repeat_once.setChecked(false);
                    }
                }
                break;
            case R.id.cb_repeat_once:
                if (isChecked) {
                    if (cb_one.isChecked()) {
                        cb_one.setChecked(false);
                    }
                    if (cb_two.isChecked()) {
                        cb_two.setChecked(false);
                    }
                    if (cb_three.isChecked()) {
                        cb_three.setChecked(false);
                    }
                    if (cb_four.isChecked()) {
                        cb_four.setChecked(false);
                    }
                    if (cb_five.isChecked()) {
                        cb_five.setChecked(false);
                    }
                    if (cb_six.isChecked()) {
                        cb_six.setChecked(false);
                    }
                    if (cb_seven.isChecked()) {
                        cb_seven.setChecked(false);
                    }
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        TimePickerDialog dialog = null;
        if (id == 1) {
            dialog = new TimePickerDialog(this, onTimeSetListener,
                    entity.hour, entity.minute, true); //false代表12小时制
        }
        return dialog;
    }

    private void callSetAlarm(int alarm_id, int enableType, int hour, int minute, int enableMonday,
                              int enableTuesday, int enableWednesday, int enableThursday, int enableFriday,
                              int enableSaturday, int enableSunday, String content, boolean isSingle) {
        if (mService != null) {
            try {
                ArrayList<AlarmInfoItem> lAlarmInfo = new ArrayList<AlarmInfoItem>();
                AlarmInfoItem item = new AlarmInfoItem(alarm_id, enableType, hour, minute, enableMonday,
                        enableTuesday, enableWednesday, enableThursday, enableFriday,
                        enableSaturday, enableSunday, content, isSingle);
                lAlarmInfo.add(item);
                BleClientOption bco = new BleClientOption(null, null, lAlarmInfo);
                mService.setOption(bco);
                mService.setAlarm();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

}
