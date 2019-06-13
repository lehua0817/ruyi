package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.entity.SitClock;
import com.bt.elderbracelet.protocal.RemoteServiceCallback;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;


public class SitRemindActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static final String[] NETITEMS = new String[]{"10", "15", "30", "45", "60"};
    private TitleView titleView;                  //标题栏
    private TextView tv_start_time;    //监测久坐的开始时间
    private TextView tv_end_time;      //监测久坐的结束时间
    private TextView tv_sit_time;                      // 久坐时间
    private CheckBox cb_swith;

    public int checkedItem;

    public SitClock sitClock;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                MethodUtils.showToast(SitRemindActivity.this, "设置成功");
                //将新的久坐提醒数据保存在SharePreference中
                SpHelp.saveObject(SpHelp.SIT_REMIND, sitClock);

            } else {
                MethodUtils.showToast(SitRemindActivity.this, "设置失败");
            }
        }
    };

    private IRemoteService mService;
    private IServiceCallback mServiceCallback = new RemoteServiceCallback() {
        @Override
        public void onSetIdleTime(int result) throws RemoteException {
            Message msg = Message.obtain();
            msg.what = result;
            mHandler.sendMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_sit_remind);
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
        tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        tv_sit_time = (TextView) findViewById(R.id.tv_sit_time);

        cb_swith = (CheckBox) findViewById(R.id.cb_swith);

        titleView.setTitle(R.string.sedentary_remind);
        titleView.setcolor("#495677");
        titleView.settextcolor("#ffffff");
        titleView.right(R.string.save_sedentary, new TitleView.onSetLister() {
            @Override
            public void onClick(View button) {
                if (!MyApplication.isConnected) {
                    MethodUtils.showToast(getApplicationContext(), "请先连接蓝牙设备");
                    return;
                }

                sitClock.isOpen = cb_swith.isChecked();

                /**
                 *  设置久坐提醒
                 *  手环本身不能关闭久坐提醒，所以需要人为实现这个关闭功能
                 *  如果想要关闭久坐提醒，则设置开始时间和结束时间相同
                 */

                if (cb_swith.isChecked()) {
                    callRemoteSetIdletime(
                            sitClock.sitMinute * 60,
                            sitClock.startHour,
                            sitClock.startMinute,
                            sitClock.endHour,
                            sitClock.endMinute);
                } else {
                    callRemoteSetIdletime(
                            sitClock.sitMinute * 60,
                            sitClock.startHour,
                            sitClock.startMinute,
                            sitClock.startHour,
                            sitClock.startMinute);
                }


            }
        });
        titleView.setBack(R.drawable.steps_back, new onBackLister() {
            @Override
            public void onClick(View button) {
                finish();
            }
        });

    }

    private void callRemoteSetIdletime(int sitSeconds, int startHour, int startMinute, int endHour, int endMinute) {
        if (mService != null) {
            try {
                mService.setIdleTime(sitSeconds, startHour, startMinute, endHour, endMinute);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initListener() {
        cb_swith.setOnCheckedChangeListener(this);
        tv_end_time.setOnClickListener(this);
        tv_start_time.setOnClickListener(this);
        tv_sit_time.setOnClickListener(this);
    }


    private void initData() {
        if (SpHelp.getObject(SpHelp.SIT_REMIND) == null) {
            sitClock = new SitClock();
        } else {
            sitClock = (SitClock) SpHelp.getObject(SpHelp.SIT_REMIND);
        }

        cb_swith.setChecked(sitClock.isOpen);

        tv_start_time.setText(BaseUtils.timeConversion(sitClock.startHour, sitClock.startMinute));
        tv_end_time.setText(BaseUtils.timeConversion(sitClock.endHour, sitClock.endMinute));

        tv_sit_time.setText(sitClock.sitMinute + "分钟");
        checkedItem = findIndex(NETITEMS, sitClock.sitMinute);
        initColor();
    }

    private void initColor() {
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
    public void onClick(View view) {
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
    protected Dialog onCreateDialog(int id) {
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
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tv_start_time.setText(BaseUtils.timeConversion(hourOfDay, minute));
            sitClock.startHour = hourOfDay;
            sitClock.startMinute = minute;
        }
    };


    TimePickerDialog.OnTimeSetListener onTimeSetListener_end = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
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


    private void showNetDialog() {
        AlertDialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                R.style.dialog_style_light);
        builder.setSingleChoiceItems(NETITEMS, checkedItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_sit_time.setText(NETITEMS[which] + "分钟");
                        sitClock.sitMinute = Integer.valueOf(NETITEMS[which]);
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }


    public int findIndex(String[] arrays, int object) {
        int index = 0;
        for (int i = 0; i < arrays.length; i++) {
            if (arrays[i].equals(String.valueOf(object))) {
                index = i;
            }
        }
        return index;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_swith:
                initColor();
                break;
            default:
                break;
        }
    }
}
