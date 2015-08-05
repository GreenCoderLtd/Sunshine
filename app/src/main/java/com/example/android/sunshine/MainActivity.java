package com.example.android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG=MainActivity.class.getSimpleName();

    public MainActivity()
    {
        Log.i(LOG_TAG,"Constrcutor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(LOG_TAG, " Created");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, " Started Visible");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, " ReStarted Restarting");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, " Resume Active");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, " Paused Partially Visible");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, " Stopped Background");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, " Destroyed closed");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.i(LOG_TAG, " On Create Option Menu");
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

            Intent settingIntent=new Intent(this,SettingsActivity.class);
            startActivity(settingIntent);
            return true;

        }

        if (id == R.id.action_show_on_map) {

            openPreferedLocationOnMap();

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void openPreferedLocationOnMap() {

        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String locationPref=sharedPref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_key));
        Uri geoLocation= Uri.parse("geo:0,0?").buildUpon()
                        .appendQueryParameter("q", locationPref)
                        .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else
        {
            Log.e(LOG_TAG,"Cannot Open Map "+geoLocation.toString());
        }
    }
}
