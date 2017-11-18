package com.example.fain_home;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.custom_adapters.UnpairedBluetoothDevicesAdapter;
import com.example.custom_classes.UnpairedBluetoothDevices;

import java.util.ArrayList;
import java.util.Set;

public class ConnectADeviceActivity extends AppCompatActivity
{
    /*
     * https://stackoverflow.com/questions/38188887/android-6-0-bluetooth-no-code-exists-for-action-found-broadcast-intent
     */
    ListView           bluetoothDevicesListView;
    BluetoothAdapter   bluetoothAdapter;
    Intent             intentBluetoothEnable;
    SwipeRefreshLayout swipeRefreshLayout;

    final String ACTIVITY_TAG                                      = "ConnectADeviceActivity";
    final String BLUETOOTH_TAG                                     = "Bluetooth Activity";
    final int    REQUEST_CODE                                      = 1;
    final int    ASK_USER_ENABLE_BLUETOOTH_REQUEST_CODE            = 2;
    final int    ASK_USER_ENABLE_BLUETOOTH_ON_REFRESH_REQUEST_CODE = 3;

    ArrayList<String>                   arrayListPairedDevices       = new ArrayList<>();
    ArrayList<BluetoothDevice>          arrayListBluetoothDevice     = new ArrayList<>();
    ArrayList<UnpairedBluetoothDevices> arrayUnpairedBluetoothDevice = new ArrayList<>();

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

                String deviceName       = foundDevice.getName();
                String deviceMacAddress = foundDevice.getAddress();

                arrayListBluetoothDevice.add(foundDevice);

                UnpairedBluetoothDevices unpairedBluetoothDevices = new UnpairedBluetoothDevices(foundDevice);

                arrayUnpairedBluetoothDevice.add(unpairedBluetoothDevices);

                UnpairedBluetoothDevicesAdapter unpairedDevice =
                        new UnpairedBluetoothDevicesAdapter(getApplicationContext(), arrayUnpairedBluetoothDevice);

                bluetoothDevicesListView.setAdapter(unpairedDevice);
                Log.i(ACTIVITY_TAG, String.format("__(Found New)%s", deviceName));
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                swipeRefreshLayout.setRefreshing(true);
                Log.i(BLUETOOTH_TAG, String.format("__%s", "ACTION_DISCOVERY_STARTED"));

                /* Clear the array list of already found devices to stop devices from showing up
                   again when the scan is initiated again.
                 */
                arrayUnpairedBluetoothDevice.clear();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                swipeRefreshLayout.setRefreshing(false);
                Log.i(BLUETOOTH_TAG, String.format("__%s", "ACTION_DISCOVERY_FINISHED"));
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
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        swipeRefreshLayout.setColorSchemeColors(
                Color.rgb(255, 0, 0),
                Color.rgb(255, 69, 0),
                Color.argb(50, 0, 0, 255));

        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                swipeRefreshLayout.setRefreshing(false);

                /* Remove the views from the layout for a fresh start */
                bluetoothDevicesListView.removeAllViewsInLayout();

                if (!bluetoothAdapter.isEnabled())
                {
                    askUserToEnableBluetooth(ASK_USER_ENABLE_BLUETOOTH_REQUEST_CODE);
                }

                if (bluetoothAdapter.isDiscovering())
                {
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothAdapter.startDiscovery();
                    swipeRefreshLayout.setRefreshing(true);
                }
                else
                {
                    bluetoothAdapter.startDiscovery();
                    swipeRefreshLayout.setRefreshing(true);
                }
                Log.i(ACTIVITY_TAG, "__refreshed");
            }
        });

        /* Initialize the device bluetooth adapter */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        showUnpairedBluetoothDevices();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (!bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.startDiscovery();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        bluetoothAdapter.cancelDiscovery();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.cancelDiscovery();
        }

        try
        {
            unregisterReceiver(broadcastReceiver);
        }
        catch (IllegalArgumentException e)
        {
            Log.i(ACTIVITY_TAG, String.format("%s", "broadcastReceiver has already been unregistered"));
        }
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

            case ASK_USER_ENABLE_BLUETOOTH_ON_REFRESH_REQUEST_CODE:
                switch (resultCode)
                {
                    case RESULT_OK:
                        Log.i(ACTIVITY_TAG, "__SAID OK");
                        break;

                    case RESULT_CANCELED:
                        swipeRefreshLayout.setRefreshing(false);
                        if (bluetoothAdapter.isDiscovering())
                        {
                            bluetoothAdapter.cancelDiscovery();
                        }
                        break;
                }
        }

        Toast.makeText(this,
                String.format("Request code returned was %d \n Result code returned was %d", requestCode, resultCode),
                Toast.LENGTH_LONG).show();
    }

    public void showUnpairedBluetoothDevices()
    {
        /* If Bluetooth is supported */
        if (isBluetoothSupported())
        {
            Log.i(ACTIVITY_TAG, String.format("%s%s", "__", getString(R.string.txtBluetoothIsSupported)));

            /* If Bluetooth IS enabled */
            if (bluetoothAdapter.isEnabled())
            {
                Log.i(ACTIVITY_TAG, "__Bluetooth is enabled");

                showPairedBluetoothDevices();
                registerBroadcastReceiver();

                /* If device is already discovering, restart discovery process */
                if (bluetoothAdapter.isDiscovering())
                {
                    bluetoothAdapter.cancelDiscovery();

                }

                /* Start bluetooth discovery */
                bluetoothAdapter.startDiscovery();
                Log.i(ACTIVITY_TAG, "__Now Discovering");
            }
            else
            {
                Log.i(ACTIVITY_TAG, "__Bluetooth is NOT enabled");
                askUserToEnableBluetooth(ASK_USER_ENABLE_BLUETOOTH_REQUEST_CODE);
            }
        }
        else
        {
            Toast.makeText(this,
                    R.string.txtBluetoothNotSupported,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void askUserToEnableBluetooth(int requestCode)
    {
        switch (requestCode)
        {
            case ASK_USER_ENABLE_BLUETOOTH_ON_REFRESH_REQUEST_CODE:
                /* Register an intent to request the user to enable the bluetooth adapter */
                intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                /* request code REQUEST_CODE locally defined integer that must be greater than 0 */
                startActivityForResult(intentBluetoothEnable, requestCode);
                break;

            case ASK_USER_ENABLE_BLUETOOTH_REQUEST_CODE:
                /* Register an intent to request the user to enable the bluetooth adapter */
                intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                /* request code REQUEST_CODE locally defined integer that must be greater than 0 */
                startActivityForResult(intentBluetoothEnable, requestCode);

        }
    }

    public void registerBroadcastReceiver()
    {
        /* Register an intent to receive broadcasts when a bluetooth device is found */
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, intentFilter);

        /* Intent for when bluetooth scanning is started */
        IntentFilter intentFilterDiscoveryStarted = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(broadcastReceiver, intentFilterDiscoveryStarted);

        /* Intent for when bluetooth scanning is finished */
        IntentFilter intentFilterDiscoveryFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilterDiscoveryFinished);

        Log.i(ACTIVITY_TAG, "__broadcastReceiver is now registered");
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
                Log.i(ACTIVITY_TAG, String.format("%s%s", "__(known)", currentPairedDevice.getName()));
            }
        }
    }

    /**
     * Returns true if the device supports Bluetooth
     *
     * @return true if Bluetooth is supported on the device.
     */
    private boolean isBluetoothSupported()
    {
        /* Get the device bluetooth radio A.K.A Adapter */
        return bluetoothAdapter != null;
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

}
