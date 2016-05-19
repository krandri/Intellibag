package com.example.kevin.intellibag;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
    }
}
