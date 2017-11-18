package com.example.custom_adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.custom_classes.PairedBluetoothDevices;
import com.example.fain_home.R;

import java.util.ArrayList;

public class PairedBluetoothDeviceAdapter extends ArrayAdapter<PairedBluetoothDevices>
{
    private ListView bluetoothConnectedDevicesListView;

    public PairedBluetoothDeviceAdapter(
            @NonNull Context context,
            ArrayList<PairedBluetoothDevices> unpairedBluetoothDevices)
    {
        super(context, 0, unpairedBluetoothDevices);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        final PairedBluetoothDevices pairedDevice         = getItem(position);
        LayoutInflater               inflatedView         = LayoutInflater.from(getContext());
        View                         customView           = inflatedView.inflate(R.layout.bluetooth_connected_devices_entry, parent, false);
        ArrayList<String>            arrayListDeviceNames = new ArrayList<>();

        assert pairedDevice != null;
        String deviceName       = pairedDevice.getDeviceName();
        String deviceMacAddress = pairedDevice.getDeviceMacAddress();

        /* Every time getView is called add the device name to array list */
        arrayListDeviceNames.add(deviceName);

        for (String names : arrayListDeviceNames)
        {
            System.out.println(String.format("__Device names %s", names));
        }

        final Switch   deviceSwitch = customView.findViewById(R.id.deviceStateSwitch);
        final TextView textView     = customView.findViewById(R.id.connectedDeviceName);
        textView.setText(deviceName);

        deviceSwitch.setTag(position);

        deviceSwitch.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (deviceSwitch.isChecked())
                {
                    Toast.makeText(
                            getContext(),
                            String.format("Checked state for switch %s is %s",
                                    textView.getText(),
                                    deviceSwitch.isChecked()
                            ),
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(
                            getContext(),
                            String.format("Checked state for switch %s is %b",
                                    textView.getText(),
                                    deviceSwitch.isChecked()),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return customView;
    }

    public class bluetoothConnectedDeviceItemClickListener implements AdapterView.OnItemClickListener
    {
        /* When the user taps an item in the list view */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Toast.makeText(
                    getContext(),
                    String.format("You tapped %s", bluetoothConnectedDevicesListView.getItemAtPosition(position)),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
