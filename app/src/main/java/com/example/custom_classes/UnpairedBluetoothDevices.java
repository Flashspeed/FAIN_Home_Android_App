package com.example.custom_classes;

public class UnpairedBluetoothDevices
{
    private String deviceName;
    private String deviceMacAddress;

    public UnpairedBluetoothDevices(String deviceName, String deviceMacAddress)
    {
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
    }

    public String getDeviceName()
    {
        return this.deviceName;
    }

    public String getDeviceMacAddress()
    {
        return this.deviceMacAddress;
    }
}
