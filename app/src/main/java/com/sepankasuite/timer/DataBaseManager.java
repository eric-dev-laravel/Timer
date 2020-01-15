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
    public static final String SERVER_URL = "http://somosucin.soysepanka.com/";
    public static final String SERVER_PATH_CHECKLOGIN = "api/checklogin/";

    /*Esta clase es la encargada de hacer movimientos en la DB*/

    /* *************************** ESTRUCTURA DE LA DB *********************************** */
    //Nombre de la tabla usuarios
    public static final String TABLE_USERS = "users";
    public static final String TABLE_RECORDS_TIMER = "records_timer";

    //Nombre de campos server
    public static final String CN_ID = "_id";
    public static final String CN_SYNC = "sincronized";
    //Nombre de campos usuarios
    public static final String CN_ID_USER = "id_user";
    public static final String CN_USER = "email";
    public static final String CN_PASSWORD = "password";
    public static final String CN_STATE = "state";
    //Nombre de campos timer
    public static final String CN_LATITUDE = "latitude";
    public static final String CN_LONGITUDE = "longitude";
    public static final String CN_ADDRESS = "address";
    public static final String CN_ABOUT = "about";
    public static final String CN_DATE = "date";
    public static final String CN_TIME = "time";

    /* ************* TABLAS ***************** */

    //Sentencia para crear la tabla usuarios
    public static final String CREATE_TABLE_USERS = "create table "+TABLE_USERS+" ("
            + CN_ID + " integer primary key autoincrement,"
            + CN_ID_USER + " integer unique,"
            + CN_USER + " text not null,"
            + CN_PASSWORD + " text not null,"
            + CN_STATE + " text not null);";

    //Sentencia para crear la tabla records timer
    public static final String CREATE_TABLE_RECORDS_TIMER = "create table "+TABLE_RECORDS_TIMER+" ("
            + CN_ID + " integer primary key autoincrement,"
            + CN_ID_USER + " integer,"
            + CN_LATITUDE + " double not null,"
            + CN_LONGITUDE + " double not null,"
            + CN_ADDRESS + " text not null,"
            + CN_ABOUT + " text not null,"
            + CN_DATE + " text not null,"
            + CN_TIME + " text not null,"
            + CN_STATE + " text not null,"
            + CN_SYNC + " int not null);";

    /* ****************** METODOS DE INSERTAR ************************** */

    //Metodo para insertar usuarios
    public void InsertParamsUsers(int id_user, String user, String password) {
        //Instruccion para insertar en android
        db.insert(TABLE_USERS, null, generarContentValuesUsers(id_user, user, password));
    }

    //Metodo para insertar usuarios
    public void InsertParamsRecordsTimer(int id_user, double latitude, double longitude, String address, String about, String date, String time) {
        //Instruccion para insertar en android
        db.insert(TABLE_RECORDS_TIMER, null, generarContentValuesRecordsTimer(id_user, latitude, longitude, address, about, date, time));
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

    public Cursor selectDataRecordsTimer() {
        //Se crea el array de las columnas que seran consultadas
        String[] columnas = new String[]{CN_ID, CN_ADDRESS, CN_ABOUT, CN_DATE, CN_TIME};

        //Recupera la informacion del estatus que queremos
        return db.query(TABLE_RECORDS_TIMER, columnas, null, null, null, null, CN_ID+" DESC");
    }

    public Cursor selectSpecificDataRecordsTimer(int Id) {
        //Se crea el array de las columnas que seran consultadas
        String[] columnas = new String[]{CN_ID, CN_LATITUDE, CN_LONGITUDE, CN_ADDRESS, CN_ABOUT, CN_DATE, CN_TIME};
        String[] args = new String[] {String.valueOf(Id)};

        //Recupera la informacion del estatus que queremos
        return db.query(TABLE_RECORDS_TIMER, columnas, "_id=?", args, null, null, null);
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

    //Metodo contenedor de valores USUARIOS
    private ContentValues generarContentValuesRecordsTimer(int id_user, double latitude, double longitude, String address, String about, String date, String time) {
        ContentValues values = new ContentValues();
        values.put(CN_ID_USER, id_user);
        values.put(CN_LATITUDE, latitude);
        values.put(CN_LONGITUDE, longitude);
        values.put(CN_ADDRESS, address);
        values.put(CN_ABOUT, about);
        values.put(CN_DATE, date);
        values.put(CN_TIME, time);
        values.put(CN_STATE, "E/S");
        values.put(CN_SYNC, 0);
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
