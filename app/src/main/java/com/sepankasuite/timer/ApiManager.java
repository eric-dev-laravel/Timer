package com.sepankasuite.timer;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ApiManager {

    public Context context;

    //Variables de configuracion de servicios
    public static final String SERVER_URL = "http://somosucin.soysepanka.com/";
    public static final String SERVER_PATH_SAVECHECK = "api/incidents/save_check/";
    public static final String SERVER_PATH_GETCHECK = "api/incidents/get_check/";

    public ApiManager(MainActivity context){
        this.context = context;
    }

    public void saveCheck(final long inserted_id, int id_user, String fecha, String hora, double longitude, double latitude, String direccion, String comment) {
        final long inserted = inserted_id;
        AsyncHttpClient client = new AsyncHttpClient();
        //Definimos la URL a la cual sera dirigidio y recuperamos los datos de las cajas de texto
        String url = generateSaveURL(id_user, fecha, hora, longitude, latitude, direccion, comment);
        Log.d("urlCheck", url);
        //Ejecutamos peticion POST para envio de parametros
        client.post(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                //Recuperamos el codigo de la operacion 200 significa que respondio el server correctamente y si existe conexion
                if (statusCode == 200) {
                    //Recibimos la respuesta del servidor en formato JSON y la mandamos a la clase que obtiene los datos
                    //Asignamos el acceso si fue correcto regresara un true de lo contrario false
                    boolean success = false;
                    int id;
                    try {
                        //recibimos el arreglo de tipo JSON en una variable JSON
                        JSONObject object = new JSONObject(new String(responseBody));
                        success = object.getBoolean("success");
                        if (success) {
                            //Aqui se supone que se deben de actualizar las sincronias
                            DataBaseManager manager = new DataBaseManager(context);
                            Log.d("apiResponseId", String.valueOf(inserted));
                            manager.updateDataCheckState(inserted);
                        }
                    } catch (Exception e) {
                        Log.d("errorJson", String.valueOf(e));
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("error_post", String.valueOf(statusCode));
            }
        });
    }

    public String generateSaveURL(int id_user, String fecha, String hora, double longitude, double latitude, String direccion, String comment) {
        String e_direccion = "";
        String e_comment = "";
        try {
            e_direccion = URLEncoder.encode(direccion, "utf-8");
            e_comment = URLEncoder.encode(comment, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = new StringBuilder()
            .append(SERVER_URL)
            .append(SERVER_PATH_SAVECHECK)
            .append(String.valueOf(id_user)).append("/")
            .append(fecha).append("/")
            .append(hora).append("/")
            .append(String.valueOf(longitude)).append("/")
            .append(String.valueOf(latitude)).append("/")
            .append(e_direccion).append("/")
            .append(e_comment).append("/").toString();

        return url;
    }

}
