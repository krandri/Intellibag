package com.example.kevin.sacconnecte;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.ImageButton;
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
    private final String DEVICE_ADDRESS="00:14:02:26:01:91";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Button btnConnexion, sendButton,stopButton, selectButton;
    private ImageButton compassButton, upButton, downButton, leftButton, rightButton;
    private ListView mListView;

    boolean deviceConnected=false;
    Thread thread;
    byte buffer[];
    int bufferPosition;
    boolean stopThread;

    private String valPoids = "00";
    private String valPodom = "00";
    private String valTempe = "00";
    private String valHumid = "00";


    private List<Fonction> fonctions = new ArrayList<Fonction>();
    private FunctionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConnexion = (Button) findViewById(R.id.btnConnexion);
        sendButton = (Button) findViewById(R.id.btnRefresh);
        stopButton = (Button) findViewById(R.id.btnDeconnexion);
        selectButton = (Button) findViewById(R.id.btnOk);

        compassButton = (ImageButton) findViewById(R.id.btnCompass);
        upButton = (ImageButton) findViewById(R.id.btnUp);
        downButton = (ImageButton) findViewById(R.id.btnDown);
        leftButton = (ImageButton) findViewById(R.id.btnLeft);
        rightButton = (ImageButton) findViewById(R.id.btnRight);

        mListView = (ListView) findViewById(R.id.listView);

        setUiEnabled(false);

        //Affichage de la date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());

        TextView txtDate = (TextView)findViewById(R.id.textView2);
        txtDate.setText(formattedDate);

        //Affichage de la liste
        afficherListeFonctions();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intentGraph = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(intentGraph);
            }
        });
    }

    public void onCompassClick(View v){
        Intent intent = new Intent(MainActivity.this, Boussole.class);
        startActivity(intent);
    }

    private List<Fonction> genererFonctions(){
        //if(!fonctions.isEmpty())fonctions.clear();
        Fonction poids = new Fonction("kilogram", "Poids", valPoids);
        Fonction podom = new Fonction("footsteps_silhouette_variant", "Nombre de pas effectués", valPodom);
        Fonction humid = new Fonction("drops", "Humidité ambiante", valHumid);
        Fonction temper = new Fonction("thermometer", "Température", valTempe);

        fonctions.add(poids);
        fonctions.add(podom);
        fonctions.add(humid);
        fonctions.add(temper);

        return fonctions;
    }

    private void afficherListeFonctions(){
        fonctions = genererFonctions();

        adapter = new FunctionsAdapter(MainActivity.this, fonctions);
        mListView.setAdapter(adapter);
    }


    public void setUiEnabled(boolean bool)
    {
        btnConnexion.setEnabled(!bool);
        sendButton.setEnabled(bool);
        stopButton.setEnabled(bool);
        upButton.setClickable(bool);
        downButton.setClickable(bool);
        leftButton.setClickable(bool);
        rightButton.setClickable(bool);
        selectButton.setClickable(bool);

    }

    public boolean linkedToBag()
    {
        boolean found=false;
        BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Votre appareil ne supporte pas le Bluetooth",Toast.LENGTH_SHORT).show();
        }
        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        if(bondedDevices.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Appairez vous tout d'abord avec le sac.",Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (BluetoothDevice btDevice : bondedDevices)
            {
                if(btDevice.getAddress().equals(DEVICE_ADDRESS))
                {
                    device=btDevice;
                    found=true;
                    break;
                }
            }
        }
        return found;
    }

    public boolean createConnection()
    {
        boolean connected=true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected=false;
        }
        if(connected)
        {
            try {
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return connected;
    }

    public void onClickStart(View view) {
        if(linkedToBag())
        {
            if(createConnection())
            {
                setUiEnabled(true);
                deviceConnected=true;
                beginListening();
                Toast.makeText(MainActivity.this, "Connexion ouverte", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void sendTrames(){
        try {
            outputStream.write("h".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(MainActivity.this, "Données envoyées, en attente de reception...", Toast.LENGTH_SHORT).show();
    }


    public void onClickSend(View v) throws IOException{
        sendTrames();
    }

    public void onClickStop(View view) throws IOException {
        stopThread = true;
        outputStream.close();
        inputStream.close();
        socket.close();
        setUiEnabled(false);
        deviceConnected=false;
        Toast.makeText(MainActivity.this, "Connexion au sac coupée", Toast.LENGTH_SHORT).show();
    }

    public void refresh(){
        fonctions.clear();
        fonctions = genererFonctions();
        adapter.notifyDataSetChanged();
    }

    void beginListening()
    {
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        Thread thread  = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopThread)
                {
                    try
                    {
                        int byteCount= inputStream.available();
                        if(byteCount > 0)
                        {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);
                            final String string=new String(rawBytes,"UTF-8");

                            String [] tab;

                            System.out.println("long chaine: " + string.length());
                            System.out.println("valeur: "+ string);
                            final String str = string.replaceAll("[a-z]","");
                            tab = str.split(";");
                            valTempe=tab[0]+"°C";
                            valHumid=tab[1]+"%";


                            handler.post(new Runnable() {
                                //opération sur l'interface
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Donneés actualisées", Toast.LENGTH_SHORT).show();
                                    refresh();
                                }
                            });

                        }
                    }
                    catch (IOException ex)
                    {
                        stopThread = true;
                    }
                    catch(ArrayIndexOutOfBoundsException aex)
                    {
                        System.out.println("Et non!");
                    }
                }
            }
        });

        thread.start();
    }

    public void onClickLeft(View v) {
        try {
            outputStream.write("g".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickRight(View v) {
        try {
            outputStream.write("d".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onClickUp(View v) {
        try {
            outputStream.write("o".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            }
    }


    public void onClickDown(View v) {
        try {
            outputStream.write("b".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickOk(View v) {
        try {
            outputStream.write("s".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
