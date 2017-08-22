package com.garcihard.adoptmx;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by garcihard on 15/04/17.
 */

public class MainFragmentAdapter extends ArrayAdapter<MascotaObj> {

    private Activity context;
    private int resource;
    private List<MascotaObj> listMascota;

    public MainFragmentAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<MascotaObj> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        listMascota = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(resource, null);

        CircleImageView imgUrlMascota = (CircleImageView) v.findViewById(R.id.imgUrlMascota);
        TextView txtNombreMascota = (TextView) v.findViewById(R.id.txtNombreM);
        TextView txtDescripcionMascotaF = (TextView) v.findViewById(R.id.txtDescripcionM);

        txtNombreMascota.setText(listMascota.get(position).getNombreM());
        txtDescripcionMascotaF.setText(listMascota.get(position).getRazaM());
        Glide.with(context).load(listMascota.get(position).getUrlMascota()).into(imgUrlMascota);

        return v;
    }
}
