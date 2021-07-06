package com.prueba.pruebaandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prueba.pruebaandroid.Login.Login_User;
import com.prueba.pruebaandroid.Login.Register_User;
import com.prueba.pruebaandroid.Mapa.MapsActivity;
import com.prueba.pruebaandroid.Model.ProductModel;
import com.prueba.pruebaandroid.Utils.Reference;
import com.prueba.pruebaandroid.Utils.Util;
import com.prueba.pruebaandroid.adapters.ListProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    //Firebase and GoogleApiClient
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mFirebaseDatabaseReference;

    private EditText txtBuscar;
    private TextView txtCategory;
    private LinearLayout lyLetrero;


    private RecyclerView recycler_product;
    private List<ProductModel> product;
    private ListProductAdapter product_adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Util.checkConnection(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(Reference.TITULOMENSAJE)
                    .setMessage(Reference.MENSAJE_ERROR)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent listIntent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(listIntent);
                            finish();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();

        } else {

            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

            recycler_product = findViewById(R.id.recycler_product);
            txtBuscar = findViewById(R.id.txtBuscar);
            lyLetrero = findViewById(R.id.lyLetrero);
            txtCategory = findViewById(R.id.txtCategory);

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API)
                    .build();


            load_main();




            txtBuscar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable s) {



                    search();


                }
            });


            txtCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(MainActivity.this, v);
                    popup.setOnMenuItemClickListener(MainActivity.this);
                    popup.inflate(R.menu.popup_menu);
                    popup.show();


                }
            });


        }

    }

    private void search() {


        recycler_product.setVisibility(View.VISIBLE);
        recycler_product.setHasFixedSize(true);
        recycler_product.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_product.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        product = new ArrayList<>();
        product_adapter = new ListProductAdapter(getApplicationContext(), product);
        recycler_product.setAdapter(product_adapter);





        mFirebaseDatabaseReference.child(Reference.PRODUCT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                product.removeAll(product);

                if (dataSnapshot.exists()) {



                    for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                        ProductModel agg = snapshot2.getValue(ProductModel.class);

                        String text_search = txtBuscar.getText().toString().trim().toLowerCase();

                        if((agg.getName().toLowerCase().contains(text_search))){


                            product.add(agg);




                        }


                    }

                    product_adapter.notifyDataSetChanged();
                }else{

                    lyLetrero.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });



    }

    private void load_main() {
        if (mFirebaseUser == null) {
            startActivity(new Intent(this, Login_User.class));
            finish();
        } else {

            //carga datos de tienda

            mFirebaseDatabaseReference.child(Reference.USERS).child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {

                      cargaCategory();

                    } else {

                        startActivity(new Intent(MainActivity.this, Register_User.class));
                        finish();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });



        }
    }

    private void cargaCategory() {



        recycler_product.setVisibility(View.VISIBLE);
        recycler_product.setHasFixedSize(true);
        recycler_product.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_product.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        product = new ArrayList<>();
        product_adapter = new ListProductAdapter(getApplicationContext(), product);
        recycler_product.setAdapter(product_adapter);





        mFirebaseDatabaseReference.child(Reference.PRODUCT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {


                    for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                        ProductModel agg = snapshot2.getValue(ProductModel.class);
                        product.add(agg);
                    }

                    product_adapter.notifyDataSetChanged();
                    lyLetrero.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });



    }



    private void cargaCategoryOption(int opc) {



        recycler_product.setVisibility(View.VISIBLE);
        recycler_product.setHasFixedSize(true);
        recycler_product.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_product.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        product = new ArrayList<>();
        product_adapter = new ListProductAdapter(getApplicationContext(), product);
        recycler_product.setAdapter(product_adapter);





        mFirebaseDatabaseReference.child(Reference.PRODUCT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {


                    for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                        ProductModel agg = snapshot2.getValue(ProductModel.class);


                        switch (opc){
                            case 1:
                                if(agg.getCategory().equals("0")){
                                    product.add(agg);
                                }
                                break;

                            case 2:
                                if(agg.getCategory().equals("1")){
                                    product.add(agg);
                                }
                                break;

                            case 3:
                                if(agg.getCategory().equals("2")){
                                    product.add(agg);
                                }
                                break;

                            case 4:
                                if(agg.getCategory().equals("3")){
                                    product.add(agg);
                                }
                                break;

                            case 5:
                                if(agg.getCategory().equals("4")){
                                    product.add(agg);
                                }
                                break;



                        }





                    }

                    product_adapter.notifyDataSetChanged();
                    lyLetrero.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });



    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnectionFailed(@NonNull  ConnectionResult connectionResult) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//selecciona dentro de la lista de opciones del activity chat

        switch (item.getItemId()) {


            case R.id.salir:



                signOut();
                break;

            case R.id.tienda:

                startActivity(new Intent(MainActivity.this, MapsActivity.class));
                finish();
                break;


        }

        return super.onOptionsItemSelected(item);
    }//menu seleccionar



    private void signOut() {



        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage("¿Desea cerrar su sesión en la aplicación?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
             salir();
            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.show();



    }

    private void salir() {

        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        startActivity(new Intent(this, Login_User.class));
        finish();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {


        switch (menuItem.getItemId()) {

            case R.id.option_1:

                cargaCategoryOption(1);

                return true;

            case R.id.option_2:
                cargaCategoryOption(2);
                return true;

            case R.id.option_3:
                cargaCategoryOption(3);
                return true;


            case R.id.option_4:
                cargaCategoryOption(4);
                return true;

            case R.id.option_5:
                cargaCategoryOption(5);
                return true;

            case R.id.option_6:
                cargaCategory();
                return true;


        }

        return false;
    }
}