package com.example.medicam;

public class BluetoothDeviceInfo {
    private String name;
    private String address;
    private String deviceType;
    private boolean isConnected;

    public BluetoothDeviceInfo() {
    }

    public BluetoothDeviceInfo(String name, String address, String deviceType, boolean isConnected) {
        this.name = name;
        this.address = address;
        this.deviceType = deviceType;
        this.isConnected = isConnected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
