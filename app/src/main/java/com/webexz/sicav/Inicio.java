package com.webexz.sicav;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        Button comenzar = (Button) findViewById(R.id.menu);
        if(!redActiva()) {
            Toast.makeText(getApplicationContext(), "SIN CONEXIÓN AL SERVIDOR", Toast.LENGTH_SHORT).show();
        }
        comenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarLogin();
            }
        });
    }

    public void lanzarLogin() {
        Intent login = new Intent(getApplicationContext(), Login.class);
        startActivity(login);
    }

    public boolean redActiva(){
        ConnectivityManager conexion = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo informacion = conexion.getActiveNetworkInfo();
        return (informacion.isConnected() && informacion != null);
    }
}