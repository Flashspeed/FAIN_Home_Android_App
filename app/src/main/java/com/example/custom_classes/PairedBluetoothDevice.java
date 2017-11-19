package com.example.custom_classes;

public class PairedBluetoothDevice
{
    public String deviceName;
    public String deviceMacAddress;

    public PairedBluetoothDevice(String deviceName, String deviceMacAddress)
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
