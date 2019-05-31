package com.bt.elderbracelet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bonten.ble.application.MyApplication;
import com.bonten.ble.servise.BleService;
import com.bt.elderbracelet.entity.ClockEntity;
import com.bt.elderbracelet.entity.SleepClock;
import com.bt.elderbracelet.protocal.OrderData;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bttow.elderbracelet.R;

import java.util.ArrayList;

public class ClockAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ClockEntity> clockList;
    private ViewHolder holder = null;
    private int flag;   //这是一个标志位，标志着这个ClockAdapter是被
    //闹钟使用，睡眠提醒使用，还是服药提醒使用
    public static final int KIND_CLOCK = 0;          //标志着这个ClockAdapter被闹钟设置使用
    public static final int KIND_MEDICINE_CLOCK = 1; //标志着这个ClockAdapter被服药提醒设置使用
    public static final int KIND_SLEEP_CLOCK = 2;  //标志着这个ClockAdapter被睡眠提醒使用

    public ClockAdapter(Context context, ArrayList<ClockEntity> clockList, int flag)
    {
        super();
        this.context = context;
        this.clockList = clockList;
        this.flag = flag;
    }

    @Override
    public int getCount()
    {
        return 5;
    }

    @Override
    public Object getItem(int position)
    {
        return clockList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
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

        if (clockList.get(position).isRepeatOnce) {
            holder.tvWeek.setText("单次提醒");
        } else {
            holder.tvWeek.setText(getWeeks(position));
        }

        if (clockEntity.isOpen) {
            holder.tvTime.setTextColor(context.getResources().getColor(R.color.text_blue_color));
            holder.tvWeek.setTextColor(context.getResources().getColor(R.color.text_gray_color));
        } else {
            holder.tvTime.setTextColor(context.getResources().getColor(R.color.gray_color));
            holder.tvWeek.setTextColor(context.getResources().getColor(R.color.gray_color));
        }

        holder.cbOpen.setChecked(clockEntity.isOpen);
        holder.cbOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked)
            {
                if (MyApplication.isConndevice) {
                    clockEntity.isOpen = isChecked;
                    MyApplication.cb_switch = isChecked;
                    if (flag == KIND_CLOCK) {
                        BleService.sendCommand(OrderData.getClockOrder(
                                position + 1,
                                clockEntity.isOpen,
                                clockEntity.whichDays,
                                clockEntity.isRepeatOnce,
                                clockEntity.hour,
                                clockEntity.minute));
                        SpHelp.saveObject(SpHelp.getClockKey(position), clockEntity);
                    }
                    if (flag == KIND_MEDICINE_CLOCK) {
                        BleService.sendCommand(OrderData.getMedicineClockOrder(
                                position + 1,
                                clockEntity.isOpen,
                                clockEntity.whichDays,
                                clockEntity.isRepeatOnce,
                                clockEntity.hour,
                                clockEntity.minute));
                        SpHelp.saveObject(SpHelp.getMedicineClockKey(position), clockEntity);
                    }
                    if (flag == KIND_SLEEP_CLOCK) {
                        final SleepClock sleepClock = (SleepClock) clockEntity;
                        sleepClock.isOpen = isChecked;
                        BleService.sendCommand(OrderData.getSleepClockOrder(
                                position + 1,
                                sleepClock.isOpen,
                                sleepClock.whichDays,
                                sleepClock.isRepeatOnce,
                                sleepClock.hour,
                                sleepClock.minute,
                                sleepClock.endHour,
                                sleepClock.endMinute));
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
     * 根据 每个 闹钟事件的 whichDays[i]参数，返回 字符串 “星期一，星期二，星期六”等
     */
    private String getWeeks(int position)
    {
        StringBuilder builder = new StringBuilder("");
        for (int i = 1; i < 7; i++) {
            if (clockList.get(position).whichDays[i]) {
                switch (i) {
                    case 1:
                        builder.append("周一 ");
                        break;
                    case 2:
                        builder.append("周二 ");
                        break;
                    case 3:
                        builder.append("周三 ");
                        break;
                    case 4:
                        builder.append("周四 ");
                        break;
                    case 5:
                        builder.append("周五 ");
                        break;
                    case 6:
                        builder.append("周六 ");
                        break;
                    default:
                        break;
                }
            }
        }
        if (clockList.get(position).whichDays[0]) {
            builder.append("周日");
        }
        return builder.toString();
    }

    public class ViewHolder {
        TextView tvTime;      //具体 几点几分闹钟想起
        TextView tvWeek;      //每周 周几闹钟会响
        CheckBox cbOpen;    //标志是否打开该闹钟
    }
}
