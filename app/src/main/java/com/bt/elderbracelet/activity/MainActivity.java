package com.bt.elderbracelet.activity;

import android.app.Activity;

/**
 * 中华人民共和国万岁，毛主席永垂不朽
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bonten.ble.servise.BleService;
import com.bonten.ble.servise.SampleBleService;
import com.bt.elderbracelet.adapter.DeviceAdapter;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.BloodOxygen;
import com.bt.elderbracelet.entity.BloodPressure;
import com.bt.elderbracelet.entity.DeviceInfo;
import com.bt.elderbracelet.entity.HeartRate;
import com.bt.elderbracelet.entity.Register;
import com.bt.elderbracelet.entity.Sport;
import com.bt.elderbracelet.entity.others.Event;
import com.bt.elderbracelet.entity.others.PushMessage;
import com.bt.elderbracelet.okhttp.HttpRequest;
import com.bt.elderbracelet.okhttp.URLConstant;
import com.bt.elderbracelet.protocal.OrderData;
import com.bt.elderbracelet.protocal.ResolveData;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import de.greenrobot.event.EventBus;

import static com.bonten.ble.servise.BleService.ACTION_GATT_CONNECTED;
import static com.bonten.ble.servise.BleService.ACTION_GATT_DISCONNECTED;

/**
 * 主界面类
 * 主要处理功能:
 * 1.选中设备
 * 2.扫描蓝牙,连接蓝牙,蓝牙通信处理
 * 3.界面功能 左右滑动
 */
public class MainActivity extends Activity implements OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    private ModelDao modelDao; //数据库处理类

    private String user_id = ""; // 和服务器通信UID 非常重要, 每一条通信指令基本都用到, 注册个人信息的时候可以获取到
    private Handler mHandler;
    private Runnable runnable_reconn;   //用于定时重连
    private Runnable runnable_stop_scan;   //用于停止扫描蓝牙

    private BleService mBluetoothLeService;  // 蓝牙主服务类,贯穿整个项目核心
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    boolean mScaning = false;     //代表当前是否正在搜索蓝牙
    public static final int SCAN_SECOND = 10 * 1000;
    private AtomicInteger pushCount = new AtomicInteger(10);
    private AlertDialog mConnectingDialog = null;
    private AlertDialog mScanningDialog = null;
    private ArrayList<DeviceInfo> deviceInfos;

    private boolean flag = false;
    // false 表示这次搜索蓝牙在 注册时或者解除绑定后的 搜索蓝牙，总之，此时尚未绑定手环
    // true  表示这次搜索蓝牙操作在 主界面上，是已经绑定了手环

    // 绑定服务
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            Log.v(TAG, "BleService連接成功!!");
            mBluetoothLeService = ((BleService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.v(TAG, "BleService断开连接!!");
            mBluetoothLeService = null;
        }
    };

    private IRemoteService mService;
    private ServiceConnection mServiceConnection2 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MainActivity.this, "Service connected", Toast.LENGTH_SHORT).show();

            mService = IRemoteService.Stub.asInterface(service);
            try {
                mService.registerCallback(mServiceCallback);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MainActivity.this, "Service disconnected", Toast.LENGTH_SHORT).show();
            mService = null;
        }
    };
    private IServiceCallback mServiceCallback = new IServiceCallback.Stub() {


        /**
         *  设备连接状态改变
         *   0:未连接 、1：连接中 、2：已连接
         */
        @Override
        public void onConnectStateChanged(int state) throws RemoteException {
            Log.v("onConnectStateChanged", "state = " + state);
//            updateConnectState(state);
            String intentAction = "";
            if (state == BluetoothProfile.STATE_CONNECTED) {
//                gatt.discoverServices();
                MyApplication.isConndevice = true;
                intentAction = ACTION_GATT_CONNECTED;
                broadcastUpdate(intentAction);
            } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                BleService.blueToothServiceclose();
                MyApplication.isConndevice = false;
                intentAction = ACTION_GATT_DISCONNECTED;
                broadcastUpdate(intentAction);
            }
        }

        /**
         * 扫描蓝牙设备接口
         */
        @Override
        public void onScanCallback(String deviceName, String deviceMacAddress, int rssi)
                throws RemoteException {
            Log.i(TAG, String.format("onScanCallback [%1$s][%2$s](%3$d)", deviceName, deviceMacAddress, rssi));
        }

        /**
         * 消息推送回调接口
         */
        @Override
        public void onSetNotify(int result) throws RemoteException {
            Log.v("onSetNotify", String.valueOf(result));
        }

        /**
         *  设置用户信息回调接口
         *  返回1成功 0失败
         */
        @Override
        public void onSetUserInfo(int result) throws RemoteException {
            Log.v("onSetUserInfo", "" + result);
        }

        /**
         *  绑定服务后会自动异步地从asset目录下读取参数，结果将在onAuthSdkResult回调接口中返回
         *  返回200成功 0失败
         */
        @Override
        public void onAuthSdkResult(int errorCode) throws RemoteException {
            Log.v("onAuthSdkResult", errorCode + "");
        }

        @Override
        public void onGetDeviceTime(int result, String time) throws RemoteException {
            Log.v("onGetDeviceTime", String.valueOf(time));
        }

        /**
         * 设置设备时间回调接口 刚连上时会自动触发
         * 返回 1成功 0失败
         */
        @Override
        public void onSetDeviceTime(int arg0) throws RemoteException {
            Log.v("onSetDeviceTime", arg0 + "");
        }

        @Override
        public void onSetDeviceInfo(int arg0) throws RemoteException {
            Log.v("onSetDeviceInfo", arg0 + "");
        }


        @Override
        public void onAuthDeviceResult(int arg0) throws RemoteException {
            Log.v("onAuthDeviceResult", arg0 + "");
        }


        @Override
        public void onSetAlarm(int arg0) throws RemoteException {
            Log.v("onSetAlarm", arg0 + "");
        }

        @Override
        public void onSendVibrationSignal(int arg0) throws RemoteException {
            Log.v("onSendVibrationSignal", "result:" + arg0);
        }

        @Override
        public void onGetDeviceBatery(int arg0, int arg1)
                throws RemoteException {
            Log.v("onGetDeviceBatery", "batery:" + arg0 + ", statu " + arg1);
        }


        @Override
        public void onSetDeviceMode(int arg0) throws RemoteException {
            Log.v("onSetDeviceMode", "result:" + arg0);
        }

        @Override
        public void onSetHourFormat(int arg0) throws RemoteException {
            Log.v("onSetHourFormat ", "result:" + arg0);

        }

        @Override
        public void setAutoHeartMode(int arg0) throws RemoteException {
            Log.v("setAutoHeartMode ", "result:" + arg0);
        }


        /**
         * 获取当前运动数据回调接口
         * 参数分别为当前时间戳,以秒为单位 ,当前步数,当前距离(米),当前卡路里(大卡)，当前睡眠时间（秒），当前运动总时间，当前记步时间
         有效的返回值根据类型而定

         type = 0  当前运动信息
         返回有效值为：手环当前时间戳,以秒为单位 ,当前步数,当前距离(米),当前卡路里(大卡)，当前睡眠时间（秒）
         Type = 1 当前跑步信息
         返回有效值为：当前时间戳,以秒为单位 当前步数 当前运动总时间 当前记步时间

         */
        @Override
        public void onGetCurSportData(int type, long timestamp, int step, int distance,
                                      int cal, int cursleeptime, int totalrunningtime, int steptime) throws RemoteException {
            if (type == 0) {
//                Date date = new Date(timestamp * 1000);
//                Log.v(TAG, "step = " + step);
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//                String time = sdf.format(date);
                System.out.println("运动数据来了");

                Log.v(TAG, "step = " + step);
                Log.v(TAG, "distance = " + distance);
                Log.v(TAG, "cal = " + cal);
                Log.v(TAG, "cursleeptime = " + cursleeptime);
                Log.v(TAG, "totalrunningtime = " + totalrunningtime);
                Log.v(TAG, "steptime = " + steptime);


                int _step = step;
                int _distance = distance;
                int _calories = cal;
                int _sportTime = 100;

                Sport sport = new Sport();
                sport.setDate(BaseUtils.getTodayDate());
                sport.setStep(String.valueOf(_step));
                sport.setDistance(String.valueOf(_distance));
                sport.setCalorie(String.valueOf(_calories));
                sport.setSportTime(String.valueOf(_sportTime));

                boolean flag = false;  //代表数据库中是否已经有今天的数据记录

                ArrayList<Sport> sportList = modelDao.queryAllSport();
                if (sportList != null && sportList.size() > 0) {
                    for (int i = 0; i < sportList.size(); i++) {
                        if (sportList.get(i).getDate().equals(BaseUtils.getTodayDate())) {
                            sport.setId(sportList.get(i).getId());
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    modelDao.updateSport(sport, BaseUtils.getTodayDate());
                } else {
                    modelDao.insertSport(sport);
                }

                Event event = new Event();
                event.update_step = true;
                event.update_distance = true;
                event.update_caloria = true;
                event.update_sport_time = true;
                EventBus.getDefault().post(event);

            }
        }

        @Override
        public void onGetSenserData(int result, long timestamp, int heartrate, int sleepstatu)
                throws RemoteException {

            Event event = null;
            if (heartrate != 0) {
                System.out.println("心率来了");
                HeartRate heartRate = new HeartRate();
                heartRate.setHeartRate(String.valueOf(heartrate));
                heartRate.setPreciseDate(BaseUtils.getPreciseDate());

                event = new Event();
                event.heartRate = heartRate;

                EventBus.getDefault().post(event);
            }
        }


        @Override
        public void onGetDataByDay(int type, long timestamp, int step, int heartrate)
                throws RemoteException {
            Date date = new Date(timestamp * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String recorddate = sdf.format(date);
            Log.v("onGetDataByDay", "type:" + type + ",time::" + recorddate + ",step:" + step + ",heartrate:" + heartrate);
            if (type == 2) {
//                sleepcount++;
            }
        }

        @Override
        public void onGetDataByDayEnd(int type, long timestamp) throws RemoteException {
            Date date = new Date(timestamp * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String recorddate = sdf.format(date);
//            Log.v("onGetDataByDayEnd", "time:" + recorddate + ",sleepcount:" + sleepcount);
//            sleepcount = 0;
        }


        @Override
        public void onSetPhontMode(int arg0) throws RemoteException {
            Log.v("onSetPhontMode", "result:" + arg0);
        }


        @Override
        public void onSetSleepTime(int arg0) throws RemoteException {
            Log.v("onSetSleepTime", "result:" + arg0);
        }


        @Override
        public void onSetIdleTime(int arg0) throws RemoteException {
            Log.v("onSetIdleTime", "result:" + arg0);
        }


        @Override
        public void onGetDeviceInfo(int version, String macaddress, String vendorCode,
                                    String productCode, int result) throws RemoteException {
            Log.v("onGetDeviceInfo", "version :" + version + ",macaddress : " + macaddress + ",vendorCode : " + vendorCode + ",productCode :" + productCode + " , CRCresult :" + result);

        }

        @Override
        public void onGetDeviceAction(int type) throws RemoteException {
            Log.v("onGetDeviceAction", "type:" + type);
        }


        @Override
        public void onGetBandFunction(int result, boolean[] results) throws RemoteException {
            Log.v("onGetBandFunction", "result : " + result + ", results :" + results.length);

//            for(int i = 0; i < results.length; i ++){
//				Log.v( "onGetBandFunction","result : " + result + ", results :" + i +  " : " + results[i]);
//			}
        }


        @Override
        public void onSetLanguage(int arg0) throws RemoteException {
            Log.v("onSetLanguage", "result:" + arg0);
        }


        @Override
        public void onSendWeather(int arg0) throws RemoteException {
            Log.v("onSendWeather", "result:" + arg0);
        }


        @Override
        public void onSetAntiLost(int arg0) throws RemoteException {
            Log.v("onSetAntiLost", "result:" + arg0);

        }


        @Override
        public void onReceiveSensorData(int heartrate, int pressureHigh, int pressureLow, int oxygen,
                                        int arg4) throws RemoteException {
//            心率, 高血压, 低血压, 血氧, 疲劳值);
            Event event = null;
            if (heartrate != 0) {
                System.out.println("心率来了");
                HeartRate heartRate = new HeartRate();
                heartRate.setHeartRate(String.valueOf(heartrate));
                heartRate.setPreciseDate(BaseUtils.getPreciseDate());

                event = new Event();
                event.heartRate = heartRate;

                EventBus.getDefault().post(event);
            }
            if (pressureHigh != 0 && pressureLow != 0) {
                System.out.println("血压来了");
                BloodPressure pressure = new BloodPressure();
                pressure.setBloodPressureHigh(String.valueOf(pressureHigh));
                pressure.setBloodPressureLow(String.valueOf(pressureLow));
                pressure.setPreciseDate(BaseUtils.getPreciseDate());
                modelDao.insertBloodPressure(pressure);

                event = new Event();
                event.pressure = pressure;
                EventBus.getDefault().post(event);
            }

            if (oxygen != 0) {
                System.out.println("血氧来了");
                BloodOxygen bloodOxygen = new BloodOxygen();
                bloodOxygen.setBloodOxygen(String.valueOf(oxygen));
                bloodOxygen.setPreciseDate(BaseUtils.getPreciseDate());

                event = new Event();
                event.oxygen = bloodOxygen;
                EventBus.getDefault().post(event);
            }

        }


        @Override
        public void onSetBloodPressureMode(int arg0) throws RemoteException {
            Log.v("onSetBloodPressureMode", "result:" + arg0);
        }


        @Override
        public void onGetMultipleSportData(int flag, String recorddate, int mode, int value)
                throws RemoteException {
//            Date date = new Date(timestamp * 1000);
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//            String recorddate = sdf.format(date);
            Log.v("onGetMultipleSportData", "flag:" + flag + " , mode :" + mode + " recorddate:" + recorddate + " , value :" + value);
        }


        @Override
        public void onSetGoalStep(int result) throws RemoteException {
            Log.v("onSetGoalStep", "result:" + result);
        }


        @Override
        public void onSetDeviceHeartRateArea(int result) throws RemoteException {
            Log.v("onSetDeviceHeartRate", "result:" + result);
        }


        @Override
        public void onSensorStateChange(int type, int state)
                throws RemoteException {

            Log.v("onSensorStateChange", "type:" + type + " , state : " + state);
        }


        @Override
        public void onReadCurrentSportData(int mode, String time, int step,
                                           int cal) throws RemoteException {

            Log.v("onReadCurrentSportData", "mode:" + mode + " , time : " + time + " , step : " + step + " cal :" + cal);
        }

        @Override
        public void onGetOtaInfo(boolean isUpdate, String version, String path) throws RemoteException {
            Log.v("onGetOtaInfo", "isUpdate " + isUpdate + " version " + version + " path " + path);
        }

        @Override
        public void onGetOtaUpdate(int step, int progress) throws RemoteException {
            Log.v("onGetOtaUpdate", "step " + step + " progress " + progress);
        }

        @Override
        public void onSetDeviceCode(int result) throws RemoteException {
            Log.v("onSetDeviceCode", "result " + result);
        }

        @Override
        public void onGetDeviceCode(byte[] bytes) throws RemoteException {
            Log.v("onGetDeviceCode", "bytes " + BaseUtils.byteArrayToString(bytes));
        }

    };

    private void callRemoteConnect(String name, String mac) {
        if (mac == null || mac.length() == 0) {
            Toast.makeText(this, "ble device mac address is not correctly!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mService != null) {
            try {
                mService.connectBt(name, mac);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("我是onCreate，我被执行了");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //注册EventBus
        EventBus.getDefault().register(this);

        modelDao = new ModelDao(getApplicationContext());

        if (deviceInfos == null) {
            deviceInfos = new ArrayList<>();
        }
        device_adapter = new DeviceAdapter(getApplicationContext(), deviceInfos);

        if (!TextUtils.isEmpty(SpHelp.getUserId())) {
            user_id = SpHelp.getUserId();
        }
        initHandlerAndRunnable();

        // 检查注册信息以及手环绑定信息
        if (!TextUtils.isEmpty(user_id) && !TextUtils.isEmpty(SpHelp.getDeviceMac())) {
            setContentView(R.layout.main_activity);

            MyApplication.getInstance().addActivity(MainActivity.this);

            flag = true;
            initServiceAndReceiver(); //初始化蓝牙服务,各种UUID以及注册广播
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            openBluetooth();          //初始化蓝牙适配器 判断是否打开蓝牙
            readyScanDeice();
            initUI();
        } else {
            //如果Registre表中数据为空，即现在这台设备是陌生的设备，那么先显示设备绑定界面
            //即搜索附近的蓝牙BLE设备
            //绑定设备
            setContentView(R.layout.bracelet_search_ble);
            MyApplication.getInstance().addActivity(this);

            flag = false;
            openBluetooth();
            readyScanDeice();

            titleView = (TitleView) findViewById(R.id.titleview);
            titleView.setbg(R.drawable.register_titlebg);
            titleView.setTitle(R.string.bind_device);
            ListView lv_device_list = (ListView) findViewById(R.id.lv_device_list);
            lv_device_list.setAdapter(device_adapter);
            titleView.right(R.string.refresh, new TitleView.onSetLister() {
                @Override
                public void onClick(View button) {
                    if (null != deviceInfos && deviceInfos.size() > 0) {
                        deviceInfos.clear();
                        device_adapter.notifyDataSetChanged();
                    }
                    readyScanDeice();
                }
            });

            //搜索出一个列表,选中相应的手环蓝牙地址MAC
            lv_device_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String mac = deviceInfos.get(i).getMac();
                    SpHelp.saveDeviceMac(mac); //保存Mac地址
                    scanBleDevice(false);  //当你都选择了一个设备以后，那就不需要再继续扫描附近的设备了
                    //选中某个MAC后 进入用户注册信息界面;
                    if (!TextUtils.isEmpty(SpHelp.getUserId())) {
                        //会跳转到这个页面只有两种情况，要么是初次使用软件，
                        //要么是解除绑定后跳转到这个页面
                        //这种情况是解除绑定
                        setContentView(R.layout.main_activity);
                        initServiceAndReceiver(); //初始化蓝牙服务,各种UUID以及注册广播
                        readyScanDeice();
                        initUI();
                    } else {    //这种情况是 初次使用软件，则需要注册
                        Intent intent1 = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(intent1);
                        MethodUtils.showToast(getApplicationContext(), "绑定设备成功");
                        finish();
                    }
                }
            });
        }
    }

    /**
     * 初始化了一个Handler、Runnable，但是Runnable并没有执行，只是先初始化，等待之后被执行
     */
    public void initHandlerAndRunnable() {
        mHandler = new Handler();
        runnable_reconn = new Runnable() {  //我去，这个runnable内部还要执行自己，
            //即每15秒 扫描一次附近的蓝牙设备
            @Override
            public void run() {
                if (MyApplication.mBluetoothAdapter.isEnabled()) {//如果蓝牙已经打开
                    readyScanDeice();
                } else {
                    //提示开启蓝牙
                    openBluetooth();
                    readyScanDeice();
                }
                mHandler.postDelayed(runnable_reconn, 1000 * 5);
            }
        };

        runnable_stop_scan = new Runnable() {
            @Override
            public void run() {
                if (mScaning) {
                    System.out.println("停止了窗口");
                    scanBleDevice(false);
                    if (mScanningDialog != null && mScanningDialog.isShowing()) {
                        mScanningDialog.dismiss();
                    }

                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("很遗憾，没有搜索到可用信号")
                            .setIcon(R.drawable.warning)
                            .setMessage("是否重新搜索?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    readyScanDeice();
                                }
                            })
                            .setNegativeButton("取消", null).create();
                    System.out.println("deviceInfos.size() :" + deviceInfos.size());
                    if (deviceInfos.size() == 0) {
                        alertDialog.show();
                    }
                    if ((flag == true) && (!deviceInfos.contains(SpHelp.getDeviceMac()))) {
                        alertDialog.show();
                    }
                }
            }
        };
    }


    //初始化蓝牙适配器 判断是否打开蓝牙
    public void openBluetooth() {
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {   //如果手机不支持蓝牙，则直接结束
            finish();
        }
        MyApplication.mBluetoothAdapter = mBluetoothAdapter;
        MyApplication.mBluetoothManager = mBluetoothManager;
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    public void initServiceAndReceiver() {
        Intent gattServiceIntent = new Intent(MainActivity.this, BleService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);


        /**
         * 这个Service 的作用是 检查一下要不要向网络服务器上传本地数据库的 健康数据，
         * 如果需要上传，那么具体要上传哪几天的数据
         */
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ACTION_GATT_CONNECTED);            //蓝牙已经连接
        mIntentFilter.addAction(ACTION_GATT_DISCONNECTED);         //蓝牙连接已经断开
        mIntentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);          //蓝牙特征值改变，数据返回，解析数据
        mIntentFilter.addAction(BleService.ACTION_RSSI_CHANGED);          //手环信号强度改变
        registerReceiver(mBroadcastReceiver, mIntentFilter);

        Intent intent = new Intent(MainActivity.this, SampleBleService.class);
        bindService(intent, mServiceConnection2, BIND_AUTO_CREATE);
    }

    //蓝牙广播
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_CONNECTED.equals(action)) {
                //蓝牙已连接
                closeAlarm();
                if (runnable_reconn != null) {
                    mHandler.removeCallbacks(runnable_reconn);
                }
                if (mConnectingDialog != null && mConnectingDialog.isShowing()) {
                    mConnectingDialog.dismiss();
                    mConnectingDialog = null;
                }

                MethodUtils.showToast(getApplicationContext(), "蓝牙设备连接成功");
                img_connect_state.setImageResource(R.drawable.connected);

                if (SpHelp.getPhoneCallRemind()) {
                    startMonitorPhone();
                }

                SystemClock.sleep(2000);
                //这里必须要 睡眠2秒，是为了保证BleService已经打开了一切通知，如果没停顿两秒，
                //则很有可能会报错
                BleService.sendCommand(OrderData.getCommonOrder(OrderData.GET_SLEEP_BIG_DATA));
                //手机向手环 获取昨天睡眠数据
                //为什么这条语句放在 同步历史数据的前面呢？因为睡眠数据就是历史数据，所以必须在同步睡眠数据之前
                //将昨天的睡眠数据补充完整
                SystemClock.sleep(500);
                syncHistoryData();    //很关键，每次连接手环后，都要将手机中的数据同步到服务器上

                SystemClock.sleep(500);
                BleService.sendCommand(OrderData.getCommonOrder(OrderData.SYN_TIME_ORDER));
                SystemClock.sleep(500);
                BleService.sendCommand(OrderData.getCommonOrder(OrderData.SYN_SPORT_ORDER));
            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {//断开报警

                img_connect_state.setImageResource(R.drawable.dis_connected);

                if (SpHelp.getFdRemind() == SpHelp.FD_REMIND_OPEN) {  //
                    //开启了防丢
                    openAlarmPrompt();
                }

                /**
                 *启动重连
                 */
                if (null != runnable_reconn) {
                    mHandler.removeCallbacks(runnable_reconn);
                }
                mHandler.post(runnable_reconn);
            } else if (BleService.ACTION_DATA_AVAILABLE.equals(action)) {
                //蓝牙特征值改变，数据返回，解析数据
                byte[] dataArray = intent.getByteArrayExtra("byteData");
                new ResolveData(MainActivity.this).decodeCommonData(dataArray);
            } else if (BleService.ACTION_RSSI_CHANGED.equals(action)) {
                int rssi = intent.getIntExtra("rssi", 0);
                if (Math.abs(rssi) > 80) {
                    BleService.sendCommand(OrderData.getCommonOrder(OrderData.SHAKE_ORDER));
                }
            }
        }
    };

    //开启手机来电监控
    private void startMonitorPhone() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        BleService.sendCommand(OrderData.getCommonOrder(OrderData.PHONE_CALL_REMIND_ORDER));
                        break;
                    default:
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        manager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * 准备扫描设备;
     * 每次扫描设备之前都要先关闭Gatt
     */
    private void readyScanDeice() {
        BleService.blueToothServiceclose();   //关闭mBluetoothGatt
        if (mScanningDialog != null && mScanningDialog.isShowing()) {
            mScanningDialog.dismiss();
            mScanningDialog = null;
        }
        mScanningDialog = MethodUtils.createDialog(MainActivity.this, "正在搜索附近手环");
        mScanningDialog.show();

        if (mScaning) {
            scanBleDevice(false);
            return;
        }
        scanBleDevice(true);
    }


    /**
     * 开启(关闭)蓝牙扫描
     *
     * @param enable
     */
    private void scanBleDevice(boolean enable) {
        if (enable) {
            mScaning = true;
            mHandler.postDelayed(runnable_stop_scan, SCAN_SECOND);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScaning = false;
            if (runnable_stop_scan != null) {
                mHandler.removeCallbacks(runnable_stop_scan);
            }
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // 扫描装置的回调
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
//            Log.v(TAG,"device.name = " + device.getName());
//            Log.v(TAG,"device.mac = " + device.getAddress());
            DeviceInfo newDevice = new DeviceInfo();
            newDevice.setName(device.getName());
            newDevice.setMac(device.getAddress());
            EventBus.getDefault().post(newDevice);

            if (device.getAddress().equals(SpHelp.getDeviceMac())) {
                //当搜到可用设备,停止扫描;
                if (mScaning) {
                    scanBleDevice(false);

                    if (mService != null) {
//                        mBluetoothLeService.connectBleDevice(device.getAddress(), MainActivity.this);
                        /**
                         *
                         * device.name = Y9A-56B9
                         device.mac = 09:20:99:09:56:B9
                         */
                        callRemoteConnect(device.getName(), device.getAddress());

                        if (mScanningDialog != null && mScanningDialog.isShowing()) {
                            mScanningDialog.dismiss();
                            mScanningDialog = null;
                        }
                        if (mConnectingDialog != null) {
                            mConnectingDialog.dismiss();
                            mConnectingDialog = null;
                        }
                        mConnectingDialog = MethodUtils.createDialog(MainActivity.this, "正在连接蓝牙设备,请稍等");
                        mConnectingDialog.show();
                    } else {
                        System.out.println("连接时 mService 为null******************");
                    }
                }
            }
        }
    };

    public void syncHistoryData() {
        if (!MyApplication.isConndevice) {
            MethodUtils.showToast(getApplicationContext(), "请先连接蓝牙设备");
            return;
        }

        if (MethodUtils.is3G(getApplicationContext()) || MethodUtils.isWifi(getApplicationContext())) {
            System.out.println("正在同步数据到服务器,请耐心等待几秒");
            MethodUtils.showToast(MainActivity.this, "正在同步数据到服务器,请耐心等待几秒");
            MethodUtils.synHistotyData(MainActivity.this);
        } else {
            MethodUtils.showToast(MainActivity.this, "请检查网络是否正常!");
        }
    }

    /**
     * 发送一个指定Action的广播
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
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

    /**
     * 报警提示
     */
    public void openAlarmPrompt() {
        //弹出窗口
        mAlertDialog = new AlertDialog.Builder(MainActivity.this)
                .setMessage("手环和手机连接中断")
                .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeAlarm();
                    }
                }).create();
        mAlertDialog.show();
        //开始响铃
        String strName = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_ALARM).toString();
        rt = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(strName));
        rt.play();
        //开始震动
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{0, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500}, -1);
    }

    /**
     * 关闭报警提示
     */
    public void closeAlarm() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        if (rt != null) {
            rt.stop();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    public void onEventMainThread(Event event) {
        if (event.update_syn_time) {
            MethodUtils.showToast(MainActivity.this, "时间同步成功");
        }
        if (event.pressure != null) {
            MethodUtils.uploadBloodPressure(MainActivity.this, event.pressure);
        }
    }

    public void onEventMainThread(DeviceInfo info)
    //info是刚刚扫描到的设备的信息
    {
        if (deviceInfos == null) {
            return;
        }
        boolean is_exist = false;
        if (deviceInfos.size() > 0) {
            for (int i = 0; i < deviceInfos.size(); i++) {
                if (deviceInfos.get(i).getMac().equals(info.getMac())) {  //如果刚刚扫描到的设备Mac和deviceInfos的设备相符合
                    //则说明该设备已经保存过
                    is_exist = true;
                    break;
                }
            }
        }
        if (!is_exist) {          //如果不存在，则把新设备添加到我们的deviceInfos列表中
            deviceInfos.add(info);
            device_adapter.changeData(deviceInfos);
        }
    }

    //发送数据

    @Override
    protected void onRestart() {
        super.onRestart();
        if (SpHelp.getJoinGround()) {
            String address = SpHelp.getDeviceMac();
            if (!TextUtils.isEmpty(address) && null != BleService.mBluetoothAdapter) {
                BluetoothDevice device = BleService.mBluetoothAdapter.getRemoteDevice(address);
                int state = BleService.mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
                if (state == BluetoothProfile.STATE_DISCONNECTED) {//断开 界面重新加载(防止某些功能失效,如EventBus)
                    //判断蓝牙是否打开,如果打开了,直接扫描;
                    openBluetooth();
                    readyScanDeice();
                }
            }
            SpHelp.saveJoinGround(false);
        }

        if (SpHelp.getUserPhoto() != null) {
            iv_photo.setImageBitmap(SpHelp.getUserPhoto());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothLeService != null) {
            BleService.blueToothServiceclose();
        }
        EventBus.getDefault().unregister(this);
        //关闭扫描
        if (mScaning) {
            scanBleDevice(false);
        }

        if (!TextUtils.isEmpty(user_id)) {

            MyApplication.isConndevice = false;
            try {
                if (mServiceConnection != null)
                    unbindService(mServiceConnection);

                if (mBroadcastReceiver != null)
                    unregisterReceiver(mBroadcastReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }

            stopService(new Intent(this, BleService.class));
            if (timer != null) {
                timer.cancel();
            }
            //关闭缓冲
            if (mConnectingDialog != null && mConnectingDialog.isShowing()) {
                mConnectingDialog.dismiss();
            }

            if (runnable_reconn != null) {
                mHandler.removeCallbacks(runnable_reconn);
            }
        }
    }

    private AlertDialog mAlertDialog = null;//报警弹窗
    private Vibrator vibrator;//报警震动
    private Ringtone rt;//报警铃声

    private TitleView titleView; //标题封装类
    private TextView tv_name; //用户姓名
    private DeviceAdapter device_adapter;

    private ViewPager vpViewPager = null;
    private ArrayList<View> views;
    private ImageView[] indicators = null; //底部滑动 圈圈
    private ImageView iv_photo, iv_top_bg;
    private ImageView img_connect_state;
    private Timer timer;

    private void initUI() {
        iv_top_bg = (ImageView) findViewById(R.id.iv_top_bg);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        iv_photo.setOnClickListener(this);
        tv_name = (TextView) findViewById(R.id.tv_name);
        img_connect_state = (ImageView) findViewById(R.id.img_connect_state);

        vpViewPager = (ViewPager) findViewById(R.id.vpViewPager1);

        Register userInfo = modelDao.getLastRegister();
        if (userInfo != null) {
            tv_name.setText(userInfo.getName());
        }

        if (SpHelp.getUserPhoto() != null) {
            iv_photo.setImageBitmap(SpHelp.getUserPhoto());
        } else {
            iv_photo.setBackgroundResource(R.drawable.home_photo);
        }
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout indicatorLayout = (LinearLayout) findViewById(R.id.indicator);

        //主界面就两个界面 由于设置了Viewpager,可以自由滑动
        views = new ArrayList<>();
        View v1 = inflater.inflate(R.layout.activity_home_page_first, null);
        View v2 = inflater.inflate(R.layout.activity_home_page_second, null);
        View v3 = inflater.inflate(R.layout.activity_home_page_third, null);
        views.add(v1);
        views.add(v2);
        views.add(v3);
        BasePagerAdapter pagerAdapter = new BasePagerAdapter(views);
        vpViewPager.setAdapter(pagerAdapter);
        vpViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        /**
         * 下面代码是初始化底部的小圆圈
         */
        int ss = 3;   //代表底部圆圈的个数
        indicators = new ImageView[ss];
        for (int s = 0; s < ss; s++) {
            indicators[s] = new ImageView(this);
            indicators[s].setBackgroundResource(R.drawable.circle_stoken); //两个圆圈都设置为空心圆
            indicatorLayout.addView(indicators[s]);
        }
        iv_top_bg.setBackgroundResource(R.drawable.home_bg_first);
        indicators[0].setBackgroundResource(R.drawable.circle_fill); //默认第一个设置为实心圆


        //两个pager有很多图像控件，给这些控件设设置触发函数
        v1.findViewById(R.id.rl_steps).setOnClickListener(this);
        v1.findViewById(R.id.rl_sportTimes).setOnClickListener(this);
        v1.findViewById(R.id.rl_distances).setOnClickListener(this);
        v1.findViewById(R.id.rl_carolias).setOnClickListener(this);
        v1.findViewById(R.id.rl_sleeps).setOnClickListener(this);

        v2.findViewById(R.id.rl_sugar).setOnClickListener(this);
        v2.findViewById(R.id.rl_heartRates).setOnClickListener(this);
        v2.findViewById(R.id.rl_pressure).setOnClickListener(this);
        v2.findViewById(R.id.rl_oxygen).setOnClickListener(this);

        v3.findViewById(R.id.rl_hearth_check).setOnClickListener(this);
        v3.findViewById(R.id.rl_set_reminder).setOnClickListener(this);
        v3.findViewById(R.id.rl_advice).setOnClickListener(this);
        v3.findViewById(R.id.rl_call_service).setOnClickListener(this);

        taskGetPush();//启动接收推送消息的线程

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /**
             * 下面6个是Page1的图片控件
             */
            case R.id.rl_steps:    //进入步数Activity
                Intent intent1 = new Intent(getApplicationContext(), StepActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_carolias:  //进入能量消耗Activity
                Intent intent2 = new Intent(getApplicationContext(), CaloriaActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_sportTimes:   //进入运动时间Activity
                Intent intent5 = new Intent(getApplicationContext(), SportTimeActivity.class);
                startActivity(intent5);
                break;
            case R.id.rl_distances:    //进入运动距离Activity
                Intent intent12 = new Intent(getApplicationContext(), DistanceActivity.class);
                startActivity(intent12);
                break;
            case R.id.rl_sleeps:   //进入睡眠质量Activity
                Intent intent4 = new Intent(getApplicationContext(), SleepActivity.class);
                startActivity(intent4);
                break;
            case R.id.rl_heartRates:  //进入心率Activity
                Intent intent3 = new Intent(getApplicationContext(), HeartRateActivity.class);
                startActivity(intent3);
                break;

            /**
             * 下面6个是page2的图片控件
             */

            case R.id.rl_sugar:  //进入血糖Activity
                Intent intent8 = new Intent(getApplicationContext(), BloodSugarActivity.class);
                startActivity(intent8);
                break;
            case R.id.rl_pressure:   //进入血压Activity
                Intent intent18 = new Intent(getApplicationContext(), BloodPressureActivity.class);
                startActivity(intent18);
                break;
            case R.id.rl_hearth_check:   //进入健康检查Activity
                Intent intent6 = new Intent(getApplicationContext(), HealthScanActivity.class);
                startActivity(intent6);
                break;
            case R.id.rl_set_reminder:   //进入设置提醒Activity
                Intent intent9 = new Intent(getApplicationContext(), RemindActivity.class);
                startActivity(intent9);
                break;
            case R.id.rl_call_service:   //进入呼叫客服Activity
                Intent intent13 = new Intent(getApplicationContext(), ContactServicesActivity.class);
                intent13.putExtra("uid", user_id);
                startActivity(intent13);
                break;
            case R.id.rl_advice:      //进入专家建议Activity
                Intent intent11 = new Intent(getApplicationContext(), PushListActivity.class);
                startActivity(intent11);
                break;

            case R.id.rl_oxygen:      //进入血氧Activity
                Intent intent15 = new Intent(getApplicationContext(), BloodOxygenActivity.class);
                startActivity(intent15);
                break;

            case R.id.iv_photo:  //主页面的用户头像 点击后进入完善用户信息的界面
                Intent intent14 = new Intent(getApplicationContext(), BasicInfomationActivity.class);
                startActivity(intent14);
                break;
        }
    }

    /**
     * 定时获取推送消息,通过广播通知
     */
    private void taskGetPush() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (MethodUtils.isWifi(getApplicationContext())
                        || MethodUtils.is3G(getApplicationContext())) {
                    pushReceive();
                }
            }
        }, 1000, 60 * 1000);// 一小时检查一次推送
    }

    private void pushReceive() {
        if (!TextUtils.isEmpty(user_id)) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", user_id);
            HttpRequest.get(URLConstant.URL_PUSH, null, params, new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optString("error").equals("0")) {
                        JSONArray list = response.optJSONArray("data");

                        ArrayList<PushMessage> pushMessages = new ArrayList<>();
                        for (int i = 0; i < list.length(); i++) {
                            PushMessage pushMessage = new PushMessage();
                            JSONObject tem = list.optJSONObject(i);
                            pushMessage.setId(tem.optString("id"));
                            pushMessage.setTitle(tem.optString("title"));
                            pushMessage.setBrief(tem.optString("brief"));
                            pushMessages.add(pushMessage);
                        }

                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        for (PushMessage pushMessage : pushMessages) {
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
                            builder.setContentTitle(pushMessage.getTitle());
                            builder.setContentText(pushMessage.getBrief());
                            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.noti_big));
                            builder.setSmallIcon(R.drawable.noti_small);
                            builder.setTicker("如医新消息");
                            builder.setWhen(System.currentTimeMillis());
                            builder.setAutoCancel(true);
                            builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring));

                            Intent intent = new Intent(MainActivity.this, PushDetailActivity.class);
                            intent.putExtra("pushMessage", pushMessage);
                            PendingIntent pt = PendingIntent.getActivity(MainActivity.this, pushCount.decrementAndGet(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            builder.setContentIntent(pt);
                            notificationManager.notify(pushCount.decrementAndGet(), builder.build());
                        }
                    }
                }

                @Override
                public void onFailure() {
                    MethodUtils.showToast(MainActivity.this, "获取新通知失败，请检查网络是否异常");
                }
            });
        }
    }


    /**
     * 这个方法是ViewPager的滑动的回调方罚方法
     */
    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                iv_top_bg.setBackgroundResource(R.drawable.home_bg_first);
            } else if (position == 1) {
                iv_top_bg.setBackgroundResource(R.drawable.home_bg_second);
            } else {
                iv_top_bg.setBackgroundResource(R.drawable.home_bg_third);
            }
            for (int i = 0; i < indicators.length; i++) {
                indicators[i].setBackgroundResource(R.drawable.circle_stoken);
            }
            indicators[position].setBackgroundResource(R.drawable.circle_fill);

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

    }

    //定义了一个PageAdapter，该适配器是为了页面滑动
    //以List<View>为基础来构造适配器
    public class BasePagerAdapter extends PagerAdapter {
        private List<View> views = new ArrayList<>();

        public BasePagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }
        //这是内部最重要的函数，要执行两个步骤：
        //第一：container.addView() 添加View到ViewPager中
        //第二：返回刚刚添加的View
    }


}
