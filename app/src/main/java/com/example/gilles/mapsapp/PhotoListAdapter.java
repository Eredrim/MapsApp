package com.example.gilles.mapsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        final LayoutInflater inflater = (LayoutInflater) contexte.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItemView = inflater.inflate(R.layout.list_item, parent, false);
        ((TextView) listItemView.findViewById(R.id.listTitle)).setText(lstPhotos.get(position).getNom());
        ((ImageView) listItemView.findViewById(R.id.listThumb)).setImageBitmap(lstPhotos.get(position).getThumnail(48));

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(lstPhotos.get(position).getNom());

                View dialoglayout = inflater.inflate(R.layout.photo_dialog, null);
                ((ImageView) dialoglayout.findViewById(R.id.ImageDialogList)).setImageBitmap(lstPhotos.get(position).getThumnail(250));
                ((TextView) dialoglayout.findViewById(R.id.ComDialogList)).setText(lstPhotos.get(position).getCommentaire());
                String fDate = (new SimpleDateFormat("dd/MM/yyyy")).format(lstPhotos.get(position).getDate());
                ((TextView) dialoglayout.findViewById(R.id.DateDialogList)).append(fDate);
                builder.setView(dialoglayout);

                builder.setPositiveButton("Envoyer par E-mail", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        Intent mailer = Intent.createChooser(intent, null);
                        getContext().startActivity(mailer);
                    }
                });
                builder.show();
            }
        });

        return listItemView;
    }
}
