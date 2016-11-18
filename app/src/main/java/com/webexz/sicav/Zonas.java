package com.webexz.sicav;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Zonas extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager servicio;
    private Location ubicacion;
    private SQLiteDatabase db;
    private AgentesDB agentes;
    private LatLng tmp;
    private String letrero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zonas);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        servicio = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        agentes = new AgentesDB(this, "Agentes", null, 2);
        db = agentes.getWritableDatabase();
        String query = "SELECT * FROM Zonas;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        letrero = "" + c.getInt(0);
        double x = Double.parseDouble(c.getString(1));
        double y = Double.parseDouble(c.getString(2));
        tmp = new LatLng(x, y);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        ubicacion = servicio.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        LatLng actual = new LatLng(ubicacion.getLatitude(), ubicacion.getLongitude());

        mMap.addMarker(new MarkerOptions().position(tmp).title(letrero));
        mMap.addMarker(new MarkerOptions().position(actual).title("AGENTE"));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(actual));
    }
}
