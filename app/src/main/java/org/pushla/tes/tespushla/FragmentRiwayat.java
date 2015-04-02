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

import org.pushla.model.RiwayatAdapter;
import org.pushla.model.RiwayatDonasi;

import java.util.ArrayList;

/**
 * Created by Anjar_Ibnu on 31/03/2015.
 */
public class FragmentRiwayat extends Fragment {
    public RecyclerView rv;
    public RiwayatAdapter ra;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.riwayat_donasi, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.riwayat_list);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(container.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        ArrayList<RiwayatDonasi> listDonasi = new ArrayList<>();
        listDonasi.add(new RiwayatDonasi("Hihi", "Hihi", "Hihi"));
        listDonasi.add(new RiwayatDonasi("Hihi", "Hihi", "Hihi"));
        listDonasi.add(new RiwayatDonasi("Hihi", "Hihi", "Hihi"));
        listDonasi.add(new RiwayatDonasi("Hihi", "Hihi", "Hihi"));
        listDonasi.add(new RiwayatDonasi("Hihi", "Hihi", "Hihi"));
        listDonasi.add(new RiwayatDonasi("Hihi", "Hihi", "Hihi"));
        listDonasi.add(new RiwayatDonasi("Hihi", "Hihi", "Hihi"));
        listDonasi.add(new RiwayatDonasi("Hihi", "Hihi", "Hihi"));

        ra = new RiwayatAdapter(listDonasi);
        rv.setAdapter(ra);
        return rootView;
    }

    class GetRiwayatDonasi extends AsyncTask<Void, Void, Void>
    {
        private String email;

        public GetRiwayatDonasi(String email)
        {
            this.email = email;
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
    }
}
