package com.example.custom_adapters;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.custom_classes.UnpairedBluetoothDevices;
import com.example.fain_home.ConnectADeviceActivity;
import com.example.fain_home.MainActivity;
import com.example.fain_home.PairingActivity;
import com.example.fain_home.R;

import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;

public class UnpairedBluetoothDevicesAdapter extends ArrayAdapter<UnpairedBluetoothDevices>
{
    public UnpairedBluetoothDevicesAdapter(
            @NonNull Context context,
            @NonNull ArrayList<UnpairedBluetoothDevices> unpairedDevices)
    {
        super(context, 0, unpairedDevices);
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

                /*https://stackoverflow.com/questions/13194081/how-to-open-a-second-activity-on-click-of-button-in-android-app*/
                Intent intentGoToPairingActivity = new Intent(getContext(), PairingActivity.class);

                /*https://stackoverflow.com/questions/5265913/how-to-use-putextra-and-getextra-for-string-data*/
                /* Here I use putExtra() to send the device name and mac address to the chosen activity */
                intentGoToPairingActivity.putExtra("DEVICE_NAME", unpairedDevice.getDeviceName());
                intentGoToPairingActivity.putExtra("DEVICE_MAC_ADDRESS", unpairedDevice.getDeviceMacAddress());
                intentGoToPairingActivity.putExtra("UNPAIRED_DEVICE", unpairedDevice.getBluetoothDevice());

                startActivity(getContext(), intentGoToPairingActivity, null);

            }
        });

        return customView;
    }
}
