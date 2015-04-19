package org.pushla.model;

/**
 * Created by Anjar_Ibnu on 19/04/2015.
 */
public class SuccessDonation {
    private int nominal;
    private int id;
    private String judul;
    private String waktu;

    public int getNominal() {
        return nominal;
    }

    public String getJudul() {
        return judul;
    }

    public int getId() {
        return id;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public RiwayatDonasi toRiwayatDonasi()
    {
        RiwayatDonasi rd = new RiwayatDonasi();
        rd.setWaktu(this.waktu);
        rd.setJudul(this.judul);
        rd.setNominal(this.nominal);
        return rd;
    }
}
