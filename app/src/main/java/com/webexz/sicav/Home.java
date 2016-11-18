package com.webexz.sicav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView txt_agente, txt_id, txt_unidad;
    private SQLiteDatabase db;
    private AgentesDB agentes;
    private JSONArray jsonArray;
    private Map<Integer, String> valores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //VISTA
        txt_agente = (TextView)findViewById(R.id.nombre_agente);
        txt_id = (TextView)findViewById(R.id.id);
        txt_unidad = (TextView)findViewById(R.id.unidad);

        //ACCIONES
        LocationManager servicio = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean habilitado = servicio.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //ARCHIVO DE SESION
        SharedPreferences setting = getSharedPreferences("Sesion", 0);
        String usuario = setting.getString("usuario", "webexz");

        if(!usuario.equalsIgnoreCase("webexz")){
            Toast.makeText(getApplicationContext(), "Logeado", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Sesión Fallida", Toast.LENGTH_SHORT).show();
        }

            //BASE DE DATOS
        agentes = new AgentesDB(this, "Agentes", null, 2);
        db = agentes.getWritableDatabase();

        String query = "SELECT nombre, numeroControl FROM Agentes WHERE numeroControl = '" + usuario +"';";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        txt_agente.setText(c.getString(0));
        txt_id.setText(c.getString(1));

        if (!habilitado) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            getUbicacion(servicio);
            new AgenteTask().execute("http://webexz-001-site1.itempurl.com/Recursos/getPolicia/1");
            new ZonaTask().execute("http://webexz-001-site1.itempurl.com/Recursos/getUbicaciones/");
        }
    }

    private void getUbicacion(LocationManager servicio) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location ubicacion = servicio.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        String actual = ubicacion.getLatitude() + ", " + ubicacion.getLongitude();
        //TextView poi = (TextView) findViewById(R.id.text_poi);
        //poi.setText(actual);
        //poi.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            LocationManager servicio = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            getUbicacion(servicio);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ubicacion) {
            Intent mapa = new Intent(getApplicationContext(), Mapa.class);
            startActivity(mapa);
        } else if (id == R.id.nav_zona) {
            Intent zonas = new Intent(getApplicationContext(), Zonas.class);
            startActivity(zonas);
        } else if (id == R.id.nav_salir) {
            this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //ZONA DE PETICIONES

    public class AgenteTask extends AsyncTask<String, String, String> {

        private String name, noControl, unidad;

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conexion = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                conexion = (HttpURLConnection) url.openConnection();
                conexion.connect();

                InputStream stream = conexion.getInputStream();
                reader  = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while((line = reader.readLine()) != null ){
                    buffer.append(line);
                }

                String json = buffer.toString();
                JSONArray array = new JSONArray(json);
                name = array.get(0).toString();
                noControl = array.get(1).toString();
                unidad = array.get(2).toString();

                return json;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                conexion.disconnect();
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //txt_agente.setText("AGENTE: " + name);
            //txt_id.setText("ID: " + noControl);
            txt_unidad.setText("UNIDAD: " + unidad);
            Toast.makeText(getBaseContext(),"Datos Cargados", Toast.LENGTH_SHORT).show();
        }
    }

    public class ZonaTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection conexion = null;
            BufferedReader reader = null;

            try {
                //CONEXION
                URL url = new URL(params[0]);
                conexion = (HttpURLConnection) url.openConnection();
                conexion.connect();

                //DATOS
                InputStream stream = conexion.getInputStream();
                reader  = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while((line = reader.readLine()) != null ){
                    buffer.append(line);
                }

                //PROCESADO
                String datos = buffer.toString();
                jsonArray = new JSONArray(datos);

                return datos;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                conexion.disconnect();
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            valores = new HashMap<>();
            for(int i = 0; i < jsonArray.length(); i++){
                try {
                    JSONObject objetos = jsonArray.getJSONObject(i);
                    int llave = objetos.getInt("Key");
                    String valor = objetos.getString("Value");
                    valores.put(llave, valor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            db = agentes.getWritableDatabase();
            //BORRAR Y CREAR ME FALTA
            for(int i = 1; i <= valores.size(); i++){
                String x = valores.get(i);
                String[] posicion = x.split(",");
                for(int j = 0; j < posicion.length; j++){
                    double lat = Double.parseDouble(posicion[0]);
                    double lng = Double.parseDouble(posicion[1]);
                    db.execSQL("INSERT INTO Zonas VALUES("+i+","+lat+","+lng+");");
                }
            }
            Toast.makeText(getApplicationContext(),"Finaliza la bd",Toast.LENGTH_SHORT).show();
        }
    }
}