package com.example.kevin.sacconnecte;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


/*
    Cette classe permet de personnaliser la listView afin d'afficher plusieurs informations dans les éléments de liste

 */


public class FunctionsAdapter extends ArrayAdapter<Fonction> {

    public FunctionsAdapter(Context context, List<Fonction> fonctions) {
        super(context, 0, fonctions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_infos,parent, false);
        }

        FunctionViewHolder viewHolder = (FunctionViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new FunctionViewHolder();
            viewHolder.categorie = (TextView) convertView.findViewById(R.id.categorie);
            viewHolder.valeur = (TextView) convertView.findViewById(R.id.valeur);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Fonction> fonctions
        Fonction func = getItem(position);
        viewHolder.categorie.setText(func.getCategorie());
        viewHolder.valeur.setText(func.getValeur());

        String img = func.getImg();
        if(img != null)
        {
            int resId = getContext().getResources().getIdentifier(img, "drawable", getContext().getPackageName());
            viewHolder.avatar.setImageResource(resId);
        }
        return convertView;
    }



    private class FunctionViewHolder{
        public TextView categorie;
        public TextView valeur;
        public ImageView avatar;

    }


}
