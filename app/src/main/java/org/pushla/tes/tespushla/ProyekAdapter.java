package org.pushla.tes.tespushla;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.pushla.model.Proyek;

import java.util.ArrayList;

/**
 * Created by Luqman on 28/02/2015.
 */
public class ProyekAdapter extends RecyclerView.Adapter<ProyekAdapter.ProyekViewHolder> {
//    private ArrayList<String> judul;
//    private ArrayList<Bitmap> gambar;
    private ArrayList<Proyek> listProyek;

//    public ProyekAdapter(ArrayList<String> judul, ArrayList<Bitmap> gambar){
//        this.judul = judul;
//        this.gambar = gambar;
//    }

    ProyekAdapter(ArrayList<Proyek> listProyek)
    {
        this.listProyek = listProyek;
    }

    private int width = 0;

    @Override
    public ProyekViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        width = parent.getWidth();

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);

        return new ProyekViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProyekViewHolder holder, int position) {
//        holder.pNama.setText(judul.get(position));
//        if (gambar.get(position) != null) {
////            holder.pGambar.setImageBitmap(gambar.get(position));
//            BitmapDrawable ob = new BitmapDrawable(gambar.get(position));
//            holder.pGambar.setBackground(ob);
//        }
        holder.pNama.setText(listProyek.get(position).getNamaProyek());
        if(listProyek.get(position).getGambar()!=null)
        {
            BitmapDrawable obj = new BitmapDrawable(listProyek.get(position).getGambar());
            holder.pGambar.setImageDrawable(obj);
        }
        holder.sisaWaktu.setText("" + listProyek.get(position).getSisaWaktu());
        holder.persentase.setText(""+listProyek.get(position).getPersentase() + "%");
        holder.terkumpul.setText("Rp " + getNominal(listProyek.get(position).getTerkumpul()));
        holder.target.setText(" dari Rp " + getNominal(listProyek.get(position).getTarget()));
        holder.pAuthor.setText("oleh " + listProyek.get(position).getAuthor());
        holder.barPersentase.setWidth(holder.barPersentase.getMaxWidth() * listProyek.get(position).getPersentase()/100);
        ViewGroup.LayoutParams layout = holder.barPersentase.getLayoutParams();
        layout.width = listProyek.get(position).getPersentase() * width/100;
        holder.barPersentase.setLayoutParams(layout);
    }

    @Override
    public int getItemCount() {
        return listProyek.size();
    }

    public static class ProyekViewHolder extends RecyclerView.ViewHolder {
        protected TextView pNama, pAuthor, terkumpul, target, persentase, sisaWaktu, barPersentase;
        protected ImageView pGambar;
        private LinearLayout progressContainer;

        public ProyekViewHolder(View v){
            super(v);
            pNama = (TextView) v.findViewById(R.id.textTitle);
            pGambar = (ImageView) v.findViewById(R.id.gambar);
            pAuthor = (TextView) v.findViewById(R.id.textOrganizer);
            terkumpul = (TextView) v.findViewById(R.id.from);
            target = (TextView) v.findViewById(R.id.total);
            persentase = (TextView) v.findViewById(R.id.percentage);
            sisaWaktu = (TextView) v.findViewById(R.id.sisaWaktu);
            barPersentase = (TextView) v.findViewById(R.id.barPersentase);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Donate.class);
                    intent.putExtra(Donate.EXTRA_JUDUL, pNama.getText().toString());
                    intent.putExtra(Donate.EXTRA_AUTHOR, pAuthor.getText().toString());
                    v.getContext().startActivity(intent);
                }
            });

            progressContainer = (LinearLayout) v.findViewById(R.id.progressContainer);
        }
    }

    public String getNominal(int harga)
    {
        return getNominalHelper(1, harga);
    }

    private String getNominalHelper(int counter, int harga)
    {
        if(harga < 10) return ""+harga;
        else
        {
            if(counter%3==0) return ""+ getNominalHelper(counter+1, harga/10) + "." + (harga%10);
            else return ""+ getNominalHelper(counter+1, harga/10) + (harga%10);
        }
    }
}
