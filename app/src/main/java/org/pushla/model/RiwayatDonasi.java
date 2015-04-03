package org.pushla.model;

import org.pushla.tes.tespushla.ResourceManager;

/**
 * Created by Anjar_Ibnu on 31/03/2015.
 */
public class RiwayatDonasi {
    private String judul, waktu;
    private int harga;

    public RiwayatDonasi(){}
    public RiwayatDonasi(String id, String waktu, int harga)
    {
        this.harga = harga;
        this.judul = ResourceManager.getNamaProyek(id);
        this.waktu = waktu;
    }

    public int getHarga() {
        return harga;
    }

    public String getJudul() {
        return judul;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }
}
