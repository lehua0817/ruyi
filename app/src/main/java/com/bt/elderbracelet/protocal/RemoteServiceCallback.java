package com.bt.elderbracelet.protocal;

import android.os.RemoteException;

import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;

public class RemoteServiceCallback extends IServiceCallback.Stub {
    @Override
    public void onAuthSdkResult(int i) throws RemoteException {

    }

    @Override
    public void onScanCallback(String s, String s1, int i) throws RemoteException {

    }

    @Override
    public void onConnectStateChanged(int i) throws RemoteException {

    }

    @Override
    public void onAuthDeviceResult(int i) throws RemoteException {

    }

    @Override
    public void onGetDeviceTime(int i, String s) throws RemoteException {

    }

    @Override
    public void onSetDeviceTime(int i) throws RemoteException {

    }

    @Override
    public void onSetUserInfo(int i) throws RemoteException {

    }

    @Override
    public void onGetCurSportData(int i, long l, int i1, int i2, int i3, int i4, int i5, int i6) throws RemoteException {

    }

    @Override
    public void onSendVibrationSignal(int i) throws RemoteException {

    }

    @Override
    public void onSetPhontMode(int i) throws RemoteException {

    }

    @Override
    public void onSetIdleTime(int i) throws RemoteException {

    }

    @Override
    public void onSetSleepTime(int i) throws RemoteException {

    }

    @Override
    public void onGetDeviceBatery(int i, int i1) throws RemoteException {

    }

    @Override
    public void onGetDeviceInfo(int i, String s, String s1, String s2, int i1) throws RemoteException {

    }

    @Override
    public void onSetAlarm(int i) throws RemoteException {

    }

    @Override
    public void onSetDeviceMode(int i) throws RemoteException {

    }

    @Override
    public void onSetNotify(int i) throws RemoteException {

    }

    @Override
    public void onGetSenserData(int i, long l, int i1, int i2) throws RemoteException {

    }

    @Override
    public void setAutoHeartMode(int i) throws RemoteException {

    }

    @Override
    public void onSetDeviceInfo(int i) throws RemoteException {

    }

    @Override
    public void onSetHourFormat(int i) throws RemoteException {

    }

    @Override
    public void onGetDataByDay(int i, long l, int i1, int i2) throws RemoteException {

    }

    @Override
    public void onGetDataByDayEnd(int i, long l) throws RemoteException {

    }

    @Override
    public void onGetDeviceAction(int i) throws RemoteException {

    }

    @Override
    public void onGetBandFunction(int i, boolean[] booleans) throws RemoteException {

    }

    @Override
    public void onSetLanguage(int i) throws RemoteException {

    }

    @Override
    public void onSendWeather(int i) throws RemoteException {

    }

    @Override
    public void onSetAntiLost(int i) throws RemoteException {

    }

    @Override
    public void onSetBloodPressureMode(int i) throws RemoteException {

    }

    @Override
    public void onReceiveSensorData(int i, int i1, int i2, int i3, int i4) throws RemoteException {

    }

    @Override
    public void onGetMultipleSportData(int i, String s, int i1, int i2) throws RemoteException {

    }

    @Override
    public void onSetGoalStep(int i) throws RemoteException {

    }

    @Override
    public void onSetDeviceHeartRateArea(int i) throws RemoteException {

    }

    @Override
    public void onSensorStateChange(int i, int i1) throws RemoteException {

    }

    @Override
    public void onReadCurrentSportData(int i, String s, int i1, int i2) throws RemoteException {

    }

    @Override
    public void onGetOtaInfo(boolean b, String s, String s1) throws RemoteException {

    }

    @Override
    public void onGetOtaUpdate(int i, int i1) throws RemoteException {

    }

    @Override
    public void onSetDeviceCode(int i) throws RemoteException {

    }

    @Override
    public void onGetDeviceCode(byte[] bytes) throws RemoteException {

    }
}
