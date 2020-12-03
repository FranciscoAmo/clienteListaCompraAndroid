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


public class ListProducts extends AppCompatActivity implements ListView.OnItemClickListener {

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


    // obtengo los valores de la lista
    private void obtenerValoresLista() {


        // llamo a la api
        Call<ListProduct.BaseList> call = RetrofitAdapter.getApiService().getallList("Bearer " + token);
        call.enqueue(new ResponseListCallback());
    }


  // establezco listener de botones
    public void establecerAccionesBotones(){
        // boton flotante
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(),"apretado boton fab",Toast.LENGTH_SHORT).show();
                /*
                // inicio la actividad de crear una cuenta
                Intent intent=new Intent(getApplicationContext(),CrearNuevaCuenta.class);
                startActivityForResult(intent,INTENTO_NUEVO_CLIENTE);

                 */

            }
        });

    }




    // devuelvo el resultado
    private void devolverResultado(List<ListProduct.List> listas) {

        // lo añado al arraylist
        Lista = (ListView) findViewById(R.id.Lista);
        for (ListProduct.List lista : listas) {
            listaToShow.add(lista);
        }


        // añado el edaptado personalizado
        AdaptadorPersonalizado adap_personalizado = new AdaptadorPersonalizado(getApplicationContext(), listaToShow);

        Lista.setAdapter(adap_personalizado);
        Lista.setOnItemClickListener(this);

       // return Lista;
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


                    // muestro un toast de que se han logeado correctamente
                    devolverResultado(listas);


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


}