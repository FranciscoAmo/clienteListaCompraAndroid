package com.francisco.listadelacompra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.francisco.listadelacompra.models.ResponseLogin;
import com.francisco.listadelacompra.retrofitUtils.RetrofitAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity  {

    // preferencias
    SharedPreferences misPreferencias;

    // para los permisos
    private static final int REQUEST_INTERNET = 1;

    // clases de las vistas
    public TextView email;
    public TextView password;

    public Button entrar;



    // private
    private static String userPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inicio la vista
        _init();
        // permisos
        permisionCheck();

        // inicio preferencias
        leerPreferencias();

    }

    // vinculacion de clases a vistas
    public void _init(){

        email=(TextView)findViewById(R.id.email);
        password=(TextView)findViewById(R.id.password);

        entrar=(Button) findViewById(R.id.entrar);
    }



    // muestra el menu_login action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflate=getMenuInflater();
        inflate.inflate(R.menu.menu_login,menu);
        return super.onCreateOptionsMenu(menu);
    }


// selecciona la opcion del menu_login

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.registrar:
                Intent intent=new Intent(this,Registrar.class);
                startActivity(intent);

                break;

        }
        return super.onOptionsItemSelected(item);
    }


// lee las preferencias por si ya esta logeado de antes
    private void leerPreferencias() {

        misPreferencias = getSharedPreferences("preferences", MODE_PRIVATE);
        String nombre1 = misPreferencias.getString("email", "");
        String contraseña1 = misPreferencias.getString("password", "");


        // los introduzco en los campos
        email.setText(nombre1);
        password.setText((contraseña1));



    }





    // pide permisos de internet
    public void permisionCheck(){

        // Pido permiso para hacer fotos si me lo dan o lo tengo sigo si no salgo
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET);

            Toast.makeText(this, "No se puede Hacer peticiones a la API si no das permiso", Toast.LENGTH_LONG).show();


        }



    }




    // se intenta logear
    public void logIn(View view){


        // obtengo el valor de los campos
        String email= this.email.getText().toString();
        String password=this.password.getText().toString();


        // LLAMADA AL METODO PARA REGISTRASE
        //httpMethod.logIn(email,password);
        logearse(email,password);




    }

    private void logearse(String email, String password){
        // guardo el password
        userPassword = password;
        // llamo a la api
        Call<ResponseLogin> call = RetrofitAdapter.getApiService().logIn(email, password);
        call.enqueue(new ResponseLoginCallback());
    }


    private void devolverResultado(String message) {
        // devuelvo el valor
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();


    }


    private void logearsecorrectamente(ResponseLogin responselogin) {

        // debo guardar los datos del login en el pref
       editarpreferencias(responselogin);

        // abro otra actividad si todo ha ido bien
        Intent intento=new Intent(this, ListProducts.class);
        intento.putExtra("nombreUsuario",responselogin.getUser().toString());
        intento.putExtra("emailUsuario",responselogin.getUserEmail().toString());
        startActivity(intento);
    }

    // editar preferencias
    private void editarpreferencias(ResponseLogin responseLogin) {
        // preferencias sin editor grafico es del archivo "pref"
        // archivo donde se guardaran las preferencias
        // vinculo el editor de las preferencias
        SharedPreferences.Editor editor = misPreferencias.edit();
        editor.putString("token",responseLogin.getToken());
        editor.putString("name",responseLogin.getUser());
        editor.putString("password",userPassword);
        editor.putString("email",responseLogin.getUserEmail());
        editor.apply();
    }



    // clase que se encargara de la llamada
    class ResponseLoginCallback implements Callback<ResponseLogin> {
        @Override
        public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {

            // peticion correcta code 200
            if (response.isSuccessful()) {
                if (response.code() == 200) {
                    ResponseLogin responselogin= response.body();
                    String responsemessage = responselogin.getMessage().toString();

                    // muestro un toast de que se han logeado correctamente
                    devolverResultado(responsemessage);

                    // ejecuto las acciones si se ha registrado correctamente
                    logearsecorrectamente(responselogin);

                }
            } else {
                // si no se ha podido logear da fallo code != 200


                try {
                    // obtengo el cuerpo del error

                    String errorBody = response.errorBody().string();

                    //parseo el errorBody para obtener el campo message del servidor
                    Gson gson=new Gson();

                    JsonObject jsonObject = gson.fromJson(errorBody, JsonObject.class);
                    if (jsonObject!=null && jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                        String messageError=  jsonObject.get("message").getAsString();

                        // muestro un toast con el error
                        devolverResultado(messageError);
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }




        }
        // fallo en la llamada
        @Override
        public void onFailure(Call<ResponseLogin> call, Throwable t) {


            devolverResultado("Error en la conexion");
        }
    }



}
