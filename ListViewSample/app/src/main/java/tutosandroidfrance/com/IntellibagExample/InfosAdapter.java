package tutosandroidfrance.com.IntellibagExample;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class InfosAdapter extends ArrayAdapter<Infos> {

    public InfosAdapter(Context context, List<Infos> infoses) {
        super(context, 0, infoses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_infos,parent, false);
        }

        TweetViewHolder viewHolder = (TweetViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new TweetViewHolder();
            viewHolder.categorie = (TextView) convertView.findViewById(R.id.categorie);
            viewHolder.valeur = (TextView) convertView.findViewById(R.id.valeur);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Infos> tweets
        Infos infos = getItem(position);
        viewHolder.categorie.setText(infos.getCategorie());
        viewHolder.valeur.setText(infos.getValeur());
        viewHolder.avatar.setImageDrawable(new ColorDrawable(infos.getColor()));

        return convertView;
    }

    private class TweetViewHolder{
        public TextView categorie;
        public TextView valeur;
        public ImageView avatar;

    }
}
