package com.sepankasuite.timer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HistoryMapActivity extends FragmentActivity implements OnMapReadyCallback {

    //Variable de instancia de clase de manejo en la BD
    DataBaseManager manager;
    String msgError;
    Cursor cursor;
    GoogleMap map;

    double latitude, longitude;
    String id, fecha, hora, direccion, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_map);

        //Creamos una nueva instancia de la clase para obtener atributos y metodos
        manager = new DataBaseManager(this);
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        id = bundle.getString("id");

        cursor = manager.selectSpecificDataRecordsTimer(Integer.parseInt(id));
        cursor.moveToFirst();
        try {
            latitude = cursor.getDouble(1);
            longitude = cursor.getDouble(2);
            comment = cursor.getString(4);
            fecha = cursor.getString(5);
            hora = cursor.getString(6);

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        float zoomLevel = (float) 16.0f; //This goes up to 21

        LatLng Ubication = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(Ubication).title(fecha + " " + hora));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Ubication, zoomLevel));
    }
}
