package com.bt.elderbracelet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.bonten.ble.servise.BleService;
import com.bt.elderbracelet.entity.others.Event;
import com.bt.elderbracelet.protocal.OrderData;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.SpHelp;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/9/15.
 */

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            System.out.println("来短信了");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                String msg = "";
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    msg += messages[i].getMessageBody();
                }
                Event event = new Event();
                event.msg = msg;
                EventBus.getDefault().post(event);
            }

            if (SpHelp.getPhoneMsgRemind()) {
                BleService.sendCommand(OrderData.getCommonOrder(OrderData.PHONE_MESSAGE_REMIND_ORDER));
            }
        }
    }
}
