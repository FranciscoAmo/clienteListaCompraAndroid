package com.francisco.listadelacompra;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.francisco.listadelacompra.adapters.GridAdapter;
import com.francisco.listadelacompra.models.ProductosBBDD;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TypeProducts extends AppCompatActivity implements LinearLayout.OnClickListener, AdapterView.OnItemClickListener {

    // int de la actividad
    private final int ADD_PRODUCTS_TYPE = 100;

    // arrayList de productos
    private ArrayList<String> diferentTypes = new ArrayList<String>();


    // vistas para presionar el boton


    private GridView gridViewProductsType;

    private FloatingActionButton fabregresar;


    private Intent type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_type_of_products);

        // relleno el arraylist
        seedTypeProducts();

        // inicio las vistas
        _init();

        // introdizco valores en el gridview
        setGridView();

    }

    private void _init() {

        gridViewProductsType = (GridView) findViewById(R.id.gridViewTypes);


        fabregresar = (FloatingActionButton)findViewById(R.id.fabregresarTypes);

        fabregresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //inicializo el arrayList con valores de distintos productos
    private void seedTypeProducts() {


        diferentTypes.add("legumbre");
        diferentTypes.add("verdura");

        diferentTypes.add("lacteo");
        diferentTypes.add("fruta");

        diferentTypes.add("pescado");
        diferentTypes.add("carne");

        diferentTypes.add("limpieza");
        diferentTypes.add("helado");

        diferentTypes.add("detergente");
        diferentTypes.add("higiene");

        diferentTypes.add("condimento");
        diferentTypes.add("aperitivo");
        diferentTypes.add("bebida");


    }


    // seteo el gridview
    public void setGridView() {
        //vinculo el adaptador
        gridViewProductsType.setAdapter(new GridAdapter(this, diferentTypes));
        // creo el boton de acccion
        gridViewProductsType.setOnItemClickListener(this);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        type = new Intent(getApplicationContext(), ProductsByType.class);
        type.putExtra("tipo",diferentTypes.get(position).toString());
        startActivityForResult(type,ADD_PRODUCTS_TYPE);
    }



    // SE DEBE SOBREESCRIBIR ON ACTIVIRY RESULT INDICANDO LO QUE HACE Y COMO RESULTA EL RESULTADO
    // REQUESTcODE INDICA EL CODIGO DEL INTENT (ESTA AL PRINCIPIO) EL RESULTcODE INDICA COMO ACABO LA ACTIVIDAD
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode== ADD_PRODUCTS_TYPE){
            if(resultCode==RESULT_OK){

                int cantidad = data.getIntExtra("cantidad",1);

                ProductosBBDD.Producto devuelto = (ProductosBBDD.Producto)data.getSerializableExtra("product");


                Intent i = new Intent();
                i.putExtra("devuelto",devuelto);
                i.putExtra("cantidad",cantidad);
                setResult(RESULT_OK,i);
                finish();
            }
            if(resultCode== RESULT_CANCELED){


            }


        }
    }
}
