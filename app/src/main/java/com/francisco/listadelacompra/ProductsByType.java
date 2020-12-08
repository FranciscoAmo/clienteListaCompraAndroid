package com.francisco.listadelacompra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.francisco.listadelacompra.adapters.GridAdapter;
import com.francisco.listadelacompra.dialogs.DialogSelectQuantity;
import com.francisco.listadelacompra.models.ProductosBBDD;
import com.francisco.listadelacompra.retrofitUtils.RetrofitAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ProductsByType extends AppCompatActivity implements DialogSelectQuantity.ListenerQuantityProduct{

    // preferencias y obtener token
    private SharedPreferences misPreferencias;
    private String token;

    // valor del intent que se pasa de la anterior con el tipo de productSelected
    private String tipo;


    // objeto que tiene los datos de los productos de la BBD
    private ProductosBBDD.BaseResponse listaProductsByType;
    private ArrayList<ProductosBBDD.Producto> listaproductByTypeObject=new ArrayList<ProductosBBDD.Producto>();
        // inicializo el array de productos
    private ArrayList<String> lista_productos=new ArrayList<String>();

    // vistas
    private GridView gridViewProductsByType ;
    private ImageView imageType;
    private TextView textType;

    private FloatingActionButton fabregresar;

    private ProductosBBDD.Producto productSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_products_by_type);

        // vinculo las vistas
        _init();


        // obtener valores del intento
        getValueFormIntent();


        // leo las preferencias para obtener el token
        leerPreferencias();

        // llamo a al api para obtener los item del tipo
        callAPItoPorducts();

    }

    // inicializo las vistas
    private void _init() {
        fabregresar = (FloatingActionButton)findViewById(R.id.fabregresarproductypes);

        gridViewProductsByType = (GridView)findViewById(R.id.gridViewProductsByType);
        imageType = (ImageView)findViewById(R.id.imageType);
        textType = (TextView)findViewById(R.id.productTypeName);

        fabregresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    // obtener valor del intent
    public void getValueFormIntent(){
      tipo = getIntent().getStringExtra("tipo");

      // seteo la imagen y el texto segun el valor
      textType.setText(tipo.toString());

      // seteo la imagen segun el tipo pasado
      setImage(tipo.toString());

    }

    // seteo la imagen segun el nombre del productSelected del titilo
    public void setImage(String nameProduct){

        int imagen= changeImageType(nameProduct);
        Bitmap bMap = BitmapFactory.decodeResource(getResources(), imagen);


        imageType.setImageBitmap(bMap);

    }


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
            case "condimento":
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
            case "lacteo":
                id = R.mipmap.lacteos;
                break;
            case "legumbre":
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





    // METODO QUE MUETRA EL TOAST
    private void showToastMessage(String message) {
        // devuelvo el valor
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();


    }


    // preferencias
    // lee las preferencias por si ya esta logeado de antes y guardo el token
    private void leerPreferencias() {

        misPreferencias = getSharedPreferences("preferences", MODE_PRIVATE);
        token = misPreferencias.getString("token", "");


    }



  // relleno el arrylist con la respuesta
    private void fillArrayListOfProducts(ProductosBBDD.BaseResponse listaProductsByType) {
        // obtengo la lista de la respuesta
        List<ProductosBBDD.Producto> listaObtenida= listaProductsByType.getProducto();
        // transformo la lista en un array de productos y lo meto en el arraylist de nombres
        int position=0;
        for (ProductosBBDD.Producto productos : listaObtenida ) {
            lista_productos.add(productos.getName().toString());
            listaproductByTypeObject.add(productos);
            position++;
        }

        setGridView();

    }

    // seteo el gridview
    public void setGridView(){
        //vinculo el adaptador
        gridViewProductsByType.setAdapter(new GridAdapter(this,lista_productos));
        // creo el boton de acccion
        gridViewProductsByType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // OBTENGO VALORES
                // abro el dialogo
                // lo instancio
                DialogSelectQuantity dialogocantidad = new DialogSelectQuantity();
                // creo en bundle
                Bundle args = new Bundle();
                //relleno los valores
                //obtengo el productSelected
                productSelected = listaproductByTypeObject.get(position);

                // envio los valores por el dialogo
                args.putString("nombreProducto",listaproductByTypeObject.get(position).getName().toString());
                args.putString("tipo",listaproductByTypeObject.get(position).getTipo().toString());
                args.putString("unidades",listaproductByTypeObject.get(position).getMed().toString());
                // los añado al bundle y los envio por el dialogo
                dialogocantidad.setArguments(args);

                // muestro el dialog
                dialogocantidad.show(getSupportFragmentManager(),"dialogoSetCantidad");
                // recogo el valor en el metodo de la interface ListernerName.sendQuantity();



            }
        });



    }

    // respuesta del dialog
    @Override
    public void sendQuantity(int cantidad) {

        // SI SE SELECCIONA ALGO SE CREA OTRO INTENT PARA VORLVER A LA APLICACON Y SE ENVIA UN EXTRA CON UN CODIGO
        // EN ESTE CASO SELECCIONADO CON EL ITEM DE LA LISTA
        Intent i = new Intent();
        i.putExtra("cantidad",cantidad);
        i.putExtra("product", productSelected);
        // ESTE SET SESULT INDICA COMO ACABO LA ACTIVIDAD CON rESULT_OK O rESULT_CANCELED Y EL INTENT
        setResult(RESULT_OK, i);
        // TERMINAMOS LA APLICACION
        finish();

        //Toast.makeText(getApplicationContext(),"seleccionada cantidad: "+ cantidad+ " del productSelected " +listaproductByTypeObject.get(position),Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(),"no se ha seleccionado nada",Toast.LENGTH_SHORT).show();
        Intent i=new Intent();
        setResult(RESULT_CANCELED,i);

    }


    // llamada a la api
    public void callAPItoPorducts(){
        Call<ProductosBBDD.BaseResponse> call = RetrofitAdapter.getApiService().getproductsByType("Bearer "+token,"tipo",tipo);
        call.enqueue(new ResponseListOfProductByTypeCallback());
    }




    // calse que se encarga de realizar la llamada asincrona y obtener la respuesta
    private class ResponseListOfProductByTypeCallback implements retrofit2.Callback<ProductosBBDD.BaseResponse> {
        @Override
        public void onResponse(Call<ProductosBBDD.BaseResponse> call, Response<ProductosBBDD.BaseResponse> response) {
            if(response.isSuccessful()){
                // obtengo los productos
                listaProductsByType = response.body();

                // lleno el arraylist de productos
                fillArrayListOfProducts(listaProductsByType);

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
                        // si se a detenido por la autentidicacion vamos al login
                        if(messageError.equals("No tienes autorizacion")|| messageError.equals("tiempo expirado vulevete a loggear")){
                            // hago que vaya al login
                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }



                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        @Override
        public void onFailure(Call<ProductosBBDD.BaseResponse> call, Throwable t) {
        showToastMessage("ERROR EN EL SERVIDOR");
        }
    }


}
