package com.prueba.pruebaandroid.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.prueba.pruebaandroid.MainActivity;
import com.prueba.pruebaandroid.R;
import com.prueba.pruebaandroid.Utils.Reference;
import com.prueba.pruebaandroid.Utils.Util;

public class Login_User extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {



    //Constants
    private static final int RC_SIGN_IN = 9001;

    //UI
    private SignInButton mSignInButton;

    //button black
    private int backButtonCount;

    //Firebase and GoogleApiClient
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        getSupportActionBar().hide();

        if (!Util.checkConnection(this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(Login_User.this);
            builder.setTitle(Reference.TITULOMENSAJE)
                    .setMessage(Reference.MENSAJE_ERROR)
                    .setNeutralButton("OK", (dialog, which) -> {
                        Intent listIntent = new Intent(Login_User.this, MainActivity.class);
                        startActivity(listIntent);
                        finish();
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }else{

            mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
            mSignInButton.setOnClickListener(this);



            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("874019607005-jd6gp0jp5rua2cl37snvmd73ivuvij33.apps.googleusercontent.com")
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            mFirebaseAuth = FirebaseAuth.getInstance();

        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /*
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
            }
        } if (resultCode != 0) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

        } else {

        }*/

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("Error", "firebaseAuthWithGoogle:" + account.getId());

                Toast.makeText(this, "Bienvenido a prueba App", Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {

                Log.w("Error", "Google sign in failed", e);
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Util.initToast(Login_User.this,"Error en la autenticación, intentalo nuevamente.");
                    } else {


                        startActivity(new Intent(Login_User.this, MainActivity.class));
                        finish();

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:

                signIn();

                break;
            default:
                return;
        }
    }

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}