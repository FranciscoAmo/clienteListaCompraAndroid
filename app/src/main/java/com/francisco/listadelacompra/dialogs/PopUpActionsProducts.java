package com.francisco.listadelacompra.dialogs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.francisco.listadelacompra.MainActivity;
import com.francisco.listadelacompra.R;
import com.francisco.listadelacompra.models.ListProduct;
import com.francisco.listadelacompra.models.ResponseMessageStandar;
import com.francisco.listadelacompra.retrofitUtils.RetrofitAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class PopUpActionsProducts extends AppCompatActivity implements DialogSelectQuantity.ListenerQuantityProduct{

    private DisplayMetrics medidaVentana;


    private LinearLayout seccionEliminar;
    private LinearLayout seccionCambiarCantidad;


    private TextView producto;
    private ImageView imagenTipo;



    // valores pasados por el intent con la lista y el id del producto seleccionado
    // variable producto
    ListProduct.Product productoSelected;
    // id de la Lista
    private String idLista;


    // para leer los valores de las preferencias
    private SharedPreferences misPreferencias;

    //para guardar el valor del token para las llamadas
    private String token;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_popup_action_products);

        // tamaño de la actividad

       displayActivityPixel();

       // vinculo las vistas al template
       _init();

        // leo las prefenciaas para obtener el token
        leerPreferencias();

       // obtengo los valores del intent
        getIntentExtras();

        // coloco la imagen y el nombre del producto
        setViews();
    }


    // inicio las vistas
    private void _init() {
        seccionEliminar = (LinearLayout)findViewById(R.id.seccionEliminar);
        seccionCambiarCantidad = ( LinearLayout)findViewById(R.id.seccionCambiarCantidad);


        producto = (TextView)findViewById(R.id.nombreProducto);
        imagenTipo = ( ImageView)findViewById(R.id.imagenDialogTipo);


        // escuchadores de las secciones de las vistas
        // eliminar producto
        seccionEliminar.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View v) {


            // realizo la llamada a la API PARA ELIMINAR
                removeproduct("name",productoSelected.getProduct().getName());


            }
        });
        // cambiar cantidad abre un dialogo personalizado
        seccionCambiarCantidad.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                // OBTENGO VALORES
                // abro el dialogo
                // lo instancio
                DialogSelectQuantity dialogocantidad = new DialogSelectQuantity();
               // creo en bundle
                Bundle args = new Bundle();
                //relleno los valores
                args.putString("nombreProducto",productoSelected.getProduct().getName().toString());
                args.putString("tipo",productoSelected.getProduct().getTipo().toString());
                args.putString("unidades",productoSelected.getProduct().getMed().toString());
                // los añado al bundle y los envio por el dialogo
                dialogocantidad.setArguments(args);

                // muestro el dialog
                dialogocantidad.show(getSupportFragmentManager(),"dialogoSetCantidad");
                // recogo el valor en el metodo de la interface ListernerName.sendQuantity();




            }
        });

    }


    // preferencias
    // lee las preferencias por si ya esta logeado de antes y guardo el token
    private void leerPreferencias() {

        misPreferencias = getSharedPreferences("preferences", MODE_PRIVATE);
        token = misPreferencias.getString("token", "");


    }






    // establece tamaño de la actividad
    private void displayActivityPixel(){
         medidaVentana = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidaVentana);

        // ancho y alto
        int alto = medidaVentana.heightPixels;
        int ancho = medidaVentana.widthPixels;

        getWindow().setLayout((int)(ancho*0.70),(int)(alto*0.5));


    }

    //obtengo valores del Intent
    private void getIntentExtras(){

         productoSelected = (ListProduct.Product) getIntent().getSerializableExtra("seleccionadaLista");
         idLista = (String)getIntent().getStringExtra("idLista");



    }

    // seteo los valores de la vista
    private void setViews(){

        producto.setText(productoSelected.getProduct().getName().toString());

        // seteo la imagen principal
         int imagen  = changeImageType(productoSelected.getProduct().getTipo().toString());
        Bitmap bMap = BitmapFactory.decodeResource(getResources(), imagen);


        imagenTipo.setImageBitmap(bMap);
    }


    // respuesta  del dialog con el valor devuelto
    @Override
    public void sendQuantity(int cantidad) {

        //con el valor llamo a la API
            changeValueQuantity("name", productoSelected.getProduct().getName().toString() , cantidad);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    // LLAMADAS A LAS APIS
    public void removeproduct(String key, String value){

        Call<ResponseMessageStandar> call = RetrofitAdapter.getApiService().removeProductFormList("Bearer "+token, idLista, key, value);
        call.enqueue(new ResponseStandarResponseCallBack());
    }

    public void changeValueQuantity(String key, String value,int quantity){
        Call<ResponseMessageStandar> call = RetrofitAdapter.getApiService().updateQuantity("Bearer "+token, idLista,quantity, key, value);
        call.enqueue(new ResponseStandarResponseCallBack());

    }


    // METODO QUE MUETRA EL TOAST
    private void showToastMessage(String message) {
        // devuelvo el valor
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();


    }

    // setea la imagen principal
    private int changeImageType(String typeProduct){
        int id=0;
        switch(typeProduct){
            case "aperitivo":
                id = R.mipmap.aperitivo;
                break;

            case "bebida":
                id = R.mipmap.bebida;
                break;
            case "carne":
                id = R.mipmap.carne;
                break;
            case "condimentos":
                id = R.mipmap.condimentos;
                break;
            case "detergente":
                id = R.mipmap.detergente;
                break;
            case "fruta":
                id = R.mipmap.fruta;
                break;
            case "helado":
                id = R.mipmap.helado;
                break;
            case "higiene":
                id = R.mipmap.higiene;
                break;
            case "lacteos":
                id = R.mipmap.lacteos;
                break;
            case "legumbres":
                id = R.mipmap.legumbres;
                break;
            case "limpieza":
                id = R.mipmap.limpieza;
                break;
            case "pescado":
                id = R.mipmap.pescado;
                break;
            case "verdura":
                id = R.mipmap.vegetales;
                break;
            default:
                id = R.mipmap.carritolleno;
        }

        return id;
    }

    // CLASES DE LA RESPUESTA DE LAS LLAMADAS
    // Implementacion de la clase
    private class ResponseStandarResponseCallBack implements retrofit2.Callback<ResponseMessageStandar> {
        @Override
        public void onResponse(Call<ResponseMessageStandar> call, Response<ResponseMessageStandar> response) {

            if (response.isSuccessful()) {

                // si no se ha podido logear da fallo code != 201
                if (response.code() == 201 || response.code()==200) {
                    String mensage = response.body().getMessage().toString();
                    boolean succes = response.body().isSuccess();
                    // respuesta correcta muestro un toas
                    showToastMessage(mensage);

                    // respuesta correcta a renuevo la pagina

                    finish();

                }else{
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

                            if(messageError.equals("No tienes autorizacion")|| messageError.equals("tiempo expirado vulevete a loggear")){

                                // si ha fallado porque no esta autentificado entonces vuelvo a la pantalla de login
                                // hago que vaya al login
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }

        @Override
        public void onFailure(Call<ResponseMessageStandar> call, Throwable t) {

            // respuesta correcta muestro un toas
            showToastMessage("Error en la conexion");

        }
    }


}
