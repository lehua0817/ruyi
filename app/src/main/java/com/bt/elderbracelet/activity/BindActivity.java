package com.bt.elderbracelet.activity;

/**
 * 中华人民共和国万岁，毛主席永垂不朽
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.adapter.DeviceAdapter;
import com.bt.elderbracelet.entity.DeviceInfo;
import com.bt.elderbracelet.protocal.RemoteServiceCallback;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

import java.util.ArrayList;

/**
 * 主界面类
 * 主要处理功能:
 * 1.选中设备
 * 2.扫描蓝牙,连接蓝牙,蓝牙通信处理
 * 3.界面功能 左右滑动
 */
public class BindActivity extends Activity {
    public static final String TAG = BindActivity.class.getSimpleName();

    boolean mScaning = false;     //代表当前是否正在搜索蓝牙
    private AlertDialog mScanningDialog = null;
    private ArrayList<DeviceInfo> deviceList;
    private DeviceAdapter device_adapter;
    private TitleView titleView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String deviceName = msg.getData().getString("name");
            String deviceMacAddress = msg.getData().getString("mac");
            addDevice(new DeviceInfo(deviceName, deviceMacAddress));
        }
    };


    private IRemoteService mService;
    private IServiceCallback mServiceCallback = new RemoteServiceCallback() {
        //扫描蓝牙设备接口
        @Override
        public void onScanCallback(String deviceName, String deviceMacAddress, int rssi)
                throws RemoteException {
            Log.v(TAG, String.format("onScanCallback [%1$s][%2$s](%3$d)", deviceName, deviceMacAddress, rssi));

            Bundle data = new Bundle();
            data.putString("name", deviceName);
            data.putString("mac", deviceMacAddress);
            Message msg = Message.obtain();
            msg.setData(data);
            mHandler.sendMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("我是onCreate，我被执行了");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_bind);
        MyApplication.getInstance().addActivity(this);
        initUI();

        mService = MyApplication.remoteService;
        try {
            mService.registerCallback(mServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        ensureOpenBluetooth();
        startScanDevice();
    }

    private void initUI() {
        titleView = (TitleView) findViewById(R.id.titleview);
        titleView.setbg(R.drawable.register_titlebg);
        titleView.setTitle(R.string.bind_device);
        ListView lv_device_list = (ListView) findViewById(R.id.lv_device_list);
        deviceList = new ArrayList<>();
        device_adapter = new DeviceAdapter(getApplicationContext(), deviceList);
        lv_device_list.setAdapter(device_adapter);
        titleView.right(R.string.refresh, new TitleView.onSetLister() {
            @Override
            public void onClick(View button) {
                if (null != deviceList && deviceList.size() > 0) {
                    deviceList.clear();
                    device_adapter.notifyDataSetChanged();
                }
            }
        });

        //搜索出一个列表,选中相应的手环蓝牙地址MAC
        lv_device_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String mac = deviceList.get(i).getMac();
                String name = deviceList.get(i).getName();
                SpHelp.saveDeviceMac(mac);
                SpHelp.saveDeviceName(name);
                MethodUtils.showToast(getApplicationContext(), "绑定设备成功");
                stopScanDevice();  //当你都选择了一个设备以后，那就不需要再继续扫描附近的设备了

                /**
                 * 绑定设备后，可能进入两个界面：
                 * 要么是主界面，要么是注册界面
                 */
                Intent intent = null;
                if (!TextUtils.isEmpty(SpHelp.getUserId())) {
                    intent = new Intent(BindActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(BindActivity.this, RegisterActivity.class);
                }
                startActivity(intent);
            }
        });
    }

    private void startScanDevice() {

        if (mService != null) {
            try {
                if (mScaning) {
                    stopScanDevice();
                }
                mScaning = true;
                mService.scanDevice(true);
                if (mScanningDialog != null && mScanningDialog.isShowing()) {
                    mScanningDialog.dismiss();
                    mScanningDialog = null;
                }
                mScanningDialog = MethodUtils.createDialog(BindActivity.this, "正在搜索附近手环");
                mScanningDialog.show();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }


    }

    private void stopScanDevice() {

        if (mService != null) {
            try {
                mScaning = false;
                mService.scanDevice(false);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

    //初始化蓝牙适配器 判断是否打开蓝牙
    public void ensureOpenBluetooth() {
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        if (adapter == null) {   //如果手机不支持蓝牙，则直接结束
            finish();
        }
        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_CANCELED:
                if (requestCode == 1) {  //若用户不打开蓝牙，退出程序。
                    finish();
                    return;
                }
                break;
            case -1: //用户打开蓝牙设备后,搜索蓝牙;
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            SpHelp.saveJoinGround(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void addDevice(DeviceInfo info) {
        if (deviceList == null) {
            return;
        }
        boolean is_exist = false;
        if (deviceList.size() > 0) {
            for (int i = 0; i < deviceList.size(); i++) {
                if (deviceList.get(i).getMac().equals(info.getMac())) {
                    //如果刚刚扫描到的设备Mac和deviceInfos的设备相符合
                    //则说明该设备已经保存过
                    is_exist = true;
                    break;
                }
            }
        }
        if (!is_exist) {
            //如果不存在，则把新设备添加到我们的deviceInfos列表中
            deviceList.add(info);
            device_adapter.changeData(deviceList);
        }
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mScaning) {
//            stopScanDevice();
//        }
//    }
}
