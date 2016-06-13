package com.example.kevin.intellibag;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.kevin.intellibag.CompassView;

public class Boussole extends Activity {
	
	//La vue de notre boussole
	private CompassView compassView;
	
	//Le gestionnaire des capteurs
	private SensorManager sensorManager;
	//Notre capteur de la boussole num�rique
	private Sensor sensor;
	
	//Notre listener sur le capteur de la boussole num�rique
	private final SensorEventListener sensorListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			updateOrientation(event.values[SensorManager.DATA_X]);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boussole);
		Intent i = getIntent();
        compassView = (CompassView)findViewById(R.id.boussole);
        //Recuperation du gestionnaire de capteurs
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //Demander au gestionnaire de capteur de nous retourner les capteurs de type boussole
        List<Sensor> sensors =sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        //s�il y a plusieurs capteurs de ce type on garde uniquement le premier
        if (sensors.size() > 0) {
        	sensor = sensors.get(0);
        }

/*		final Button loginButton = (Button) findViewById(R.id.btnRetour);
		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});*/
    }
    
	//Mettre a jour l'orientation
    protected void updateOrientation(float rotation) {
		compassView.setNorthOrientation(rotation);
	}

	@Override
    protected void onResume(){
    	super.onResume();
    	//Lier les evenements de la boussole numerique au listener
    	sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		//Retirer le lien entre le listener et les evenements de la boussole numerique
		sensorManager.unregisterListener(sensorListener);
	}
}