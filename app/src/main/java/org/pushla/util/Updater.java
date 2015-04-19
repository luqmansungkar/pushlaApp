package org.pushla.util;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.pushla.model.SuccessDonation;
import org.pushla.tes.tespushla.ResourceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anjar_Ibnu on 19/04/2015.
 */

public class Updater {
    private Context context;
    private ArrayList<SuccessDonation> listDonasi;

    public Updater(Context context)
    {
        this.context = context;
        listDonasi = ResourceManager.getListDonasiSukses(context);
        if(!listDonasi.isEmpty())
            update(0);
    }

    public void update(int index)
    {
        if(index >= listDonasi.size()) {
            ResourceManager.setListDonasiSukses(context, listDonasi);
        }
        else
        {
            SuccessDonation sd = listDonasi.get(index);
            new UpdaterHelper(ResourceManager.getEmail(context), ""+sd.getNominal(), ""+sd.getId(),
                    context, index).execute();
        }
    }

    public void removeDonation(int index)
    {
        listDonasi.set(index, null);
    }

    class UpdaterHelper extends AsyncTask<Void, Void, Void> {
        private String url;
        private String nomorHP;
        private String nominal;
        private String key;
        private String idProyek;
        private Context context;
        private int updateResponse = -1;
        private int index = -1;

        public UpdaterHelper(String nomorHP, String nominal, String idProyek, Context context, int index) {
            this.url = "http://pushla.org/server/transaksi/TambahTransaksi";
            this.key = "CMUeRSv4TKDFbJhN8VcWRrcfuWYJNJ";
            this.nominal = nominal;
            this.nomorHP = nomorHP;
            this.idProyek = idProyek;
            this.context = context;
            this.index = index;
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
                updateResponse = response.getStatusLine().getStatusCode();
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

            if(updateResponse == 200)
            {
                removeDonation(index);
            }

            update(index+1);
        }
    }
}