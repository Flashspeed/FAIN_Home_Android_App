package com.example.fain_home;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class PairingActivity extends AppCompatActivity
{
    BluetoothAdapter bluetoothAdapter;
    Intent           intentGoToConnectADeviceActivity;
    Intent           intentGoToConnectedDeviceActivity;
    /* https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
         *
         * Hint: If you are connecting to a Bluetooth serial board then try using the well-known
         * SPP UUID 00001101-0000-1000-8000-00805F9B34FB. However if you are connecting to an
         * Android peer then please generate your own unique UUID. */
    private final String HC06_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private Handler messageHandler;

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
        acceptBluetoothConnection.start();

    }

    public class ConnectThread extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;

        public ConnectThread(BluetoothDevice bluetoothDeviceQ)
        {
            BluetoothSocket tempBluetoothSocket = null;

            this.bluetoothDevice = bluetoothDeviceQ;

            try
            {
                /* Get a Bluetooth socket to connect with the given BluetoothDevice */
                tempBluetoothSocket = this.bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(HC06_UUID));
                Log.i(TAG, String.format("__Attempting to connect to %s", this.bluetoothDevice.getName()));
            }
            catch (IOException e)
            {
                Log.e(TAG, "__Socket's create() method failed", e);
                e.printStackTrace();
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
                connectException.printStackTrace();
                startActivity(intentGoToConnectADeviceActivity);
                return;
            }

            /* If execution reaches this line it means a connection attempt succeeded */
            Log.i(TAG, "__Connection succeeded");

            /* Send the user to the Connected Device Activity Screen when the connection succeeds */
            startActivity(intentGoToConnectedDeviceActivity);

            /* Destroy this activity so this PairingActivity won't show if the user attempts to go
             * back to the previous Activity which is this current activity.
             */
            finish();
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

    public class ConnectedThread extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream     inputStream;
        private final OutputStream    outputStream;
        private final int MESSAGE_READ = 0;
        private final int MESSAGE_WRITE = 1;
        private final int MESSAGE_TOAST = 2;
        private final int SUCCESS_CONNECT = 0;
        private byte[] dataBuffer;


        public ConnectedThread(BluetoothSocket passedBluetoothSocket)
        {

            this.bluetoothSocket = passedBluetoothSocket;
            InputStream tempInputStream = null;
            OutputStream tempOutputStream = null;

            try
            {
                this.bluetoothSocket.connect();
                Log.i(TAG, "__Socket Connection Succeeded");
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.i(TAG, "__Socket Connection Failed");
            }

            try
            {
                tempInputStream = passedBluetoothSocket.getInputStream();
            }
            catch (IOException e)
            {
                Log.e(TAG, "__Error opening InputStream");
                e.printStackTrace();
            }

            try
            {
                tempOutputStream = passedBluetoothSocket.getOutputStream();
            }
            catch (IOException e)
            {
                Log.e(TAG, "__Error opening OutputStream");
                e.printStackTrace();
            }

            inputStream = tempInputStream;
            outputStream = tempOutputStream;
        }

        @Override
        public void run()
        {
            dataBuffer = new byte[1024];

            /* Bytes returned from read(); */
            int numOfBytes;

            /* Keep listening to the InputStream until an exception occurs */
            while (true)
            {
                try
                {
                    /* Read from the InputStream */
                    numOfBytes = this.inputStream.read(dataBuffer);

                    Message readMessage = messageHandler.obtainMessage(MESSAGE_READ, numOfBytes, -1, dataBuffer);
                    readMessage.sendToTarget();

                    /* Send any read bytes to the app activity */
                    Log.i(TAG, String.format("__Message %s", numOfBytes));

                }
                catch (IOException e)
                {
                    Log.e(TAG, "__Input stream was disconnected", e);
                    break;
                }

//                Log.i(TAG, String.format("__Message %s", "IFE"));
            }
        }

        public void write(byte[] bytes)
        {
            try
            {
                this.outputStream.write(bytes);

                Message writtenMessage = messageHandler.obtainMessage(MESSAGE_WRITE, -1, -1, dataBuffer);
                writtenMessage.sendToTarget();
            }
            catch (IOException e)
            {
                Log.e(TAG, "__Error occurred while trying to send data", e);
                e.printStackTrace();

                /* Send a failure message back to the activity */
//                Message errorMessage = messageHandler.obtainMessage(MESSAGE_TOAST);
//                Bundle bundle = new Bundle();
//                bundle.putString("toast", "Data could not be sent to bluetooth device");
//                errorMessage.setData(bundle);
//                messageHandler.sendMessage(errorMessage);
            }
        }

        public void cancel()
        {
            try
            {
                this.bluetoothSocket.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "__Could not close the connect socket", e);
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
                        ("MyServer", UUID.fromString(HC06_UUID));
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
