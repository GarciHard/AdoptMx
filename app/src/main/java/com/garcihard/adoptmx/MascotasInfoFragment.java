package com.garcihard.adoptmx;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MascotasInfoFragment extends Fragment {

    private final String FB_DATABASE_PATH = "mascotas/";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseRef;

    private View view;
    private ImageView imgMascotaInfo;
    private TextView txtNombreMascotaInfo;
    private TextView txtRazaMascotaInfo;
    private TextView txtTamañoMascotaInfo;
    private TextView txtEdadMascotaInfo;
    private TextView txtSexoMascotaInfo;
    private TextView txtDescripcionMascotaInfo;
    private FloatingActionButton btnInfoUser;

    private String userId = "";

    public MascotasInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String fragmentArgs = getArguments().getString("mascotaUrl");

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_mascotas_info, container, false);

        imgMascotaInfo = (ImageView) view.findViewById(R.id.imgMascotaInfo);
        txtNombreMascotaInfo = (TextView) view.findViewById(R.id.nombreMascotaInfo);
        txtRazaMascotaInfo = (TextView) view.findViewById(R.id.razaMascotaInfo);
        txtTamañoMascotaInfo = (TextView) view.findViewById(R.id.tamañoMascotaInfo);
        txtEdadMascotaInfo = (TextView) view.findViewById(R.id.edadMascotaInfo);
        txtSexoMascotaInfo = (TextView) view.findViewById(R.id.sexoMascotaInfo);
        txtDescripcionMascotaInfo = (TextView) view.findViewById(R.id.descripcionMascotaInfo);
        btnInfoUser = (FloatingActionButton) view.findViewById(R.id.btnInfoUser);
        btnInfoUser.show();

        btnInfoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LayoutInflater inflater = LayoutInflater.from(getContext());
                final View dialogLayout = inflater.inflate(R.layout.user_item, null);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setView(dialogLayout);
                dialog.setTitle("Información de contacto");

                final CircleImageView userImg = (CircleImageView)dialogLayout.findViewById(R.id.imgUrlUsuario);
                final TextView txtUsrName = (TextView)dialogLayout.findViewById(R.id.txtNombreUsuario);
                final TextView txtUsrMail = (TextView)dialogLayout.findViewById(R.id.txtCorreoUsuario);

                mDatabaseRef = database.getReference("usuarios/" + userId);
                mDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserObj usrObj = dataSnapshot.getValue(UserObj.class);
                        if (usrObj != null) {
                            Picasso.with(getContext())
                                    .load(usrObj.getImagen())
                                    .into(userImg);
                            txtUsrName.setText(usrObj.getNombre() + " " + usrObj.getApellido());
                            txtUsrMail.setText(usrObj.getCorreo());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                dialog.show();
            }
        });

        //Listado de mascotas
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /*Traemos la información de la mascota
                 *para crear un objeto de tipo MascotaObj
                */
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Para crear el objeto requerimos el constructor por defecto
                    MascotaObj img = snapshot.getValue(MascotaObj.class);

                    //Listar una mascota a partir de su URL
                    if (img.getUrlMascota().compareTo(fragmentArgs) == 0) {
                        //Obtenemos el ID del usuario
                        userId = img.getIdUserM();
                        //Glide.with(getContext()).load(img.getUrlMascota()).into(imgMascotaInfo);
                        Picasso.with(getContext())
                                .load(img.getUrlMascota())
                                .into(imgMascotaInfo);
                        txtNombreMascotaInfo.setText("Nombre: " + img.getNombreM());
                        txtRazaMascotaInfo.setText("Raza: " + img.getRazaM());
                        txtTamañoMascotaInfo.setText("Tamaño: " + img.getTamañoM());
                        txtEdadMascotaInfo.setText("Edad: " + img.getEdadM());
                        txtSexoMascotaInfo.setText("Sexo: " + img.getSexoM());
                        txtDescripcionMascotaInfo.setText("Descripcion: " + img.getDescripcionM());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //return inflater.inflate(R.layout.fragment_mascotas_info, container, false);
        return view;
    }



}
