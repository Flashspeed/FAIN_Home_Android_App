package com.example.fain_home;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.custom_classes.BluetoothService;

import java.util.UUID;

public class PairingActivity extends AppCompatActivity
{
    public static BluetoothService bluetoothService;
    final String TAG            = "Pairing Activity";
    final String BED_1_LIGHT_ON = "bed_1_on!";
    /* https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
         *
         * Hint: If you are connecting to a Bluetooth serial board then try using the well-known
         * SPP UUID 00001101-0000-1000-8000-00805F9B34FB. However if you are connecting to an
         * Android peer then please generate your own unique UUID. */
    private final UUID HC06_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter bluetoothAdapter;
    Intent           intentGoToConnectADeviceActivity;
    Intent           intentGoToConnectedDeviceActivity;
    private       Handler          messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pairing);

        TextView pairingDeviceName = findViewById(R.id.txtPairingDeviceName);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        intentGoToConnectADeviceActivity = new Intent(getApplicationContext(), ConnectADeviceActivity.class);
        intentGoToConnectedDeviceActivity = new Intent(getApplicationContext(), ConnectedDevicesActivity.class);

        /* Getting the data that was passed into .putExtra() from
        UnpairedBluetoothDevicesAdapter class */
        assert getIntent().getExtras() != null;
        String          passedDeviceName       = getIntent().getExtras().getString("DEVICE_NAME");
        String          passedDeviceMacAddress = getIntent().getExtras().getString("DEVICE_MAC_ADDRESS");
        BluetoothDevice bluetoothDevice        = getIntent().getExtras().getParcelable("UNPAIRED_DEVICE");

        /* Set the device name that is being paired to the device name passed from
         * unpaired bluetooth device adapter.
         */
        pairingDeviceName.setText(
                String.format("%s : %s", passedDeviceName, passedDeviceMacAddress));

        bluetoothService = new BluetoothService(this, bluetoothDevice);
        bluetoothService.startClient(bluetoothDevice, HC06_UUID);
    }
}
