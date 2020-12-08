package com.francisco.listadelacompra;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.francisco.listadelacompra.dialogs.DialogNewList;
import com.francisco.listadelacompra.models.ListProduct;
import com.francisco.listadelacompra.retrofitUtils.RetrofitAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListProducts extends AppCompatActivity implements ListView.OnItemClickListener , DialogNewList.ListenerNameList{

    // VISTAS
    TextView Nombre;
    TextView email;

    FloatingActionButton fab;
    FloatingActionButton fabregresar;

    private ListView Lista;


    // VISTAS DEL ADAPTADOR
    private TextView nombreListaadpatdor;
    private TextView cantidadUsuarioadaptador;
    private TextView cantidadProductosadaptador;
    private TextView numeroDeListas;

    // ARRAYLIST DE LAS LISTAS A MOSTRAR
    private ArrayList<ListProduct.List> listaToShow = new ArrayList<ListProduct.List>();

    // PREFERENCIAS
    private SharedPreferences misPreferencias;

    // VARIABLES A GUARDAR LOCALMENTE
    private String currentUseremail;
    private String currentDisplayName;

    // VARIABLE DEL TOKEN PARA REALIZAR LLAMADAS
    private static String token;

    // CLASE DEL ADAPTADOR
    AdaptadorPersonalizado adap_personalizado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_activity_list_products);


        // obtengo instancia de las preferencias
        misPreferencias = getSharedPreferences("preferences", MODE_PRIVATE);

        // obtengo los valores del intent
        currentUseremail = getIntent().getStringExtra("emailUsuario");
        currentDisplayName = getIntent().getStringExtra("nombreUsuario");


        // inicio la vista
        _init();

        //leo y guardo el token de las preferencias
        readPreferences();


        // establezco acciones de los botones
        establisedActionButtons();
    }


    // obtengo los valores de las listas se llama cada vez que se ponga el foco en la actividad incluso en la primera instancia
    @Override
    protected void onResume() {
        super.onResume();
        listaToShow.clear();  // limpio la lista de listas de productos
        returnValuesFromApi();

    }


    // VINCUALCION DE VISTAS
    public void _init() {

        numeroDeListas = ( TextView)findViewById(R.id.numOfLists);

        // botones floating
        // botones superiores

        // boton inferior para
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabregresar = (FloatingActionButton)findViewById(R.id.fabregresarlist);

        Nombre = (TextView) findViewById(R.id.usuario);
        email = (TextView) findViewById(R.id.email);

        // introduzco valores en los campos
        Nombre.setText(currentDisplayName);
        email.setText(currentUseremail);
    }


    // lee las preferencias por si ya esta logeado de antes y guardo el token
    private void readPreferences() {

        misPreferencias = getSharedPreferences("preferences", MODE_PRIVATE);
        token = misPreferencias.getString("token", "");


    }




    // BOTON Y LLAMADA A DIALOGOS
  // establezco listener de botones
    public void establisedActionButtons(){
        // boton flotante
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // abro el dialogo para añadir una nueva lista
                DialogNewList dialogo=new DialogNewList();
                dialogo.show(getSupportFragmentManager(),"dialogoNuevaLista");
                // recogo el valor en el metodo de la interface ListernerName.sendNameList();

            }
        });


        fabregresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });



    }

    // recogo el valor del nombre de la lista y hago la peticion al servidor para crearlo
    @Override
    public void sendNameList(String nombre) {

        listaToShow.clear();
        // llamada a la api
        createList(nombre);
    }



    // LLAMADAS A LAS API

    // obtengo los valores de la lista
    private void returnValuesFromApi() {
        listaToShow.clear();
        // llamo a la api
        Call<ListProduct.BaseList> call = RetrofitAdapter.getApiService().getallList("Bearer " + token);
        call.enqueue(new ResponseListCallback());
    }

    // peticion de creacion de listaCon el nombre indicado
    public void createList(String nombre){
        Call<ListProduct.List> call = RetrofitAdapter.getApiService().createList("Bearer " + token,nombre);
        call.enqueue(new ResponseCreateListCallback());

    }


    // ESTABLECER ACCIONES BOTONES
    // metodo para ejecutar acciones segun la lista que se seleccione
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // obtengo el objeto Lista

        ListProduct.List listaselecionada= listaToShow.get(position);



        // abro otra actividad con el detalle de la lista
        Intent i = new Intent(this, ListDetail.class);
        i.putExtra("lista",listaselecionada);

        startActivity(i);

    }


      // ADAPTADORES

        // clase adaptador dentro de la clase del main debo  extiende dArrayAdapter y se sobreescribe getView
        public class AdaptadorPersonalizado extends ArrayAdapter {


            public AdaptadorPersonalizado(@NonNull Context context, @NonNull ArrayList lista) {
                super(context, R.layout.adapter_users_list, listaToShow);
                context = getContext();
                lista = listaToShow;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                LayoutInflater inflater = getLayoutInflater();
                View listasAdapter = inflater.inflate(R.layout.adapter_users_list, null);


                // ojo lo debo buscar a partir del view del adaptador no del principal en este caso "luchadores"
                nombreListaadpatdor = (TextView) listasAdapter.findViewById(R.id.namelistdapter);
                nombreListaadpatdor.setText(listaToShow.get(position).getNameList().toString());

                cantidadProductosadaptador = (TextView) listasAdapter.findViewById(R.id.numOfProducts);
                cantidadProductosadaptador.setText(listaToShow.get(position).getProducts().size()+"");

                cantidadUsuarioadaptador = (TextView) listasAdapter.findViewById(R.id.numOfUsers);
                cantidadUsuarioadaptador.setText((listaToShow.get(position).getAssociatedUsers().size()+""));


                return listasAdapter;
            }


        }

    //RETORNOS DE LOS valores

    // devuelvo el resultado de todas las listas
    private void returnAllListFormUsers(List<ListProduct.List> listas) {

        // lo añado al arraylist
        Lista = (ListView) findViewById(R.id.Lista);
        for (ListProduct.List lista : listas) {
            listaToShow.add(lista);
        }


        // añado el adaptado personalizado
        adap_personalizado = new AdaptadorPersonalizado(getApplicationContext(), listaToShow);
        Lista.setAdapter(adap_personalizado);
        Lista.setOnItemClickListener(this);

        // establezco el valor del numero de listas
        numeroDeListas.setText(" Numero de listas : "+ listaToShow.size());

    }




    // metodo que devuelve la lista al haber cambios
    private void returnListCreated(ListProduct.List lista) {
        // la añado al arrayList de la vista
        listaToShow.add(lista);
        adap_personalizado.notifyDataSetChanged();


    }


    // METODO QUE MUETRA EL TOAST
    private void showToastMessage(String message) {
        // devuelvo el valor
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();


    }


    //CLASES CALLBACK DE LAS LLAMADAS A LA API


        // clase que devuelve si se ha creado o no la lista
    private class ResponseCreateListCallback implements Callback<ListProduct.List> {
            @Override
            public void onResponse(Call<ListProduct.List> call, Response<ListProduct.List> response) {
                // peticion correcta code 200
                if (response.isSuccessful()) {

                    returnValuesFromApi();

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

            @Override
            public void onFailure(Call<ListProduct.List> call, Throwable t) {

            }
        }









    // clase que devuelve la lista del usuario de la API
    private class ResponseListCallback implements Callback<ListProduct.BaseList> {
        @Override
        public void onResponse(Call<ListProduct.BaseList> call, Response<ListProduct.BaseList> response) {
            // peticion correcta code 200
            if (response.isSuccessful()) {

                    // obtengo la respuesta y la parseo a una clase java clase list
                    ListProduct.BaseList baseList = response.body();

                    // obtengo las listas del producto del usuario
                    List<ListProduct.List> listas = baseList.getList();


                    // devuelvo valores a ListView
                    returnAllListFormUsers(listas);



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

        @Override
        public void onFailure(Call<ListProduct.BaseList> call, Throwable t) {

        }

    }
}