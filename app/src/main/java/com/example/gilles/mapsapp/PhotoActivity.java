package com.example.gilles.mapsapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class PhotoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Intent lastIntent = getIntent();
        Long photoId = lastIntent.getLongExtra("idPhoto", 0);
        if(photoId != 0){
            SQLiteDatabaseHandler sqlDH = new SQLiteDatabaseHandler(this);
            try{
                MPhoto photo = sqlDH.get(photoId);
                ImageView photoView = (ImageView)findViewById(R.id.photoview);
                photoView.setImageResource(R.drawable.camera);

                Bitmap bmp = photo.rotateIfNeeded();

                photoView.setImageBitmap(bmp);


                ((TextView)findViewById(R.id.photoviewTitle)).setText(photo.getNom());
                ((TextView)findViewById(R.id.photoviewCom)).setText(photo.getCommentaire());

                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                ((TextView) findViewById(R.id.photoviewDate)).setText(df.format(photo.getDate()));

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
