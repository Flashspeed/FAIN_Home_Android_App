package com.example.fain_home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ConnectADeviceActivity extends AppCompatActivity
{
    ListView bluetoothDevicesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle("Connect a Device");
        /* Set the XML view for this activity */
        setContentView(R.layout.activity_connect_a_device);

        /* Initialize a list view to show the cheeses in vertically */
        bluetoothDevicesListView = findViewById(R.id.listView);
        showAvailableBluetoothDevices();
    }

    public void showAvailableBluetoothDevices()
    {
        /*
            https://developer.android.com/guide/topics/ui/declaring-layout.html#AdapterViews (LIST VIEW)
         */
        String[] cheeses = {
                "Parmesan",
                "Ricotta",
                "Fontina",
                "Mozzarella",
                "Fontina",
                "Mozzarella",
                "Fontina",
                "Mozzarella",
                "Fontina",
                "Mozzarella",
                "Fontina",
                "Mozzarella",
                "Cheddar"
        };

        /*
            Arguments: app context, name of the resource file, the identifier of the TextView, and a reference to the array
         */
        ArrayAdapter<String> cheeseAdapter =
                new ArrayAdapter<String>(this, R.layout.bluetooth_device_entry, R.id.cheese_names, cheeses);

        /* Attach the array adapter to the list view */
        bluetoothDevicesListView.setAdapter(cheeseAdapter);

        /* Apply an item click listener to the list view items */
        bluetoothDevicesListView.setOnItemClickListener(new bluetoothItemClickListener());
    }

    public class bluetoothItemClickListener implements AdapterView.OnItemClickListener
    {
        /* When the user taps an item in the list view */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Log.i("ConnectADeviceActivity", String.format("Item %d clicked. It's text is %s", position, bluetoothDevicesListView.getItemAtPosition(position)));
            Toast.makeText(
                    ConnectADeviceActivity.this,
                    String.format("You tapped %s", bluetoothDevicesListView.getItemAtPosition(position)),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
