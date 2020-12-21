package com.francisco.listadelacompra.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.francisco.listadelacompra.R;

public class DialogLoading extends DialogFragment {


    private ImageView imagenCarga;
    private DisplayMetrics medidaVentana;

    // creacion del dialogo
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // creo un alert dialog
        final AlertDialog.Builder constructor = new AlertDialog.Builder(getActivity());

        // inflamos la actividad(Dialog) para mostrar lo creado
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // se crea la vista personalizada
        View v = inflater.inflate(R.layout.dialog_loading, null);










        // realionamos elementos con los elementos
        _init(v);

        constructor.setView(v);
        return constructor.create();
    }

    private void _init(View v) {

        imagenCarga = (ImageView)v.findViewById(R.id.animacionCircular);
        animarImagen();
    }


    // metodos para animar imagen
    public void animarImagen() {



        Animation btnfgiro = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.animacion);
        imagenCarga.startAnimation(btnfgiro);

    }



}
