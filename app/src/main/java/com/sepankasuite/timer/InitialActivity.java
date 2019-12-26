package com.sepankasuite.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InitialActivity extends AppCompatActivity implements View.OnClickListener {

    //Creamos las variables globales
    private Button btn_acceder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asignamos que el activity solo se abra de tipo vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_initial);

        //Enlazamos las variables a los objetos fisicos
        btn_acceder = findViewById(R.id.btn_init_acceder);

        //Generamos el evento clic de cada boton
        btn_acceder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //Recuperamos el id del elemento al que fue dado clic
        switch (v.getId()){
            case R.id.btn_init_acceder:
                //Creamos una instancia de la otra ventana
                Intent intent1 = new Intent(this, LogInActivity.class);
                //Nos aseguramos de cerrar las ventanas activas o que no se
                //repitan si es que ya esta abiertas
                startActivity(intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
                finish();
                break;
        }
    }
}
