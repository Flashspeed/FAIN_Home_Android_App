package com.example.fain_home;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Switch;

import com.example.custom_adapters.PairedBluetoothDevicesAdapter;
import com.example.custom_classes.PairedBluetoothDevice;

import java.util.ArrayList;
import java.util.Set;

public class ConnectedDevicesActivity extends AppCompatActivity
{
    ListView         bluetoothConnectedDevicesListView;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<PairedBluetoothDevice> arrayListPairedBluetoothDevices = new ArrayList<>();
    Switch deviceSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_devices);
        setTitle("Connected Devices");

        bluetoothConnectedDevicesListView = findViewById(R.id.bluetoothConnectedDevicesListView);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        showConnectedBluetoothDevices();
    }

    public void showConnectedBluetoothDevices()
    {
        String[] connectedBluetoothDevices = new String[10];
        for (int i = 0; i < connectedBluetoothDevices.length; i++)
        {
            connectedBluetoothDevices[i] = "Device " + (i + 1);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice currentPairedDevice : pairedDevices)
            {
                /* Only Show compatible devices. Determined by manufacturer identifier details
                 * contained in first 6 hex digits of the devices MAC Address.
                 */
                if (currentPairedDevice.getAddress().toLowerCase().contains("98:d3:31"))
                {
                    /* Create a new paired device object */
                    PairedBluetoothDevice pairedBluetoothDevice =
                            new PairedBluetoothDevice(currentPairedDevice.getName(), currentPairedDevice.getAddress());

                    /* Add the paired device object to the paired device array list */
                    arrayListPairedBluetoothDevices.add(pairedBluetoothDevice);
                }

            }

            /* Pass the paired device array list to an adapter */
            PairedBluetoothDevicesAdapter pairedBluetoothDevicesAdapter =
                    new PairedBluetoothDevicesAdapter(getApplicationContext(), arrayListPairedBluetoothDevices);

            /* Set the preferred list view to use the paired bluetooth device adapter */
            bluetoothConnectedDevicesListView.setAdapter(pairedBluetoothDevicesAdapter);
        }
    }
}
