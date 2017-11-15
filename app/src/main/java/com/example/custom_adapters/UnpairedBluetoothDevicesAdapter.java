package com.example.custom_adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.custom_classes.UnpairedBluetoothDevices;
import com.example.fain_home.R;

import java.util.ArrayList;

public class UnpairedBluetoothDevicesAdapter extends ArrayAdapter<UnpairedBluetoothDevices>
{
    public UnpairedBluetoothDevicesAdapter(
            @NonNull Context context,
            @NonNull ArrayList<UnpairedBluetoothDevices> unpairedDevice)
    {
        super(context, 0, unpairedDevice);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        final UnpairedBluetoothDevices unpairedDevice = getItem(position);
        LayoutInflater inflatedView = LayoutInflater.from(getContext());
        View customView = inflatedView.inflate(R.layout.bluetooth_device_entry, parent, false);

        final TextView textView = customView.findViewById(R.id.unpairedBluetoothDeviceName);
        textView.setText(String.format("%s:%s", unpairedDevice.getDeviceName(), unpairedDevice.getDeviceMacAddress()));
        textView.setTag(position);

        textView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(
                        getContext(),
                        String.format("You tapped %s at index position %d", unpairedDevice.getDeviceName(), position),
                        Toast.LENGTH_SHORT).show();
            }
        });

        return customView;
    }
}
