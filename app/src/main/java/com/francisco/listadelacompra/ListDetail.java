package com.francisco.listadelacompra;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.francisco.listadelacompra.models.ListProduct;
import com.francisco.listadelacompra.retrofitUtils.RetrofitAdapter;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ListDetail extends AppCompatActivity {

    // listas para los adaptadores
        // lista de los productos con cantidad y un objeto producto
    private ArrayList<ListProduct.Product> lista_productos=new ArrayList<ListProduct.Product>();

    private ArrayList<ListProduct.AssociatedUser> lista_usuarios=new ArrayList<ListProduct.AssociatedUser>();


    // lista selecionada
    ListProduct.List listacogida= null;

    // clases de la vista principal
    private TextView nombreLista;
    private ListView ListUsersDetail;
    private ListView ListProductDetail;

    // clases de la vista generica

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


    private String token;

    private SharedPreferences misPreferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);

        // vinculo las vistas del layout principal
        _init();

        // si la lista se obtine de la actividad ListProducts la coge si no la pido
        if(getIntent().hasExtra("lista")) {
            listacogida = (ListProduct.List) getIntent().getSerializableExtra("lista");
            Log.i("Intent","esto enevio"+listacogida.getAssociatedUsers().get(0).getDisplayName());
            // lleno el arryList de productos lo paso de List a ArrayList
           lista_productos=pasarAArrayList(listacogida.getProducts());

           // lleno el arrayList de usuarios
            lista_usuarios= (ArrayList<ListProduct.AssociatedUser>) pasarAArrayList(listacogida.getAssociatedUsers());
           // Log.i("Intent","Email"+lista_usuarios.get(0).getEmail().toString());



            // añado el edaptado personalizado
            AdaptadorDetailUsersPersonalizado adap_personalizadoUsers = new AdaptadorDetailUsersPersonalizado(getApplicationContext(), lista_usuarios);

            ListUsersDetail.setAdapter(adap_personalizadoUsers);



            // añado el edaptado personalizado
            AdaptadorDetailProductsPersonalizado adap_personalizadoProducts = new AdaptadorDetailProductsPersonalizado(getApplicationContext(), lista_productos);

            ListProductDetail.setAdapter(adap_personalizadoProducts);


        }else{
            // si se navega desde añadir productos  hago una peticion a la api

        }

        asignar_valores();


        leerPreferencias();

    }

    // inicailizo vistas
    private void _init() {
        // vinculo vistas
        nombreLista=(TextView)findViewById(R.id.nameListDetail);


        // para mostrar el listado de usuarios con el adpatador
        ListUsersDetail=(ListView)findViewById(R.id.ListDetailUsers);

        // para mostrar el listado de Productos con el adpatador
        ListProductDetail=(ListView)findViewById(R.id.ListDetailProducts);

    }

    // preferencias
    // lee las preferencias por si ya esta logeado de antes y guardo el token
    private void leerPreferencias() {

        misPreferencias = getSharedPreferences("preferences", MODE_PRIVATE);
        token = misPreferencias.getString("token", "");


    }




    // toolbar

    // muestra el menu_login action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflate=getMenuInflater();
        inflate.inflate(R.menu.menu_products_list,menu);
        return super.onCreateOptionsMenu(menu);
    }


// selecciona la opcion del menu_login

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.exitList:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("SALIR DE LA LISTA");
                builder.setMessage("¿Seguro que quieres salir de la lista?");

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("BORRADO",listacogida.getNameList().toString());
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

        }
        return super.onOptionsItemSelected(item);
    }


    // CALLERS
    public void eliminarLista(String listid){
        Call<ListProduct.List> call = RetrofitAdapter.getApiService().removefromList("Bearer " + token,listid);
        Log.i("nombre lista a eliminar:",listid);
        call.enqueue(new ResponseRemoveFromListCallback());

    }

    // metodo que sale de la actividad
    private void exitActivity() {
        Toast.makeText(getApplicationContext(),"eliminada la lista",Toast.LENGTH_SHORT).show();


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





    // clase adaptador para los los usuarios
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
            View listasUsersAdapter = inflater.inflate(R.layout.list_detail_user_adapter, null);


            // ojo lo debo buscar a partir del view del adaptador no del principal en este caso "luchadores"
            nameUserdetail = (TextView) listasUsersAdapter.findViewById(R.id.nameUserdetail);
            nameUserdetail.setText(lista_usuarios.get(position).getDisplayName().toString());



            emailUserdetail = (TextView) listasUsersAdapter.findViewById(R.id.emailUserDetail);
            emailUserdetail.setText((lista_usuarios.get(position).getEmail().toString()));


            return listasUsersAdapter;
        }


    }



    // clase adaptador para los Productos
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
            View listasProductsAdapter = inflater.inflate(R.layout.list_products_detail_adapter, null);


            // ojo lo debo buscar a partir del view del adaptador no del principal en este caso "lista"

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

            return listasProductsAdapter;
        }


    }


    // class Callback que elimina usuarios de las listas
    private class ResponseRemoveFromListCallback implements retrofit2.Callback<ListProduct.List> {
        @Override
        public void onResponse(Call<ListProduct.List> call, Response<ListProduct.List> response) {

            // peticion correcta code 200
            if (response.isSuccessful()) {

                // si no se ha podido logear da fallo code != 201
                if (response.code() == 200) {

                    exitActivity();


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


}
