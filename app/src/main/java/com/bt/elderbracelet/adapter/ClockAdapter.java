package com.bt.elderbracelet.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.entity.ClockEntity;
import com.bt.elderbracelet.entity.SleepClock;
import com.bt.elderbracelet.protocal.RemoteServiceCallback;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.AlarmInfoItem;
import com.sxr.sdk.ble.keepfit.aidl.BleClientOption;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

import java.util.ArrayList;

public class ClockAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ClockEntity> clockList;
    private ViewHolder holder = null;
    private int flag;
    //这是一个标志位，标志着这个ClockAdapter是被闹钟使用，睡眠提醒使用，还是服药提醒使用
    public static final int KIND_CLOCK = 0;          //标志着这个ClockAdapter被闹钟设置使用
    public static final int KIND_MEDICINE_CLOCK = 1; //标志着这个ClockAdapter被服药提醒设置使用
    public static final int KIND_SLEEP_CLOCK = 2;  //标志着这个ClockAdapter被睡眠提醒使用

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                MethodUtils.showToast(context, "闹钟设置成功");
            } else {
                MethodUtils.showToast(context, "闹钟设置失败");
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

    public ClockAdapter(Context context, ArrayList<ClockEntity> clockList, int flag) {
        super();
        this.context = context;
        this.clockList = clockList;
        this.flag = flag;
        this.mService = MyApplication.remoteService;
        try {
            mService.registerCallback(mServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return clockList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.bracelet_clock_item, null);
            holder = new ViewHolder();
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvWeek = (TextView) convertView.findViewById(R.id.tv_week);
            holder.cbOpen = (CheckBox) convertView.findViewById(R.id.cb_open);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ClockEntity clockEntity = clockList.get(position);

        if (flag == KIND_SLEEP_CLOCK) {
            final SleepClock sleepClock = (SleepClock) clockEntity;
            StringBuilder builder = new StringBuilder("");
            builder.append(BaseUtils.timeConversion(sleepClock.hour, sleepClock.minute));
            builder.append(" --> ");
            builder.append(BaseUtils.timeConversion(sleepClock.endHour, sleepClock.endMinute));
            holder.tvTime.setText(builder.toString());
        } else {
            holder.tvTime.setText(BaseUtils.timeConversion(clockEntity.hour, clockEntity.minute));
        }

        if (clockList.get(position).isSingle) {
            holder.tvWeek.setText("单次提醒");
        } else {
            holder.tvWeek.setText(getWeeks(clockList.get(position)));
        }

        if (clockEntity.isOpen > 0) {
            holder.tvTime.setTextColor(context.getResources().getColor(R.color.text_blue_color));
            holder.tvWeek.setTextColor(context.getResources().getColor(R.color.text_gray_color));
        } else {
            holder.tvTime.setTextColor(context.getResources().getColor(R.color.gray_color));
            holder.tvWeek.setTextColor(context.getResources().getColor(R.color.gray_color));
        }

        holder.cbOpen.setChecked(clockEntity.isOpen > 0);
        holder.cbOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (MyApplication.isConnected) {
                    clockEntity.isOpen = isChecked ? 1 : 0;
                    MyApplication.cb_switch = isChecked;
                    if (flag == KIND_CLOCK) {
                        //发送设置闹钟指令
                        callSetAlarm(clockEntity.id,
                                clockEntity.isOpen,
                                clockEntity.hour,
                                clockEntity.minute,
                                clockEntity.enableMonday,
                                clockEntity.enableTuesday,
                                clockEntity.enableWednesday,
                                clockEntity.enableThursday,
                                clockEntity.enableFriday,
                                clockEntity.enableSaturday,
                                clockEntity.enableSunday,
                                "",
                                clockEntity.isSingle
                        );
                        SpHelp.saveObject(SpHelp.getClockKey(position), clockEntity);
                    }
                    if (flag == KIND_MEDICINE_CLOCK) {
                        // TODO: 2019/6/1
//                        BleService.sendCommand(OrderData.getMedicineClockOrder(
//                                position + 1,
//                                clockEntity.isOpen,
//                                clockEntity.whichDays,
//                                clockEntity.isRepeatOnce,
//                                clockEntity.hour,
//                                clockEntity.minute));
                        SpHelp.saveObject(SpHelp.getMedicineClockKey(position), clockEntity);
                    }
                    if (flag == KIND_SLEEP_CLOCK) {
                        final SleepClock sleepClock = (SleepClock) clockEntity;
                        sleepClock.isOpen = isChecked ? 1 : 0;
                        // TODO: 2019/6/1
//                        BleService.sendCommand(OrderData.getSleepClockOrder(
//                                position + 1,
//                                sleepClock.isOpen,
//                                sleepClock.whichDays,
//                                sleepClock.isRepeatOnce,
//                                sleepClock.hour,
//                                sleepClock.minute,
//                                sleepClock.endHour,
//                                sleepClock.endMinute));
                        SpHelp.saveObject(SpHelp.getSleepClockKey(position), sleepClock);
                    }
                    notifyDataSetChanged();

                } else {
                    holder.cbOpen.setChecked(!isChecked);   //这是还原cbOpen之前的样子
                    MethodUtils.showToast(context, "请先连接蓝牙设备");
                }
            }
        });

        return convertView;
    }

    /**
     * 根据 每个 闹钟事件，返回 字符串 “星期一，星期二，星期六”等
     */
    private String getWeeks(ClockEntity entity) {
        StringBuilder builder = new StringBuilder("");
        if (entity.enableMonday > 0) {
            builder.append("周一 ");
        }
        if (entity.enableTuesday > 0) {
            builder.append("周二 ");
        }
        if (entity.enableWednesday > 0) {
            builder.append("周三 ");
        }
        if (entity.enableThursday > 0) {
            builder.append("周四 ");
        }
        if (entity.enableFriday > 0) {
            builder.append("周五 ");
        }
        if (entity.enableSaturday > 0) {
            builder.append("周六 ");
        }
        if (entity.enableSunday > 0) {
            builder.append("周日");
        }
        return builder.toString();
    }

    public class ViewHolder {
        TextView tvTime;      //具体 几点几分闹钟想起
        TextView tvWeek;      //每周 周几闹钟会响
        CheckBox cbOpen;    //标志是否打开该闹钟
    }

    private void callSetAlarm(int alarm_id, int enableType, int hour, int minute,
                              int enableMonday,
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
                Toast.makeText(context, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
}
