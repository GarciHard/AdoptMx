package com.garcihard.adoptmx;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
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
public class MainFragment extends Fragment {

    private final String FB_DATABASE_PATH = "mascotas/";

    private DatabaseReference mDatabaseRef;
    private List<MascotaObj> mascotaList;
    private ListView listView;
    private MainFragmentAdapter adapter;
    private ProgressDialog progressDialog;
    private View view;

    private FloatingActionButton fab;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fab = (FloatingActionButton)  getActivity().findViewById(R.id.fab);

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        mascotaList = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.lvMascotasMain);

        //Se muestra un progress dialog mientras la lista carga
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Espere mientras carga la lista . . .");
        progressDialog.show();

        //Prueba de listado (usuario actual logeado en Facebook)
        //String user = AccessToken.getCurrentAccessToken().getUserId();

        //Listado de todas las mascotas
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
                    mascotaList.add(img);
                }

                //CON ESTO SOLUCIONE EL ERROR AL AGREGAR UN NUEVO ELEMENTO
                if (getActivity() != null) {
                    //Iniciamos el adapter
                    adapter = new MainFragmentAdapter(getActivity(), R.layout.mascota_item, mascotaList);
                    //Agregamos el adapter a la listview
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();
            }
        });

        //Evento para los items del listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(), "Apreto: " + position, Toast.LENGTH_SHORT).show();
                //Log.i("LIST VIEW ITEM EVENT: ", mascotaList.get(position).getNombreM());
                Bundle args = new Bundle();
                args.putString("mascotaUrl", mascotaList.get(position).getUrlMascota().toString());

                MascotasInfoFragment fragment = new MascotasInfoFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragment.setArguments(args);
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
                fab.hide();
            }
        });
        //return inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }
}
