package com.garcihard.adoptmx;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.garcihard.adoptmx.R.id.fab;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateFragment extends Fragment {

    private final Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;

    private boolean imgFlag;
    private boolean validationFlag;
    private EditText txtDescripcion = null;
    private EditText txtNombre = null;
    private EditText txtRaza = null;
    private FloatingActionButton btnGuardar = null;
    private FloatingActionButton btnImagen = null;
    private FloatingActionButton btnOptions = null;
    private ImageView imgMascota = null;
    private Snackbar snackbar = null;
    private Spinner comboEdad = null;
    private Spinner comboEspecie = null;
    private Spinner comboSexo = null;
    private Spinner comboTamaño = null;
    private Uri imgUri = null;
    private View view = null;

    private String facebookUserId = "";
    private String firebaseUserId = "";

    //prueba almacenamiento firebase
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private final String FB_DATABASE_PATH = "mascotas/";
    private final String FB_STORAGE_PATH = "imagenes/";

    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (AccessToken.getCurrentAccessToken() != null) {
            //prueba almacenamiento facebook-firebase
            facebookUserId = AccessToken.getCurrentAccessToken().getUserId();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

            // Inflate the layout for this fragment
            Log.i("ESTADO INICIAL IMG: ", String.valueOf(imgFlag));
            view = inflater.inflate(R.layout.fragment_create, container, false);

            txtDescripcion = (EditText) view.findViewById(R.id.txtDescripcion);
            txtNombre = (EditText) view.findViewById(R.id.txtNombre);
            txtRaza = (EditText) view.findViewById(R.id.txtRaza);
            btnOptions = (FloatingActionButton) view.findViewById(R.id.btnOptions);
            btnImagen = (FloatingActionButton) view.findViewById(R.id.btnImagen);
            btnGuardar = (FloatingActionButton) view.findViewById(R.id.btnGuardar);
            imgMascota = (ImageView) view.findViewById(R.id.imgMascota);
            comboEdad = (Spinner) view.findViewById(R.id.comboEdad);
            comboEspecie = (Spinner) view.findViewById(R.id.comboEspecie);
            comboSexo = (Spinner) view.findViewById(R.id.comboSexo);
            comboTamaño = (Spinner) view.findViewById(R.id.comboTamaño);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.edadMascotas, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            comboEdad.setAdapter(adapter);

            ArrayAdapter<CharSequence> adapterE = ArrayAdapter.createFromResource(getContext(),
                    R.array.especieMascota, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            comboEspecie.setAdapter(adapterE);

            ArrayAdapter<CharSequence> adapterS = ArrayAdapter.createFromResource(getContext(),
                    R.array.sexoMascota, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            comboSexo.setAdapter(adapterS);

            ArrayAdapter<CharSequence> adapterT = ArrayAdapter.createFromResource(getContext(),
                    R.array.tamañoMascotas, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            comboTamaño.setAdapter(adapterT);

            btnOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fabEfectos();
                }
            });

            btnImagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    seleccionarImagen();
                    btnGuardar.hide();
                    btnImagen.hide();
                }
            });

            btnGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadFirebaseFB();
                    btnGuardar.hide();
                    btnImagen.hide();
                }
            });
        } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            //prueba almacenamiento firebase
            firebaseUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

            // Inflate the layout for this fragment
            Log.i("ESTADO INICIAL IMG: ", String.valueOf(imgFlag));
            view = inflater.inflate(R.layout.fragment_create, container, false);

            txtDescripcion = (EditText) view.findViewById(R.id.txtDescripcion);
            txtNombre = (EditText) view.findViewById(R.id.txtNombre);
            txtRaza = (EditText) view.findViewById(R.id.txtRaza);
            btnOptions = (FloatingActionButton) view.findViewById(R.id.btnOptions);
            btnImagen = (FloatingActionButton) view.findViewById(R.id.btnImagen);
            btnGuardar = (FloatingActionButton) view.findViewById(R.id.btnGuardar);
            imgMascota = (ImageView) view.findViewById(R.id.imgMascota);
            comboEdad = (Spinner) view.findViewById(R.id.comboEdad);
            comboEspecie = (Spinner) view.findViewById(R.id.comboEspecie);
            comboSexo = (Spinner) view.findViewById(R.id.comboSexo);
            comboTamaño = (Spinner) view.findViewById(R.id.comboTamaño);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.edadMascotas, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            comboEdad.setAdapter(adapter);

            ArrayAdapter<CharSequence> adapterE = ArrayAdapter.createFromResource(getContext(),
                    R.array.especieMascota, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            comboEspecie.setAdapter(adapterE);

            ArrayAdapter<CharSequence> adapterS = ArrayAdapter.createFromResource(getContext(),
                    R.array.sexoMascota, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            comboSexo.setAdapter(adapterS);

            ArrayAdapter<CharSequence> adapterT = ArrayAdapter.createFromResource(getContext(),
                    R.array.tamañoMascotas, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            comboTamaño.setAdapter(adapterT);

            btnOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fabEfectos();
                }
            });

            btnImagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    seleccionarImagen();
                    btnGuardar.hide();
                    btnImagen.hide();
                }
            });

            btnGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadFirebase();
                    btnGuardar.hide();
                    btnImagen.hide();
                }
            });
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {

                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                imgMascota.setImageBitmap(bmp);

            } else if (requestCode == SELECT_FILE) {

                imgUri = data.getData();
                imgMascota.setImageURI(imgUri);
                imgFlag = true;
                Log.i("IMAGEN: ", String.valueOf(imgFlag));

            }
        }
    }

    private void fabEfectos() {
        if (!btnGuardar.isShown() && !btnImagen.isShown()) {
            btnGuardar.show();
            btnImagen.show();
        } else {
            btnGuardar.hide();
            btnImagen.hide();
        }
    }

    private void seleccionarImagen() {
        final CharSequence[] items = {"Cámara", "Galería", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Seleccionar imagen");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Cámara")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else if (items[i].equals("Galería")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_FILE);

                } else if (items[i].equals("Cancelar")) {

                    dialogInterface.dismiss();

                }
            }
        });
        builder.show();
    }

    @SuppressWarnings("VisibleForTests")
    private void uploadFirebaseFB() {
        if (validarCampos()) {
            final ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setTitle("Almacenando datos . . .");
            dialog.show();

            //Obtenemos la referencia de firebase_storage
            StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUri));

            ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Dismiss dialog de success
                    dialog.dismiss();

                    //Mostramos snackbar/toast de success
                    snackbar = Snackbar
                            .make(getView(), "Almacenado con éxito", Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    //Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                    MascotaObj mascotaObj = new MascotaObj(facebookUserId.toString(),
                            comboEspecie.getSelectedItem().toString(),
                            comboTamaño.getSelectedItem().toString(),
                            comboEdad.getSelectedItem().toString(),
                            txtNombre.getText().toString(),
                            txtRaza.getText().toString(),
                            comboSexo.getSelectedItem().toString(),
                            txtDescripcion.getText().toString(),
                            taskSnapshot.getDownloadUrl().toString()
                    );

                    //Guardamos la informacion de la imagen en firebase_database
                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(mascotaObj);

                    /*Remplazamos el fragment actual con una nueva instancia del mismo
                     *esto nos ahorra el tener que limpiar los campos uno a uno . . .
                     *espero no represente un problema a futuro, pero de momento va bien
                     */
                    CreateFragment fragment = new CreateFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                                                        getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.commit();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Dismiss dialog error
                            dialog.dismiss();

                            //Mostramos snackbar/toast de error
                            snackbar = Snackbar
                                    .make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //Mostramos progreso de almacenamiento
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage("Almacenando " + (int) progress + "%");
                        }
                    });
            //Log.i("URI DE LA IMAGEN: ", getImageExt(imgUri));
        }
    }

    @SuppressWarnings("VisibleForTests")
    private void uploadFirebase() {
        if (validarCampos()) {
            final ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setTitle("Almacenando datos . . .");
            dialog.show();

            //Obtenemos la referencia de firebase_storage
            StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUri));

            ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Dismiss dialog de success
                    dialog.dismiss();

                    //Mostramos snackbar/toast de success
                    snackbar = Snackbar
                            .make(getView(), "Almacenado con éxito", Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    //Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                    MascotaObj mascotaObj = new MascotaObj(firebaseUserId.toString(),
                            comboEspecie.getSelectedItem().toString(),
                            comboTamaño.getSelectedItem().toString(),
                            comboEdad.getSelectedItem().toString(),
                            txtNombre.getText().toString(),
                            txtRaza.getText().toString(),
                            comboSexo.getSelectedItem().toString(),
                            txtDescripcion.getText().toString(),
                            taskSnapshot.getDownloadUrl().toString()
                    );

                    //Guardamos la informacion de la imagen en firebase_database
                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(mascotaObj);

                    /*Remplazamos el fragment actual con una nueva instancia del mismo
                     *esto nos ahorra el tener que limpiar los campos uno a uno . . .
                     *espero no represente un problema a futuro, pero de momento va bien
                     */
                    CreateFragment fragment = new CreateFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.commit();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Dismiss dialog error
                            dialog.dismiss();

                            //Mostramos snackbar/toast de error
                            snackbar = Snackbar
                                    .make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //Mostramos progreso de almacenamiento
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage("Almacenando " + (int) progress + "%");
                        }
                    });
            //Log.i("URI DE LA IMAGEN: ", getImageExt(imgUri));
        }
    }

    private boolean validarCampos() {
        if (comboEspecie.getSelectedItem().toString().trim().compareTo("") == 0) {
            snackbar = Snackbar
                    .make(getView(), "Debe seleccionar una especie", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (comboTamaño.getSelectedItem().toString().trim().compareTo("") == 0) {
            snackbar = Snackbar
                    .make(getView(), "Debe seleccionar un tamaño", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (comboEdad.getSelectedItem().toString().trim().compareTo("") == 0) {
            snackbar = Snackbar
                    .make(getView(), "Debe seleccionar una edad", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (comboSexo.getSelectedItem().toString().trim().compareTo("") == 0) {
            snackbar = Snackbar
                    .make(getView(), "Debe seleccionar un sexo", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (txtDescripcion.getText().toString().trim().compareTo("") == 0) {
            snackbar = Snackbar
                    .make(getView(), "Campo descripción vacío", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (txtNombre.getText().toString().trim().compareTo("") == 0) {
            snackbar = Snackbar
                    .make(getView(), "Campo nombre vacío", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (txtRaza.getText().toString().trim().compareTo("") == 0) {
            snackbar = Snackbar
                    .make(getView(), "Campo raza vacío", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (imgFlag == false) {
            snackbar = Snackbar
                    .make(getView(), "Debe seleccionar una imagen", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            //todo esta en orden
            validationFlag = true;
        }
        return validationFlag;
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}