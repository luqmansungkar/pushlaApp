package org.pushla.tes.tespushla;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Luqman on 28/02/2015.
 */
public class ProyekAdapter extends RecyclerView.Adapter<ProyekAdapter.ProyekViewHolder> {

    private ArrayList<String> judul;
    private ArrayList<Bitmap> gambar;

    public ProyekAdapter(ArrayList<String> judul, ArrayList<Bitmap> gambar){
        this.judul = judul;
        this.gambar = gambar;
    }

    @Override
    public ProyekViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);

        return new ProyekViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProyekViewHolder holder, int position) {
        holder.pNama.setText(judul.get(position));
        if (gambar.get(position) != null) {
            holder.pGambar.setImageBitmap(gambar.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return judul.size();
    }

    public static class ProyekViewHolder extends RecyclerView.ViewHolder {
        protected TextView pNama;
        protected ImageView pGambar;

        public ProyekViewHolder(View v){
            super(v);
            pNama = (TextView) v.findViewById(R.id.judul);
            pGambar = (ImageView) v.findViewById(R.id.gambar);
        }
    }
}
