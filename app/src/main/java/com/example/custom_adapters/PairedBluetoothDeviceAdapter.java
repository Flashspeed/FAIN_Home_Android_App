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

public class PairedBluetoothDeviceAdapter extends ArrayAdapter<String>
{

    private ListView bluetoothConnectedDevicesListView;

    public PairedBluetoothDeviceAdapter(@NonNull Context context, String[] bluetoothDeviceName)
    {
        super(context, R.layout.bluetooth_connected_devices_entry, bluetoothDeviceName);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflatedView = LayoutInflater.from(getContext());
        View customView = inflatedView.inflate(R.layout.bluetooth_connected_devices_entry, parent, false);

        String deviceName = getItem(position);
        String[] arrayDeviceNames = new String[0];
        if (deviceName != null)
        {
            arrayDeviceNames = new String[deviceName.length()];
        }

        if (deviceName != null)
        {
            for (int i = 0; i < deviceName.length(); i++)
            {
                arrayDeviceNames[i] = deviceName;
            }
        }

        for (String names : arrayDeviceNames)
        {
            System.out.println(String.format("__Device names %s", names));
        }

        final Switch deviceSwitch = customView.findViewById(R.id.deviceStateSwitch);
        TextView textView = customView.findViewById(R.id.connectedDeviceName);
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
