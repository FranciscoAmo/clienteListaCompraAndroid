package com.francisco.listadelacompra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.francisco.listadelacompra.models.ResponseLogin;
import com.francisco.listadelacompra.retrofitUtils.RetrofitAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    // PREFERENCES
    SharedPreferences misPreferencias;

    //VISTAS
    public TextView emailReg;
    public TextView passwordReg;
    public TextView displayNameReg;

    public Button entrarReg;

    private FloatingActionButton fabregresar;

   // VARIABLE PARA GUARDAR CONTRASEÑA
    private static String userPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_activity_registry);

        // obtengo una instancia de mis preferencias
        misPreferencias = getSharedPreferences("preferences", MODE_PRIVATE);


        // inicio las vistas
        _init();
    }


    //VINCULA LAS VISTAS
    public void _init(){

        fabregresar = (FloatingActionButton)findViewById(R.id.fabreturn);

        emailReg=(TextView)findViewById(R.id.emailReg);
        passwordReg=(TextView)findViewById(R.id.passwordReg);
        displayNameReg=(TextView)findViewById(R.id.displayNameReg);

        entrarReg=(Button) findViewById(R.id.entrarReg);


        fabregresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    // EJECUCION AL HACER CLICK
    public void onclick(View view){


        // obtengo el valor de los campos
        String email= this.emailReg.getText().toString();
        String password=this.passwordReg.getText().toString();
        String displayName=this.displayNameReg.getText().toString();

        // LLAMADA AL METODO PARA REGISTRASE
         regristryCallAPI(email,password,displayName);


    }

    // llamo metodo para regristryCallAPI
    private void regristryCallAPI(String email, String password, String displayName) {
        // guardo la variable password
        userPassword=password;

        // llamada a la api
        Call<ResponseLogin> call = RetrofitAdapter.getApiService().logUp(email, password, displayName);
        call.enqueue(new ResponseRegisterCallback());

    }


    private void showToastMessage(String message) {
        // devuelvo el valor
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();


    }


    private void registryCorrectActions(ResponseLogin responselogin) {

        // debo guardar los datos del login en el pref
        editPreferences(responselogin);

        // abro otra actividad si todo ha ido bien
        Intent intento=new Intent(this, ListProducts.class);
        intento.putExtra("nombreUsuario",responselogin.getUser().toString());
        intento.putExtra("emailUsuario",responselogin.getUserEmail().toString());

        startActivity(intento);
    }

    // editar preferencias
    private void editPreferences(ResponseLogin responseLogin) {
      // editas referencias
        SharedPreferences.Editor editor = misPreferencias.edit();
        editor.putString("token",responseLogin.getToken());
        editor.putString("name",responseLogin.getUser());
        editor.putString("password",userPassword);
        editor.putString("email",responseLogin.getUserEmail());
        editor.apply();
    }


    // RESPUESTAS DE LA API
    // se encarga de la respuesta del registro
    private class ResponseRegisterCallback implements Callback<ResponseLogin> {
        @Override
        public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
            // peticion correcta code 200
            if (response.isSuccessful()) {
                if (response.code() == 200) {

                    // obtengo el objeto ResponseLogin
                    ResponseLogin responselogin = response.body();


                    // devuelvo el mensage obtenido
                    String message= responselogin.getMessage().toString();

                    // muestro el mensage
                    showToastMessage(message);

                    // registro en los preference y abro nueva actividad
                    registryCorrectActions(responselogin);



                }
            } else {
                // si no se ha podido logear da fallo code != 200

                // obtengo el cuerpo del error

                try {
                    // obtengo el cuerpo del error

                    String errorBody = response.errorBody().string();

                    //parseo el errorBody para obtener el campo message del servidor
                    Gson gson=new Gson();

                    JsonObject jsonObject = gson.fromJson(errorBody, JsonObject.class);
                    if (jsonObject!=null && jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                        String messageError=  jsonObject.get("message").getAsString();

                        // muestro un toast con el error
                        showToastMessage(messageError);
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        @Override
        public void onFailure(Call<ResponseLogin> call, Throwable t) {

              showToastMessage("Error en el servicio");

        }
    }
}
