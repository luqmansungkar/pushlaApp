package org.pushla.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Luqman on 28/02/2015.
 */
public class Proyek {
    private String namaProyek;
    private String urlGambar;
    private String author;
    private int persentase;
    private int sisaWaktu, terkumpul, target;
    private Bitmap gambar;
    private String id;
    private String deskripsi;
    private int pendukung;
    public Proyek()
    {

    }

    public String getAuthor() {
        return author;
    }

    public int getTerkumpul() {
        return terkumpul;
    }

    public int getTarget() {
        return target;
    }

    public int getPersentase() {
        return persentase;
    }

    public int getSisaWaktu() {
        return sisaWaktu;
    }

    public String getNamaProyek() {
        return namaProyek;
    }

    public String getUrlGambar() {
        return urlGambar;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setNamaProyek(String namaProyek) {
        this.namaProyek = namaProyek;
    }

    public void setPersentase(int persentase) {
        this.persentase = persentase;
    }

    public void setSisaWaktu(int sisaWaktu) {
        this.sisaWaktu = sisaWaktu;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public void setTerkumpul(int terkumpul) {
        this.terkumpul = terkumpul;
    }

    public void setUrlGambar(String urlGambar) {
        this.urlGambar = urlGambar;
    }

    public void setGambar(Bitmap gambar) {
        this.gambar = gambar;
    }

    public Bitmap getGambar() {
        return gambar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setPendukung(int pendukung) {
        this.pendukung = pendukung;
    }

    public int getPendukung() {
        return pendukung;
    }
}
