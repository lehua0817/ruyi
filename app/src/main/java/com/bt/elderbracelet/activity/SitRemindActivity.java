package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bonten.ble.application.MyApplication;
import com.bonten.ble.servise.BleService;
import com.bt.elderbracelet.entity.SitClock;
import com.bt.elderbracelet.entity.others.Event;
import com.bt.elderbracelet.protocal.OrderData;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bttow.elderbracelet.R;

import de.greenrobot.event.EventBus;


public class SitRemindActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static final String[] NETITEMS = new String[]{"2", "15", "30", "45", "60", "90", "120"};
    private TitleView titleView;                  //标题栏
    private TextView tv_start_time;    //监测久坐的开始时间
    private TextView tv_end_time;      //监测久坐的结束时间
    private TextView tv_sit_time;                      // 久坐时间
    private CheckBox cb_swith, cb_one, cb_two, cb_three, cb_four, cb_five, cb_six, cb_seven, cb_repeat_once;    //久坐提醒开关

    public int checkedItem;

    public SitClock sitClock;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        EventBus.getDefault().register(this);
        setContentView(R.layout.bracelet_sit_remind);
        MyApplication.getInstance().addActivity(this);
        initView();
        initListener();
    }

    private void initView()
    {
        titleView = (TitleView) findViewById(R.id.titleview);
        tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        tv_sit_time = (TextView) findViewById(R.id.tv_sit_time);

        cb_swith = (CheckBox) findViewById(R.id.cb_swith);
        cb_one = (CheckBox) findViewById(R.id.cb_one);
        cb_two = (CheckBox) findViewById(R.id.cb_two);
        cb_three = (CheckBox) findViewById(R.id.cb_three);
        cb_four = (CheckBox) findViewById(R.id.cb_four);
        cb_five = (CheckBox) findViewById(R.id.cb_five);
        cb_six = (CheckBox) findViewById(R.id.cb_six);
        cb_seven = (CheckBox) findViewById(R.id.cb_seven);
        cb_repeat_once = (CheckBox) findViewById(R.id.cb_repeat_once);

        titleView.setTitle(R.string.sedentary_remind);
        titleView.setcolor("#495677");
        titleView.settextcolor("#ffffff");
        titleView.right(R.string.save_sedentary, new TitleView.onSetLister() {
            @Override
            public void onClick(View button)
            {
                if (MyApplication.isConndevice) {
                    MyApplication.cb_switch = cb_swith.isChecked();
                    sitClock.isOpen = cb_swith.isChecked();

                    boolean[] whichDays = new boolean[7];
                    whichDays[0] = cb_seven.isChecked();  //周日
                    whichDays[1] = cb_one.isChecked();  //周一
                    whichDays[2] = cb_two.isChecked();  //周二
                    whichDays[3] = cb_three.isChecked();  //周三
                    whichDays[4] = cb_four.isChecked();  //周四
                    whichDays[5] = cb_five.isChecked();  //周五
                    whichDays[6] = cb_six.isChecked();  //周六
                    sitClock.whichDays = whichDays;

                    sitClock.isRepeatOnce = cb_repeat_once.isChecked();

                    BleService.sendCommand(OrderData.getSedentaryOrder(
                            sitClock.isOpen,
                            sitClock.whichDays,
                            sitClock.isRepeatOnce,
                            sitClock.startHour,
                            sitClock.startMinute,
                            sitClock.endHour,
                            sitClock.endHour,
                            sitClock.sitTime));//发送久坐提醒指令
                    SpHelp.saveObject(SpHelp.SIT_REMIND, sitClock);
                } else {
                    MethodUtils.showToast(getApplicationContext(), "请先连接蓝牙设备");
                    finish();
                }

            }
        });
        titleView.setBack(R.drawable.steps_back, new onBackLister() {
            @Override
            public void onClick(View button)
            {
                finish();
            }
        });

    }

    private void initListener()
    {
        cb_swith.setOnCheckedChangeListener(this);
        cb_one.setOnCheckedChangeListener(this);
        cb_two.setOnCheckedChangeListener(this);
        cb_three.setOnCheckedChangeListener(this);
        cb_four.setOnCheckedChangeListener(this);
        cb_five.setOnCheckedChangeListener(this);
        cb_six.setOnCheckedChangeListener(this);
        cb_seven.setOnCheckedChangeListener(this);
        cb_repeat_once.setOnCheckedChangeListener(this);
        tv_end_time.setOnClickListener(this);
        tv_start_time.setOnClickListener(this);
        tv_sit_time.setOnClickListener(this);
    }


    private void initData()
    {
        if (SpHelp.getObject(SpHelp.SIT_REMIND) == null) {
            sitClock = new SitClock();
        } else {
            sitClock = (SitClock) SpHelp.getObject(SpHelp.SIT_REMIND);
        }

        cb_swith.setChecked(sitClock.isOpen);

        tv_start_time.setText(BaseUtils.timeConversion(sitClock.startHour, sitClock.startMinute));
        tv_end_time.setText(BaseUtils.timeConversion(sitClock.endHour, sitClock.endMinute));

        cb_one.setChecked(sitClock.whichDays[1]);
        cb_two.setChecked(sitClock.whichDays[2]);
        cb_three.setChecked(sitClock.whichDays[3]);
        cb_four.setChecked(sitClock.whichDays[4]);
        cb_five.setChecked(sitClock.whichDays[5]);
        cb_six.setChecked(sitClock.whichDays[6]);
        cb_seven.setChecked(sitClock.whichDays[0]);

        cb_repeat_once.setChecked(sitClock.isRepeatOnce);

        tv_sit_time.setText(sitClock.sitTime + "分钟");
        checkedItem = findIndex(NETITEMS, sitClock.sitTime);
        initColor();

    }

    private void initColor()
    {
        if (cb_swith.isChecked()) {
            tv_start_time.setTextColor(getResources().getColor(R.color.text_blue_color));
            tv_start_time.setClickable(true);
            tv_end_time.setTextColor(getResources().getColor(R.color.text_blue_color));
            tv_end_time.setClickable(true);
            tv_sit_time.setTextColor(getResources().getColor(R.color.text_blue_color));
            tv_sit_time.setClickable(true);
        } else {
            tv_start_time.setTextColor(getResources().getColor(R.color.gray_color));
            tv_start_time.setClickable(false);
            tv_end_time.setTextColor(getResources().getColor(R.color.gray_color));
            tv_end_time.setClickable(false);
            tv_sit_time.setTextColor(getResources().getColor(R.color.gray_color));
            tv_sit_time.setClickable(false);
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.tv_start_time:
                showDialog(1);
                break;
            case R.id.tv_end_time:
                showDialog(2);
                break;
            case R.id.tv_sit_time:
                showNetDialog();
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        if (id == 1) {
            return new TimePickerDialog(this, onTimeSetListener,
                    sitClock.startHour, sitClock.startMinute, true);
        } else if (id == 2) {
            return new TimePickerDialog(this, onTimeSetListener_end,
                    sitClock.endHour, sitClock.endMinute, true);
        }
        return super.onCreateDialog(id);
    }


    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            tv_start_time.setText(BaseUtils.timeConversion(hourOfDay, minute));
            sitClock.startHour = hourOfDay;
            sitClock.startMinute = minute;
        }
    };


    TimePickerDialog.OnTimeSetListener onTimeSetListener_end = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            if (((hourOfDay * 60 + minute) <= (sitClock.startHour * 60 + sitClock.startMinute))) {
                MethodUtils.showToast(getApplicationContext(), "结束时间必须大于开始时间");
                return;
            } else {
                sitClock.endHour = hourOfDay;
                sitClock.endMinute = minute;
                tv_end_time.setText(BaseUtils.timeConversion(hourOfDay, minute));
            }
        }
    };


    private void showNetDialog()
    {
        AlertDialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                R.style.dialog_style_light);
        builder.setSingleChoiceItems(NETITEMS, checkedItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        tv_sit_time.setText(NETITEMS[which] + "分钟");
                        sitClock.sitTime = Integer.valueOf(NETITEMS[which]);
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }


    public void onEventMainThread(Event event)
    {
        if (event.update_keep_sit) {
            if (MyApplication.cb_switch) {
                MethodUtils.showToast(getApplicationContext(), "久坐提醒设置成功!");
            } else {
                MethodUtils.showToast(getApplicationContext(), "久坐提醒已关闭!");
            }
        }
    }

    public int findIndex(String[] arrays, int object)
    {
        int index = 0;
        for (int i = 0; i < arrays.length; i++) {
            if (arrays[i].equals(String.valueOf(object))) {
                index = i;
            }
        }
        return index;
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        initData();

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
            case R.id.cb_swith:
                initColor();
                break;

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
