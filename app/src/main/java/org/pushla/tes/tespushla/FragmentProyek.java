package org.pushla.tes.tespushla;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pushla.model.Proyek;
import org.pushla.util.ReportSender;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Anjar_Ibnu on 31/03/2015.
 */
public class FragmentProyek extends Fragment {
    private static String url = "http://pushla.org/server/project/getallproyek";
    public RecyclerView rv;
    public ProyekAdapter pa;
    LayoutInflater inflater;
//    ProgressBar pb;
    View rootView;
    FragmentManager fragmentManager;
    static ArrayList<Proyek> listProyek = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        fragmentManager = getFragmentManager();

        if(listProyek == null)
        {
            final View rootView = inflater.inflate(R.layout.riwayat_loading, container, false);
            this.rootView = rootView;
            new GetProyek(rootView.getContext()).execute();
            return rootView;
        }
        else if(listProyek.isEmpty())
        {
            final View rootView = inflater.inflate(R.layout.no_internet, container, false);
            this.rootView = rootView;
            Button tryAgain = (Button) rootView.findViewById(R.id.try_again);
//            pb = (ProgressBar) rootView.findViewById(R.id.progress_bar_no_internet);
//            pb.setVisibility(View.INVISIBLE);
            tryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    pb.setVisibility(View.VISIBLE);
                    refreshList();
                }
            });
            listProyek = null;
            return rootView;
        }
        else
        {
            final View rootView = inflater.inflate(R.layout.list_proyek, container, false);
            rv = (RecyclerView) rootView.findViewById(R.id.proyek_list);
            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(container.getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llm);

            pa = new ProyekAdapter(listProyek);
            rv.setAdapter(pa);
            this.rootView = rootView;
            listProyek = null;
            return rootView;
        }
    }

    private void refreshList()
    {
        new GetProyek(rootView.getContext()).execute();
    }

//    public void reload()
//    {
//        pb.setVisibility(View.INVISIBLE);
//        fragmentManager.beginTransaction().replace(R.id.frame_container, new FragmentProyek()).commit();
//    }

    public void reload()
    {
        fragmentManager.beginTransaction().replace(R.id.frame_container, new FragmentProyek()).commit();
    }

    private class GetProyek extends AsyncTask<Void, Void, Void> {
        private Context context;

        public GetProyek(Context context) {
            super();
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
//            if(jsonStr == null)
//            {
////                judul = ResourceManager.getListGambar(activity);
////                for(String s : judul)
////                {
////                    Bitmap localImage = ResourceManager.getGambar(s, activity);
////                    if(localImage != null)
////                    {
////                        gambar.add(localImage);
////                    }
////                }
//            }
            if (jsonStr != null){
                try{
                    JSONArray proyek = new JSONArray(jsonStr);
                    String listJudul = "";
                    for (int i=0;i<proyek.length();i++){
                        JSONObject p = proyek.getJSONObject(i);
                        Log.d("Judul: "," > "+p.getString("judul"));
//                        judul.add(p.getString("judul").trim());
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
                        {
                            if(listProyek == null) listProyek = new ArrayList<>();
                            listProyek.add(tempProyek);
                        }
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
            if(listProyek == null) listProyek = new ArrayList<>();
            new Handler().post(new Runnable() {
                public void run() {
                    reload();
                }
            });
        }
    }
}
