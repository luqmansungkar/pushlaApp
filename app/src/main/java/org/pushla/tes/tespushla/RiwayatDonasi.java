package org.pushla.tes.tespushla;

/**
 * Created by Anjar_Ibnu on 31/03/2015.
 */
public class RiwayatDonasi {
    private String judul, waktu, harga;

    public RiwayatDonasi(){}
    public RiwayatDonasi(String judul, String waktu, String harga)
    {
        this.harga = harga;
        this.judul = judul;
        this.waktu = waktu;
    }

    public String getHarga() {
        return harga;
    }

    public String getJudul() {
        return judul;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }
}
