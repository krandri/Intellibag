package com.example.kevin.bluetoothsimpletest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Button boutonOn;
    Button boutonCo;
    TextView txtBluetooth;
    TextView txtCo;
    TextView txtDevices;

    BluetoothAdapter mBluetooth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boutonOn = (Button) findViewById(R.id.button1);
        boutonCo = (Button) findViewById(R.id.button2);

        txtBluetooth = (TextView) findViewById(R.id.textView_blue);
        txtCo = (TextView) findViewById(R.id.textView_co);
        txtDevices = (TextView) findViewById(R.id.textView_devices);
        mBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(!mBluetooth.isEnabled())
        {
            txtBluetooth.setText("Bluetooth: non connecté");
        }
        else txtBluetooth.setText("Bluetooth: connecté");

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    public void onClickOn(View v){
        if(mBluetooth == null)
        {
            Toast.makeText(getApplicationContext(),"Votre appareil ne supporte pas le bluetooth",Toast.LENGTH_SHORT).show();
        }

        if(!mBluetooth.isEnabled())
        {
            Intent intentBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentBluetooth, 1);
        }

        else Toast.makeText(getApplicationContext(),"Votre appareil est déjà connecté en bluetooth",Toast.LENGTH_SHORT).show();
    }

    public void onClickConnect(View v){
        txtDevices.setText("Appareils appairés: ");
        if(mBluetooth.isEnabled())
        {
            Set<BluetoothDevice> pairedDevices = mBluetooth.getBondedDevices();
            if(pairedDevices.size() > 0){
                String devices = (String) txtDevices.getText();
                for (BluetoothDevice device : pairedDevices){
                    devices += "\n" + device.getName() + ": " + device.getAddress();
                }
                txtDevices.setText(devices);


            }
            else txtDevices.setText("Aucun appareil appairé.");
    }
        else Toast.makeText(getApplicationContext(),"Connectez vous d'abord en bluetooth",Toast.LENGTH_SHORT).show();

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        txtBluetooth.setText("Bluetooth: non connecté");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        txtBluetooth.setText("Bluetooth: deconnexion...");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        txtBluetooth.setText("Bluetooth: connecté");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        txtBluetooth.setText("Bluetooth: connexion ...");
                        break;
                }
            }
        }
    };
}
