package org.pushla.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pushla.tes.tespushla.R;
import org.pushla.util.Converter;

import java.util.ArrayList;

/**
 * Created by Anjar_Ibnu on 31/03/2015.
 */
public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.RiwayatViewHolder> {
    private ArrayList<RiwayatDonasi> listRiwayatDonasi;

    public RiwayatAdapter(ArrayList<RiwayatDonasi> listRiwayatDonasi)
    {
        this.listRiwayatDonasi = listRiwayatDonasi;
    }

    @Override
    public RiwayatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_riwayat_layout, parent, false);

        return new RiwayatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RiwayatViewHolder holder, int position) {
        System.out.println("harga = " + holder.harga);
        System.out.println("list = " + listRiwayatDonasi);
        System.out.println("current = " + listRiwayatDonasi.get(position));
        holder.harga.setText("Rp " + Converter.getNominal(listRiwayatDonasi.get(position).getHarga()));
        holder.judul.setText(listRiwayatDonasi.get(position).getJudul());
        holder.waktu.setText(listRiwayatDonasi.get(position).getWaktu());
    }

    @Override
    public int getItemCount() {
        return listRiwayatDonasi.size();
    }

    class RiwayatViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView judul, harga, waktu;
        public RiwayatViewHolder(View v) {
            super(v);
            judul = (TextView) v.findViewById(R.id.judul_riwayat);
            harga = (TextView) v.findViewById(R.id.harga_riwayat);
            waktu = (TextView) v.findViewById(R.id.waktu_riwayat);
        }
    }
}
