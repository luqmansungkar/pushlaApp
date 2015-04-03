package org.pushla.model;

import java.util.ArrayList;

/**
 * Created by Anjar_Ibnu on 29/03/2015.
 */
public class Donation extends Proyek{
    private boolean xl;
    private ArrayList<Integer> listNominal;
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

    public boolean isXl() {
        return xl;
    }

    public int getPendukung() {
        return pendukung;
    }

    public void setPendukung(int pendukung) {
        this.pendukung = pendukung;
    }

    public ArrayList<Integer> getListNominal() {
        return listNominal;
    }

    public void setListNominal(int nominal) {
        this.listNominal = new ArrayList<>();
        if(isXl())
        {
            if(nominal == 2500)
            {
                listNominal.add(2500);
            }
            else if(nominal==5000)
            {
                listNominal.add(5000);
            }
            else if(nominal == 7500)
            {
                listNominal.add(5000);
                listNominal.add(2500);
            }
            else if(nominal == 10000)
            {
                listNominal.add(5000);
                listNominal.add(5000);
            }
        }
        else
        {
            listNominal.add(nominal);
        }
    }

    public int getNominal()
    {
        int hasil = 0;
        for (int i = 0; i < listNominal.size(); i++) {
            hasil += listNominal.get(i);
        }
        return hasil;
    }
}
