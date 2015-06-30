package com.example.gilles.mapsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
    private final SQLiteDatabaseHandler sqldh = new SQLiteDatabaseHandler(getContext());

    public PhotoListAdapter(Context context, List<MPhoto> objects) {
        super(context, -1, objects);
        this.lstPhotos = objects;
        this.contexte = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        final LayoutInflater inflater = (LayoutInflater) contexte.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View listItemView = inflater.inflate(R.layout.list_item, parent, false);
        ((TextView) listItemView.findViewById(R.id.listTitle)).setText(lstPhotos.get(position).getNom());
        ((ImageView) listItemView.findViewById(R.id.listThumb)).setImageBitmap(lstPhotos.get(position).getThumnail(48));

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(lstPhotos.get(position).getNom());

                        final View dialoglayout = inflater.inflate(R.layout.photo_dialog, null);
                        ((ImageView) dialoglayout.findViewById(R.id.ImageDialogList)).setImageBitmap(lstPhotos.get(position).getThumnail(250));
                        ((TextView) dialoglayout.findViewById(R.id.ComDialogList)).setText(lstPhotos.get(position).getCommentaire());
                        final String fDate = (new SimpleDateFormat("dd/MM/yyyy")).format(lstPhotos.get(position).getDate());
                        dialoglayout.findViewById(R.id.DateDialogList).post(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) dialoglayout.findViewById(R.id.DateDialogList)).append(fDate);
                            }
                        });

                        builder.setView(dialoglayout);

                        builder.setPositiveButton("Envoyer par E-mail", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/email"); //nécessaire pour appeler les applications de type messagerie

                                intent.putExtra(Intent.EXTRA_SUBJECT, "PhotoMaps - " + lstPhotos.get(position).getNom()); //sujet
                                intent.putExtra(android.content.Intent.EXTRA_TEXT, lstPhotos.get(position).getCommentaire()); //texte du mail

                                Uri uri = Uri.parse("file://" + lstPhotos.get(position).getFilepath());
                                intent.putExtra(Intent.EXTRA_STREAM, uri); //pièce jointe

                                getContext().startActivity(Intent.createChooser(intent, "Send email"));
                            }
                        });
                        listItemView.post(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
                    }
                }).start();
            }
        });

        listItemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Supprimer la photo ?");
                        builder.setMessage("Voulez vous vraiment supprimer la photo \"" + lstPhotos.get(position).getNom() + "\"");
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                lstPhotos.get(position).deleteFile();
                                sqldh.delete(lstPhotos.get(position));

                                //on refresh l'activité
                                lstPhotos.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                        listItemView.post(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
                    }
                }).start();
                return true;
            }
        });

        return listItemView;
    }
}
