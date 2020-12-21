package com.francisco.listadelacompra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.francisco.listadelacompra.dialogs.DialogLoading;
import com.francisco.listadelacompra.models.ResponseLogin;
import com.francisco.listadelacompra.retrofitUtils.RetrofitAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity  {

    // PREFERENCIAS
    SharedPreferences misPreferencias;

    // PERMISOS PARA SI LA API SE PUBLICA EN UNA DIRECCION DE INTENT
    private static final int REQUEST_INTERNET = 1;

    // VISTAS
    public TextView email;
    public TextView password;
    public Button entrar;

    private ImageView imagenCarga;

    private FloatingActionButton fabregistrar;


    // VARIABLE QUE GUARDA EL PASSWORD LOCALMENTE
    private static String userPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_activity_main);

        // inicio la vista
        _init();
        // permisos
        permisionCheck();

        // inicio preferencias
        readPreferences();

    }

    // VINCULACION DE VISTAS
    public void _init(){

        imagenCarga = (ImageView)findViewById(R.id.imagencarga);
        imagenCarga.setVisibility(View.INVISIBLE);

        fabregistrar = (FloatingActionButton)findViewById(R.id.fabregistrar);

        email=(TextView)findViewById(R.id.email);
        password=(TextView)findViewById(R.id.password);

        entrar=(Button) findViewById(R.id.entrar);


        fabregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });
    }


    // imagen de carga
    public void animateloading(ImageView imagen){
        imagen.setVisibility(View.VISIBLE);
        Animation btnfgiro = AnimationUtils.loadAnimation(this, R.anim.animacion);
        imagen.startAnimation(btnfgiro);


    }

    public void  stoploading(ImageView imagen){
        imagen.setVisibility(View.INVISIBLE);
        imagen.setAnimation(null);
    }



    // LEO LAS PREFERENCIAS
    private void readPreferences() {

        misPreferencias = getSharedPreferences("preferences", MODE_PRIVATE);
        String nombre1 = misPreferencias.getString("email", "");
        String contraseña1 = misPreferencias.getString("password", "");

        if(nombre1 != null && contraseña1 != null) {
            // los introduzco en los campos
            email.setText(nombre1);
            password.setText((contraseña1));


            email.setBackgroundColor(Color.YELLOW);
            password.setBackgroundColor(Color.YELLOW);
        }

    }


    // pide permisos de internet
    public void permisionCheck(){

        // Pido permiso para hacer fotos si me lo dan o lo tengo sigo si no salgo
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET);

            Toast.makeText(this, "No se puede Hacer peticiones a la API si no das permiso", Toast.LENGTH_LONG).show();

        }


    }



    // METODO SE EJECUTAL AL CLICAR EN CONTINUAR
    public void logIn(View view){

        // obtengo el valor de los campos
        String email= this.email.getText().toString();
        String password=this.password.getText().toString();


        // LLAMADA A LA API PARA REGISTRASE
        ejecuteLoginAPI(email,password);


    }

    // METODOS DE LLAMADA A LA API
    private void ejecuteLoginAPI(String email, String password){


    animateloading(imagenCarga);

        // guardo el password
        userPassword = password;
        // llamo a la api
        Call<ResponseLogin> call = RetrofitAdapter.getApiService().logIn(email, password);
        call.enqueue(new ResponseLoginCallback());

    }



    // METODO QUE MUETRA EL TOAST
    private void showToastMessage(String message) {
        // devuelvo el valor
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();


    }


    private void actionLoginCorrect(ResponseLogin responselogin) {

        // debo guardar los datos del login en el pref
        editPreferences(responselogin);

        // abro otra actividad si todo ha ido bien y envio los datos
        Intent intento=new Intent(this, ListProducts.class);
        intento.putExtra("nombreUsuario",responselogin.getUser().toString());
        intento.putExtra("emailUsuario",responselogin.getUserEmail().toString());
        startActivity(intento);
    }

    // editar preferencias
    private void editPreferences(ResponseLogin responseLogin) {
        // inicio las preferencias y guardo los valores
        SharedPreferences.Editor editor = misPreferencias.edit();
        editor.putString("token",responseLogin.getToken().toString());
        editor.putString("name",responseLogin.getUser());
        editor.putString("password",userPassword);
        editor.putString("email",responseLogin.getUserEmail());
        editor.apply();
    }


    // RESPUESTAS DE LA API
    // clase que se encarga de la respuesta de la api al registrase
    class ResponseLoginCallback implements Callback<ResponseLogin> {
        @Override
        public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {

            stoploading(imagenCarga);
            // peticion correcta code 200
            if (response.isSuccessful()) {
                if (response.code() == 200) {
                    // cogo el cuerpo de la peticion
                    //obtengo la clase responseLogin
                    ResponseLogin responselogin= response.body();



                    // muestro un toast de que se han logeado correctamente
                    showToastMessage(responselogin.getMessage().toString());


                    // ejecuto las acciones si se ha registrado correctamente
                    actionLoginCorrect(responselogin);



                }
            } else {
                // si no se ha podido logear da fallo code != 200

                try {
                    // obtengo el cuerpo de la respuesta si ha habido un error
                    String errorBody = response.errorBody().string();

                    // instancio la utilidad gson
                    Gson gson=new Gson();

                    // lo parsea
                    JsonObject jsonObject = gson.fromJson(errorBody, JsonObject.class);
                    if (jsonObject!=null && jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                        // obtengo el string con el mensage
                        String messageError=  jsonObject.get("message").getAsString();


                        // muestro un toast con el error
                        showToastMessage(messageError);

                    }

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }

        }
        // fallo en la llamada a al API
        @Override
        public void onFailure(Call<ResponseLogin> call, Throwable t) {

            stoploading(imagenCarga);
            showToastMessage("Error en la conexion");

        }
    }



}
