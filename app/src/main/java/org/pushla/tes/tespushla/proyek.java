package org.pushla.tes.tespushla;

/**
 * Created by Luqman on 28/02/2015.
 */
public class proyek {
    private String namaProyek;
    private String urlGambar;

    public proyek(String namaProyek, String urlGambar) {
        this.namaProyek = namaProyek;
        this.urlGambar = urlGambar;
    }

    public String getNamaProyek() {
        return namaProyek;
    }

    public void setNamaProyek(String namaProyek) {
        this.namaProyek = namaProyek;
    }

    public String getUrlGambar() {
        return urlGambar;
    }

    public void setUrlGambar(String urlGambar) {
        this.urlGambar = urlGambar;
    }
}
