package org.pushla.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pushla.model.Proyek;
import org.pushla.tes.tespushla.FragmentProyek;
import org.pushla.tes.tespushla.MainActivity;
import org.pushla.tes.tespushla.ResourceManager;
import org.pushla.tes.tespushla.ServiceHandler;
import org.pushla.tes.tespushla.SignIn;
import org.pushla.tes.tespushla.SplashScreen;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReportSender extends AsyncTask<Void, Void, Void>
{
    private String url;
    private String nomorHP;
    private String nominal;
    private String key;
    private String idProyek;
    private Context context;
    int statusCode = -1;

    public ReportSender(String nomorHP, String nominal, String idProyek, Context context)
    {
        this.url = "http://pushla.org/server/transaksi/TambahTransaksi";
        this.key = "CMUeRSv4TKDFbJhN8VcWRrcfuWYJNJ";
        this.nominal = nominal;
        this.nomorHP = nomorHP;
        this.idProyek = idProyek;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        System.out.println("Ngirim Report");
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("kunci", key));
            nameValuePairs.add(new BasicNameValuePair("nope", nomorHP));
            nameValuePairs.add(new BasicNameValuePair("nominal", nominal));
            nameValuePairs.add(new BasicNameValuePair("id_project", idProyek));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            statusCode = response.getStatusLine().getStatusCode();
            System.out.println(response.getStatusLine().getStatusCode());

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(statusCode != 200)
            ResourceManager.addDonasiSukses(ResourceManager.getCurrentDonation().toSuccessDonation()
                    , context);
        //TODO : panggil kalo udah donasi dengan sukses
        new GetProyek(context).execute();
    }

    public static class GetProyek extends AsyncTask<Void, Void, Void> {
        Context context;
        public GetProyek(Context context) {
            super();
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();
            ArrayList<String> judul = new ArrayList<>();

            String jsonStr = sh.makeServiceCall("http://pushla.org/server/project/getallproyek", ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
//            if(jsonStr == null)
//            {
//                judul = ResourceManager.getListGambar(context);
//                for(String s : judul)
//                {
//                    Bitmap localImage = ResourceManager.getGambar(s, context);
////                    if(localImage != null)
////                    {
////                        gambar.add(localImage);
////                    }
//                }
//            }
            if (jsonStr != null){
                try{
                    JSONArray proyek = new JSONArray(jsonStr);
                    String listJudul = "";
                    for (int i=0;i<proyek.length();i++){
                        JSONObject p = proyek.getJSONObject(i);
                        Log.d("Judul: "," > "+p.getString("judul"));
                        judul.add(p.getString("judul").trim());

                        Proyek tempProyek = new Proyek();
                        tempProyek.setNamaProyek(p.getString("judul").trim());
                        tempProyek.setAuthor(p.getString("namaAuthor").trim());
                        String persen = p.getString("persen").replace("%", "").trim();
                        if(persen.length()>0) {
                            float temp = Float.parseFloat(persen);
                            tempProyek.setPersentase(Math.round(temp));
                        }else
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

                        String pendukung = p.getString("pendukung");
                        try
                        {
                            tempProyek.setPendukung(Integer.parseInt(pendukung));
                        }
                        catch(NumberFormatException e)
                        {
                            tempProyek.setTerkumpul(-1);
                        }

                        tempProyek.setUrlGambar(p.getString("linkGambarHeader"));

                        listJudul =  listJudul + p.getString("judul").trim() + ",";
                        //check localImage file first
                        Bitmap localImage = ResourceManager.getGambar(tempProyek.getNamaProyek(), context);
                        if(localImage != null)
                        {
//                            gambar.add(localImage);
                            tempProyek.setGambar(localImage);
                        }
                        else
                        {
                            Bitmap newImage = getBitmap(tempProyek.getUrlGambar());
//                            gambar.add(newImage);
                            tempProyek.setGambar(newImage);
                            ResourceManager.saveGambar(p.getString("judul").trim(), newImage, context, false);
                        }
                        String id = p.getString("id");
                        tempProyek.setId(id);

                        ResourceManager.addNamaProyek(id, tempProyek.getNamaProyek());

                        String deskripsi = p.getString("long_desc");
                        tempProyek.setDeskripsi(deskripsi);
                        if(tempProyek.getSisaWaktu() > 0)
                            SplashScreen.listProyek.add(tempProyek);
                    }
                    ResourceManager.setListGambar(context, listJudul);
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
            System.out.println("udah kelar nih eksekusinya");
        }
    }
}