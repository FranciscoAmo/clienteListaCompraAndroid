package com.francisco.listadelacompra;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.francisco.listadelacompra.dialogs.DialogoNuevaLista;
import com.francisco.listadelacompra.models.ListProduct;
import com.francisco.listadelacompra.retrofitUtils.RetrofitAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListProducts extends AppCompatActivity implements ListView.OnItemClickListener ,DialogoNuevaLista.ListenerNameList{

    // clases de las vistas
    TextView Nombre;
    TextView email;

    FloatingActionButton fab;

    private ListView Lista;


    // clases de las vistas del adaptador
    private TextView nombreListaadpatdor;
    private TextView cantidadUsuarioadaptador;
    private TextView cantidadProductosadaptador;


    private ArrayList<ListProduct.List> listaToShow = new ArrayList<ListProduct.List>();

    private SharedPreferences misPreferencias;

    private String currentUseremail;
    private String currentDisplayName;

    private static String token;

    // clase del adpatdor personalizado del listview
    AdaptadorPersonalizado adap_personalizado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiry_list_products_content);


        // obtengo instancia de las preferencias
        misPreferencias = getSharedPreferences("preferences", MODE_PRIVATE);

        // obtengo los valores del intent
        currentUseremail = getIntent().getStringExtra("emailUsuario");
        currentDisplayName = getIntent().getStringExtra("nombreUsuario");


        // inicio la vista
        _init();

        //leo y guardo el token de las preferencias
        leerPreferencias();

        // obtengo los valores de las listas
        obtenerValoresLista();

        // establezco acciones de los botones
        establecerAccionesBotones();
    }

    // VINCUALCION DE VISTAS
    public void _init() {

        fab = (FloatingActionButton) findViewById(R.id.fab);

        Nombre = (TextView) findViewById(R.id.usuario);
        email = (TextView) findViewById(R.id.email);

        // introduzco valores en los campos
        Nombre.setText(currentDisplayName);
        email.setText(currentUseremail);
    }


    // lee las preferencias por si ya esta logeado de antes y guardo el token
    private void leerPreferencias() {

        misPreferencias = getSharedPreferences("preferences", MODE_PRIVATE);
        token = misPreferencias.getString("token", "");


    }




    // BOTON Y LLAMADA A DIALOGOS
  // establezco listener de botones
    public void establecerAccionesBotones(){
        // boton flotante
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // abro el dialogo
                DialogoNuevaLista dialogo=new DialogoNuevaLista();
                dialogo.show(getSupportFragmentManager(),"dialogoNuevaLista");
                // recogo el valor en el metodo de la interface ListernerName.sendNameList();


            }
        });

    }

    // recogo el valor del nombre de la lista y hago la peticion al servidor para crearlo
    @Override
    public void sendNameList(String nombre) {
         createList(nombre);
    }

    // LLAMADAs A LOS CALLBACKS

    // obtengo los valores de la lista
    private void obtenerValoresLista() {

        // llamo a la api
        Call<ListProduct.BaseList> call = RetrofitAdapter.getApiService().getallList("Bearer " + token);
        call.enqueue(new ResponseListCallback());
    }

    // peticion de creacion de listaCon el nombre indicado
    public void createList(String nombre){
        Call<ListProduct.List> call = RetrofitAdapter.getApiService().createList("Bearer " + token,nombre);
        call.enqueue(new ResponseCreateListCallback());

    }


    @Override
    protected void onResume() {
        super.onResume();
        obtenerValoresLista();

    }

    // metodo para ejecutar acciones segun la lista que se seleccione
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // obtengo el objeto Lista

        ListProduct.List listaselecionada= listaToShow.get(position);

        // muestro un toast
        Toast.makeText(this, "selecionada la lista " + listaToShow.get(position).getNameList().toString(), Toast.LENGTH_SHORT).show();

        // abro otra actividad con el detalle de la lista
        Intent i = new Intent(this, ListDetail.class);
        i.putExtra("lista",listaselecionada);

        startActivity(i);

    }






            // ADAPTADOR PERSONALIZADO PARA MOSTRAR EL LISTADO

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




    //RETORNOS DE LOS CALLBACKS

    // devuelvo el resultado de todas las listas
    private void returnAllListFormUsers(List<ListProduct.List> listas) {

        // lo añado al arraylist
        Lista = (ListView) findViewById(R.id.Lista);
        for (ListProduct.List lista : listas) {
            listaToShow.add(lista);
        }


        // añado el edaptado personalizado
        adap_personalizado = new AdaptadorPersonalizado(getApplicationContext(), listaToShow);
        Lista.setAdapter(adap_personalizado);
        Lista.setOnItemClickListener(this);


    }


    // respuesta al crear una lista

    // metodo que devuelve la lista
    private void returnListCreated(ListProduct.List lista) {
        // la añado al arrayList de la vista
        listaToShow.add(lista);
        adap_personalizado.notifyDataSetChanged();


    }






    //CLASES CALLBACK DE LAS LLAMADAS A LA API


        // clase que devuelve si se ha creado o no la lista
    private class ResponseCreateListCallback implements Callback<ListProduct.List> {
            @Override
            public void onResponse(Call<ListProduct.List> call, Response<ListProduct.List> response) {
                // peticion correcta code 200
                if (response.isSuccessful()) {

                    // si no se ha podido logear da fallo code != 201
                    if (response.code() == 201) {

                         obtenerValoresLista();
                  } else {

                        try {
                            // obtengo el cuerpo del error

                            String errorBody = response.errorBody().string();

                            //parseo el errorBody para obtener el campo message del servidor
                            Gson gson = new Gson();

                   } catch (IOException e) {
                            e.printStackTrace();
                        }
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
                if (response.code() == 200) {
                    // obtengo la respuesta y la parseo a una clase java clase list
                    ListProduct.BaseList baseList = response.body();

                    // obtengo las listas del producto del usuario
                    List<ListProduct.List> listas = baseList.getList();


                    // devuelvo valores a ListView
                    returnAllListFormUsers(listas);


                }
            } else {
                // si no se ha podido logear da fallo code != 200


                try {
                    // obtengo el cuerpo del error

                    String errorBody = response.errorBody().string();

                    //parseo el errorBody para obtener el campo message del servidor
                    Gson gson = new Gson();
/*
                    JsonObject jsonObject = gson.fromJson(errorBody, JsonObject.class);
                    if (jsonObject!=null && jsonObject.has("message") && !jsonObject.get("message").isJsonNull()) {
                        String messageError=  jsonObject.get("message").getAsString();

                        // muestro un toast con el error
                        devolverResultado(messageError);
                    }
*/


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