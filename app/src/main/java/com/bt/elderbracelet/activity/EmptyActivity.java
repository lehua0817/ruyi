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

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(EmptyActivity.this, "Service connected", Toast.LENGTH_SHORT).show();

            MyApplication.remoteService = IRemoteService.Stub.asInterface(service);

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "触发了EmptyActivity.onDestroy()");
        unbindService(mServiceConnection);
    }
}
