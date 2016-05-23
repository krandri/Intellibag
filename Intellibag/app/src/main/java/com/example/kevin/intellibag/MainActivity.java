package com.example.kevin.intellibag;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.graphics.Color;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    ListView listFonct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listFonct = (ListView)findViewById(R.id.lstFonctionnalites);
      //  String[] fonctionnalites = new String[]{"Température", "Poids", "Podomètre", "Humidité"};
        afficherListeTweets();

/*        // Au démarrage : affichage de l'heure
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
        }*/

/*        listFonct = (ListView)findViewById(R.id.lstFonctionnalites);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, fonctionnalites);
        listFonct.setAdapter(adapter);*/

    }

    //
    //                      PARTIE CHANGEE - LISTE DES INFOS
    //

    private List<Infos> genererTweets(){
        List<Infos> infos = new ArrayList<Infos>();
        infos.add(new Infos(Color.BLACK, "Floret", "Mon premier tweet !"));
        infos.add(new Infos(Color.BLUE, "Kevin", "C'est ici que ça se passe !"));
        infos.add(new Infos(Color.GREEN, "Logan", "Que c'est beau..."));
        infos.add(new Infos(Color.RED, "Mathieu", "Il est quelle heure ??"));
        return infos;
    }


    private void afficherListeTweets(){
        List<Infos> infos = genererTweets();

        Adapter myAdapter = new Adapter(MainActivity.this, infos);
        listFonct.setAdapter(myAdapter);
    }
}
