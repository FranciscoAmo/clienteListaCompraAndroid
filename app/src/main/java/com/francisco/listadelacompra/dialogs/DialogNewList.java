package com.francisco.listadelacompra.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.francisco.listadelacompra.R;

public class DialogNewList extends DialogFragment {

    EditText nombrelista;
    Button confirmaNombreLista;
    Button cancelarNombreLista;




    // interfaz del escuchador solo creo la interfaz y los metodos pasare como argumentos lo que quiera pasar
    public interface ListenerNameList{
        public void sendNameList(String nombre);
    }

    ListenerNameList escuchador;




    // creacion del dialogo
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // creo un alert dialog
        final AlertDialog.Builder constructor = new AlertDialog.Builder(getActivity());

        // inflamos la actividad(Dialog) para mostrar lo creado
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // se crea la vista personalizada
        View v = inflater.inflate(R.layout.dialog_new_list_creation, null);

        // realionamos elementos con los elementos
        _init(v);

        constructor.setView(v);
        return constructor.create();
    }


    // metodo para vincular elementos del diseño y las acciones de los botones
    public void _init(View v) {

        // vinculando vistas del dialogo
        cancelarNombreLista = (Button)v.findViewById(R.id.cancelbtndialog);
        confirmaNombreLista= (Button) v.findViewById(R.id.confirmbtndialog);

        confirmaNombreLista.setVisibility(View.INVISIBLE);

        nombrelista=(EditText)v.findViewById(R.id.editTextListName);

        // acciones de los botones
            // boton de cancelar
        cancelarNombreLista.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       // cancelada accion no hago nada solo cierro la ventana
                                                       getDialog().dismiss();
                                                   }
                                               });
            // accion a tomar si se pulsa el boton confirmar
        confirmaNombreLista.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    // obtengo el valor del nombrede la lista
                                                    String nameListinput = nombrelista.getText().toString();

                                                    // lo mando por el interface
                                                    escuchador.sendNameList(nameListinput);


                                                //cierro el dialogo
                                                    getDialog().dismiss();
                                                }
                                            });


        // hago que si no se ha escrito nada en el edittext no se muestre el boton de confirmar
                // si el campo esta vacio no muestro nada
            nombrelista.addTextChangedListener(new TextWatcher() {
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
                        confirmaNombreLista.setVisibility(View.VISIBLE);

                    }else{
                        confirmaNombreLista.setVisibility(View.INVISIBLE);

                    }

                }
                });



    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        escuchador=(ListenerNameList) context;
    }
}
