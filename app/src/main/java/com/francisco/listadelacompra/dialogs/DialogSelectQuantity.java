package com.francisco.listadelacompra.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.francisco.listadelacompra.R;

public class DialogSelectQuantity extends DialogFragment {

    // vistas
    private TextView nombreProducto;
    private TextView unidadProducto;
    private EditText cantidadProducto;
    private ImageView imagenTipoProducto;
    private Button confirmar;
    private Button cancelar;

    // valores enviados para mostrar

    private String nombreProductBundle;
    private String tipoProductBundle;
    private String unidadesProductBundle;




    // interfaz del escuchador solo creo la interfaz y los metodos pasare como argumentos lo que quiera pasar
    public interface ListenerQuantityProduct{
        public void sendQuantity(int cantidad);

        void onNothingSelected(AdapterView<?> parent);
    }

    ListenerQuantityProduct escuchador;







    // creacion del dialogo
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // creo un alert dialog
        final AlertDialog.Builder constructor = new AlertDialog.Builder(getActivity());

        // inflamos la actividad(Dialog) para mostrar lo creado
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // se crea la vista personalizada
        View v = inflater.inflate(R.layout.dialog_set_quantity, null);

        // realionamos elementos con los elementos
        _init(v);

        // obtengo valores enviador desde la actividad PopUpActionsProducts
        getBundleValues(savedInstanceState);

        // seteo los valores
        setValueViews();



        // creo la vista
        constructor.setView(v);
        return constructor.create();
    }

    // inicializo las vistas
    private void _init(View v) {

        nombreProducto= (TextView)v.findViewById(R.id.nombreDialogProduct);
        cantidadProducto = (EditText)v.findViewById(R.id.cantidadDialogProducto);
        unidadProducto = (TextView)v.findViewById(R.id.unidaddialogProducto);
        imagenTipoProducto = ( ImageView) v.findViewById(R.id.imagenDialogTipo);
        confirmar = (Button)v.findViewById(R.id.btndialogconfirmar);
        cancelar = ( Button)v.findViewById(R.id.btndialogcancelar);


        confirmar.setVisibility(View.INVISIBLE);


        // acciones de los botones
        // boton de cancelar
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cancelada accion no hago nada solo cierro la ventana
                getDialog().dismiss();
            }
        });
        // accion a tomar si se pulsa el boton confirmar
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // obtengo el valor de la cantidad de producto
                String cantidadTexto= cantidadProducto.getText().toString();

                int cantidad = (Integer)Integer.parseInt(cantidadTexto);

                // lo mando por el interface
                escuchador.sendQuantity(cantidad);


                //cierro el dialogo
                getDialog().dismiss();
            }
        });


        // hago que si no se ha escrito nada en el edittext no se muestre el boton de confirmar
        // si el campo esta vacio no muestro nada
        cantidadProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                //cambio la visibilidad del boton confirmar si no hay nada
                if(s.length()!=0){
                    confirmar.setVisibility(View.VISIBLE);

                }else{
                    confirmar.setVisibility(View.INVISIBLE);

                }

            }
        });



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








    private void getBundleValues(Bundle bundle){
        nombreProductBundle = getArguments().getString("nombreProducto");
        tipoProductBundle = getArguments().getString("tipo");
        unidadesProductBundle= getArguments().getString("unidades");



    }


    private void setValueViews(){

        nombreProducto.setText(nombreProductBundle);
        unidadProducto.setText(unidadesProductBundle);


        // seteo la imagen segun el tipo del producto
        int imagen = changeImageType(tipoProductBundle);

        Bitmap bMap = BitmapFactory.decodeResource(getResources(), imagen);

        imagenTipoProducto.setImageBitmap(bMap);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        escuchador=(ListenerQuantityProduct) context;
    }


}
