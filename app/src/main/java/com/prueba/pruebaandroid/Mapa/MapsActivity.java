package com.prueba.pruebaandroid.Mapa;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prueba.pruebaandroid.MainActivity;
import com.prueba.pruebaandroid.Model.ProductModel;
import com.prueba.pruebaandroid.R;
import com.prueba.pruebaandroid.Utils.CustomInfoWindowAdapter;
import com.prueba.pruebaandroid.Utils.Reference;
import com.prueba.pruebaandroid.Utils.Util;
import com.prueba.pruebaandroid.databinding.ActivityMapsBinding;

import static java.security.AccessController.getContext;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Firebase and GoogleApiClient
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mFirebaseDatabaseReference;


    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private LatLngBounds.Builder builder;



    private static final float ZOOM = 15f;



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MapsActivity.this, MainActivity.class));
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!Util.checkConnection(MapsActivity.this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setTitle(Reference.TITULOMENSAJE)
                    .setMessage(Reference.MENSAJE_ERROR)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent listIntent = new Intent(MapsActivity.this, MainActivity.class);
                            startActivity(listIntent);

                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else{


            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            //loadMap();

        }
    }

    private void loadMap() {

        final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker);

        mMap.setOnMapLoadedCallback(() -> {

            mFirebaseDatabaseReference.child(Reference.TIENDA).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {



                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                            Double lat = Double.valueOf(snapshot.child("Ln").getValue().toString());
                            Double lng = Double.valueOf(snapshot.child("Lt").getValue().toString());

                            LatLng thePosition = new LatLng(lat, lng);


                            mMap.addMarker(new MarkerOptions()
                                    .position(thePosition)
                                    .icon(icon)
                                    .title(snapshot.child("name").getValue().toString())
                                    .snippet("1"));
                            builder.include(thePosition);
                            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(MapsActivity.this)));

                            CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(thePosition, ZOOM);
                            mMap.animateCamera(camera);

                        }


                    }else{

                        Toast.makeText(MapsActivity.this, "sin datos", Toast.LENGTH_SHORT).show();


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapsInitializer.initialize(MapsActivity.this);
        builder = new LatLngBounds.Builder();



        mFirebaseDatabaseReference.child(Reference.TIENDA).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {

                    final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.tienda);

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                        Double lat = Double.valueOf(snapshot.child("Lt").getValue().toString());
                        Double lng = Double.valueOf(snapshot.child("Ln").getValue().toString());

                        LatLng thePosition = new LatLng(lat, lng);


                        mMap.addMarker(new MarkerOptions()
                                .position(thePosition)
                                .title(snapshot.child("name").getValue().toString())
                                .icon(icon)
                                .snippet("1"));
                        builder.include(thePosition);
                        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(MapsActivity.this)));


                        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(thePosition, ZOOM);
                        mMap.animateCamera(camera);

                    }


                }else{

                    Toast.makeText(MapsActivity.this, "sin datos", Toast.LENGTH_SHORT).show();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });



    }
}