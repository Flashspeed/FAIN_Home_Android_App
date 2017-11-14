package com.example.fain_home;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Set;

public class ConnectADeviceActivity extends AppCompatActivity
{
    ListView bluetoothDevicesListView;
    BluetoothAdapter bluetoothAdapter;
    Intent intentBluetoothEnable;
    final String TAG = "ConnectADeviceActivity";
    final int REQUEST_CODE = 1;
    final int ASK_USER_ENABLE_BLUETOOTH_REQUEST_CODE = 2;
    ArrayList<String> arrayListUnpairedDevices = new ArrayList<>();
    ArrayList<String> arrayListPairedDevices = new ArrayList<>();
    ArrayAdapter<String> arrayAdapterDeviceName;

    /* Create broadcast receiver for when a bluetooth device is found */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                    /* Discovery process has found a device. Now get the BluetoothDevice object
                       and its info from the Intent.
                       getParcelableExtra() -> Retrieve extended data from the intent.
                     */
                BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String deviceName = foundDevice.getName();
                String deviceMacAddress = foundDevice.getAddress();

                arrayListUnpairedDevices.add(String.format("%s : %s", deviceName, deviceMacAddress));
                arrayAdapterDeviceName =
                        new ArrayAdapter<String>(getApplicationContext(),
                                R.layout.bluetooth_device_entry,
                                R.id.unpairedBluetoothDeviceName,
                                arrayListUnpairedDevices);

                bluetoothDevicesListView.setAdapter(arrayAdapterDeviceName);
                Log.i(TAG, String.format("__(Found New)%s", deviceName));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle("Connect a Device");

        /* Set the XML view for this activity */
        setContentView(R.layout.activity_connect_a_device);

        /* Initialize a list view to show the cheeses in vertically */
        bluetoothDevicesListView = findViewById(R.id.listView);

        /* Initialize the device bluetooth adapter */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        showUnpairedBluetoothDevices();
    }

    public void showUnpairedBluetoothDevices()
    {
        /* If Bluetooth is supported */
        if (isBluetoothSupported())
        {
            Log.i(TAG, String.format("%s%s", "__", getString(R.string.txtBluetoothIsSupported)));

            /* If Bluetooth IS enabled */
            if (bluetoothAdapter.isEnabled())
            {
                Log.i(TAG, "__Bluetooth is enabled");

                showPairedBluetoothDevices();
                registerBroadcastReceiver();

                /* If device is already discovering, restart discovery process */
                if (bluetoothAdapter.isDiscovering())
                {
                    bluetoothAdapter.cancelDiscovery();

                }

                /* Start bluetooth discovery */
                bluetoothAdapter.startDiscovery();
                Log.i(TAG, "__Now Discovering");
            }
            else
            {
                Log.i(TAG, "__Bluetooth is NOT enabled");
                askUserToEnableBluetooth();
            }
        }
        else
        {
            Toast.makeText(this,
                    R.string.txtBluetoothNotSupported,
                    Toast.LENGTH_LONG).show();
        }

        bluetoothDevicesListView.setOnItemClickListener(new bluetoothItemClickListener());
    }

    public void askUserToEnableBluetooth()
    {
        /* Register an intent to request the user to enable the bluetooth adapter */
        intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        /* request code REQUEST_CODE locally defined integer that must be greater than 0 */
        startActivityForResult(intentBluetoothEnable, ASK_USER_ENABLE_BLUETOOTH_REQUEST_CODE);
    }

    public void registerBroadcastReceiver()
    {
        /* Register an intent to receive broadcasts when a bluetooth device is found */
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, intentFilter);

        Log.i(TAG, "__broadcastReceiver is now registered");
    }

    /**
     * List the names of paired devices to LogCat
     */
    public void showPairedBluetoothDevices()
    {
        /* Get a list of known paired devices */
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice currentPairedDevice : pairedDevices)
            {
                arrayListPairedDevices.add(currentPairedDevice.getName());
                Log.i(TAG, String.format("%s%s", "__(known)", currentPairedDevice.getName()));
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        try
        {
            unregisterReceiver(broadcastReceiver);
        }
        catch (IllegalArgumentException e)
        {
            Log.i(TAG, String.format("%s", "broadcastReceiver has already been unregistered"));
        }
    }

    /**
     * Returns true if the device supports Bluetooth
     * @return true if Bluetooth is supported on the device.
     */
    private boolean isBluetoothSupported()
    {
        /* Get the device bluetooth radio A.K.A Adapter */
        return bluetoothAdapter != null;
    }

    /*
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it. The resultCode will be RESULT_CANCELED if
     * the activity explicitly returned that, didn't return
     * any result, or crashed during its operation.
     * https://developer.android.com/reference/android/app/Activity.html#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case ASK_USER_ENABLE_BLUETOOTH_REQUEST_CODE:
                switch (resultCode)
                {
                    case RESULT_OK:
                        registerBroadcastReceiver();
                        showUnpairedBluetoothDevices();
                        break;

                    case RESULT_CANCELED:
                        break;
                }
        }

        Toast.makeText(this,
                String.format("Request code returned was %d \n Result code returned was %d", requestCode, resultCode),
                Toast.LENGTH_LONG).show();
    }

    /* Called when the user makes a choice on a permissions dialog */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
//    {
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//        {
//
//        }
//    }

    public class bluetoothItemClickListener implements AdapterView.OnItemClickListener
    {
        /* When the user taps an item in the list view */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Log.i("ConnectADeviceActivity", String.format("Item %d clicked. It's text is %s", position, bluetoothDevicesListView.getItemAtPosition(position)));
            Toast.makeText(
                    ConnectADeviceActivity.this,
                    String.format("You tapped %s at index position %d", bluetoothDevicesListView.getItemAtPosition(position), position),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
