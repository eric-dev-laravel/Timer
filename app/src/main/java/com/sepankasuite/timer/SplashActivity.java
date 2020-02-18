package com.sepankasuite.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    //Variable de instancia de clase de manejo en la BD
    DataBaseManager manager;

    //# Variables #
    Animation animation;
    ImageView imageView;
    Cursor cursor;

    //Generamos la variable global del tiempo que estara abierto el splash screen en milisegundos
    private final int SPLASH_DURATION = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asignamos que el activity solo se abra de tipo vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Asignamos que el activity abarque completamente el fondo de la pantalla
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Enlace de la clase java con el activity
        setContentView(R.layout.activity_splash);

        //Creamos una nueva instancia de la clase para obtener atributos y metodos
        manager = new DataBaseManager(this);
        cursor = manager.selectDataUsers();

        //Enlace imagen
        imageView = (ImageView) findViewById(R.id.iv_imageSplash);
        //Enlace a la animación
        animation= AnimationUtils.loadAnimation(SplashActivity.this,R.anim.pulse);
        //Iniciar la animación
        startAnimation();

        //Generamos la funcion que lleva el contador interno del tiempo y lo que se hara cuando termine el tiempo
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cursor.getCount() > 0){
                    //Creamos una instancia de la otra ventana
                    Intent intent1 = new Intent(SplashActivity.this, MainActivity.class);
                    //Nos aseguramos de cerrar las ventanas activas o que no se
                    //repitan si es que ya esta abiertas
                    startActivity(intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                    overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
                    finish();
                } else {
                    //Generamos un intent de la actividad nueva
                    Intent intent = new Intent(SplashActivity.this, InitialActivity.class);
                    //Lanzamos el activity
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
                    //Cerramos el intent actual para que no se quede en cola
                    finish();
                }
            };
        }, SPLASH_DURATION);
    }

    //Método de inicio de animación
    private void startAnimation(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.startAnimation(animation);
            }
        }, 1000);
    }
}
