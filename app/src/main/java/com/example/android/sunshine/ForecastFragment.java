package com.example.android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> weekForecastAdapter;

    ListView listviewForecast;

    private final String LOG_TAG=ForecastFragment.class.getSimpleName();



    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_main, container, false);

        listviewForecast=(ListView)rootView.findViewById(R.id.listview_forecast);

        weekForecastAdapter=new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview);

        listviewForecast.setAdapter(weekForecastAdapter);

        listviewForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, weekForecastAdapter.getItem(i));
                startActivity(detailIntent);


            }
        });

        return rootView;

        /*String []weatherArray={"Today-Rainy-88/40",
                            "Tomorrow-Sunny-86/63",
                            "Tuesday-Thunderstorm-90/56",
                            "Wednesday-Sunny-86/63",
                            "Today-Rainy-88/40",
                            "Tomorrow-Sunny-86/63",
                            "Tuesday-Thunderstorm-90/56",
                            "Wednesday-Sunny-86/63",
                            "Today-Rainy-88/40",
                            "Tomorrow-Sunny-86/63",
                            "Tuesday-Thunderstorm-90/56",
                            "Wednesday-Sunny-86/63",
                            "Today-Rainy-88/40",
                            "Tomorrow-Sunny-86/63",
                            "Tuesday-Thunderstorm-90/56",
                            "Wednesday-Sunny-86/63",
                            "Today-Rainy-88/40",
                            "Tomorrow-Sunny-86/63",
                            "Tuesday-Thunderstorm-90/56",
                            "Wednesday-Sunny-86/63"
                            };

        List<String> weekForecast=new ArrayList<String>(Arrays.asList(weatherArray));

        weekForecastAdapter=new ArrayAdapter<String>(this.getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,weekForecast);

        ListView listviewForecast=(ListView)rootView.findViewById(R.id.listview_forecast);

        listviewForecast.setAdapter(weekForecastAdapter);*/

        //new FetchWeatherTask().execute("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");


    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecastfragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            updateWeather();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();

    }

    public void updateWeather()
    {

        FetchWeatherTask weatherTask=new FetchWeatherTask();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String locationPref = sharedPref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        weatherTask.execute(locationPref);
    }

    public class FetchWeatherTask extends AsyncTask<String,Void,String[]>
    {

        private final String LOG_TAG=FetchWeatherTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        String format = "json";
        String units = "metric";
        int numDays = 7;


        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast

            final String FORECAST_BASE_URL ="http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "q";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            forecastJsonStr = buffer.toString();

            //Log.e(LOG_TAG,forecastJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
            String []forecast=null;


            try {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String tempUnitPref = sharedPref.getString(getString(R.string.pref_temp_unit_key), getString(R.string.pref_temp_unit_metric));
                forecast=new WeatherDataParser().getWeatherDataFromJson(forecastJsonStr,numDays,tempUnitPref);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return forecast;
        }

        protected void onPostExecute(String []result) {

            weekForecastAdapter.clear();

            for(String dayForecast:result)
            {

                weekForecastAdapter.add(dayForecast);
            }

        }


    }
}
