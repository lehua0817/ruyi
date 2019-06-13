package com.bt.elderbracelet.activity;

/**
 * 中华人民共和国万岁，毛主义永垂不朽
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.Register;
import com.bt.elderbracelet.entity.others.PersonalDetailOne;
import com.bt.elderbracelet.entity.others.PersonalDetailThree;
import com.bt.elderbracelet.entity.others.PersonalDetailTwo;
import com.bt.elderbracelet.entity.others.PushMessage;
import com.bt.elderbracelet.okhttp.HttpRequest;
import com.bt.elderbracelet.okhttp.URLConstant;
import com.bt.elderbracelet.protocal.OrderData;
import com.bt.elderbracelet.protocal.RemoteServiceCallback;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bttow.elderbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

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

    private String user_id = "";
    private Handler mHandler;
    private Runnable runnable_reconn;   //用于定时重连
    private Runnable runnable_stop_scan;   //用于停止扫描蓝牙

    private AtomicInteger pushCount = new AtomicInteger(10);
    private AlertDialog mConnectingDialog = null;
    private AlertDialog mScanningDialog = null;

    private IRemoteService mService;
    private IServiceCallback mServiceCallback = new RemoteServiceCallback() {

        @Override
        public void onConnectStateChanged(int state) throws RemoteException {
            Log.v("onConnectStateChanged", "state = " + state);
            if (state == BluetoothProfile.STATE_CONNECTED) {
                doAfterConnect();
            } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                doAfterDisConnect();
            }
        }

    };


    /**
     * 手环和手机连接成功后的动作
     */
    private void doAfterConnect() {
        MyApplication.isConnected = true;
        closeAlarm();
//        if (runnable_reconn != null) {
//            mHandler.removeCallbacks(runnable_reconn);
//        }
        if (mConnectingDialog != null && mConnectingDialog.isShowing()) {
            mConnectingDialog.dismiss();
            mConnectingDialog = null;
        }

        MethodUtils.showToast(MainActivity.this, "蓝牙设备连接成功");
        img_connect_state.setImageResource(R.drawable.connected);

        if (SpHelp.getPhoneCallRemind()) {
            startMonitorPhone();
        }

        /**
         * todo  BleService.sendCommand(OrderData.getCommonOrder(OrderData.GET_SLEEP_BIG_DATA));
         */
        //手机向手环 获取昨天睡眠数据
        //为什么这条语句放在 同步历史数据的前面呢？因为睡眠数据就是历史数据，所以必须在同步睡眠数据之前
        //将昨天的睡眠数据补充完整
        SystemClock.sleep(500);
        syncHistoryData();    //很关键，每次连接手环后，都要将手机中的数据同步到服务器上

        saveUserDetailInfo();
    }

    /**
     * 手环和手机取消连接后的动作
     */
    private void doAfterDisConnect() {
        Log.v(TAG, "断开连接");
        MyApplication.isConnected = false;
        img_connect_state.setImageResource(R.drawable.dis_connected);

        if (SpHelp.getFdRemind() == SpHelp.FD_REMIND_OPEN) {
            //开启了防丢
            openAlarmPrompt();
        }
        //启动重连
        if (null != runnable_reconn) {
            mHandler.removeCallbacks(runnable_reconn);
        }
        mHandler.post(runnable_reconn);
    }

    /**
     * 之前保存了蓝牙设备，现在重新连接
     */
    private void connectSavedDevice() {
        String deviceName = SpHelp.getDeviceName();
        String deviceMac = SpHelp.getDeviceMac();
        //判断蓝牙是否打开,如果打开了,直接扫描;
        if (!TextUtils.isEmpty(deviceName) && !TextUtils.isEmpty(deviceMac)) {
            ensureOpenBluetooth();
            connectRomoteDevice(deviceName, deviceMac);
            if (mConnectingDialog != null) {
                mConnectingDialog.dismiss();
                mConnectingDialog = null;
            }
            mConnectingDialog = MethodUtils.createDialog(MainActivity.this, "正在连接蓝牙设备,请稍等");
            mConnectingDialog.show();
        }

    }


    /**
     * @param name 蓝牙设备名字
     * @param mac  设备硬件地址
     *             连接陌生的设备，此方法只在搜索蓝牙后使用
     */
    private void connectRomoteDevice(String name, String mac) {
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
        setContentView(R.layout.main_activity);
        modelDao = new ModelDao(getApplicationContext());
        initUI();
        MyApplication.getInstance().addActivity(MainActivity.this);
        taskGetPush();//启动接收推送消息的线程

        mService = MyApplication.remoteService;
        try {
            mService.registerCallback(mServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        initHandlerAndRunnable();
        registerSmsReceiver();
        connectSavedDevice();
    }

    public void registerSmsReceiver() {
        IntentFilter smsIntentFilter = new IntentFilter();
        // 接收短信的广播
        smsIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, smsIntentFilter);
    }

    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                System.out.println("来短信了");
                if (SpHelp.getPhoneMsgRemind()) {
                    callNotify(OrderData.NOTIFICATION_SMS, "", "");
                }
            }
        }
    };

    private void callNotify(int type, String title, String content) {
        boolean result;
        if (mService != null) {
            try {
                result = mService.setNotify(System.currentTimeMillis() + "", type, title, content);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 初始化了一个Handler、Runnable，但是Runnable并没有执行，只是先初始化，等待之后被执行
     */
//    public void initHandlerAndRunnable() {
//        mHandler = new Handler();
//        runnable_reconn = new Runnable() {
//            //即每15秒 扫描一次附近的蓝牙设备
//            @Override
//            public void run() {
//                connectSavedDevice();
//                mHandler.postDelayed(runnable_reconn, 1000 * 5);
//            }
//        };
//
//        runnable_stop_scan = new Runnable() {
//            @Override
//            public void run() {
//                if (mScaning) {
//                    stopScanDevice();
//                    if (mScanningDialog != null && mScanningDialog.isShowing()) {
//                        mScanningDialog.dismiss();
//                    }
//
//                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity2.this)
//                            .setTitle("很遗憾，没有搜索到可用信号")
//                            .setIcon(R.drawable.warning)
//                            .setMessage("是否重新搜索?")
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    connectSavedDevice();
//                                }
//                            })
//                            .setNegativeButton("取消", null).create();
//                    System.out.println("deviceList.size() :" + deviceList.size());
//                    if (deviceList.size() == 0) {
//                        alertDialog.show();
//                    }
//                    if ((isBound == true) && (!deviceList.contains(SpHelp.getDeviceMac()))) {
//                        alertDialog.show();
//                    }
//                }
//            }
//        };
//    }


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
                        callNotify(OrderData.NOTIFICATION_PHONE, "", "");
                        break;
                    default:
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        manager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void syncHistoryData() {
        if (!MyApplication.isConnected) {
            MethodUtils.showToast(getApplicationContext(), "请先连接蓝牙设备");
            return;
        }

        if (MethodUtils.is3G(getApplicationContext()) || MethodUtils.isWifi(getApplicationContext())) {
            MethodUtils.showToast(MainActivity.this, "正在同步数据到服务器,请耐心等待几秒");
            MethodUtils.synHistotyData(MainActivity.this);
        } else {
            MethodUtils.showToast(MainActivity.this, "请检查网络是否正常!");
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


    @Override
    protected void onRestart() {
        super.onRestart();
        if (SpHelp.getJoinGround()) {
            SpHelp.saveJoinGround(false);
        }
        if (!MyApplication.isConnected) {
            connectSavedDevice();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
        }

        if (mConnectingDialog != null && mConnectingDialog.isShowing()) {
            mConnectingDialog.dismiss();
        }

        if (runnable_reconn != null) {
            mHandler.removeCallbacks(runnable_reconn);
        }
    }

    private AlertDialog mAlertDialog = null;//报警弹窗
    private Vibrator vibrator;//报警震动
    private Ringtone rt;//报警铃声
    private TextView tv_name; //用户姓名
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

    private void saveUserDetailInfo() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", SpHelp.getUserId());
        HttpRequest.get(URLConstant.URL_GET_PERSONAL_DETAIL, null, params, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response.optString("error").equals("0")) {
                    JSONObject data = response.optJSONObject("data");

                    PersonalDetailOne one = new PersonalDetailOne();
                    one.setNation(data.optString("folk"));
                    one.setEducation(data.optString("education"));
                    one.setOccupation(data.optString("job"));
                    one.setAddress(data.optString("address"));
                    one.setWatchHealthTv(data.optBoolean("isWatchHealthTV"));

                    SpHelp.saveObject(SpHelp.PERSONAL_DETAIL_ONE, one);

                    PersonalDetailTwo two = new PersonalDetailTwo();
                    two.setHobby(data.optString("art"));
                    two.setSport(data.optString("sportsRate"));
                    two.setDiet(data.optString("diet"));
                    two.setSmoke(data.optString("smoke"));
                    two.setDrink(data.optString("drink"));
                    two.setAllergic(data.optString("allergic"));

                    SpHelp.saveObject(SpHelp.PERSONAL_DETAIL_TWO, two);

                    PersonalDetailThree three = new PersonalDetailThree();
                    three.setIllness(data.optString("illness"));
                    three.setPhysique(data.optString("body"));
                    SpHelp.saveObject(SpHelp.PERSONAL_DETAIL_THREE, three);

                } else {
                    MethodUtils.showToast(getApplicationContext(), "验证失败: " + response.optString("error_info"));
                }
            }

            @Override
            public void onFailure() {
                MethodUtils.showToast(getApplicationContext(), "获取用户详细信息失败");
            }
        });
    }


}
