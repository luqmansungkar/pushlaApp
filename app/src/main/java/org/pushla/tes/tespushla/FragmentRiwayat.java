package org.pushla.tes.tespushla;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pushla.model.RiwayatAdapter;
import org.pushla.model.RiwayatDonasi;
import org.pushla.util.Converter;

import java.util.ArrayList;

/**
 * Created by Anjar_Ibnu on 31/03/2015.
 */
public class FragmentRiwayat extends Fragment {
    static boolean isConnected = true;
    public RecyclerView rv;
    public RiwayatAdapter ra;
    static ArrayList<RiwayatDonasi> listDonasi = null;
    private TextView totalDonasi;
    FragmentManager fragmentManager = null;
    MainActivity main;
    Context context;

//
//    public void setMain(MainActivity main) {
//        this.main = main;
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        main = (MainActivity)getActivity();
        System.out.println("is connected = " + isConnected);
        System.out.println("list riwayat = " + listDonasi);
        if(fragmentManager == null)fragmentManager = getFragmentManager();
        if(!isConnected)
        {
            final View rootView = inflater.inflate(R.layout.no_internet, container, false);
            Button tryAgain = (Button) rootView.findViewById(R.id.try_again);
            tryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshList();
                }
            });
            listDonasi = null;
            isConnected = true;
            return rootView;
        }
        else if(listDonasi == null)
        {
            final View rootView = inflater.inflate(R.layout.riwayat_loading, container, false);
            new GetRiwayatDonasi(ResourceManager.getEmail(rootView.getContext())).execute();
            return rootView;
        }
        else if(listDonasi.isEmpty())
        {
            final View rootView = inflater.inflate(R.layout.no_riwayat, container, false);
            Button search_donation = (Button) rootView.findViewById(R.id.cari_proyek);
            search_donation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cariProyek();
                }
            });
            listDonasi = null;
            isConnected = true;
            return rootView;
        }
        else
        {
            final View rootView = inflater.inflate(R.layout.riwayat_donasi, container, false);
            rv = (RecyclerView) rootView.findViewById(R.id.riwayat_list);
            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(container.getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llm);
            totalDonasi = (TextView)rootView.findViewById(R.id.total_donasi);

            setTotalDonasi(listDonasi);

            ra = new RiwayatAdapter(listDonasi);
            rv.setAdapter(ra);
            listDonasi = null;
            return rootView;
        }
    }

    public void setTotalDonasi(ArrayList listRiwayat)
    {
        this.totalDonasi.setText("Rp "+ Converter.getNominal(getTotalDonasi(listRiwayat)));
    }

    private int getTotalDonasi (ArrayList<RiwayatDonasi> r)
    {
        int totalDonasi = 0;
        for(int ii=0; ii<r.size(); ii++)
        {
            totalDonasi += r.get(ii).getHarga();
        }
        return totalDonasi;
    }

    public void reload()
    {
        fragmentManager.beginTransaction().replace(R.id.frame_container, new FragmentRiwayat()).commit();
    }

    private void refreshList()
    {
        new GetRiwayatDonasi(ResourceManager.getEmail(context)).execute();
    }

    public void cariProyek()
    {
        fragmentManager.beginTransaction().replace(R.id.frame_container, new FragmentProyek()).commit();
        main.changeMenuToListProyek();
    }

    class GetRiwayatDonasi extends AsyncTask<Void, Void, Void>
    {
        String url = "";

        public GetRiwayatDonasi(String email)
        {
            listDonasi = new ArrayList<>();
            url = "http://pushla.org/server/transaksi/gettransaksi/CMUeRSv4TKDFbJhN8VcWRrcfuWYJNJ/" + email;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            System.out.println("json = (" + jsonStr + ")");
            if(jsonStr == null)
            {
                isConnected = false;
            }
            else if(!jsonStr.isEmpty())
            {
                try {
                    JSONArray temp = new JSONArray(jsonStr);
                    for(int ii=0; ii<temp.length(); ii++)
                    {
                        JSONObject cur = temp.getJSONObject(ii);
                        String id = cur.getString("id_project");
                        String waktu = cur.getString("datetime");
                        int harga = cur.getInt("nominal");
                        RiwayatDonasi rd = new RiwayatDonasi(id, waktu, harga);
                        if(listDonasi == null ) listDonasi = new ArrayList<>();
                        listDonasi.add(rd);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(listDonasi == null) listDonasi = new ArrayList<>();
            new Handler().post(new Runnable() {
                public void run() {
                    reload();
                }
            });
        }
    }
}
