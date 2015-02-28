package org.pushla.tes.tespushla;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public RecyclerView rv;
    public ProyekAdapter pa;

    private static String url = "http://pushla.org/server/project/getallproyek";
    private ProgressDialog pDialog;

    JSONArray proyek = null;
    ArrayList<String> judul = new ArrayList<String>();
    ArrayList<Bitmap> gambar = new ArrayList<Bitmap>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetProyek().execute();
        rv = (RecyclerView) findViewById(R.id.proyek_list);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        //pa = new ProyekAdapter(judul);
        //rv.setAdapter(pa);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class GetProyek extends AsyncTask<Void, Void, Void>{

        public GetProyek() {
            super();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ","> "+jsonStr);

            if (jsonStr != null){
                try{
                    proyek = new JSONArray(jsonStr);
                    for (int i=0;i<proyek.length();i++){
                        JSONObject p = proyek.getJSONObject(i);
                        Log.d("Judul: "," > "+p.getString("judul"));
                        judul.add(p.getString("judul"));
                        gambar.add(getBitmap(p.getString("linkGambarHeader")));
                    }

                }catch (Exception e){
                    Log.d("gagal: "," > "+e.getMessage());
                }
            }

            return null;
        }
        public Bitmap getBitmap(String sumber){
            try{
                URL src = new URL(sumber);
                HttpURLConnection connection = (HttpURLConnection) src.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                Log.e("Bitmap","returned");
                return myBitmap;
            }catch (Exception e){
                Log.d("gagal: "," > "+e.getMessage());
                return null;
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Mohon Tunggu");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pa = new ProyekAdapter(judul, gambar);
            rv.setAdapter(pa);
            if (pDialog.isShowing()){
                pDialog.dismiss();
            }
        }
    }
}
