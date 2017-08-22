package com.garcihard.adoptmx;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MascotasFragment extends Fragment {

    private String facebookUserId = "";
    private String firebaseUserId = "";
    private final String FB_DATABASE_PATH = "mascotas/";

    private DatabaseReference mDatabaseRef;
    private List<MascotaObj> mascotaList;
    private ListView listView;
    private MascotasFragmentAdapter adapter;
    private ProgressDialog progressDialog;
    private View view;
    private Snackbar snackbar;

    public MascotasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (AccessToken.getCurrentAccessToken() != null) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_mascotas, container, false);

            mascotaList = new ArrayList<>();
            listView = (ListView) view.findViewById(R.id.lvMisMascotas);

            //Se muestra un progress dialog mientras la lista carga
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Espere mientras carga la lista . . .");
            progressDialog.show();

            //Prueba de listado (usuario actual logeado en Facebook)
            facebookUserId = AccessToken.getCurrentAccessToken().getUserId();

            mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();

                /*Traemos la información de la mascota
                 *para crear un objeto de tipo MascotaObj
                */
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Para crear el objeto requerimos el constructor por defecto
                        MascotaObj img = snapshot.getValue(MascotaObj.class);

                        //Listar las mascotas propias de un usuario
                        if (img.getIdUserM().compareTo(facebookUserId) == 0) {
                            mascotaList.add(img);
                        }
                    }

                    //CON ESTO SOLUCIONE EL ERROR AL AGREGAR UN NUEVO ELEMENTO
                    if (getActivity() != null) {
                        //Iniciamos el adapter
                        adapter = new MascotasFragmentAdapter(getActivity(), R.layout.fragment_mascotas_item, mascotaList);
                        //Agregamos el adapter a la listview
                        listView.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    progressDialog.dismiss();
                }
            });
            /*

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i("ID: ",String.valueOf(id));
                    Log.i("URL MASCOTA: ", mascotaList.get(position).getUrlMascota());
                    final int pos = Integer.parseInt(mascotaList.get(position).getUrlMascota());
                    mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

                    mDatabaseRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                //Para crear el objeto requerimos el constructor por defecto
                                MascotaObj img = snapshot.getValue(MascotaObj.class);

                                //Listar las mascotas propias de un usuario
                                if (img.getUrlMascota().compareTo(mascotaList.get(pos).getUrlMascota()) == 0) {
                                    Log.i("VALOR ALGUNO: ", dataSnapshot.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
            });

            */


        } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_mascotas, container, false);

            mascotaList = new ArrayList<>();
            listView = (ListView) view.findViewById(R.id.lvMisMascotas);

            //Se muestra un progress dialog mientras la lista carga
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Espere mientras carga la lista . . .");
            progressDialog.show();

            //Prueba de listado (usuario actual logeado en Facebook)
            firebaseUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();

                /*Traemos la información de la mascota
                 *para crear un objeto de tipo MascotaObj
                */
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Para crear el objeto requerimos el constructor por defecto
                        MascotaObj img = snapshot.getValue(MascotaObj.class);

                        //Listar las mascotas propias de un usuario
                        if (img.getIdUserM().compareTo(firebaseUserId) == 0) {
                            mascotaList.add(img);
                        }
                    }
                    if (mascotaList.isEmpty()){ //Mensaje si no existen mascotas
                        snackbar = Snackbar
                                .make(getView(), "Aún no tienes mascotas en adopción!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                    //CON ESTO SOLUCIONE EL ERROR AL AGREGAR UN NUEVO ELEMENTO
                    if (getActivity() != null) {
                        //Iniciamos el adapter
                        adapter = new MascotasFragmentAdapter(getActivity(), R.layout.fragment_mascotas_item, mascotaList);
                        //Agregamos el adapter a la listview
                        listView.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    progressDialog.dismiss();
                }
            });
        }
        //return inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }
}