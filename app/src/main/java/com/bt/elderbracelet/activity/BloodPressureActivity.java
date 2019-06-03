package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.BloodPressure;
import com.bt.elderbracelet.protocal.RemoteServiceCallback;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onSetLister;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;


public class BloodPressureActivity extends Activity {
    public static final String TAG = BloodPressureActivity.class.getSimpleName();
    TitleView titleview;
    //    TextView tvdate;
    TextView tvHighPressure;
    TextView tvLowPressure;

    private ModelDao modelDao;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateUI(msg.arg1, msg.arg2);
        }
    };


    private IRemoteService mService;
    private IServiceCallback mServiceCallback = new RemoteServiceCallback() {

        @Override
        public void onReceiveSensorData(int heartrate, int pressureHigh, int pressureLow, int oxygen,
                                        int tired) throws RemoteException {
            // 心率, 高血压, 低血压, 血氧, 疲劳值);
            Log.v("onReceiveSensorData", "result:" + heartrate + " , " + pressureHigh + " , " + pressureLow + " , " + oxygen + " , " + tired);

            if (pressureHigh != 0 && pressureLow != 0) {
                callRemoteOpenBlood(false);
                System.out.println("血压来了");
                BloodPressure pressure = new BloodPressure();
                pressure.setBloodPressureHigh(String.valueOf(pressureHigh));
                pressure.setBloodPressureLow(String.valueOf(pressureLow));
                pressure.setPreciseDate(BaseUtils.getPreciseDate());
                modelDao.insertBloodPressure(pressure);

                Message msg = Message.obtain();
                msg.arg1 = pressureLow;
                msg.arg2 = pressureHigh;
                mHandler.sendMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_pressure);
        MyApplication.getInstance().addActivity(this);
        modelDao = new ModelDao(getApplicationContext());
        mService = MyApplication.remoteService;
        try {
            mService.registerCallback(mServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        initView();
        callRemoteOpenBlood(true);
    }

    private void initView() {
        titleview = (TitleView) findViewById(R.id.titleview);
        titleview.setcolor("#F8C301");
        titleview.titleImg(R.drawable.pressure_icon);
        titleview.setTitle(R.string.pressure);
        titleview.right(R.string.history, new onSetLister() {
            @Override
            public void onClick(View button) {
                Intent intent = new Intent(getApplicationContext(), PressureHistoryActivity.class);
                intent.putExtra("dataType", MyApplication.PRESSURE_TYPE);
                startActivity(intent);
            }
        });

//        tvdate = (TextView) findViewById(R.id.tv_date);

        tvHighPressure = (TextView) findViewById(R.id.tv_high_pressure);
        tvLowPressure = (TextView) findViewById(R.id.tv_low_pressure);
        tvHighPressure.setText("0.0");
        tvLowPressure.setText("0.0");
        titleview.setBack(R.drawable.steps_back, new TitleView.onBackLister() {
            @Override
            public void onClick(View button) {
                finish();
            }
        });
    }


    private void updateUI(int pressureLow, int pressureHigh) {
        tvLowPressure.setText(pressureLow + "");
        tvHighPressure.setText(pressureHigh + "");
    }

    private void callRemoteOpenBlood(boolean enable) {
        if (mService != null) {
            try {
                mService.setBloodPressureMode(enable);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callRemoteOpenBlood(false);
    }
}
