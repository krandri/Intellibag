package com.example.kevin.intellibag;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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

import org.w3c.dom.Text;

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

    private List<Fonction> fonctions = new ArrayList<Fonction>();

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
    private boolean connecte = false;

    private FunctionsAdapter adapter;

    private ListView mListView;
    private Button btnRefresh;
    private Button compassButton;
    private TextView textStatus;
    private Button btnStop;


    String humid = "h";
    String temperature = "t";
    String poids = "w";
    String podometre = "p";

    String humidValue = "00";
    String temperatureValue = "00";
    String poidsValue = "00";
    String podometreValue = "00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.lstFunc);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnStop = (Button) findViewById(R.id.btnStop);
        compassButton = (Button) findViewById(R.id.btnBoussole);
        textStatus = (TextView) findViewById(R.id.txtStatus);

        fonctions = genererFonctions();

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
            textStatus.setText("Statut: non connecté");
            textStatus.setTextColor(Color.parseColor("#FE0101"));
            connexionBt();
        }

        else{
            try{
                refresh();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }


        afficherListeFonctions();


        compassButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Boussole.class);
                startActivity(intent);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //Toast.makeText(MainActivity.this, "Click sur l'item numero " + position + " id " + id, Toast.LENGTH_LONG).show();
                switch(position)
                {
                    case 0:
                        Toast.makeText(MainActivity.this, "0", Toast.LENGTH_LONG).show();
                        Intent intentPoids = new Intent(MainActivity.this, PoidsActivity.class);
                        startActivity(intentPoids);
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "1", Toast.LENGTH_LONG).show();
                        Intent intentPodom = new Intent(MainActivity.this, PodomActivity.class);
                        startActivity(intentPodom);
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, "2", Toast.LENGTH_LONG).show();
                        Intent intentHumid = new Intent(MainActivity.this, HumidActivity.class);
                        startActivity(intentHumid);
                        break;
                    case 3:
                        Toast.makeText(MainActivity.this, "3", Toast.LENGTH_LONG).show();
                        Intent intentTemper = new Intent(MainActivity.this, TemperatureActivity.class);
                        startActivity(intentTemper);
                        break;

                }

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

    public void onClickRefresh(View v)
    {
        try
        {
            refresh();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean linkedToBag()
    {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if(pairedDevices.isEmpty()){
            return false;
        }
        else
        {
            for(BluetoothDevice btDevice : pairedDevices){
                if(btDevice.getAddress().equals(DEVICE_ADDRESS)){
                    mBluetoothDevice = btDevice;
                    Toast.makeText(MainActivity.this,"Vous êtes bien relié au sac",Toast.LENGTH_SHORT).show();
                    return true;
                }
            }

        }

        return false;
    }

    public void refresh() throws IOException{
        if (!connecte)
        {
            if(!mBluetoothAdapter.isEnabled())
            {
                connexionBt();
            }
            else {
                if (!linkedToBag())
                {
                    Toast.makeText(MainActivity.this,"Veuillez d'abord vous appairer au sac",Toast.LENGTH_SHORT).show();
                    textStatus.setText("Statut: Non relié au sac");
                    btnStop.setEnabled(false);
                    textStatus.setTextColor(Color.parseColor("#FEB201"));
                }
                else
                {
                    try{
                        mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(PORT_UUID);
                        mBluetoothSocket.connect();

                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }


                    try{
                        oStream = mBluetoothSocket.getOutputStream();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }

                    try {
                        iStream=mBluetoothSocket.getInputStream();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    connecte = true;
                }

            }

        }

        if(connecte) {
            textStatus.setText("Statut: connecté au sac");
            textStatus.setTextColor(Color.parseColor("#01DCFE"));
            btnStop.setEnabled(true);
            beginListening();
        }



    }

    public void beginListening(){
        Toast.makeText(MainActivity.this,"Début de la lecture des données...",Toast.LENGTH_SHORT).show();
        sendData();
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
                                    Toast.makeText(MainActivity.this,"Récupération des données",Toast.LENGTH_SHORT).show();
                                    humidValue=string.substring(1, 2);
                                    temperatureValue=string.substring(3, 4);
                                    poidsValue=string.substring(5, 6);
                                    podometreValue=string.substring(7, 9);

                                    //TOAST POUR SAVOIR SI ON RECOIT DES DONNEES SI LE REMPLISSAGE DES TXTVIEW NE SE FAIT PAS
                                    Toast.makeText(MainActivity.this,string,Toast.LENGTH_SHORT).show();

                                    //Ici operations pour remplir txtViews+ remplissage BDD
                                    for(Fonction f : fonctions){
                                        String nom = f.getCategorie();
                                        switch(nom){
                                            case "Poids":
                                                f.setValeur(poidsValue);
                                                //instructions BDD
                                                break;
                                            case "Nombre de pas effectués":
                                                f.setValeur(podometreValue);
                                                //instructions BDD
                                                break;
                                            case "Humidité ambiante":
                                                f.setValeur(humidValue);
                                                //instructions BDD
                                                break;
                                            case "Température":
                                                f.setValeur(temperatureValue);
                                                //instructions BDD
                                                break;

                                            default:
                                                break;
                                        }

                                    }

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

        try {
            oStream.write(humid.getBytes());
            //oStream.write(temperature.getBytes());
            //oStream.write(poids.getBytes());
            //oStream.write(podometre.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStopClick(View v) throws IOException{
        stopThreadCom = true;
        oStream.close();
        iStream.close();
        mBluetoothSocket.close();
    }

    private List<Fonction> genererFonctions(){
        if(!fonctions.isEmpty())fonctions.clear();

        Fonction poids = new Fonction("kilogram", "Poids", poidsValue);
        Fonction podom = new Fonction("footsteps_silhouette_variant", "Nombre de pas effectués", podometreValue);
        Fonction humid = new Fonction("drops", "Humidité ambiante", humidValue);
        Fonction temper = new Fonction("thermometer", "Température", temperatureValue);

        fonctions.add(poids);
        fonctions.add(podom);
        fonctions.add(humid);
        fonctions.add(temper);

        return fonctions;
    }

    @Override
    public void onBackPressed()
    {
        unregisterReceiver(mReceiver);
        Toast.makeText(MainActivity.this, "Fermeture de l'app", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void afficherListeFonctions(){
        //fonctions = genererFonctions();

        adapter = new FunctionsAdapter(MainActivity.this, fonctions);
        mListView.setAdapter(adapter);
    }

   public void onBtnClick(View v)
    {
        podometreValue = temperatureValue = humidValue = poidsValue = "k";
        fonctions = genererFonctions();
        for (int i = 0; i < fonctions.size(); i++)
        {
            //adapter.getItem(i).setValeur(podometreValue);
            System.out.println(fonctions.get(i).getCategorie() + " valeur = " + fonctions.get(i).getValeur());


        }
        adapter.notifyDataSetChanged();


    }

    //Permet de vérifier l'état du Bluetooth
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
                        connecte = false;
                        btnStop.setEnabled(false);
                        textStatus.setText("Statut: non connecté");
                        textStatus.setTextColor(Color.parseColor("#FE0101"));
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(MainActivity.this, "Bluetooth: déconnexion...", Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(MainActivity.this, "Bluetooth: connecté", Toast.LENGTH_LONG).show();
                        try {
                            refresh();
                        } catch (IOException e) {
                            Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(MainActivity.this, "Bluetooth: connexion...", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    };
}
