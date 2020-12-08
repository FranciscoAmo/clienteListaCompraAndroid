package com.francisco.listadelacompra;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.francisco.listadelacompra.dialogs.DialogAddUserToList;
import com.francisco.listadelacompra.dialogs.PopUpActionsProducts;
import com.francisco.listadelacompra.models.ListProduct;
import com.francisco.listadelacompra.models.ProductosBBDD;
import com.francisco.listadelacompra.models.ResponseMessageStandar;
import com.francisco.listadelacompra.retrofitUtils.RetrofitAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ListDetail extends AppCompatActivity implements DialogAddUserToList.ListenerAddUser, AdapterView.OnItemClickListener,FloatingActionButton.OnClickListener {

   // lista de productos para el adaptador
    private ArrayList<ListProduct.Product> lista_productos=new ArrayList<ListProduct.Product>();
   // arrayList de usuarios para el adaptador
    private ArrayList<ListProduct.AssociatedUser> lista_usuarios=new ArrayList<ListProduct.AssociatedUser>();


    AdaptadorDetailUsersPersonalizado adap_personalizadoUsers;
    AdaptadorDetailProductsPersonalizado adap_personalizadoProducts;

    // lista selecionada
    ListProduct.List listacogida= null;

    // clases de la vista principal
    private TextView nombreLista;
    private ListView ListUsersDetail;
    private ListView ListProductDetail;

    // VISTAS

                // clases de los usuarios
            private TextView nameUserdetail;
            private TextView emailUserdetail;
                // clases de los productos
            private TextView nameProductdetail;
            private TextView typeProductdetail;
            private TextView cantidadProductdetail;
            private TextView unidadProductdetail;
            private TextView precioProductdetail;
            private TextView totalProductdetail;
            private TextView cantidadProductosDistintos;
            private TextView cantidadUsuarios;
            private ImageView imagenTipodetail;


                // botones fab
            private FloatingActionButton fabregresar;
            private FloatingActionButton fabaddproduct;
            private FloatingActionButton fabaddUser;
            private FloatingActionButton fabeliminarlista;


    //valor de token para realizar las llamadas
    private String token;

    // preferencias
    private SharedPreferences misPreferencias;

    // codigo para devolver el resultado de añadir un producto
    private final int ADD_PRODUCT_CODE =200;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_activity_list_detail);

        // vinculo las vistas del layout principal
        _init();
        // obtengo los datos
        getData();

        // asigno los valores a las vistas
        asignar_valores();

        // leo las preferencias para realizar llamadas
        readPreferences();

    }



    // INICIALIZO VISTAS
    private void _init() {

        // floating Buttons
        fabregresar =(FloatingActionButton)findViewById(R.id.fabregresardetail);
        fabaddproduct=(FloatingActionButton)findViewById(R.id.fabaddproduct);
        fabaddUser = ( FloatingActionButton)findViewById(R.id.fabadduser);
        fabeliminarlista =(FloatingActionButton)findViewById(R.id.fabeliminarlist);

        fabaddUser.setOnClickListener(this);
        fabaddproduct.setOnClickListener(this);
        fabeliminarlista.setOnClickListener(this);
        fabregresar.setOnClickListener(this);



        // vinculo vistas
        nombreLista=(TextView)findViewById(R.id.nameListDetail);


        // para mostrar el listado de usuarios con el adpatador
        ListUsersDetail=(ListView)findViewById(R.id.ListDetailUsers);

        // para mostrar el listado de Productos con el adpatador
        ListProductDetail=(ListView)findViewById(R.id.ListDetailProducts);

        // paramostrar la cantidad de usuarios y de productos

        cantidadProductosDistintos = (TextView)findViewById(R.id.quantityOfProducts);
        cantidadUsuarios = (TextView)findViewById(R.id.quantityOfUsers);

    }

    // preferencias
    // lee las preferencias por si ya esta logeado de antes y guardo el token
    private void readPreferences() {

        misPreferencias = getSharedPreferences("preferences", MODE_PRIVATE);
        token = misPreferencias.getString("token", "");


    }

    @Override
    protected void onResume() {
        super.onResume();
        // respuesta correcta a renuevo la pagina
        getList(listacogida.getId());
    }


    // METODO QUE MUETRA EL TOAST
    private void showToastMessage(String message) {
        // devuelvo el valor
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();


    }



// OBTENER LOS DATOS DEL INTENT LA PRIMERA VEZ O DE LA API SI SE NAVEGA HACIA ATRAS


    public void getData(){

        // si la lista se obtine de la actividad ListProducts la coge si no la pido

            listacogida = (ListProduct.List) getIntent().getSerializableExtra("lista");

            setAdapters();

    }



    // ADAPTADORES
    public void setAdapters(){

        // lleno el arryList de productos lo paso de List a ArrayList
        lista_productos=pasarAArrayList(listacogida.getProducts());

        // lleno el arrayList de usuarios
        lista_usuarios= (ArrayList<ListProduct.AssociatedUser>) pasarAArrayList(listacogida.getAssociatedUsers());



        // adaptador usuarios
        adap_personalizadoUsers = new AdaptadorDetailUsersPersonalizado(getApplicationContext(), lista_usuarios);

        ListUsersDetail.setAdapter(adap_personalizadoUsers);


        // aadaptador productos
         adap_personalizadoProducts = new AdaptadorDetailProductsPersonalizado(getApplicationContext(), lista_productos);

        ListProductDetail.setAdapter(adap_personalizadoProducts);

        ListProductDetail.setOnItemClickListener(this);

        // coloco los valores en la cantidad de productos y de usuarios

        cantidadProductosDistintos.setText("Cantidad: "+listacogida.getProducts().size());
        cantidadUsuarios.setText("cantidad: " +listacogida.getAssociatedUsers().size());


    }





// segun el  producto que se seleccione


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ListProduct.Product producto = lista_productos.get(position); // obtengo el producto seleccionado

        Intent i = new Intent(this, PopUpActionsProducts.class);

        // paso los la lista
        i.putExtra("seleccionadaLista",lista_productos.get(position));
        i.putExtra("idLista",listacogida.getId());

        startActivity(i);


    }






    // implementacion de la respuesta del dialogo llamo a Retrofit para realizar llamad a la api
    @Override
    public void sendUsertoAdd(String email) {

        addUsertoList(email, listacogida.getId().toString());
    }






    // CALLERS
    public void eliminarLista(String listid){
        Call<ListProduct.List> call = RetrofitAdapter.getApiService().removefromList("Bearer " + token,listid);

        call.enqueue(new ResponseRemoveFromListCallback());

    }

    // llamada para añadir usuario a la lista
    public void addUsertoList(String email ,String listid){

        Call<ResponseMessageStandar> call = RetrofitAdapter.getApiService().addUser("Bearer "+ token, listid, email );
        call.enqueue(new ResponseAddUserToListCallback());
    }


    public void addProducttoList(String key, String value,int quantity){
        boolean exist=false;
        for(ListProduct.Product product: lista_productos){
            if(product.getProduct().getName().toString().equals(value)) {
                exist=true;
                break;
            }
        }

        if(!exist) {
            Call<ResponseMessageStandar> call = RetrofitAdapter.getApiService().addProduct("Bearer " + token, listacogida.getId(), quantity, key, value);
            call.enqueue(new ResponseAddProductCallBack());

        }else{
            showToastMessage("Ya existe el producto en la lista");
        }
    }

    public void getList(String listid){
        Call<ListProduct.BaseList> call = RetrofitAdapter.getApiService().getOneList("Bearer " + token, listid);
        call.enqueue(new RenewCallBack());

    }



    // metodo que sale de la actividad
    private void exitActivity() {
       showToastMessage("eliminandola lista");

        this.finish();
    }




    // asigno valores a la vista principal
    private void asignar_valores(){

        // establezco nombres en las listas
        nombreLista.setText(listacogida.getNameList().toString());


    }

        // transmorma una Lista de Objetos en un arry de Objetos para el edaptador
    private <T> ArrayList<T> pasarAArrayList(List<T> lista){
        ArrayList<T> listaarraylistdevuelta= new ArrayList<T>();

        for ( T item  :lista ) {
            listaarraylistdevuelta.add(item);
        }

        return listaarraylistdevuelta;
    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();
        switch (idView){
            case R.id.fabadduser:

                // abro el dialogo
                DialogAddUserToList dialogo=new DialogAddUserToList();
                dialogo.show(getSupportFragmentManager(),"dialogoAñadirUsuario");
                // recogo el valor en el metodo de la interface Listerneruser.sendUserName();
                break;

            case R.id.fabregresardetail:
                finish();
                break;

            case R.id.fabeliminarlist:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("SALIR DE LA LISTA");
                builder.setMessage("¿Seguro que quieres salir de la lista?");

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        eliminarLista(listacogida.getId().toString());

                    }
                }) ;

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                break;
            case R.id.fabaddproduct:
                // inicio la actividad de selecionar tipos

                Intent intentType = new Intent(getApplicationContext(),TypeProducts.class);


                startActivityForResult(intentType,ADD_PRODUCT_CODE);

                break;

        }

    }


    // ADAPTADORES

    // ADAPTADOR USUARIOS
    public class AdaptadorDetailUsersPersonalizado extends ArrayAdapter {


        public AdaptadorDetailUsersPersonalizado(@NonNull Context context, @NonNull ArrayList lista) {
            super(context, R.layout.adapter_users_list, lista_usuarios);
            context = getContext();
            lista = lista_usuarios;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View listasUsersAdapter = inflater.inflate(R.layout.adapter_list_detail_user, null);


            // ojo lo debo buscar a partir del view del adaptador no del principal en este caso "luchadores"
            nameUserdetail = (TextView) listasUsersAdapter.findViewById(R.id.nameUserdetail);
            nameUserdetail.setText(lista_usuarios.get(position).getDisplayName().toString());



            emailUserdetail = (TextView) listasUsersAdapter.findViewById(R.id.emailUserDetail);
            emailUserdetail.setText((lista_usuarios.get(position).getEmail().toString()));


            return listasUsersAdapter;
        }


    }



    // ADAPTADOR PRODUCTOS
    public class AdaptadorDetailProductsPersonalizado extends ArrayAdapter {


        public AdaptadorDetailProductsPersonalizado(@NonNull Context context, @NonNull ArrayList lista) {
            super(context, R.layout.adapter_users_list, lista_productos);
            context = getContext();
            lista = lista_productos;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View listasProductsAdapter = inflater.inflate(R.layout.adapter_list_products_detail, null);


            // ojo lo debo buscar a partir del view del adaptador no del principal en este caso "lista"
            imagenTipodetail = (ImageView)listasProductsAdapter.findViewById(R.id.imagenTipo);

             nameProductdetail = (TextView) listasProductsAdapter.findViewById(R.id.nameProductdetail);
             typeProductdetail= (TextView) listasProductsAdapter.findViewById(R.id.typeProductDetail);
             cantidadProductdetail= (TextView) listasProductsAdapter.findViewById(R.id.cantidadUnidadDetail);;
             unidadProductdetail= (TextView) listasProductsAdapter.findViewById(R.id.unidadProductDetail);;
             precioProductdetail= (TextView) listasProductsAdapter.findViewById(R.id.precioProductDetail);;
             totalProductdetail= (TextView) listasProductsAdapter.findViewById(R.id.totalProductDetail);;


             // asigno los valores

             nameProductdetail.setText(lista_productos.get(position).getProduct().getName().toString());
             typeProductdetail.setText(lista_productos.get(position).getProduct().getTipo().toString());
             cantidadProductdetail.setText(lista_productos.get(position).getQuantity().toString());
             unidadProductdetail.setText(lista_productos.get(position).getProduct().getMed().toString());
             precioProductdetail.setText(lista_productos.get(position).getProduct().getPrecio().toString());

             int total =lista_productos.get(position).getProduct().getPrecio()*lista_productos.get(position).getQuantity();
             // realizo el calculo del precio * cantidad
             totalProductdetail.setText(total+" € ");

             // seteo la imagen segun el tipo


                int imagen= changeImageType(lista_productos.get(position).getProduct().getTipo().toString());
                 Bitmap bMap = BitmapFactory.decodeResource(getResources(), imagen);


                imagenTipodetail.setImageBitmap(bMap);

            return listasProductsAdapter;
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



    }
    // RESPUESTAS DE La API

    // RESPUESTA DE LA API PARA SALIR DE LA LISTA
    private class ResponseRemoveFromListCallback implements retrofit2.Callback<ListProduct.List> {
        @Override
        public void onResponse(Call<ListProduct.List> call, Response<ListProduct.List> response) {

            // peticion correcta code 200
            if (response.isSuccessful()) {

                // si no se ha podido logear da fallo code != 201
                if (response.code() == 200) {
                    // Salgo de la actividad
                    exitActivity();


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
                            // si se a detenido por la autentidicacion vamos al login
                            if(messageError.equals("No tienes autorizacion")|| messageError.equals("tiempo expirado vulevete a loggear")){
                                // hago que vaya al login
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

        }


        @Override
        public void onFailure(Call<ListProduct.List> call, Throwable t) {
            showToastMessage("ERROR EN EL SERVIDOR");
        }
    }

    // RESPUESTA DE LA API PARA AÑADIR UN USUARIO
    private class ResponseAddUserToListCallback implements retrofit2.Callback<ResponseMessageStandar> {
        @Override
        public void onResponse(Call<ResponseMessageStandar> call, Response<ResponseMessageStandar> response) {
            if(response.isSuccessful()){

                String mensage = response.body().getMessage().toString();
                boolean succes = response.body().isSuccess();
                // respuesta correcta muestro un toast
                showToastMessage(mensage);

                // respuesta correcta a renuevo la pagina
               getList(listacogida.getId());

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
        public void onFailure(Call<ResponseMessageStandar> call, Throwable t) {
                showToastMessage("ERROR EN EL SERVIDOR");
        }
    }





// RESPUESTA A LA API PARA AÑADIR UN PRODUCTO
    public  class ResponseAddProductCallBack implements retrofit2.Callback<ResponseMessageStandar> {
        @Override
        public void onResponse(Call<ResponseMessageStandar> call, Response<ResponseMessageStandar> response) {
            if(response.isSuccessful()) {

                String mensage = response.body().getMessage().toString();

                // respuesta correcta muestro un toas
                showToastMessage(mensage);

                // obtengo la nueva lista
                getList(listacogida.getId().toString());
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
        public void onFailure(Call<ResponseMessageStandar> call, Throwable t) {
            showToastMessage("ERROR EN EL SERVIDOR");
        }
    }

        // RESPUESTA A LA API PARA RENOVAR DATOS
    private class RenewCallBack implements retrofit2.Callback<ListProduct.BaseList> {
        @Override
        public void onResponse(Call<ListProduct.BaseList> call, Response<ListProduct.BaseList> response) {
            if(response.isSuccessful()){
                if(response.code()==200 || response.code()==201){

                    listacogida= (ListProduct.List)response.body().getList().get(0);

                    setAdapters();
                }

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
        public void onFailure(Call<ListProduct.BaseList> call, Throwable t) {
          showToastMessage("ERROR EN EL SERVIDOR");
        }
    }


    // RESPUESTA DE LA ACTIVIDAD SELCIONAR PRODUCTO UNA VEZ REALIZADO
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode== ADD_PRODUCT_CODE){
            if(resultCode==RESULT_OK){
                boolean exist=false;
                // si todo ha ido bien seobtiene el producto que queremos añadir y llamamos a la api para añadirlo

                ProductosBBDD.Producto nombre =(ProductosBBDD.Producto)data.getSerializableExtra("devuelto");
                int cantidad = data.getIntExtra("cantidad",1);

                // busco el producto en la lista
                for( ListProduct.Product product : lista_productos){
                    if(product.getProduct().getName().toString()== nombre.getName().toString()){
                        exist=true;
                        break;
                    }

                }


                if(!exist) {


                    addProducttoList("name", nombre.getName().toString(), cantidad);

                  // añado el producto a la base de datos
                }else{
                    showToastMessage("YA EXISTE EL PRODUCTO EN LA LISTA");
                }

            }
            if(resultCode== RESULT_CANCELED){
                // si se ha cancelado todo no hago nada

            }

        }
    }



}
