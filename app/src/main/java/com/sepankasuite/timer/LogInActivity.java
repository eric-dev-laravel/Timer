package com.sepankasuite.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.regex.Pattern;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    //Variable de instancia de clase de manejo en la BD
    DataBaseManager manager;

    //Creamos las variables globales
    private Button btn_acceder;
    private EditText et_user, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asignamos que el activity solo se abra de tipo vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_log_in);

        //Creamos una nueva instancia de la clase para obtener atributos y metodos
        manager = new DataBaseManager(this);

        //Enlazamos las variables con los objetos fisicos
        btn_acceder = findViewById(R.id.btn_login_acceder);
        et_user = (EditText) findViewById(R.id.et_user);
        et_password = (EditText) findViewById(R.id.et_password);

        //Generamos el metodo de clic para los botones
        btn_acceder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //Recuperamos el Id del elemento al que se le dio clic
        switch (v.getId()){
            case R.id.btn_login_acceder:
                if (et_user.length() != 0 ){
                    if (et_password.length() != 0){
                        if (!validarEmail(et_user.getText().toString())){
                            et_user.setError("Email no válido");
                        } else {
                            //Toast.makeText(this, "parametros usuario y contrasela", Toast.LENGTH_LONG).show();
                            //Creamos un nuevo cliente de conexion
                            AsyncHttpClient client = new AsyncHttpClient();
                            //Definimos la URL a la cual sera dirigidio y recuperamos los datos de las cajas de texto
                            String url = manager.SERVER_URL + manager.SERVER_PATH_CHECKLOGIN + et_user.getText().toString() +"/" + et_password.getText().toString() +"/";

                            //Ejecutamos peticion POST para envio de parametros
                            client.post(url, null, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                                    //Recuperamos el codigo de la operacion 200 significa que respondio el server correctamente y si existe conexion
                                    if(statusCode == 200){
                                        //Recibimos la respuesta del servidor en formato JSON y la mandamos a la clase que obtiene los datos
                                        //Asignamos el acceso si fue correcto regresara un true de lo contrario false
                                        boolean access = manager.obtDatosJSONLogin(new String(responseBody), et_user.getText().toString(), et_password.getText().toString());
                                        if(access == true) {
                                            //Creamos una instancia de la otra ventana
                                            Intent intent1 = new Intent(getApplicationContext(), IntroActivity.class);
                                            //Nos aseguramos de cerrar las ventanas activas o que no se
                                            //repitan si es que ya esta abiertas
                                            startActivity(intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                                            overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
                                            //Cerramos la actividad actual para que no este en cola
                                            finish();
                                        } else {
                                            //En caso de conectar con el servidor pero que el acceso sea incorrecto mandara el sig. mensaje
                                            Toast.makeText(getApplicationContext(), "Usuario o Contraseña incorrectos. Verifique sus datos", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        //En caso de conectar con el server pero mandar un codigo distinto al 200
                                        Toast.makeText(getApplicationContext(), "Ocurrio un detalle al intentar conectar. Code: "+statusCode, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    //En caso de no conectar con el servidor se muestra este msg
                                    Toast.makeText(getApplicationContext(), "Imposible conectar con el servidor.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "El password no puede estar vacío", Toast.LENGTH_LONG).show();
                        et_password.setError("Vacío");
                    }
                } else {
                    Toast.makeText(this, "El usuario no puede estar vacío", Toast.LENGTH_LONG).show();
                    et_user.setError("Vacío");
                }
                break;

        }
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
