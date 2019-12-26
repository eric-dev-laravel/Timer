package com.sepankasuite.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    //# Variables #
    Animation animation;
    ImageView imageView;

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

        imageView = (ImageView) findViewById(R.id.iv_imageSplash);
    }
}
