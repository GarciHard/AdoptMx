package com.garcihard.adoptmx;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private boolean imgFlag;
    private boolean validationFlag;
    private EditText txtRegisterName;
    private EditText txtRegisterLastName;
    private EditText txtRegisterMail;
    private EditText txtRegisterPass;
    private ImageView imgRegisterUser;
    private FloatingActionButton btnRegisterOptions;
    private FloatingActionButton btnRegisterImage;
    private FloatingActionButton btnRegisterUploadFirebase;
    private Snackbar snackbar;
    private Uri imgUri;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private final String FB_DATABASE_PATH = "usuarios/";
    private final String FB_STORAGE_PATH = "imagenes/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Log.i("ESTADO INICIAL IMG: ", String.valueOf(imgFlag));
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        txtRegisterName = (EditText) findViewById(R.id.txtRegisterName);
        txtRegisterLastName = (EditText) findViewById(R.id.txtRegisterLastName);
        txtRegisterMail = (EditText) findViewById(R.id.txtRegisterMail);
        txtRegisterPass = (EditText) findViewById(R.id.txtRegisterPass);
        imgRegisterUser = (ImageView) findViewById(R.id.imgRegisterUser);
        btnRegisterOptions = (FloatingActionButton) findViewById(R.id.btnRegisterOptions);
        btnRegisterImage = (FloatingActionButton) findViewById(R.id.btnRegisterImage);
        btnRegisterUploadFirebase = (FloatingActionButton) findViewById(R.id.btnRegisterUploadFirebase);

        btnRegisterOptions.setOnClickListener(this);
        btnRegisterImage.setOnClickListener(this);
        btnRegisterUploadFirebase.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mainScreen();
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 1) {

                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                imgRegisterUser.setImageBitmap(bmp);

            } else if (requestCode == 0) {

                imgUri = data.getData();
                imgRegisterUser.setImageURI(imgUri);
                imgFlag = true;
                Log.i("ESTADO INICIAL IMG: ", String.valueOf(imgFlag));
            }
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            case R.id.btnRegisterOptions:
                fabEfectos();
                break;

            case R.id.btnRegisterImage:
                btnRegisterImage.hide();
                btnRegisterUploadFirebase.hide();
                seleccionarImagen();
                break;

            case R.id.btnRegisterUploadFirebase:
                btnRegisterImage.hide();
                btnRegisterUploadFirebase.hide();

                if (validateFields(v)) {

                    mAuth.createUserWithEmailAndPassword(txtRegisterMail.getText().toString(), txtRegisterPass.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        final ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
                                        dialog.setTitle("Almacenando datos . . .");
                                        dialog.show();


                                        //Obtenemos la referencia de firebase_storage
                                        StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUri));
                                        ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @SuppressWarnings("VisibleForTests")
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                //Dismiss dialog de success
                                                dialog.dismiss();

                                                //Mostramos snackbar/toast de success
                                                snackbar = Snackbar
                                                        .make(v, "Usuario registrado con éxito", Snackbar.LENGTH_SHORT);
                                                snackbar.show();

                                                //Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                                                UserObj userObj = new UserObj(txtRegisterName.getText().toString(),
                                                        txtRegisterLastName.getText().toString(),
                                                        txtRegisterMail.getText().toString(),
                                                        taskSnapshot.getDownloadUrl().toString()
                                                );

                                                //Guardamos la informacion de la imagen en firebase_database
                                                String uploadId = mAuth.getCurrentUser().getUid();
                                                mDatabaseRef.child(uploadId).setValue(userObj);

                                                //AQUI HACEMOS QUE NOS MANDE A LA VENTANA PRINCIPAL
                                                //DE LA APLICACIÓN COMO TAL X_X
                                                gotoLoginForm(v);
                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        //Dismiss dialog error
                                                        dialog.dismiss();

                                                        //Mostramos snackbar/toast de error
                                                        snackbar = Snackbar
                                                                .make(v, e.getMessage(), Snackbar.LENGTH_SHORT);
                                                        snackbar.show();
                                                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                                                    @SuppressWarnings("VisibleForTests")
                                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                        //Mostramos progreso de almacenamiento
                                                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                        dialog.setMessage("Almacenando " + (int) progress + "%");
                                                    }
                                                });
                                        //Toast.makeText(RegisterActivity.this, "Success" + mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;
        }
    }

    private void fabEfectos() {
        if (!btnRegisterUploadFirebase.isShown() && !btnRegisterImage.isShown()) {
            btnRegisterUploadFirebase.show();
            btnRegisterImage.show();
        } else {
            btnRegisterUploadFirebase.hide();
            btnRegisterImage.hide();
        }
    }

    private void seleccionarImagen() {
        final CharSequence[] items = {"Cámara", "Galería", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar imagen");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Cámara")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);//1 = REQUEST_CAMERA

                } else if (items[i].equals("Galería")) {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, 0);//0 = SELECT_FILE

                } else if (items[i].equals("Cancelar")) {

                    dialogInterface.dismiss();

                }
            }
        });
        builder.show();
    }

    private String getImageExt(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private boolean validateFields(View v) {
        if (txtRegisterName.getText().toString().trim().compareTo("") == 0) {
            snackbar = Snackbar
                    .make(v, "Campo nombre vacío", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (txtRegisterLastName.getText().toString().trim().compareTo("") == 0) {
            snackbar = Snackbar
                    .make(v, "Campo apellidos vacío", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (txtRegisterMail.getText().toString().trim().compareTo("") == 0) {
            snackbar = Snackbar
                    .make(v, "Campo correo vacío", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (txtRegisterPass.getText().toString().trim().compareTo("") == 0) {
            snackbar = Snackbar
                    .make(v, "Campo password vacío", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (imgFlag == false) {
            snackbar = Snackbar
                    .make(v, "Seleccione una imagen de usuario", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            //todo esta en orden
            validationFlag = true;
        }
        return validationFlag;
    }

    private void gotoLoginForm(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void mainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
