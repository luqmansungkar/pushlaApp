package org.pushla.tes.tespushla;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public RecyclerView rv;
    public RiwayatAdapter ra;
    ArrayList<RiwayatDonasi> listDonasi = new ArrayList<>();
    private TextView totalDonasi;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.riwayat_donasi, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.riwayat_list);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(container.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        totalDonasi = (TextView)rootView.findViewById(R.id.total_donasi);

//        listDonasi.add(new RiwayatDonasi("Hihi", "Hihi", 10));
//        listDonasi.add(new RiwayatDonasi("Hihi", "Hihi", 10));
//        listDonasi.add(new RiwayatDonasi("Hihi", "Hihi", 10));
//        listDonasi.add(new RiwayatDonasi("Hihi", "Hihi", 10));
//        listDonasi.add(new RiwayatDonasi("Hihi", "Hihi", 10));
//        listDonasi.add(new RiwayatDonasi("Hihi", "Hihi", 10));

        ra = new RiwayatAdapter(listDonasi);
        rv.setAdapter(ra);

        new GetRiwayatDonasi(ResourceManager.getEmail()).execute();

        return rootView;
    }

    public void setListRiwayat(ArrayList<RiwayatDonasi> riwayatBaru)
    {
        ra = new RiwayatAdapter(riwayatBaru);
        rv.setAdapter(ra);
    }

    public void setTotalDonasi(String total)
    {
        this.totalDonasi.setText(total);
    }

    class GetRiwayatDonasi extends AsyncTask<Void, Void, Void>
    {
        String url = "";
        ArrayList<RiwayatDonasi> listRiwayat = new ArrayList<>();

        public GetRiwayatDonasi(String email)
        {
            url = "http://pushla.org/server/transaksi/gettransaksi/CMUeRSv4TKDFbJhN8VcWRrcfuWYJNJ/" + email;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            if(jsonStr != null)
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
                        listRiwayat.add(rd);
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
            setListRiwayat(listRiwayat);
            setTotalDonasi(Converter.getNominal(getTotalDonasi(listRiwayat)));
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
    }
}
