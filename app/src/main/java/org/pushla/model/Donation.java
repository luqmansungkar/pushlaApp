package org.pushla.model;

/**
 * Created by Anjar_Ibnu on 29/03/2015.
 */
public class Donation {
    private String nominal, id;

    public Donation(String nominal, String id)
    {
        this.nominal = nominal;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public void setId(String id) {
        this.id = id;
    }
}
