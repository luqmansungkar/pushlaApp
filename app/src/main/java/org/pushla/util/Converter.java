package org.pushla.util;

/**
 * Created by Anjar_Ibnu on 03/04/2015.
 */
public class Converter {
    public static String getNominal(int harga)
    {
        return getNominalHelper(1, harga);
    }

    private static String getNominalHelper(int counter, int harga)
    {
        if(harga < 10) return ""+harga;
        else
        {
            if(counter%3==0) return ""+ getNominalHelper(counter+1, harga/10) + "." + (harga%10);
            else return ""+ getNominalHelper(counter+1, harga/10) + (harga%10);
        }
    }
}
