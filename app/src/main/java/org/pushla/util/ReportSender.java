package org.pushla.util;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportSender extends AsyncTask<Void, Void, Void>
{
    private String url;
    private String nomorHP;
    private String nominal;
    private String key;
    private String idProyek;

    public ReportSender(String nomorHP, String nominal, String idProyek)
    {
        this.url = "http://pushla.org/server/transaksi/TambahTransaksi";
        this.key = "CMUeRSv4TKDFbJhN8VcWRrcfuWYJNJ";
        this.nominal = nominal;
        this.nomorHP = nomorHP;
        this.idProyek = idProyek;
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
        //TODO : panggil kalo udah donasi dengan sukses
    }
}