/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.polytech.orientatewatch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link android.widget.ListView} layout.
 */
public class ForecastFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks{


    private final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private ArrayAdapter<String> mForecastAdapter;
    private ArrayAdapter<String> mForecastAdapterDetails;

    private  Bitmap bmpIcon = null;

    //Roue de chargement
    protected ProgressDialog myProgressDialog;


    private static final String START_ACTIVITY = "/start_activity";
    private static final String WEAR_MESSAGE_PATH = "/message";
    private static final String WEAR_MESSAGE_PATH2 = "/message2";
    private static final String HELLO_WORLD_WEAR_PATH = "/hello-world-wear";

    private GoogleApiClient mApiClient;
    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this
        initGoogleApiClient();
        sendMessage(HELLO_WORLD_WEAR_PATH, "");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ArrayAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mForecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_forecast, // The name of the layout ID.
                        R.id.list_item_forecast_textview, // The ID of the textview to populate.
                        new ArrayList<String>());
        mForecastAdapterDetails =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_forecast, // The name of the layout ID.
                        R.id.list_item_forecast_textview, // The ID of the textview to populate.
                        new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);







        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmpIcon.compress(Bitmap.CompressFormat.PNG, 100, stream);
                final byte[] byteArray = stream.toByteArray();
                String forecast = mForecastAdapterDetails.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast)
                        .putExtra("icon", byteArray);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void moreResults() {
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        myProgressDialog = ProgressDialog.show(getActivity(),
                "", "Chargement", true);
        weatherTask.execute(Utility.actualRange);
    }

    public void updateWeather() {
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String range = prefs.getString(getString(R.string.pref_range_key),
                getString(R.string.pref_range_default));
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String tspType = sharedPrefs.getString(
                getString(R.string.pref_trnspt_key),
                getString(R.string.pref_trnspt_pied));

        SharedPreferences sPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String typePOI = sharedPrefs.getString(
                getString(R.string.pref_type_key),
                "restaurant");

        String modeTsp;
        if (tspType.equals(getString(R.string.pref_trnspt_voiture))) {
            modeTsp = "driving";
        } else  {
            modeTsp = "walking";
        }

        if(range!=Utility.actualRange || modeTsp != Utility.actualTrspt || typePOI != Utility.actualType ){
           myProgressDialog = ProgressDialog.show(getActivity(),
                  "", "Chargement", true);
            Utility.nextPageToken=null;
            Utility.actualRange = range;
            Utility.actualTrspt = modeTsp;
            Utility.actualType = typePOI;
            weatherTask.execute(range);
            sendMessage(WEAR_MESSAGE_PATH2, "Debut");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder(this.getActivity()  )
                .addApi( Wearable.API )
                .build();

        mApiClient.connect();
    }

    public void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                }


            }
        }).start();
    }
    @Override
    public void onConnected(Bundle bundle) {
        sendMessage(START_ACTIVITY, "");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();


        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String JsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_NPT = "next_page_token";
            final String OWM_RES = "results";
            final String OWM_NAME = "name";

            // Location coordinate
            final String OWM_GEOM = "geometry";
            final String OWM_LOCATION_MIAM = "location";
            final String OWM_LATITUDE_MIAM = "lat";
            final String OWM_LONGITUDE_MIAM = "lng";
            final String OWM_ADRESS = "vicinity";


            JSONObject places_Json = new JSONObject(JsonStr);
            JSONArray PlacesArray = places_Json.getJSONArray(OWM_RES);

            Utility.nextPageToken = null;
            try {
                Utility.nextPageToken = places_Json.getString(OWM_NPT);
            } catch (JSONException j) {
                Utility.nextPageToken = null;
            }
            Log.d(LOG_TAG, "Toto : " + PlacesArray.length());
            String[] resultStrs = new String[PlacesArray.length()];
            for (int i = 0; i < PlacesArray.length(); i++) {
                GooglePlace poi = new GooglePlace();




                // Get the JSON object representing the place
                JSONObject place = PlacesArray.getJSONObject(i);

                poi.setName(place.getString(OWM_NAME));
                poi.setAddr(place.getString(OWM_ADRESS));

                poi.setLat(place.getJSONObject(OWM_GEOM).getJSONObject(OWM_LOCATION_MIAM).getDouble(OWM_LATITUDE_MIAM));
                poi.setLong(place.getJSONObject(OWM_GEOM).getJSONObject(OWM_LOCATION_MIAM).getDouble(OWM_LONGITUDE_MIAM));


                if (PlacesArray.getJSONObject(i).has("opening_hours")) {
                    if (PlacesArray.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) {
                        if (PlacesArray.getJSONObject(i).getJSONObject("opening_hours").getString("open_now").equals("true")) {
                            poi.setOpenNow("YES");
                        } else {
                            poi.setOpenNow("NO");
                        }
                    }
                }

               if (PlacesArray.getJSONObject(i).has("price_level")) {
                   poi.setPrice(PlacesArray.getJSONObject(i).getString("price_level"));

               }
                else {
                   poi.setPrice("Echelle de prix non renseignée");
               }


                if (PlacesArray.getJSONObject(i).has("rating")) {
                    poi.setRate(PlacesArray.getJSONObject(i).getString("rating"));
                }
                else{
                    poi.setRate("Note pas renseignée");
                }

                if (PlacesArray.getJSONObject(i).has("icon")) {
                    poi.setIcon(PlacesArray.getJSONObject(i).getString("icon"));


                        URL url = null;
                        try {
                            url = new URL(poi.getIcon());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        try {
                            bmpIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                else {
                    poi.setIcon("pas icon");
                }


                //----------------------------------------
                SharedPreferences sharedPrefs =
                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                String tspType = sharedPrefs.getString(
                        getString(R.string.pref_trnspt_key),
                        getString(R.string.pref_trnspt_pied));
                String modeTsp;

                if (tspType.equals(getString(R.string.pref_trnspt_voiture))) {
                  modeTsp = "driving";
                }
                else if (tspType.equals(getString(R.string.pref_trnspt_bike))){
                  modeTsp ="bicycling";



                }
                else  {
                  modeTsp = "walking";
                }



                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                HttpURLConnection urlConnection2 = null;
                BufferedReader reader2 = null;
                String JsonDuree = null;
                String JsonDureeVoiture = null;
                String latQuery = Double.toString(Utility.latitude);
                String lngQuery = Double.toString(Utility.longitude);
                try {
                    String uriduree = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" +
                            latQuery + "," + lngQuery +
                            "&destinations=" +
                            Double.toString(poi.getLat()) + "," + Double.toString(poi.getLong()) + "&mode=" +
                            modeTsp;
                    Uri builtUri = Uri.parse(uriduree).buildUpon().build();

                    URL url = new URL(builtUri.toString());
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
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    JsonDuree = buffer.toString();

                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    return null;
                } finally {
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

                JSONObject duree_Json = new JSONObject(JsonDuree);
               poi.setDuree(duree_Json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text"));

                //-----------------------------------------
                if (poi.isOpenNow()){
                    resultStrs[i] = poi.getName() + "\n" + poi.getAddr() + "\n" + Double.toString(poi.getLat()) + "\n" + Double.toString(poi.getLong()) + "\n" + poi.getDuree() + "\n" + "Ouvert en ce moment" + "\n" + poi.getPrice() + "\n" + "La note est de : "+  ((poi.getRate() == "Note pas renseignée") ? poi.getRate() : poi.getRate()+"/5  basé sur les avis utilisateurs") + "\n" + poi.getIcon();
                    Log.d(LOG_TAG, "open");
                }
                else if (poi.isNotOpenNow()){
                    resultStrs[i] = poi.getName() + "\n" + poi.getAddr() + "\n" + Double.toString(poi.getLat()) + "\n" + Double.toString(poi.getLong()) + "\n" + poi.getDuree() + "\n" + "Pas Ouvert en ce moment" + "\n" + poi.getPrice() + "\n" + "La note est de : "+ ((poi.getRate() == "Note pas renseignée") ? poi.getRate() : poi.getRate()+"/5 basé sur les avis utilisateurs") + "\n" + poi.getIcon();
                    Log.d(LOG_TAG, "pas open");
                }
                else if (poi.isNotInformed()){
                    resultStrs[i] = poi.getName() + "\n" + poi.getAddr() + "\n" + Double.toString(poi.getLat()) + "\n" + Double.toString(poi.getLong()) + "\n" + poi.getDuree() + "\n" + "Pas de renseignement sur l'ouverture" + "\n" + poi.getPrice() + "\n" + "La note est de : "+ ((poi.getRate() == "Note pas renseignée") ? poi.getRate() : poi.getRate()+"/5  basé sur les avis utilisateurs") + "\n" + poi.getIcon();
                    Log.d(LOG_TAG, "pas d'info");
                }
                 }
            myProgressDialog.dismiss();

            if(PlacesArray.length()==0){
                resultStrs = new String[1];
                resultStrs[0]= Utility.noResult;
            }

            return resultStrs;

        }
        @Override
        protected String[] doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;

            String type =  Utility.actualType;
            Log.d(LOG_TAG, "Choix du type : "+type);
            String key = "AIzaSyDafj_vmRc5A7bQqu31OvXUa_RKY9vRNvI";
            String latQuery = Double.toString(Utility.latitude);
            String lngQuery = Double.toString(Utility.longitude);



            try {
                // Construct the URL for the GooglePlace API
                final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
                final String LOCATION_PARAM = "location";
                final String TYPES_PARAM  = "types";
                final String UNITS_PARAM  = "radius";
                final String RANK_PARAM = "rankby";
                final String KEY_PARAM = "key";
                final String PTOKEN_PARAM  = "pagetoken";

                //final String OPENNOW_PARAM = "opennow";

                Uri builtUri;
                if (Utility.nextPageToken == null) {
                    builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter(LOCATION_PARAM, latQuery + "," + lngQuery)
                            .appendQueryParameter(UNITS_PARAM, params[0])
                            .appendQueryParameter(TYPES_PARAM, type)
                            //.appendQueryParameter(OPENNOW_PARAM, "")
                            .appendQueryParameter(RANK_PARAM,"prominence")
                            .appendQueryParameter(KEY_PARAM, key)
                            .build();
                }else{
                    builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter(PTOKEN_PARAM, Utility.nextPageToken)
                            .appendQueryParameter(KEY_PARAM, key)
                            .build();
                }

                URL url = new URL(builtUri.toString());

                Log.d(LOG_TAG, "check URL" + builtUri.toString());

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
                JsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
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

            try {
                return getWeatherDataFromJson(JsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                int i;
                mForecastAdapter.clear();
                mForecastAdapterDetails.clear();
                String[] mForecastSplit = new String[9];


            for(String dayForecastStr : result) {
                    i=0;
                    for(String retval: dayForecastStr.split("\n")){
                        mForecastSplit[i]=retval;
                        i++;
                    }
                    if(mForecastSplit[0] != Utility.noResult || mForecastSplit[4]!= null){
                        mForecastAdapter.add(mForecastSplit[0] + " - " + mForecastSplit[4]);
                        //Log.d(LOG_TAG, "add"+mForecastSplit[0]);
                    }else{
                        mForecastAdapter.add(mForecastSplit[0]);
                    }

                    mForecastAdapterDetails.add(dayForecastStr);

                    sendMessage(WEAR_MESSAGE_PATH, dayForecastStr);

                   }
                // New data is back from the server.  Hooray!
            }
        }
    }
}
