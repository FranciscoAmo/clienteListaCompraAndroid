package com.francisco.listadelacompra.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.francisco.listadelacompra.R;

import java.util.ArrayList;

public class GridAdapter  extends BaseAdapter {


    private Context context;

    private ArrayList<String> valoresceldas;


    public GridAdapter(Context context, ArrayList<String> valoresceldas) {

        this.context = context;
        this.valoresceldas = valoresceldas;

    }

    @Override
    public int getCount() {
        return valoresceldas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    // para cargar la imagen de cada elemento
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;



            gridView = new View(context);


            gridView = inflater.inflate(R.layout.adapter_grid_products, null);


            TextView textView = (TextView) gridView.findViewById(R.id.nameItem);
            textView.setText(valoresceldas.get(position).toString());



            ImageView imageView = (ImageView) gridView.findViewById(R.id.imageItem);


            // seteo la imagen


            int imagen= changeImageType(valoresceldas.get(position).toString());
            Bitmap bMap = BitmapFactory.decodeResource(context.getResources(), imagen);


            imageView.setImageBitmap(bMap);




        return gridView;
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




}
