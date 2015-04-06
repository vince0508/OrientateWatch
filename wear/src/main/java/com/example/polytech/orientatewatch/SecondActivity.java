package com.example.polytech.orientatewatch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamdaniy on 05/04/15.
 */
public class SecondActivity extends Activity implements WearableListView.ClickListener, MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {
    private final String LOG_TAG = SecondActivity.class.getSimpleName();
    private WearableListView listView;

    private static final String WEAR_MESSAGE_PATH = "/message";
    private static final String WEAR_MESSAGE_PATH2 = "/message2";
    private List<String> recus = new ArrayList<String>();
    private GoogleApiClient mApiClient;
    private WatchViewStub stub;
    public List<SettingsItems> items;
    public  SettingsAdapter mAdapter;

    public static final String ACTION_CLOSE = "yourPackageName.ACTION_CLOSE";
    private FirstReceiver firstReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        IntentFilter filter = new IntentFilter(ACTION_CLOSE);
        firstReceiver = new FirstReceiver();
        registerReceiver(firstReceiver, filter);
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                listView = (WearableListView) stub.findViewById(R.id.sample_list_view);
                loadAdapter();

            }
        });
        items = new ArrayList<>();

        initGoogleApiClient();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void initGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks( this )
                .build();

        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
        if( mApiClient != null && !( mApiClient.isConnected() || mApiClient.isConnecting() ) )
            mApiClient.connect();
    }

    private void loadAdapter() {
        //items.add(new SettingsItems(R.drawable.ic_action_locate, "toto"));

        mAdapter = new SettingsAdapter(this, items);

        listView.setAdapter(mAdapter);

        listView.setClickListener(this);
    }


    @Override
    public void onClick(final WearableListView.ViewHolder viewHolder) {
        Intent intent = new Intent(this, Infos.class)
                .putExtra(Intent.EXTRA_TEXT, recus.get(viewHolder.getPosition()));
        startActivity(intent);
    }

    @Override
    public void onTopEmptyRegionClick() {
        //Prevent NullPointerException
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void onDestroy() {
        if( mApiClient != null )
            mApiClient.unregisterConnectionCallbacks( this );
        super.onDestroy();
        unregisterReceiver(firstReceiver);
    }

    protected void onStop() {
        if ( mApiClient != null ) {
            Wearable.MessageApi.removeListener( mApiClient, this );
            if ( mApiClient.isConnected() ) {
                mApiClient.disconnect();
            }
        }
        super.onStop();
    }
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener( mApiClient, this );
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {

                if( messageEvent.getPath().equalsIgnoreCase( WEAR_MESSAGE_PATH ) ) {

                    System.out.println("Recuuuu !!!!");
                    String recu =new String(messageEvent.getData());
                    recus.add(recu);
                    Log.i(LOG_TAG, "data :" + recu.split("\n")[0]);
                    items.add(new SettingsItems(R.drawable.ic_action_locate, recu.split("\n")[0]));
                    mAdapter.notifyDataSetChanged();



                }
                if( messageEvent.getPath().equalsIgnoreCase( WEAR_MESSAGE_PATH2 ) ) {
                    Log.i(LOG_TAG, "Recu debut !!");
                    items.clear();
                    recus.clear();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    class FirstReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("FirstReceiver", "FirstReceiver");
            if (intent.getAction().equals(ACTION_CLOSE)) {
                SecondActivity.this.finish();
            }
        }
    }
}
