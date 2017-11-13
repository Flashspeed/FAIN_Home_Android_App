package com.example.custom_adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fain_home.R;

public class BluetoothDeviceAdapter extends ArrayAdapter<String>
{
    private String deviceName;
    private String[] arrayDeviceNames;

    private ListView bluetoothConnectedDevicesListView;

    public BluetoothDeviceAdapter(@NonNull Context context, String[] bluetoothDeviceName)
    {
        super(context, R.layout.bluetooth_connected_devices_entry, bluetoothDeviceName);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflatedView = LayoutInflater.from(getContext());
        View customView = inflatedView.inflate(R.layout.bluetooth_connected_devices_entry, parent, false);

        deviceName = getItem(position);

        arrayDeviceNames = new String[deviceName.length()];
        for(int i=0; i<deviceName.length(); i++)
        {
            arrayDeviceNames[i] = deviceName;
        }

        for(String names : arrayDeviceNames)
        {
            Log.i("BluetoothDeviceAdapter", String.format("Device names %s", names));
        }

        final Switch deviceSwitch = customView.findViewById(R.id.deviceStateSwitch);
        TextView textView = customView.findViewById(R.id.connectedDeviceName);
        textView.setText(deviceName);

        deviceSwitch.setTag(position);

//        deviceSwitch.setOnCheckedChangeListener(new switchCheckedListener());


        deviceSwitch.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(deviceSwitch.isChecked())
                {
                    Toast.makeText(
                            getContext(),
                            String.format("Checked state for switch %s is %s",
                                    v.getTag().toString(),
                                    deviceSwitch.isChecked()
                            ),
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(
                            getContext(),
                            String.format("Checked state for switch %s is %b",
                                    v.getTag().toString(),
                                    deviceSwitch.isChecked()),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return customView;
    }

    public class switchCheckChangeListener implements OnCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {

            Toast.makeText(
                    getContext(),
                    String.format("Checked state r switch %b", isChecked),
                    Toast.LENGTH_SHORT).show();
        }
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
