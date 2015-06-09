package com.example.gilles.mapsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gilles on 19/05/15.
 */
public class PhotoListAdapter extends ArrayAdapter<MPhoto> {
    private List<MPhoto> lstPhotos;
    private Context contexte;

    public PhotoListAdapter(Context context, List<MPhoto> objects) {
        super(context, -1, objects);
        this.lstPhotos = objects;
        this.contexte = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        LayoutInflater inflater = (LayoutInflater) contexte.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItemView = inflater.inflate(R.layout.list_item, parent, false);
        ((TextView) listItemView.findViewById(R.id.listTitle)).setText(lstPhotos.get(position).getNom());
        ((ImageView) listItemView.findViewById(R.id.listThumb)).setImageBitmap(lstPhotos.get(position).getThumnail(48));

        return listItemView;
    }
}
