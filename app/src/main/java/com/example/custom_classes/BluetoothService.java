package com.example.custom_classes;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.fain_home.ConnectedDevicesActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothService
{
    private final String TAG       = "BluetoothService";
    /* https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
     *
     * Hint: If you are connecting to a Bluetooth serial board then try using the well-known
     * SPP UUID 00001101-0000-1000-8000-00805F9B34FB. However if you are connecting to an
     * Android peer then please generate your own unique UUID. */
    private final String HC06_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    BluetoothAdapter bluetoothAdapter;
    Context context;

    AcceptThread acceptThread;
    UUID deviceUUID;
    private ConnectThread   connectThread;
    private ConnectedThread connectedThread;
    private BluetoothDevice bluetoothDevice;

    public BluetoothService(Context context, BluetoothDevice bluetoothDevice)
    {
        this.context = context;
        this.bluetoothDevice = bluetoothDevice;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }

    public void start()
    {
        if (connectThread != null)
        {
            connectThread.cancel();
            connectThread = null;
        }
        if (acceptThread == null)
        {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    public void startClient(BluetoothDevice device, UUID uuid)
    {
        connectThread = new ConnectThread(device, uuid);
        connectThread.start();
    }

    private void connected(BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice)
    {
        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();
    }

    public void write(byte[] out)
    {
        connectedThread.write(out);
    }

    private class AcceptThread extends Thread
    {
        private final BluetoothServerSocket bluetoothServerSocket;

        public AcceptThread()
        {
            BluetoothServerSocket tempBluetoothServerSocket = null;
            try
            {
                tempBluetoothServerSocket =
                        bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
                                "FAIN",
                                UUID.fromString(HC06_UUID));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            this.bluetoothServerSocket = tempBluetoothServerSocket;
        }

        @Override
        public void run()
        {
            BluetoothSocket bluetoothSocket = null;

            try
            {
                Log.i(TAG, "__Socket start");
                bluetoothSocket = bluetoothServerSocket.accept();
                Log.i(TAG, "__Socket connection accept Successful");
            }
            catch (IOException e)
            {
                Log.i(TAG, "__Socket connection Failed");
                e.printStackTrace();
            }

            if (bluetoothSocket != null)
            {
                connected(bluetoothSocket, bluetoothDevice);
            }
        }

        public void cancel()
        {
            try
            {
                bluetoothServerSocket.close();
                Log.i(TAG, "__Socket Closed");
            }
            catch (IOException e)
            {
                Log.i(TAG, "__Socket Could not be closed");
                e.printStackTrace();
            }
        }
    }

    private class ConnectThread extends Thread
    {
        private BluetoothSocket bluetoothSocket;

        public ConnectThread(BluetoothDevice innerBluetoothDevice, UUID uuid)
        {
            bluetoothDevice = innerBluetoothDevice;
            deviceUUID = uuid;
        }

        @Override
        public void run()
        {
            BluetoothSocket tempBluetoothSocket = null;

            try
            {
                tempBluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(deviceUUID);
            }
            catch (IOException e)
            {
                Log.i(TAG, "__Socket creation failed");
                e.printStackTrace();
            }

            bluetoothSocket = tempBluetoothSocket;

            bluetoothAdapter.cancelDiscovery();

            try
            {
                bluetoothSocket.connect();
                Log.i(TAG, "__Socket Connection successful");

                Intent intentGoToConnectedDeviceActivity = new Intent(context, ConnectedDevicesActivity.class);
                context.startActivity(intentGoToConnectedDeviceActivity);

                /* https://stackoverflow.com/questions/10559904/closing-an-activity-from-another-class */
                ((Activity) context).finish();
            }
            catch (IOException e)
            {
                try
                {
                    bluetoothSocket.close();
                    Log.i(TAG, "__Socket Closed");
                }
                catch (IOException e1)
                {
                    Log.i(TAG, "__Socket Connection Failed");
                    e1.printStackTrace();
                }
            }

            connected(bluetoothSocket, bluetoothDevice);
        }

        public void cancel()
        {
            try
            {
                bluetoothSocket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public class ConnectedThread extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream     inputStream;
        private final OutputStream    outputStream;

        public ConnectedThread(BluetoothSocket bluetoothSocket)
        {
            this.bluetoothSocket = bluetoothSocket;
            InputStream  tempInputStream  = null;
            OutputStream tempOutputStream = null;

            try
            {
                tempInputStream = this.bluetoothSocket.getInputStream();
                tempOutputStream = this.bluetoothSocket.getOutputStream();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            this.inputStream = tempInputStream;
            this.outputStream = tempOutputStream;
        }

        public void run()
        {
            byte[] buffer = new byte[1024];
            int    bytes;

            while (true)
            {
                try
                {
                    bytes = this.inputStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.i(TAG, String.format("__InputStream: %s", incomingMessage));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(byte[] bytes)
        {
            String writeString = new String(bytes, Charset.defaultCharset());
            Log.i(TAG, String.format("__Writing %s to OutputStream", writeString));

            try
            {
                outputStream.write(bytes);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        public void cancel()
        {
            try
            {
                bluetoothSocket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
