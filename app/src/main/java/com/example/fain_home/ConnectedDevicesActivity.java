package com.example.fain_home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.custom_adapters.BluetoothDeviceAdapter;

public class ConnectedDevicesActivity extends AppCompatActivity
{
    ListView bluetoothConnectedDevicesListView;
    Switch deviceSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_devices);
        setTitle("Connected Devices");

        bluetoothConnectedDevicesListView = findViewById(R.id.bluetoothConnectedDevicesListView);

        showConnectedBluetoothDevices();
    }

    public void showConnectedBluetoothDevices()
    {
        String[] connectedBluetoothDevices = new String[10];
        for(int i=0; i<connectedBluetoothDevices.length; i++)
        {
            connectedBluetoothDevices[i] = "Device " + (i+1);
        }

        ArrayAdapter<String> connectedBluetoothDevicesAdapter =
                new ArrayAdapter<String>(this,
                        R.layout.bluetooth_connected_devices_entry,
                        R.id.connectedDeviceName,
                        connectedBluetoothDevices);

//        bluetoothConnectedDevicesListView.setAdapter(connectedBluetoothDevicesAdapter);
//        bluetoothConnectedDevicesListView.setOnItemClickListener(new bluetoothConnectedDeviceItemClickListener());

        ListAdapter listAdapter = new BluetoothDeviceAdapter(this, connectedBluetoothDevices);
        bluetoothConnectedDevicesListView.setAdapter(listAdapter);
    }

    public class bluetoothConnectedDeviceItemClickListener implements OnItemClickListener
    {
        /* When the user taps an item in the list view */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Toast.makeText(
                    ConnectedDevicesActivity.this,
                    String.format("You tapped %s", bluetoothConnectedDevicesListView.getItemAtPosition(position)),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public class switchCheckedListener implements CompoundButton.OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            Toast.makeText(
                    ConnectedDevicesActivity.this,
                    String.format("Checked state is %s", isChecked),
                    Toast.LENGTH_SHORT).show();
        }
    }


}
