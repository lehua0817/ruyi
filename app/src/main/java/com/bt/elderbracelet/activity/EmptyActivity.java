package com.bt.elderbracelet.activity;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bonten.ble.servise.SampleBleService;
import com.bt.elderbracelet.tools.SpHelp;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;

public class EmptyActivity extends Activity {
    public static final String TAG = EmptyActivity.class.getSimpleName();

    Intent intent = null;

    private IRemoteService mService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(EmptyActivity.this, "Service connected", Toast.LENGTH_SHORT).show();

            MyApplication.remoteService = IRemoteService.Stub.asInterface(service);
            mService = MyApplication.remoteService;
            // todo 这里设置userID只是为了测试使用
            SpHelp.saveUserId("13368176003");
            Log.v(TAG, "Mac = " + SpHelp.getDeviceMac());
            if (!TextUtils.isEmpty(SpHelp.getDeviceMac())) {
                intent = new Intent(EmptyActivity.this, MainActivity.class);
            } else {
                intent = new Intent(EmptyActivity.this, BindActivity.class);
            }
            startActivity(intent);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(EmptyActivity.this, "Service disconnected", Toast.LENGTH_SHORT).show();
            MyApplication.remoteService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        MyApplication.getInstance().addActivity(this);

        Intent intent = new Intent(EmptyActivity.this, SampleBleService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
//        registerSmsReceiver();

//        //关于短信提醒功能,首先根据系统设置，进行初始化
//        //其次需要监控APP中的系统设置，这样可以随时动态的开启关闭短信提醒功能
//        if (SpHelp.getPhoneMsgRemind()) {
//            registerSmsReceiver();
//        }
//
//        registerSystemSettingReceiver();


    }

//    public void registerSystemSettingReceiver() {
//        IntentFilter smsIntentFilter = new IntentFilter();
//        // 接收短信的广播
//        smsIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
//        registerReceiver(smsReceiver, smsIntentFilter);
//    }
//
//    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
//                System.out.println("来短信了");
//                if (SpHelp.getPhoneMsgRemind()) {
//                    callNotify(OrderData.NOTIFICATION_SMS, "", "");
//                }
//            }
//        }
//    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "触发了EmptyActivity.onDestroy()");
        unbindService(mServiceConnection);
    }
}
