package com.example.gilles.mapsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ActionBarActivity implements LocationListener{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_LIST = 2;
    static final LatLng EPSI = new LatLng(45.769769, 4.859136);
    private GoogleMap map;
    private LocationManager locationManager;
    private String lastPicturePath;
    private HashMap<Marker, Long> markersMap = new HashMap<>();
    private final SQLiteDatabaseHandler sqlDH = new SQLiteDatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        loadMarkers();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1000, this);


        map.setMyLocationEnabled(true);

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
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        map.moveCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroy(){
        //SQLiteDatabaseHandler sqlDH = new SQLiteDatabaseHandler(this);
        //sqlDH.deleteAll();
        super.onDestroy();
    }

    @Override
    public void onResume(){
        testLocationEnabled();
        loadMarkers();
        super.onResume();
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

    public void loadMarkers(){
        map.clear();
        markersMap.clear();
        try
        {
            for(MPhoto pict : sqlDH.getAll()){
                Marker mark = map.addMarker(new MarkerOptions().position(new LatLng(pict.getLatitude(), pict.getLongitude())).title(pict.getNom()).snippet(pict.getCommentaire()));
                markersMap.put(mark, pict.getId());
            }
        } catch (ParseException e) {
            e.printStackTrace();
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

        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(EPSI, 15));
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

    private void testLocationEnabled() {
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Cette application est basée sur les services de localisation");
            builder.setCancelable(false);
            builder.setPositiveButton("Je les active", new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, final int id) {
                    dialog.cancel();
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton("Je quitte l\'appli", new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, final int id) {
                    finish();
                }
            });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

}