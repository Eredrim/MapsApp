package com.example.gilles.mapsapp;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.text.ParseException;
import java.util.List;


public class PhotoListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        SQLiteDatabaseHandler sqlDH = new SQLiteDatabaseHandler(this);

        try {
            List<MPhoto> lstPhotos = sqlDH.getAll();

            ((ListView)findViewById(R.id.listView)).setAdapter(new PhotoListAdapter(this, lstPhotos));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_photo)
        {
            setResult(255);
            finish();
            return true;
        }
        else if(item.getItemId() == R.id.action_map){
            finish();
            return true;
        }
        else
        {
            return false;
        }
    }
}
