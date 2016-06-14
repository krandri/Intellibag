package com.example.kevin.intellibag;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CapteurBDD {

    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "intellibag.db";

    private static final String TABLE_CAPTEUR = "table_capteur";
    private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;
    private static final String COL_NOM = "NOM";
    private static final int NUM_COL_NOM = 1;
    private static final String COL_DATE = "DATE";
    private static final int NUM_COL_DATE = 2;
    private static final String COL_VALEUR = "VALEUR";
    private static final int NUM_COL_VALEUR = 3;

    private SQLiteDatabase bdd;

    private MaBaseSQLite maBaseSQLite;

    public CapteurBDD(Context context){
        //On crée la BDD et sa table
        maBaseSQLite = new MaBaseSQLite(context, NOM_BDD, null, VERSION_BDD);
    }

    public void open(){
        //on ouvre la BDD en écriture
        bdd = maBaseSQLite.getWritableDatabase();
    }

    public void close(){
        //on ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }

    public long insertCapteur(Capteur capteur){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_NOM, capteur.getNom());
        values.put(COL_DATE, capteur.getDate());
        values.put(COL_VALEUR, capteur.getValeur());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_CAPTEUR, null, values);
    }

    public int updateCapteur(int id, Capteur capteur){
        //La mise à jour d'un capteur dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simplement préciser quel capteur on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_NOM, capteur.getNom());
        values.put(COL_DATE, capteur.getDate());
        values.put(COL_VALEUR, capteur.getValeur());
        return bdd.update(TABLE_CAPTEUR, values, COL_ID + " = " +id, null);
    }

    public int removeCapteurWithID(int id){
        //Suppression d'un capteur de la BDD grâce à l'ID
        return bdd.delete(TABLE_CAPTEUR, COL_ID + " = " +id, null);
    }

    //Cette méthode permet de convertir un cursor en un capteur
    private Capteur cursorToCapteur(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un capteur
        Capteur capteur = new Capteur();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        capteur.setId(c.getInt(NUM_COL_ID));
        capteur.setNom(c.getString(NUM_COL_NOM));
        capteur.setDate(c.getString(NUM_COL_DATE));
        capteur.setValeur(c.getInt(NUM_COL_VALEUR));
        //On ferme le cursor
        c.close();

        //On retourne le capteur
        return capteur;
    }
}