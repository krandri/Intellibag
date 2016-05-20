package com.example.kevin.intellibag;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    ListView listFonct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] fonctionnalites = new String[]{"Température", "Poids", "Podomètre", "Humidité"};

        // Au démarrage : affichage de l'heure
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());

        TextView txtDate = (TextView)findViewById(R.id.textDate);
        txtDate.setText(formattedDate);

        //Vérifier que le bluetooth est déclenché
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(MainActivity.this, "Votre appareil ne possède pas le bluetooth.", Toast.LENGTH_LONG).show();
        }
        else
        {
            if (!mBluetoothAdapter.isEnabled())
            {
                Toast.makeText(MainActivity.this, "Votre appareil n'est pas connecté en bluetooth.", Toast.LENGTH_LONG).show();
            }
            else
            {
                //Traitement avec arduino
            }
        }

        listFonct = (ListView)findViewById(R.id.lstFonctionnalites);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, fonctionnalites);
        listFonct.setAdapter(adapter);

    }
}
