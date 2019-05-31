package com.bonten.ble.servise;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;

import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;

public class BleService extends Service {
    private final static String TAG = BleService.class.getSimpleName();
    public static BluetoothManager mBluetoothManager;
    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothGatt mBluetoothGatt;

    public final static String ACTION_GATT_CONNECTED = "com.bonten.bluetooth.ACTION_GATT_CONNECTED";
    //蓝牙连接成功
    public final static String ACTION_GATT_DISCONNECTED = "com.bonten.bluetooth.ACTION_GATT_DISCONNECTED";
    //蓝牙连接关闭
    public final static String ACTION_DATA_AVAILABLE = "com.bonten.bluetooth.ACTION_DATA_AVAILABLE";
    public final static String ACTION_RSSI_CHANGED = "com.bonten.bluetooth.ACTION_RSSI_CHANGED";
    //蓝牙特征值改变，数据返回，解析数据

    UUID UUID_PublicService, UUID_WriteCharacter, UUID_ReceiveCharacter;
    static BluetoothGattService publicService;
    static BluetoothGattCharacteristic writeCharacter;
    static BluetoothGattCharacteristic receiveCharacter;

    private Handler handler;
    private ModelDao modelDao;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(BleService.this.getMainLooper());

        modelDao = new ModelDao(this);
        mBluetoothAdapter = MyApplication.mBluetoothAdapter;
        mBluetoothManager = MyApplication.mBluetoothManager;
        initUuid();

    }

    private void initUuid() {
        UUID_PublicService = UUID.fromString("98ABFE01-ABD6-86F4-E1AB-F61F35ADCBBF");
        UUID_WriteCharacter = UUID.fromString("98ABFE02-ABD6-86F4-E1AB-F61F35ADCBBF");
        UUID_ReceiveCharacter = UUID.fromString("98ABFE03-ABD6-86F4-E1AB-F61F35ADCBBF");
    }

    /**
     * 连接指定Mac地址的蓝牙设备
     */
    public void connectBleDevice(String address, Context context) {
        MyApplication.isConndevice = false;
        if (null == mBluetoothAdapter) {
            return;
        }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return;
        }
        try {
            BluetoothGatt mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
            connect(mBluetoothGatt); //赋值
        } catch (Exception e) {
            Log.e(TAG, "连接异常******************" + e.getMessage());
        }
    }


    public static boolean connect(BluetoothGatt bluetoothGatt) {
        mBluetoothGatt = bluetoothGatt;
        return true;
    }

    /**
     * 蓝牙连接回调
     */
    public BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
                MyApplication.isConndevice = true;
                intentAction = ACTION_GATT_CONNECTED;
                broadcastUpdate(intentAction);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                BleService.blueToothServiceclose();
                MyApplication.isConndevice = false;
                intentAction = ACTION_GATT_DISCONNECTED;
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            publicService = gatt.getService(UUID_PublicService);
            if (publicService == null) {
                System.out.println("找不到公共服务");
                return;
            }
            writeCharacter = gatt.getService(UUID_PublicService).getCharacteristic(UUID_WriteCharacter);
            if (writeCharacter == null) {
                System.out.println("找不到写入特征值");
                return;
            }
            if (status == BluetoothGatt.GATT_SUCCESS) {
                setCharacteristicNotification(writeCharacter, true);  //这是自定义方法，会打开写入特征值的通知
            }
            receiveCharacter = gatt.getService(UUID_PublicService).getCharacteristic(UUID_ReceiveCharacter);
            if (receiveCharacter == null) {
                System.out.println("找不到接收特征值");
                return;
            }
            if (status == BluetoothGatt.GATT_SUCCESS) {
                setCharacteristicNotification(receiveCharacter, true);  //这是自定义方法，会打开接收特征值的通知
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (characteristic.getUuid().equals(UUID_ReceiveCharacter)) {
                byte[] data = characteristic.getValue();
                broadcastUpdatevalue(ACTION_DATA_AVAILABLE, data);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            System.out.println(rssi);
            Intent intent = new Intent(ACTION_RSSI_CHANGED);
            intent.putExtra("rssi", rssi);
            sendBroadcast(intent);
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (null != modelDao) {
            modelDao = null;
        }
    }


    /**
     * 设置特征值通知
     */
    public void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            System.out.println("mBluetoothAdapter 或者 mBluetoothGatt 没有初始化");
            return;
        }
        boolean flag = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if (flag) {
            System.out.println("成功打开通知");
        } else {
            System.out.println("打开通知失败");
        }
        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
        for (BluetoothGattDescriptor descriptor : descriptors) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * 发送通信命令
     */
    public static void sendCommand(byte[] bytes) {
        if (writeCharacter == null) {
            return;
        }
        writeCharacter.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        writeCharacter.setValue(bytes);
        mBluetoothGatt.writeCharacteristic(writeCharacter);
    }

    /**
     * 发送一个指定Action的广播
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }


    /**
     * 发送一个指定Action的,同时携带数据的广播
     *
     * @param action
     * @param Data
     */
    public void broadcastUpdatevalue(final String action, byte[] Data) {
        final Intent intent = new Intent(action);
        intent.putExtra("byteData", Data);
        sendBroadcast(intent);
    }

    /**
     * 好棒的用法，这就相当于，别人通过binder绑定了该Service以后，你返回的是整个Service实例
     * 相当于说，别人可以调用该Service中的所有方法
     */
    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();


    /**
     * 每一次只要蓝牙断开了，就必须关闭Gatt,否则下回连接就可能出问题
     * 关闭mBluetoothGatt
     */
    public static void blueToothServiceclose() {
        MyApplication.isConndevice = false;   //蓝牙没有连接
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }


    public void broadcastUpdateRssi(final String action, int rssis) {
        final Intent intent = new Intent(action);
        intent.putExtra("rssi", rssis);
        sendBroadcast(intent);
    }

    /**
     * 读取蓝牙信号强度
     */
    public static void readRemoteRssi() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readRemoteRssi();
    }


}
