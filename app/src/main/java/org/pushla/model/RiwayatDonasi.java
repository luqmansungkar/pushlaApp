package org.pushla.model;

import org.pushla.tes.tespushla.ResourceManager;

/**
 * Created by Anjar_Ibnu on 31/03/2015.
 */
public class RiwayatDonasi implements Comparable<RiwayatDonasi>{
    private String judul, waktu;
    private int nominal;

    public RiwayatDonasi(){}
    public RiwayatDonasi(String id, String waktu, int nominal)
    {
        this.nominal = nominal;
        this.judul = ResourceManager.getNamaProyek(id);
        this.waktu = waktu;
    }

    public int getNominal() {
        return nominal;
    }

    public String getJudul() {
        return judul;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    @Override
    public int compareTo(RiwayatDonasi another) {
        return another.getWaktu().compareTo(this.getWaktu());
    }
}
