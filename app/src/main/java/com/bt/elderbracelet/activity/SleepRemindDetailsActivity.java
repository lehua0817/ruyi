package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bonten.ble.application.MyApplication;
import com.bonten.ble.servise.BleService;
import com.bt.elderbracelet.entity.SleepClock;
import com.bt.elderbracelet.entity.others.Event;
import com.bt.elderbracelet.protocal.OrderData;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;

import de.greenrobot.event.EventBus;

/**
 * @author Administrator
 *         闹钟提醒详情类
 *         <p>
 *         这个类具体执行过程：在ClockRemindActivity中点击某个闹钟条目，会跳转到ClockRemindDetailsActivity中
 *         ClockRemindDetailsActivity的初始显示界面由 被点击中的闹钟条目 来决定
 *         首先：根据传递过来的闹钟事件实体对象(clockEntity)，设置本界面中的每天闹钟时间(tvStartTime)，
 *         同时，根据clockEntity中的属性boolean[] isSetDay = {true,true,true,true,true,false,false};
 *         来设置本界面中7个复选框是否被选中
 *         如果我们要修改闹钟时间，点击tvStartTime，弹出TimePickerDialog，用传递过来的闹钟事件实体对象(clockEntity)
 *         来初始化要显示的TimePickerDialog ，当你在TimePickerDialog 中选中修改时间以后，以新时间修改clockEntity；
 *         当你点击了"保存"按钮以后，要将新的闹钟事件保存在SharePreferencr中，同时向手环发送闹钟指令，手环会自动更新
 *         其内部的闹钟设置时间
 */
public class SleepRemindDetailsActivity extends Activity implements OnClickListener, CompoundButton.OnCheckedChangeListener {
    private TitleView titleView;//title
    private TextView tvStartTime; //计划入睡时间
    private TextView tvEndTime; //计划醒来时间
    private CheckBox cb_open;
    private CheckBox cb_music;
    private CheckBox cb_shock;

    private CheckBox cb_one;
    private CheckBox cb_two;
    private CheckBox cb_three;
    private CheckBox cb_four;
    private CheckBox cb_five;
    private CheckBox cb_six;
    private CheckBox cb_seven;
    private CheckBox cb_repeat_once;

    private SleepClock clockEntity;

    TimePickerDialog.OnTimeSetListener startTimeListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            clockEntity.hour = hourOfDay;
            clockEntity.minute = minute;
            tvStartTime.setText(BaseUtils.timeConversion(hourOfDay, minute));
        }
    };
    TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            clockEntity.endHour = hourOfDay;
            clockEntity.endMinute = minute;
            tvEndTime.setText(BaseUtils.timeConversion(hourOfDay, minute));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_sleep_remind_details);
        MyApplication.getInstance().addActivity(this);
        EventBus.getDefault().register(this);
        initView();
        initListener();
        initData();
    }

    private void initView()
    {
        titleView = (TitleView) findViewById(R.id.titleview);
        titleView.setTitle(R.string.sleep_remind_detail);
        titleView.setcolor("#a9d559");
        titleView.settextcolor("#ffffff");
        titleView.titleImg(R.drawable.sleep_icon);
        titleView.setBack(R.drawable.steps_back, new onBackLister() {
            @Override
            public void onClick(View button)
            {
                finish();
            }
        });
        titleView.right(R.string.save, new onSetLister() {

            @Override
            public void onClick(View button)
            {
                if (!MyApplication.isConndevice) {
                    MethodUtils.showToast(getApplicationContext(), "请先连接蓝牙设备");
                    return;
                }

                MyApplication.cb_switch = cb_open.isChecked();
                boolean[] whichDays = new boolean[7];
                whichDays[0] = cb_seven.isChecked();
                whichDays[1] = cb_one.isChecked();
                whichDays[2] = cb_two.isChecked();
                whichDays[3] = cb_three.isChecked();
                whichDays[4] = cb_four.isChecked();
                whichDays[5] = cb_five.isChecked();
                whichDays[6] = cb_six.isChecked();

                clockEntity.whichDays = whichDays;
                clockEntity.isRepeatOnce = cb_repeat_once.isChecked();
                clockEntity.isOpen = cb_open.isChecked();
                clockEntity.isMusic = cb_music.isChecked();
                clockEntity.isShock = cb_shock.isChecked();

                //发送睡眠提醒指令
                BleService.sendCommand(OrderData.getSleepClockOrder(clockEntity.id + 1,
                        clockEntity.isOpen,
                        clockEntity.whichDays,
                        clockEntity.isRepeatOnce,
                        clockEntity.hour,
                        clockEntity.minute,
                        clockEntity.endHour,
                        clockEntity.endMinute));


                //将新的闹钟数据保存在SharePreference中
                SpHelp.saveObject(SpHelp.getSleepClockKey(clockEntity.id), clockEntity);
                finish();
            }
        });

        tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);

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

    private void initListener()
    {
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

        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
    }

    private void initData()
    {
        clockEntity = (SleepClock) getIntent().getSerializableExtra("clock");
        if (clockEntity == null) {
            return;
        }
        tvStartTime.setText(BaseUtils.timeConversion(clockEntity.hour, clockEntity.minute));
        tvEndTime.setText(BaseUtils.timeConversion(clockEntity.endHour, clockEntity.endMinute));

        cb_open.setChecked(clockEntity.isOpen);
        cb_music.setChecked(clockEntity.isMusic);
        cb_shock.setChecked(clockEntity.isShock);

        boolean[] whichDays = clockEntity.whichDays;
        cb_one.setChecked(whichDays[1]);
        cb_two.setChecked(whichDays[2]);
        cb_three.setChecked(whichDays[3]);
        cb_four.setChecked(whichDays[4]);
        cb_five.setChecked(whichDays[5]);
        cb_six.setChecked(whichDays[6]);
        cb_seven.setChecked(whichDays[0]);
        cb_repeat_once.setChecked(clockEntity.isRepeatOnce);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.tv_start_time:
                showDialog(1);
                break;
            case R.id.tv_end_time:
                showDialog(2);
                break;
            default:
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        TimePickerDialog dialog = null;
        if (id == 1) {
            dialog = new TimePickerDialog(this, startTimeListener,
                    clockEntity.hour, clockEntity.minute, true); //false代表12小时制
        }
        if (id == 2) {
            dialog = new TimePickerDialog(this, endTimeListener,
                    clockEntity.endHour, clockEntity.endMinute, true); //false代表12小时制
        }
        return dialog;
    }


    public void onEventMainThread(Event event)
    {
        if (event.update_sleep_remind) {
            if (MyApplication.cb_switch) {
                MethodUtils.showToast(getApplicationContext(), "睡眠提醒打开!");
            } else {
                MethodUtils.showToast(getApplicationContext(), "睡眠提醒关闭!");
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
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
                System.out.println(cb_repeat_once.isChecked());
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
}
