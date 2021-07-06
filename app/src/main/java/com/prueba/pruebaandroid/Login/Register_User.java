package com.prueba.pruebaandroid.Login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prueba.pruebaandroid.MainActivity;
import com.prueba.pruebaandroid.Model.RegisterUserModel;
import com.prueba.pruebaandroid.R;
import com.prueba.pruebaandroid.Utils.Reference;
import com.prueba.pruebaandroid.Utils.Util;

public class Register_User extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mFirebaseDatabaseReference;


    private EditText txtNombre;
    private EditText txtApellido;
    private EditText txtTelefono;
    private EditText txtCorreo;
    private Button btnRegister;


    private String Nombre;
    private String Apellido;
    private String Telefono;
    private String Correo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);


        if (!Util.checkConnection(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Register_User.this);
            builder.setTitle(Reference.TITULOMENSAJE)
                    .setMessage(Reference.MENSAJE_ERROR)
                    .setNeutralButton("OK", (dialog, which) -> {
                        Intent listIntent = new Intent(Register_User.this, MainActivity.class);
                        startActivity(listIntent);
                        finish();
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {

            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();

            txtNombre = findViewById(R.id.txtNombre);
            txtApellido = findViewById(R.id.txtApellido);
            txtTelefono = findViewById(R.id.txtTelefono);
            txtCorreo = findViewById(R.id.txtCorreo);
            btnRegister = findViewById(R.id.btnRegister);




            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Nombre = txtNombre.getText().toString();
                    Apellido = txtApellido.getText().toString();
                    Telefono = txtTelefono.getText().toString();
                    Correo = txtCorreo.getText().toString();


                    if (!Nombre.equals("") & !Apellido.equals("") & !Telefono.equals("") & !Correo.equals("")) {


                        RegisterUserModel registerUserModel = new RegisterUserModel(Nombre, Apellido, Telefono, Correo);
                        mFirebaseDatabaseReference.child(Reference.USERS).child(mFirebaseUser.getUid()).setValue(registerUserModel);

                        Toast.makeText(Register_User.this, "Usuario registrado", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(Register_User.this, MainActivity.class));
                        finish();


                    } else {

                        Snackbar.make(view, "Completa todos los campos", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }
            });


        }

    }
}