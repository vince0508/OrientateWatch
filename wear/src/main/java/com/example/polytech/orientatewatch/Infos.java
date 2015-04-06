package com.example.polytech.orientatewatch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Infos extends Activity {

    ViewPager viewPager;
    MyPagerAdapter myPagerAdapter;
    String str;
    String latitude;
    String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);

        viewPager = (ViewPager)findViewById(R.id.myviewpager);
        myPagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(myPagerAdapter);

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            int i = 0;
            str = intent.getStringExtra(Intent.EXTRA_TEXT);
            String[] split = new String[9];
            for(String retval: str.split("\n")){
                split[i]=retval;
                i++;
            }
            str = split[0] + "\n" + split[1] + "\n" + split[4] + "\n" +split[5] + "\n" +split[7];
            latitude = split[2];
            longitude = split[3];
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_infos, menu);
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

    private class MyPagerAdapter extends PagerAdapter{

        int NumberOfPages = 2;

        int[] backgroundcolor = {
                0xFF101010,
                0xFF101010};

        @Override
        public int getCount() {
            return NumberOfPages;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            LinearLayout layout = new LinearLayout(Infos.this);;
            if (position == 0){
                TextView textView = new TextView(Infos.this);
                textView.setTextColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                textView.setTextSize(15);
                textView.setText(str);
                layout.setOrientation(LinearLayout.VERTICAL);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                layout.setBackgroundColor(backgroundcolor[position]);
                layout.setLayoutParams(layoutParams);
                layout.addView(textView);
            }else if (position == 1){
                ImageView imageView = new ImageView(Infos.this);
                imageView.setBackgroundColor(Color.BLACK);
                imageView.setImageResource(R.drawable.itineraire);
                layout.setOrientation(LinearLayout.VERTICAL);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                layout.setBackgroundColor(backgroundcolor[position]);
                layout.setLayoutParams(layoutParams);
                layout.addView(imageView);
            }

            final int page = position;
            layout.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if (page == 1){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("google.navigation:///?q=" + latitude + "," + longitude + "&mode=w"));
                        startActivity(intent);

                        Intent myIntent = new Intent(MainActivity.ACTION_CLOSE);
                        sendBroadcast(myIntent);

                        Intent myIntent2 = new Intent(SecondActivity.ACTION_CLOSE);
                        sendBroadcast(myIntent2);
                        Log.e("onCreate", "onCreate");
                        finish();
                    }
                }});

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout)object);
        }

    }
}
