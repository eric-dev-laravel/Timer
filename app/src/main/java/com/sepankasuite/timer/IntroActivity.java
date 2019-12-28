package com.sepankasuite.timer;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Al usar este tipo de elemento no requiere hacer una ligadura del codigo java con la actividad
        //setContentView(R.layout.activity_intro);
        setFlowAnimation();

        //Generamos el numero de elementos que tendra el intro cada uno debe cubrir background, title, description, image
        addSlide(AppIntroFragment.newInstance("BIENVENIDO", "",
                R.drawable.intro1, ContextCompat.getColor(getApplicationContext(), R.color.colorBlueUcin)));
        addSlide(AppIntroFragment.newInstance("INSTRUCCIONES", "Esta app te servirá de apoyo para poder reportar tu hora de inicio de labores donde estes",
                R.drawable.intro2, ContextCompat.getColor(getApplicationContext(), R.color.colorBlueUcin)));
        addSlide(AppIntroFragment.newInstance("INSTRUCCIONES", "Al acceder al app con tus credenciales, podrás marcar tu llegada con un solo clic",
                R.drawable.intro3, ContextCompat.getColor(getApplicationContext(), R.color.colorBlueUcin)));
        addSlide(AppIntroFragment.newInstance("INSTRUCCIONES", "Simplemente da clic en el botón y listo",
                R.drawable.intro4, ContextCompat.getColor(getApplicationContext(), R.color.colorBlueUcin)));
    }

    //Metodo para cambiar de actividad cuando se presiona el boton de hecho
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        //Creamos una instancia del la actividad siguiente
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        //Cerramos la actividad actual para que no este en cola
        finish();
    }

    //Metodo para cambiar de actividad cuando se presiona el boton de skape
    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        //Creamos una instancia del la actividad siguiente
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        //Cerramos la actividad actual para que no este en cola
        finish();
    }
}
