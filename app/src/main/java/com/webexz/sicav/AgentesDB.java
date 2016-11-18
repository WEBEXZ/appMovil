package com.webexz.sicav;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AgentesDB extends SQLiteOpenHelper {

    String[] insertAgentes = new String[7];

    public String[] datosAgentes(String[] data){
        data[0] = "CREATE TABLE Agentes(id INTEGER, nombre TEXT, numeroControl TEXT, password TEXT)";
        data[1] = "INSERT INTO Agentes VALUES(1, 'ALEJANDRO MARTÍNEZ SÁNCHEZ', 'P-185', '123456')";
        data[2] = "INSERT INTO Agentes VALUES(2, 'LUIS ROMERO DELGADO', 'P-518', '123456')";
        data[3] = "INSERT INTO Agentes VALUES(3, 'JOSÉ REYES ARAGÓN', 'P-612', '123456')";
        data[4] = "INSERT INTO Agentes VALUES(4, 'MARÍO ALBERTO LEAL SALINAS', 'P-352', '123456')";
        data[5] = "INSERT INTO Agentes VALUES(5, 'ÁNGEL SANTIAGO JIMENEZ', 'P-875', '123456')";
        data[6] = "CREATE TABLE Zonas(id INTEGER, lat TEXT, lng TEXT)";
        return data;
    }

    public AgentesDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String[] array = datosAgentes(insertAgentes);
        for(int i = 0; i < array.length; i++ ) {
            db.execSQL(array[i]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Agentes");
        db.execSQL("DROP TABLE IF EXISTS Zonas");
        String[] array = datosAgentes(insertAgentes);
        for(int i = 0; i < array.length; i++ ) {
            db.execSQL(array[i]);
        }
    }
}
