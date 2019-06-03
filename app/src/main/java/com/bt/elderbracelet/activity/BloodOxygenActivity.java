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
import com.bt.elderbracelet.entity.BloodOxygen;
import com.bt.elderbracelet.protocal.RemoteServiceCallback;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.other.TasksCompletedOxygenView;
import com.bt.elderbracelet.view.TitleView;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

import java.util.List;

public class BloodOxygenActivity extends Activity {
    TitleView titleview;
    TextView tvOxygen;
    //    TextView tvTime;
    TasksCompletedOxygenView taskViewOxygen;
    BloodOxygen oxygen;
    private List<Integer> allOxygenNum;  //记录一次测血氧数据的过程中变化的所有数据

    private ModelDao modelDao;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateUI(msg.what);
        }
    };


    private IRemoteService mService;
    private IServiceCallback mServiceCallback = new RemoteServiceCallback() {

        @Override
        public void onReceiveSensorData(int heartrate, int pressureHigh, int pressureLow, int oxygen,
                                        int tired) throws RemoteException {
            Log.v("onReceiveSensorData", "result:" + heartrate + " , " + pressureHigh + " , " + pressureLow + " , " + oxygen + " , " + tired);
            if (oxygen != 0) {
                callRemoteOpenBlood(false);
                System.out.println("血氧来了");
                BloodOxygen bloodOxygen = new BloodOxygen();
                bloodOxygen.setBloodOxygen(String.valueOf(oxygen));
                bloodOxygen.setPreciseDate(BaseUtils.getPreciseDate());
                modelDao.insertBloodOxygen(bloodOxygen);
                Message msg = Message.obtain();
                msg.what = oxygen;
                mHandler.sendMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_oxygen);
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
        titleview.setcolor("#B7278F");
        titleview.titleImg(R.drawable.oxygen_icon);
        titleview.setTitle(R.string.oxygen);
        titleview.right(R.string.history, new TitleView.onSetLister() {
            @Override
            public void onClick(View button) {
                Intent intent = new Intent(getApplicationContext(), OxygenHistoryActivity.class);
                startActivity(intent);
            }
        });
        titleview.setBack(R.drawable.steps_back, new TitleView.onBackLister() {
            @Override
            public void onClick(View button) {
                finish();
            }
        });

        taskViewOxygen = (TasksCompletedOxygenView) findViewById(R.id.task_view_oxygen);
        tvOxygen = (TextView) findViewById(R.id.tv_oxygen);
//        tvTime = (TextView) findViewById(R.id.tv_time);
    }

    private void updateUI(int oxygen) {
        if (oxygen > 0) {
            tvOxygen.setText(oxygen + "");
            taskViewOxygen.setProgress(oxygen);
        }
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
