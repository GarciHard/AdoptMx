package com.garcihard.adoptmx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private Snackbar snackbar;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference userRef = ref.child("usuarios");

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button btnLoginFirebase;
    private EditText txtLoginFirebaseUsr;
    private EditText txtLoginFirebasePwd;

    private final String firebaseLoginExceptionOne = "com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.";
    private final String firebaseLoginExceptionTwo = "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.";
    private final String firebaseLoginExceptionThree = "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton)findViewById(R.id.btnLogin);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                String email = null;
                                String nombre = null;
                                String apellido = null;
                                String id = null;
                                try {
                                    id = object.getString("id");
                                    email = object.getString("email");
                                    nombre = object.getString("first_name");
                                    apellido = object.getString("last_name");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                userRef.child(id);
                                userRef.child(id + "/nombre").setValue(nombre);
                                userRef.child(id + "/apellido").setValue(apellido);
                                userRef.child(id + "/correo").setValue(email);
                                userRef.child(id + "/imagen").setValue("https://graph.facebook.com/" + id + "/picture?type=large");

                                Log.i("ID:", id);
                                Log.i("Correo:", email);
                                Log.i("Nombre:", nombre);
                                Log.i("Apellido:", apellido);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email");
                request.setParameters(parameters);
                request.executeAsync();
                mainScreen();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.cancel_login, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
            }
        });


        //FIREBASE LOGIN BUTTON
        mAuth = FirebaseAuth.getInstance();
        txtLoginFirebaseUsr = (EditText) findViewById(R.id.txtUsuario);
        txtLoginFirebasePwd = (EditText) findViewById(R.id.txtPassword);
        btnLoginFirebase = (Button) findViewById(R.id.btnLoginFirebase);

        btnLoginFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtLoginFirebaseUsr.getText().toString().trim().compareTo("") == 0
                        || txtLoginFirebasePwd.getText().toString().trim().compareTo("") == 0) {
                    snackbar = Snackbar
                            .make(v, "Verifique los campos", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("Autenticando datos . . .");
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(txtLoginFirebaseUsr.getText().toString()
                            , txtLoginFirebasePwd.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mainScreen();
                                    } else {
                                        progressDialog.dismiss();
                                        final AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                                        dialog.setTitle("¡Error!");
                                        switch (task.getException().toString()) {
                                            case firebaseLoginExceptionOne:
                                                dialog.setMessage("El usuario no existe y/o fue eliminado");
                                                dialog.show();
                                                break;
                                            case firebaseLoginExceptionTwo:
                                            case firebaseLoginExceptionThree:
                                                dialog.setMessage("Correo y/o password incorrecto");
                                                dialog.show();
                                                break;
                                        }

                                        //Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                        //Log.i("ERROR LOGIN", task.getException().toString());
                                    }
                                }
                            });
                }
            }
        });



    }

    public void gotoRegisterForm(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void mainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
