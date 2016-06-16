package com.example.kevin.intellibag;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class MaBaseSQLite extends SQLiteOpenHelper {

    private static final String TABLE_CAPTEUR = "table_capteur";
    private static final String COL_ID = "ID";
    private static final String COL_NOM = "NOM";
    private static final String COL_DATE = "DATE";
    private static final String COL_VALEUR = "VALEUR";

    private static final String CREATE_BDD = "CREATE TABLE " + TABLE_CAPTEUR + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NOM + " TEXT NOT NULL, "
            + COL_DATE + " DATE NOT NULL), " + COL_VALEUR + "INTEGER NOT NULL;";

    public MaBaseSQLite(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //on crée la table à partir de la requête écrite dans la variable CREATE_BDD
        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On peut faire ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0
        db.execSQL("DROP TABLE " + TABLE_CAPTEUR + ";");
        onCreate(db);
    }

}