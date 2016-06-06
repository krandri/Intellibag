package com.example.kevin.intellibag;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final String DEVICE_ADDRESS="00:14:02:26:01:91"; //Adresse MAC de l'Arduino
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;

    private OutputStream oStream;
    private InputStream iStream;
    private Thread threadCom;
    private boolean stopThreadCom;
    private byte buffer[];

    private ListView mListView;
    private Button btnRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.lstFunc);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());

        TextView txtDate = (TextView)findViewById(R.id.txtDate);
        txtDate.setText(formattedDate);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        //Vérifier que le bluetooth est déclenché
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(MainActivity.this, "Votre appareil ne possède pas le bluetooth.", Toast.LENGTH_LONG).show();
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            connexionBt();
        }

        afficherListeFonctions();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
              Toast.makeText(MainActivity.this, "Click sur l'item numero", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void connexionBt(){
        if(!mBluetoothAdapter.isEnabled()){
            Toast.makeText(MainActivity.this,"Vous n'êtes pas connecté en Bluetooth, tentative de connexion ...",Toast.LENGTH_SHORT).show();
            Intent intentBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentBluetooth, 1);
        }
    }

    public void refresh(View v) throws IOException{
        if(!mBluetoothAdapter.isEnabled())
        {
            connexionBt();
        }

        else {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.isEmpty()){
                Toast.makeText(MainActivity.this,"Veuillez d'abord vous appairer au sac",Toast.LENGTH_SHORT).show();
            }
            else
            {
                for(BluetoothDevice btDevice : pairedDevices){
                    if(btDevice.getAddress().equals(DEVICE_ADDRESS)){
                        mBluetoothDevice = btDevice;
                        Toast.makeText(MainActivity.this,"Vous êtes bien relié au sac",Toast.LENGTH_SHORT).show();
                    }
                }

            }

            /*boolean connected = true;
            try{
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(PORT_UUID);
                mBluetoothSocket.connect();

            }
            catch (IOException e) {
                e.printStackTrace();
            }

            if(connected){
                try{
                    oStream = mBluetoothSocket.getOutputStream();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }*/

        }



    }

   /* public void BeginListening(){
        final Handler handler = new Handler();
        stopThreadCom = false;
        buffer = new byte[1024];
        Thread thread  = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopThreadCom)
                {
                    try
                    {
                        int byteCount = iStream.available();
                        if(byteCount > 0)
                        {
                            byte[] rawBytes = new byte[byteCount];
                            iStream.read(rawBytes);
                            final String string=new String(rawBytes,"UTF-8");
                            handler.post(new Runnable() {
                                public void run()
                                {
                                    //Ici operations pour remplir txtViews
                                }
                            });

                        }
                    }
                    catch (IOException ex)
                    {
                        stopThreadCom = true;
                    }
                }
            }
        });

        thread.start();
    }

    public void sendData() {

    }*/

    private List<Fonction> genererFonctions(){
        List<Fonction> fonctions = new ArrayList<Fonction>();
        fonctions.add(new Fonction("kilogram", "Poids:", 20));
        fonctions.add(new Fonction("footsteps_silhouette_variant", "Nombre de pas effectués:", 10));
        fonctions.add(new Fonction("drops", "Humidité ambiante:", 20));
        fonctions.add(new Fonction("thermometer", "Température:", 20));
        return fonctions;
    }

    private void afficherListeFonctions(){
        List<Fonction> fonctions = genererFonctions();

        FunctionsAdapter adapter = new FunctionsAdapter(MainActivity.this, fonctions);
        mListView.setAdapter(adapter);
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
                        Toast.makeText(MainActivity.this, "Bluetooth: non connecté", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(MainActivity.this, "Bluetooth: déconnexion...", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(MainActivity.this, "Bluetooth: connecté", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(MainActivity.this, "Bluetooth: connexion...", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    };
}
