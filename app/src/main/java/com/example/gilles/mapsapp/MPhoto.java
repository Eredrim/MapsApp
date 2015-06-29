package com.example.gilles.mapsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gilles on 05/05/15.
 */
public class MPhoto {

    private long id;
    private String nom;
    private String commentaire;
    private Date date;
    private double latitude;
    private double longitude;
    private float orientation;
    private String filepath;

    public MPhoto(int id, String nom, String commentaire, Date date, double latitude, double longitude, float orientation, String filepath) {
        this.id = id;
        this.nom = nom;
        this.commentaire = commentaire;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.orientation = orientation;
        this.filepath = filepath;
    }

    public MPhoto(String nom, String commentaire, double latitude, double longitude, float orientation, String filepath) {
        this.nom = nom;
        this.commentaire = commentaire;
        this.latitude = latitude;
        this.longitude = longitude;
        this.orientation = orientation;
        this.date = new Date();
        this.filepath = filepath;
    }

    public MPhoto() {
    }

    public Bitmap rotateIfNeeded() {
        Bitmap bmp = BitmapFactory.decodeFile(filepath);

        try {

        ExifInterface exif = null;
            exif = new ExifInterface(filepath);
        Matrix matrix = new Matrix();

        if (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) == ExifInterface.ORIENTATION_ROTATE_90) {
            matrix.postRotate(90);
        }

        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bmp;
    }

    public Bitmap getThumnail(int thumSize){

        Bitmap img = BitmapFactory.decodeFile(filepath);
        Bitmap bmp = ThumbnailUtils.extractThumbnail(img, thumSize, thumSize);

        try {

            ExifInterface exif = null;
            exif = new ExifInterface(filepath);
            Matrix matrix = new Matrix();

            if (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return bmp;
    }

    public void deleteFile(){
        File file = new File(this.getFilepath());
        file.delete();
    }

    public long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getNom() {
        return nom;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getOrientation() {
        return orientation;
    }

    public String getFilepath() {
        return filepath;
    }

}
