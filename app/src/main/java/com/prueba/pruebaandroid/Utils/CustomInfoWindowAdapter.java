package com.prueba.pruebaandroid.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import com.prueba.pruebaandroid.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater;


    public CustomInfoWindowAdapter(LayoutInflater inflater){
        this.inflater = inflater;

    }

    @Override
    public View getInfoContents(final Marker m) {
        //Carga layout personalizado.
        View v = inflater.inflate(R.layout.infowindow_layout, null);

        ((TextView)v.findViewById(R.id.textView_name_info_maker)).setText(m.getTitle());

        return v;
    }

    @Override
    public View getInfoWindow(Marker m) {
        return null;
    }

}