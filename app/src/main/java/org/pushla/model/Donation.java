package org.pushla.model;

/**
 * Created by Anjar_Ibnu on 29/03/2015.
 */
public class Donation extends Proyek{
    private boolean xl;
    private int nominal;
    private int pendukung;

    public Donation()
    {
        super();
        xl = true;
        pendukung = -1;
    }

    public void setXl(boolean xl) {
        this.xl = xl;
    }

    public int getNominal() {
        return nominal;
    }

    public boolean isXl() {
        return xl;
    }

    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    public int getPendukung() {
        return pendukung;
    }

    public void setPendukung(int pendukung) {
        this.pendukung = pendukung;
    }
}
