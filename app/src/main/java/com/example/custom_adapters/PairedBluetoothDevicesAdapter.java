package com.example.custom_adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.custom_classes.PairedBluetoothDevice;
import com.example.fain_home.PairingActivity;
import com.example.fain_home.R;

import java.util.ArrayList;

public class PairedBluetoothDevicesAdapter extends ArrayAdapter<PairedBluetoothDevice>
{
    private final String BED_1_LIGHT_ON  = "bed_1_on!";
    private final String BED_1_LIGHT_OFF = "bed_1_off!";
    private ListView bluetoothConnectedDevicesListView;

    public PairedBluetoothDevicesAdapter(
            @NonNull Context context,
            ArrayList<PairedBluetoothDevice> unpairedBluetoothDevices)
    {
        super(context, 0, unpairedBluetoothDevices);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        final PairedBluetoothDevice pairedDevice         = getItem(position);
        LayoutInflater              inflatedView         = LayoutInflater.from(getContext());
        View                        customView           = inflatedView.inflate(R.layout.bluetooth_connected_devices_entry, parent, false);
        ArrayList<String>           arrayListDeviceNames = new ArrayList<>();

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
        textView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                //TODO Allow user to unpair device by long holding on the connected device
                Toast.makeText(
                        getContext(),
                        String.format("You long held textview %s",
                                textView.getText()
                        ),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        textView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(
                        getContext(),
                        String.format("You tapped textview %s",
                                textView.getText()
                        ),
                        Toast.LENGTH_SHORT).show();
            }
        });

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
                    PairingActivity.bluetoothService.write(BED_1_LIGHT_ON.getBytes());
                }
                else
                {
                    Toast.makeText(
                            getContext(),
                            String.format("Checked state for switch %s is %b",
                                    textView.getText(),
                                    deviceSwitch.isChecked()),
                            Toast.LENGTH_SHORT).show();
                    PairingActivity.bluetoothService.write(BED_1_LIGHT_OFF.getBytes());
                }
            }
        });

        return customView;
    }
}
