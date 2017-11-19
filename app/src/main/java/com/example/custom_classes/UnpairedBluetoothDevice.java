package com.example.custom_classes;

import android.bluetooth.BluetoothDevice;

public class UnpairedBluetoothDevice
{
    private BluetoothDevice bluetoothDevice;

    public UnpairedBluetoothDevice(BluetoothDevice bluetoothDevice)
    {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getDeviceName()
    {
        return this.bluetoothDevice.getName();
    }

    public String getDeviceMacAddress()
    {
        return this.bluetoothDevice.getAddress();
    }

    public BluetoothDevice getBluetoothDevice()
    {
        return this.bluetoothDevice;
    }
}
