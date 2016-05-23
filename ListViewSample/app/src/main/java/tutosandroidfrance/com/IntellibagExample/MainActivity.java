package tutosandroidfrance.com.IntellibagExample;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    BluetoothAdapter mBluetoothAdapter;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listView);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());

        TextView txtDate = (TextView)findViewById(R.id.txtDate);
        txtDate.setText(formattedDate);


        //Vérifier que le bluetooth est déclenché
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(MainActivity.this, "Votre appareil ne possède pas le bluetooth.", Toast.LENGTH_LONG).show();
        }
        else
        {
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(MainActivity.this, "Votre appareil n'est pas connecté en bluetooth.", Toast.LENGTH_LONG).show();
            }
            else
            {
                //Traitement avec arduino
            }
        }
        afficherListeTweets();
    }

    private List<Infos> genererTweets(){
        List<Infos> infoses = new ArrayList<Infos>();
        infoses.add(new Infos(Color.BLACK, "Poids:", 20));
        infoses.add(new Infos(Color.BLUE, "Nombre de pas effectués:", 10));
        infoses.add(new Infos(Color.GREEN, "Humidité ambiante:", 20));
        infoses.add(new Infos(Color.GREEN, "Température:", 20));
        return infoses;
    }

    private void afficherListeTweets(){
        List<Infos> infoses = genererTweets();

        InfosAdapter adapter = new InfosAdapter(MainActivity.this, infoses);
        mListView.setAdapter(adapter);
    }
}
