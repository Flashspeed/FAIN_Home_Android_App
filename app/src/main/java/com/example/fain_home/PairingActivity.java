package com.example.fain_home;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class PairingActivity extends AppCompatActivity
{
    BluetoothAdapter bluetoothAdapter;
    Intent           intentGoToConnectADeviceActivity;
    Intent           intentGoToConnectedDeviceActivity;
    final String TAG = "Pairing Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);

        TextView pairingDeviceName = findViewById(R.id.txtPairingDeviceName);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        intentGoToConnectADeviceActivity = new Intent(getApplicationContext(), ConnectADeviceActivity.class);
        intentGoToConnectedDeviceActivity = new Intent(getApplicationContext(), ConnectedDevicesActivity.class);

        assert getIntent().getExtras() != null;
        String          passedDeviceName       = getIntent().getExtras().getString("DEVICE_NAME");
        String          passedDeviceMacAddress = getIntent().getExtras().getString("DEVICE_MAC_ADDRESS");
        BluetoothDevice bluetoothDevice        = getIntent().getExtras().getParcelable("UNPAIRED_DEVICE");

        /* Set the device name that is being paired to the device name passed from
         * unpaired bluetooth device adapter.
         */
        pairingDeviceName.setText(
                String.format("%s : %s", passedDeviceName, passedDeviceMacAddress));

        ConnectThread acceptBluetoothConnection = new ConnectThread(bluetoothDevice);
        acceptBluetoothConnection.run();

    }

    private class ConnectThread extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;

        /* https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
         *
         * Hint: If you are connecting to a Bluetooth serial board then try using the well-known
         * SPP UUID 00001101-0000-1000-8000-00805F9B34FB. However if you are connecting to an
         * Android peer then please generate your own unique UUID. */
        private final String HC06_UUID = "00001101-0000-1000-8000-00805F9B34FB";

        public ConnectThread(BluetoothDevice bluetoothDeviceQ)
        {
            BluetoothSocket tempBluetoothSocket = null;

            this.bluetoothDevice = bluetoothDeviceQ;

            try
            {
                /* Get a Bluetooth socket to connect with the given BluetoothDevice */
                tempBluetoothSocket = bluetoothDeviceQ.createRfcommSocketToServiceRecord(UUID.fromString(HC06_UUID));
                Log.i(TAG, String.format("__Attempting to connect to %s", this.bluetoothDevice.getName()));
            }
            catch (IOException e)
            {
                Log.e(TAG, "__Socket's create() method failed", e);
            }

            this.bluetoothSocket = tempBluetoothSocket;
        }

        @Override
        public void run()
        {
            bluetoothAdapter.cancelDiscovery();
            try
            {
                this.bluetoothSocket.connect();
            }
            catch (IOException connectException)
            {
                try
                {
                    this.bluetoothSocket.close();
                }
                catch (IOException closeException)
                {
                    Log.e(TAG, "__Could not close the client socket", closeException);
                }
                Log.i(TAG, "__Connection Failed");
                Toast.makeText(
                        getApplicationContext(),
                        String.format(
                                "Failed to connect to %s",
                                this.bluetoothDevice.getName()),
                        Toast.LENGTH_LONG).show();

                startActivity(intentGoToConnectADeviceActivity);
                return;
            }

            /* If execution reaches this line it means a connection attempt succeeded */
            Log.i(TAG, "__Connection succeeded");
            startActivity(intentGoToConnectedDeviceActivity);
        }

        public void cancel()
        {
            try
            {
                this.bluetoothSocket.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "__Could not close the client socket", e);
            }
        }
    }

    private class AcceptBluetoothConnection extends Thread
    {
        private final BluetoothServerSocket bluetoothServerSocket;

        public AcceptBluetoothConnection()
        {
            BluetoothServerSocket tempBluetoothServerSocket = null;

            try
            {
                tempBluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord
                        ("MyServer", UUID.fromString("6e653a4a-7294-4f38-8ca1-446143553a1c"));
            }
            catch (IOException e)
            {
                Log.e(TAG, String.format("__Socket listen method failed. %s", e.toString()));
            }

            bluetoothServerSocket = tempBluetoothServerSocket;
        }

        @Override
        public void run()
        {
            BluetoothSocket bluetoothSocket = null;

            while (true)
            {
                try
                {
                    bluetoothSocket = bluetoothServerSocket.accept();
                }
                catch (IOException e)
                {
                    Log.e(TAG, String.format("__Socket accept method failed. %s", e.toString()));
                    break;
                }

                if (bluetoothSocket != null)
                {
                    /* A connection was accepted */
                    Log.i(TAG, "__Connection was accepted");

                    try
                    {
                        bluetoothServerSocket.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void cancel()
        {
            try
            {
                bluetoothServerSocket.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "__Could not close the connect socket", e);
            }
        }
    }
}
