package com.francisco.listadelacompra;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.francisco.listadelacompra.models.ListProduct;

import java.util.ArrayList;
import java.util.List;

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

    }

    private void _init() {
        // vinculo vistas
        nombreLista=(TextView)findViewById(R.id.nameListDetail);


        // para mostrar el listado de usuarios con el adpatador
        ListUsersDetail=(ListView)findViewById(R.id.ListDetailUsers);

        // para mostrar el listado de Productos con el adpatador
        ListProductDetail=(ListView)findViewById(R.id.ListDetailProducts);

    }

    // asigno valores a la vista principal
    private void asignar_valores(){

        // establezco nombres en las listas
        nombreLista.setText(listacogida.getNameList().toString());


    }


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

}
