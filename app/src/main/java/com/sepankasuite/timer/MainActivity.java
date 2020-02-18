package com.sepankasuite.timer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.AlertDialog.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    //Variable de instancia de clase de manejo en la BD
    DataBaseManager manager;
    ApiManager api;

    //Creamos las variables globales
    private Button btn_timer, btn_history;
    private LocationManager locationManager;
    String comment = "";

    Location location1;
    Cursor cursorManager;
    Integer idUser=0;

    Cursor cursorLastCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asignamos que el activity solo se abra de tipo vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                if (isFirstStart){
                    //Creamos una instancia del la actividad siguiente
                    startActivity(new Intent(getApplicationContext(), IntroActivity.class));
                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });

        thread.start();

        //Creamos una nueva instancia de la clase para obtener atributos y metodos
        manager = new DataBaseManager(this);
        api = new ApiManager(this);
        cursorManager = manager.selectDataUsers();
        if (cursorManager.moveToFirst()){
            idUser = cursorManager.getInt(0);
        }

        //Enlazamos las variables con los objetos fisicos
        btn_timer = findViewById(R.id.btn_timer);
        btn_history = findViewById(R.id.btn_history);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Generamos el metodo de clic para los botones
        btn_timer.setOnClickListener(this);
        btn_history.setOnClickListener(this);

        final boolean ejecutar = true;
        new Thread(new Runnable() {
            public void run() {
                try {
                    while(ejecutar){
                        new UploadChecksTimer().execute();
                        Thread.sleep(20000);
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
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

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                location1 = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

                //final Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                String direccion = setLocation(location1);
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date date = new Date();

                String fecha = dateFormat1.format(date);
                String hora = dateFormat2.format(date);

                Builder alertDialog = new Builder(this);
                alertDialog.setTitle("Nuevo Registro");
                alertDialog.setMessage("\nDirección:\n"+ direccion + "\n\nFecha:\n" + fecha + "\n\nHora:\n" + hora + "\n\nComentario:\n");

                final EditText et_comment = new EditText(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                et_comment.setLayoutParams(layoutParams);
                et_comment.setHint("Opcional");

                alertDialog.setView(et_comment);
                alertDialog.setIcon(R.drawable.button_timer);

                alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        comment = et_comment.getText().toString();
                        //onLocationChanged(location);
                        saveLocationChanged(location1, dialogInterface);
                    }
                });

                alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                alertDialog.show();
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
        location1 = location;
        /*SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat1.format(date);
        String hora = dateFormat2.format(date);

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String direccion = setLocation(location);

        try {
            manager.InsertParamsRecordsTimer(1, latitude, longitude, direccion, comment, fecha, hora);
            api.saveCheck(1, fecha, hora, longitude, latitude, direccion, comment);

        } catch (Exception e) {
            Log.d("error_location_changed", String.valueOf(e));
        }*/
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


    public void saveLocationChanged(Location location, DialogInterface dialogInterface) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        String fecha = dateFormat1.format(date);
        String hora = dateFormat2.format(date);

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String direccion = setLocation(location);

        try {
            long inserted_id = manager.InsertParamsRecordsTimer(idUser, latitude, longitude, direccion, comment, fecha, hora);
            api.saveCheck(inserted_id, idUser, fecha, hora, longitude, latitude, direccion, comment);
            dialogInterface.cancel();
        } catch (Exception e) {
            Log.d("error_location_changed", String.valueOf(e));
        }
    }

    public class UploadChecksTimer extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            final Handler handler = new Handler(Looper.getMainLooper());
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    cursorLastCheck = manager.selectDataRecordsTimerSync();
                    cursorLastCheck.moveToFirst();
                    if (cursorLastCheck.getCount() != 0){
                        while (!cursorLastCheck.isAfterLast()){
                            Log.d("status", "Sincronizando..." + "id: " + cursorLastCheck.getString(0));
                            api.saveCheck(cursorLastCheck.getLong(0), idUser, cursorLastCheck.getString(6), cursorLastCheck.getString(7), cursorLastCheck.getDouble(3), cursorLastCheck.getDouble(2), cursorLastCheck.getString(4), cursorLastCheck.getString(5));
                            cursorLastCheck.moveToNext();
                        }
                    }
                }
            };
            handler.post(runnable);
            return null;
        }
    }
}
