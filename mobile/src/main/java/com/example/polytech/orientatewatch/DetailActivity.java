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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {

    DetailFragment detailFragment = new DetailFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, detailFragment)
                    .commit();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_itineraire){
            detailFragment.itineraire();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String FORECAST_SHARE_HASHTAG = " #ProjetRICM4";
        private String mForecastStr;
        private String mForecastDet;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                mForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
                mForecastDet=null;
                int i = 0;
                String[] mForecastSplit = new String[9];
                    for(String retval: mForecastStr.split("\n")){
                        mForecastSplit[i]=retval;
                        i++;
                    }
                    mForecastDet = mForecastSplit[0] + "\n" + mForecastSplit[1] + "\n" + mForecastSplit[4] + "\n" +mForecastSplit[5] + "\n" +mForecastSplit[6] + "\n" +mForecastSplit[7];
                ((TextView) rootView.findViewById(R.id.detail_text))
                        .setText(mForecastDet);

            }

           if (intent != null && intent.hasExtra("icon")) {

                byte[] byteArray = intent.getByteArrayExtra("icon");
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                ImageView img = (ImageView) rootView.findViewById(R.id.imageView1);
                if (bmp != null){
                    Log.d(LOG_TAG, "bmp pas null !!!!!");
                    img.setImageBitmap(bmp);}

            }
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);

            // Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);

            // Get the provider and hold onto it to set/change the share intent.
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            // Attach an intent to this ShareActionProvider.  You can update this at any time,
            // like when the user selects a new piece of data they might like to share.
            if (mShareActionProvider != null ) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    mForecastStr + FORECAST_SHARE_HASHTAG);
            return shareIntent;
        }

        private String buildGoogleMapURL() {

            String[] mForecastSplit = new String[9];

            int i=0;
            for(String retval: mForecastStr.split("\n")){
                mForecastSplit[i]=retval;
                i++;
            }

            String latitude =  mForecastSplit[2];
            String longitude = mForecastSplit[3];

            StringBuilder url = new StringBuilder();
            url.append("http://maps.google.com/maps?f=d&hl=");
            url.append("locale");
            url.append("&saddr=");//from
            url.append(Double.toString(Utility.latitude));
            url.append(",");
            url.append(Double.toString(Utility.longitude));
            url.append("&daddr=");//to
            url.append(latitude);
            url.append(",");
            url.append(longitude);

            url.append("&ie=UTF8&0&om=0&output=kml");
            System.out.println(url);
            return url.toString();
        }

        public void itineraire(){
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(buildGoogleMapURL()));
            startActivity(intent);
        }

    }






}
