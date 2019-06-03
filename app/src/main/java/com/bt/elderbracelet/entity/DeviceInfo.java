package com.bt.elderbracelet.entity;

import java.io.Serializable;

/**
 * Created by 中庸 on 2016/4/11.
 */
public class DeviceInfo implements Serializable {

    private String name;
    private String mac;

    public DeviceInfo() {
    }
    public DeviceInfo(String name, String mac) {
        this.name = name;
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
