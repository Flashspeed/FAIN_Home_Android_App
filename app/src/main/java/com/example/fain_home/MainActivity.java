package com.example.fain_home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /* Set the XML view for this activity */
        setContentView(R.layout.activity_main);
    }

    public void onFindNewDevicesClick(View view)
    {
        Intent intentFindNewDevices = new Intent(this, ConnectADeviceActivity.class);
        startActivity(intentFindNewDevices);
    }

    public void onConnectedDevicesClick(View view)
    {
        Intent intentShowConnectedDevices = new Intent(this, ConnectedDevicesActivity.class);
        startActivity(intentShowConnectedDevices);
    }
}


