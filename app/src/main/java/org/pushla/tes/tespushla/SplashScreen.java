package org.pushla.tes.tespushla;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pushla.donateSender.Operator;
import org.pushla.model.Proyek;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class SplashScreen extends ActionBarActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    private static String url = "http://pushla.org/server/project/getallproyek";
    static ArrayList<String> judul;
    static ArrayList<Bitmap> gambar;

    static ArrayList<Proyek> listProyek;

    JSONArray proyek = null;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        judul = new ArrayList<>();
        gambar = new ArrayList<>();
        listProyek = new ArrayList<>();

        Operator.init();
        String operator = Operator.readOperatorName(this);
        Operator.storeRegistrationData(this, operator);

        new GetProyek(this).execute();
//        new Handler().postDelayed(new Runnable(){
//            @Override
//            public void run() {
//                /* Create an Intent that will start the Menu-Activity. */
//                Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
//                SplashScreen.this.finish();
//                SplashScreen.this.startActivity(mainIntent);
//            }
//        }, SPLASH_DISPLAY_LENGTH);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
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

    private class GetProyek extends AsyncTask<Void, Void, Void> {
        private Activity activity;

        public GetProyek(Activity activity) {
            super();
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            if(jsonStr == null)
            {
                judul = ResourceManager.getListGambar(activity);
                for(String s : judul)
                {
                    Bitmap localImage = ResourceManager.getGambar(s, activity);
                    if(localImage != null)
                    {
                        gambar.add(localImage);
                    }
                }
            }
            else if (jsonStr != null){
                try{
                    proyek = new JSONArray(jsonStr);
                    String listJudul = "";
                    for (int i=0;i<proyek.length();i++){
                        JSONObject p = proyek.getJSONObject(i);
                        Log.d("Judul: "," > "+p.getString("judul"));
                        judul.add(p.getString("judul").trim());

                        Proyek tempProyek = new Proyek();
                        tempProyek.setNamaProyek(p.getString("judul").trim());
                        tempProyek.setAuthor(p.getString("namaAuthor").trim());
                        String persen = p.getString("persen").replace("%", "").trim();
                        if(persen.length()>0)
                            tempProyek.setPersentase(Integer.parseInt(persen));
                        else
                            tempProyek.setPersentase(0);
                        String sisaWaktu = p.getString("sisaWaktu").trim();
                        if(sisaWaktu.contains("CLOSED") || sisaWaktu.contains("TELAH BERAKHIR"))
                        {
                            tempProyek.setSisaWaktu(-1);
                        }
                        else
                        {
                            if(sisaWaktu.replace("hari lagi", "").trim().length()>0)
                                tempProyek.setSisaWaktu(Integer.parseInt(sisaWaktu.replace("hari lagi", "").trim()));
                            else
                                tempProyek.setSisaWaktu(0);
                        }
                        String terkumpul = p.getString("terkumpul").replace(".","");
                        terkumpul = terkumpul.replace("Rp", "").trim();
                        if(terkumpul.length()>0)
                            tempProyek.setTerkumpul(Integer.parseInt(terkumpul));
                        else
                            tempProyek.setTerkumpul(0);

                        String target = p.getString("target").replace(".", "");
                        target = target.replace("Rp", "").trim();
                        if(target.length() > 0)
                            tempProyek.setTarget(Integer.parseInt(target));
                        else
                            tempProyek.setTarget(0);

                        tempProyek.setUrlGambar(p.getString("linkGambarHeader"));

                        listJudul =  listJudul + p.getString("judul").trim() + ",";
                        //check localImage file first
                        Bitmap localImage = ResourceManager.getGambar(tempProyek.getNamaProyek(), activity);
                        if(localImage != null)
                        {
                            gambar.add(localImage);
                            tempProyek.setGambar(localImage);
                        }
                        else
                        {
                            Bitmap newImage = getBitmap(tempProyek.getUrlGambar());
                            gambar.add(newImage);
                            tempProyek.setGambar(newImage);
                            ResourceManager.saveGambar(p.getString("judul").trim(), newImage, activity, false);
                        }
                        String id = p.getString("id");
                        tempProyek.setId(id);

                        ResourceManager.addNamaProyek(id, tempProyek.getNamaProyek());

                        String deskripsi = p.getString("long_desc");
                        tempProyek.setDeskripsi(deskripsi);
                        if(tempProyek.getSisaWaktu() > 0)
                            listProyek.add(tempProyek);
                    }
                    ResourceManager.setListGambar(activity, listJudul);
                }catch (Exception e){
                    Log.d("gagal: "," > "+e.getMessage());
                    e.printStackTrace();
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
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            boolean loggedIn = false;
            sp = getSharedPreferences(SignIn.PREFS, Context.MODE_PRIVATE);
            loggedIn = sp.getBoolean("loggedIn",false);
            if (loggedIn){
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                System.out.println("Banyaknya proyek = " + SplashScreen.listProyek.size());
                SplashScreen.this.finish();
                SplashScreen.this.startActivity(mainIntent);
            }else {
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
//                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                System.out.println("Banyaknya proyek = " + SplashScreen.listProyek.size());
                SplashScreen.this.finish();
                SplashScreen.this.startActivity(mainIntent);
            }
        }
    }
}
