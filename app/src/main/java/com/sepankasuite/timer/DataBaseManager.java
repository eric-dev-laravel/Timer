package com.sepankasuite.timer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataBaseManager {

    //Instancia de las clases
    private DBHelper helper;
    private SQLiteDatabase db;

    //Variables de configuracion de servicios
    public static final String SERVER_URL = "http://test.rally.sepankasuite.com/";
    public static final String SERVER_PATH_CHECKLOGIN = "checklogin/";

    /*Esta clase es la encargada de hacer movimientos en la DB*/

    /* *************************** ESTRUCTURA DE LA DB *********************************** */
    //Nombre de la tabla usuarios
    public static final String TABLE_USERS = "users";

    //Nombre de campos server
    public static final String CN_ID = "_id";
    public static final String CN_SYNC = "sincronized";
    //Nombre de campos usuarios
    public static final String CN_ID_USER = "id_user";
    public static final String CN_USER = "email";
    public static final String CN_PASSWORD = "password";
    public static final String CN_STATE = "state";

    /* ************* TABLAS ***************** */

    //Sentencia para crear la tabla usuarios
    public static final String CREATE_TABLE_USERS = "create table "+TABLE_USERS+" ("
            + CN_ID + " integer primary key autoincrement,"
            + CN_ID_USER + " integer unique,"
            + CN_USER + " text not null,"
            + CN_PASSWORD + " text not null,"
            + CN_STATE + " text not null);";

    /* ****************** METODOS DE INSERTAR ************************** */

    //Metodo para insertar usuarios
    public void InsertParamsUsers(int id_user, String user, String password) {
        //Instruccion para insertar en android
        db.insert(TABLE_USERS, null, generarContentValuesUsers(id_user, user, password));
    }

    /* ****************** METODOS DE BORRAR ************************** */

    //Borrar el contenido de la tabla usuarios
    public void clearDataUsers(){
        db.delete(TABLE_USERS, null, null);
    }

    /* ****************** METODOS DE SELECCIONAR ************************** */
    public Cursor selectDataUsers() {
        //Se crea el array de las columnas que seran consultadas
        String[] columnas = new String[]{CN_ID_USER};

        //Recupera la informacion del estatus que queremos
        return db.query(TABLE_USERS, columnas, null, null, null, null, null);
    }

    //#########################    CONTENEDORES   ###############################################

    //Metodo contenedor de valores USUARIOS
    private ContentValues generarContentValuesUsers(int id_user, String user, String password) {
        ContentValues values = new ContentValues();
        values.put(CN_ID_USER, id_user);
        values.put(CN_USER, user);
        values.put(CN_PASSWORD, password);
        values.put(CN_STATE, 0);
        return values;
    }

    //########################### SERVER OPERATIONS ####################################################

    //Metodo para leer respuesta del login en el server
    public boolean obtDatosJSONLogin(String response, String user, String psw) {
        boolean acceso = false;
        int id_user = 0;
        try {
            //recibimos el arreglo de tipo JSON en una variable JSON
            JSONObject object = new JSONObject(response);
            JSONObject jsonArray = object.getJSONObject("success");
            acceso = jsonArray.getBoolean("existe");
            id_user = jsonArray.getInt("id_user");

            //Log.d("response_user1", String.valueOf(acceso));
            //Log.d("response_user2", String.valueOf(id_user));

            InsertParamsUsers(id_user, user, psw);
        } catch (Exception e) {
            Log.d("errorJson", String.valueOf(e));
            return acceso;
        }
        return acceso;
    }

    //Metodo de Inicializacion
    public DataBaseManager(Context context) {
        helper = new DBHelper(context);

        db = helper.getWritableDatabase();
    }
}
