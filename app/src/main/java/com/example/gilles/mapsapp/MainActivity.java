package com.example.gilles.mapsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_LIST = 2;
    static final LatLng EPSI = new LatLng(45.769769, 4.859136);
    static final LatLng TETEOR = new LatLng(45.773844, 4.856336);
    private GoogleMap map;
    private String lastPicturePath;
    private HashMap<Marker, Long> markersMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
//        Marker hamburg = map.addMarker(new MarkerOptions().position(EPSI)
//                .title("EPSI Lyon"));
//        Marker kiel = map.addMarker(new MarkerOptions()
//                .position(TETEOR)
//                .title("Parc de la tête d\'or")
//                .snippet("c\'est cool"));
        SQLiteDatabaseHandler sqlDH = new SQLiteDatabaseHandler(this);
        //sqlDH.getReadableDatabase();
        try
        {
            List<MPhoto> lstPhotos = sqlDH.getAll();
            for(MPhoto pict : lstPhotos){
                Marker mark = map.addMarker(new MarkerOptions().position(new LatLng(pict.getLatitude(), pict.getLongitude())).title(pict.getNom()).snippet(pict.getCommentaire()));
                markersMap.put(mark, pict.getId());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }



        map.setMyLocationEnabled(true);

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(EPSI, 15));

        // Zoom in, animating the camera.
        //map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MainActivity.this,PhotoActivity.class);
                intent.putExtra("idPhoto", markersMap.get(marker));

                startActivity(intent);
            }
        });

    }

    @Override
    public void onDestroy(){
        //SQLiteDatabaseHandler sqlDH = new SQLiteDatabaseHandler(this);
        //sqlDH.deleteAll();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_photo)
        {
            takePicture();
            return true;
        }
        else if(item.getItemId() == R.id.action_liste){
            Intent intent = new Intent(this, PhotoListActivity.class);
            this.startActivityForResult(intent, REQUEST_IMAGE_LIST);
            return true;
        }
        else
        {
            return false;
        }
    }


    //Photos
    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Creation d'un fichier vide pour enregistrer les photo
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                //Toast.makeText(this, "erreur d\'ecriture", Toast.LENGTH_LONG).show();
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap img = BitmapFactory.decodeFile(lastPicturePath);

            Bitmap ThumbImage = Bitmap.createScaledBitmap(img, 300, 300, true);

            //Bitmap ThumbImage = ThumbnailUtils.extractThumbnail( img, thumnailW, thumnailH);

            ((ImageView) findViewById(R.id.imgview)).setImageBitmap(ThumbImage);
            findViewById(R.id.llhidden).setVisibility(View.VISIBLE);
        }
        else if(requestCode == REQUEST_IMAGE_LIST){
            if(resultCode == 255){
                takePicture();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        lastPicturePath = /*"file:" +*/ image.getAbsolutePath();
        return image;
    }

    public void cancelAndBackToMapView(View v){
        findViewById(R.id.llhidden).setVisibility(View.INVISIBLE);
        //on vide les zones de texte
        ((EditText) findViewById(R.id.tfNomPhoto)).setText("");
        ((EditText) findViewById(R.id.tfComPhoto)).setText("");

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(EPSI, 15));
        File ftodelete = new File(lastPicturePath);
        ftodelete.delete();
        ((EditText) findViewById(R.id.tfNomPhoto)).setText("");
        ((EditText) findViewById(R.id.tfComPhoto)).setText("");
    }

    public void savePhoto(View v){
        String pnom = ((EditText) findViewById(R.id.tfNomPhoto)).getText().toString();
        String pcom = ((EditText) findViewById(R.id.tfComPhoto)).getText().toString();
        Double plati = map.getMyLocation().getLatitude();
        Double plong = map.getMyLocation().getLongitude();
        Float porient = map.getMyLocation().getBearing();

        MPhoto photo = new MPhoto(pnom, pcom, plati, plong, porient, lastPicturePath);

        SQLiteDatabaseHandler sqlDH = new SQLiteDatabaseHandler(this);
        long photoId = sqlDH.insert(photo);

        LatLng lastPhotoPos = new LatLng(plati, plong);
        Marker marker = map.addMarker(new MarkerOptions().position(lastPhotoPos).title(pnom).snippet(pcom));
        markersMap.put(marker, photoId);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPhotoPos, 15));
        marker.showInfoWindow();
        findViewById(R.id.llhidden).setVisibility(View.INVISIBLE);
        //on vide les zones de texte
        ((EditText) findViewById(R.id.tfNomPhoto)).setText("");
        ((EditText) findViewById(R.id.tfComPhoto)).setText("");
    }

}