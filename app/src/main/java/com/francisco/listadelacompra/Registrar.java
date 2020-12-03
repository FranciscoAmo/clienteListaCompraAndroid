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
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registrar extends AppCompatActivity {

    // preferencias
    SharedPreferences misPreferencias;

    public TextView emailReg;
    public TextView passwordReg;
    public TextView displayNameReg;

    public Button entrarReg;




    private static String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        // obtengo una instancia de mis preferencias
        misPreferencias = getSharedPreferences("preferences", MODE_PRIVATE);


        // inicio las vistas
        _init();
    }



    public void _init(){

        emailReg=(TextView)findViewById(R.id.emailReg);
        passwordReg=(TextView)findViewById(R.id.passwordReg);
        displayNameReg=(TextView)findViewById(R.id.displayNameReg);

        entrarReg=(Button) findViewById(R.id.entrarReg);
    }




    public void onclick(View view){


        // obtengo el valor de los campos
        String email= this.emailReg.getText().toString();
        String password=this.passwordReg.getText().toString();
        String displayName=this.displayNameReg.getText().toString();

        // LLAMADA AL METODO PARA REGISTRASE
      registrase(email,password,displayName);


    }

    // llamo metodo para registrase
    private void registrase(String email, String password, String displayName) {
        // guardo la variable password
        userPassword=password;

        // llamada a la api
        Call<ResponseLogin> call = RetrofitAdapter.getApiService().logUp(email, password, displayName);
        call.enqueue(new ResponseRegisterCallback());

    }


    private void devolverResultado(String message) {
        // devuelvo el valor
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();


    }


    private void registrarsecorrectamente(ResponseLogin responselogin) {

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
                    devolverResultado(message);

                    // registro en los preference y abro nueva actividad
                    registrarsecorrectamente(responselogin);



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
                        devolverResultado(messageError);
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        @Override
        public void onFailure(Call<ResponseLogin> call, Throwable t) {

              devolverResultado("Error en el servicio");

        }
    }
}
