package org.pushla.tes.tespushla;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    // inisiasi toolbar
    private Toolbar toolbar;

    // navigation drawer
    private DrawerLayout drawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle drawerToggle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    public RecyclerView rv;
    public ProyekAdapter pa;

    private static String url = "http://pushla.org/server/project/getallproyek";
    private ProgressDialog pDialog;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences sp;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private static final int RC_SIGN_IN = 0;


    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        sp = getSharedPreferences(SignIn.PREFS,Context.MODE_PRIVATE);
        initMenu();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        initDrawer();

//        rv = (RecyclerView) findViewById(R.id.proyek_list);
//        rv.setHasFixedSize(true);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        rv.setLayoutManager(llm);
//
//        pa = new ProyekAdapter(SplashScreen.listProyek);
//        rv.setAdapter(pa);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        //default display
        displayView(1);
    }


    private void initMenu(){
        //load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerList = (ListView) findViewById(R.id.slider_menu);
        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem(sp.getString("nama","Pushla"), navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
    }

    private void resolveSignInError(){
        Log.d("debug: ","resolve sign in error");
        if (mConnectionResult.hasResolution()){
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);

            }catch (IntentSender.SendIntentException e){
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
    protected void onStart(){
        super.onStart();
        Log.d("debug: ","on Start");
        mGoogleApiClient.connect();
        signInWithGplus();
    }

    protected void onStop(){
        super.onStop();
        Log.d("debug: ","on Stop");
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.d("debug: ","on Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mSignInClicked = false;
        Log.d("debug: ","on Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("debug: ","on Connection Failed");
        if (!connectionResult.hasResolution()){
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress){
            mConnectionResult = connectionResult;
            if (mSignInClicked){
                resolveSignInError();
            }
        }
    }
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            displayView(position);
        }
    }

    private void displayView(int position) {
        Fragment fragment = null;
        boolean not_fragment = false;

        switch (position) {
//            case 0: // Masuk
//                not_fragment = true;
//                break;
            case 1: // Home
                fragment = new FragmentProyek();
                break;
            case 2: // Cari Ulasan
                fragment = new FragmentRiwayat();
                break;
            case 3: // Bantuan
                if (mGoogleApiClient.isConnected()){
                    pDialog = new ProgressDialog(MainActivity.this);
                    pDialog.setMessage("Logging out..");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                            .setResultCallback(new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    mGoogleApiClient.connect();
                                    sp = getSharedPreferences(SignIn.PREFS,Context.MODE_PRIVATE);
                                    sp.edit().clear().commit();
                                    //Toast.makeText(MainActivity.this, "Logging Out...", Toast.LENGTH_SHORT).show();
                                    Intent mainIntent = new Intent(MainActivity.this, SignIn.class);
                                    System.out.println("Banyaknya proyek = " + SplashScreen.listProyek.size());
                                    MainActivity.this.finish();
                                    pDialog.dismiss();
                                    MainActivity.this.startActivity(mainIntent);
                                }
                            });
                }else{
                    Log.d("debug: ","konek : "+mGoogleApiClient.isConnected());
                    Toast.makeText(this,"Tidak bisa Log Out, internet bermasalah..",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);

            drawerLayout.closeDrawer(mDrawerList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * init navigation drawer thing
     */
    private void initDrawer() {
        //setup navigation drawer
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.txt_open, R.string.txt_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                // when drawer closed
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                // when drawer open
            }
        };

        // setDrawerlisterner
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
