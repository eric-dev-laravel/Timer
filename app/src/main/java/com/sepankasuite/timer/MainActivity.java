package com.sepankasuite.timer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    //Variable de instancia de clase de manejo en la BD
    DataBaseManager manager;

    //Creamos las variables globales
    private Button btn_timer, btn_history;
    TextView tv_latitud,tv_longitud;
    TextView tv_direccion, tv_fechahora;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asignamos que el activity solo se abra de tipo vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        //Creamos una nueva instancia de la clase para obtener atributos y metodos
        manager = new DataBaseManager(this);

        //Enlazamos las variables con los objetos fisicos
        btn_timer = findViewById(R.id.btn_timer);
        btn_history = findViewById(R.id.btn_history);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        tv_latitud = (TextView) findViewById(R.id.txtLatitud);
        tv_longitud = (TextView) findViewById(R.id.txtLongitud);
        tv_direccion = (TextView) findViewById(R.id.txtDireccion);
        tv_fechahora = (TextView) findViewById(R.id.txtFechaHora);

        //Generamos el metodo de clic para los botones
        btn_timer.setOnClickListener(this);
        btn_history.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //Recuperamos el Id del elemento al que se le dio clic
        switch (v.getId()) {
            case R.id.btn_timer:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                    return;
                }
                Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                onLocationChanged(location);
                break;

            case R.id.btn_history:
                //Creamos una instancia de la otra ventana
                Intent intent1 = new Intent(this, MyHistoryActivity.class);
                //Nos aseguramos de cerrar las ventanas activas o que no se
                //repitan si es que ya esta abiertas
                startActivity(intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    public String setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    //direccion.setText(DirCalle.getAddressLine(0));
                    return DirCalle.getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "N/A";
    }

    @Override
    public void onLocationChanged(Location location) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat1.format(date);
        String hora = dateFormat2.format(date);

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String direccion = setLocation(location);
        tv_latitud.setText("Latitud: " + latitude);
        tv_longitud.setText("Longitude: " + longitude);
        tv_direccion.setText(direccion);
        tv_fechahora.setText("Fecha: " + fecha + " Hora: " + hora);

        try {
            manager.InsertParamsRecordsTimer(1, latitude, longitude, direccion, "", fecha, hora);
            Log.d("Success: ", "Record add correctly");
        } catch (Exception e) {
            Log.d("Error", String.valueOf(e));
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
