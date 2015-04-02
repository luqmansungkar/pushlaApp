package org.pushla.tes.tespushla;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.pushla.model.Donation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by anjar on 01/03/15.
 */
public class ResourceManager {
    private static final String PREFS = "pref";
    private static final String KEY_LIST_GAMBAR = "list gambar";
    private static Donation currentDonation;
    public static void saveGambar(String namaFile, Bitmap gambar, Activity activity, boolean delete)
    {
        if(isSaved(namaFile, activity) || gambar == null) return;
        try {
            //Write file
            FileOutputStream stream = activity.openFileOutput(namaFile, Context.MODE_PRIVATE);
            gambar.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Cleanup
            stream.close();
            if(delete)
            {
                gambar.recycle();
            }
            System.out.println("Menyimpan gambar <"+namaFile+">");
            //Change state to Saved
            setSaved(namaFile, activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getGambar(String namaFile, Activity activity)
    {
        System.out.println("Membaca gambar <" + namaFile + ">");
        if(!isSaved(namaFile, activity)) {
            System.out.println("Gambar belom disimpan :(");
            return null;
        }
        System.out.println("Gambar sudah disimpan");
        Bitmap bmp = null;
        try {
            FileInputStream is = activity.openFileInput(namaFile);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    private static boolean isSaved(String namaFile, Activity activity)
    {
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        boolean saved = sharedPref.getBoolean(namaFile, false);
        return saved;
    }

    private static void setSaved(String namaFile, Activity activity)
    {
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(namaFile, true);
        editor.commit();
    }

    public static ArrayList<String> getListGambar(Activity activity)
    {
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String listGambar = sharedPref.getString(KEY_LIST_GAMBAR, "");
        ArrayList<String> hasil = new ArrayList<>();
        for(String s : listGambar.split(","))
        {
            hasil.add(s);
        }
        return hasil;
    }

    public static void setListGambar(Activity activity, String listGambar)
    {
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_LIST_GAMBAR, listGambar);
        editor.commit();
    }

    public static String getEmail()
    {
        return "ginanjar.ibnu@gmail.com";
    }

    public static void setCurrentDonation(String id, int target, int sisaWaktu,
                                          String judul, String deskripsi, Bitmap gambar) {
        if(currentDonation == null) currentDonation = new Donation();
        currentDonation.setId(id);
        currentDonation.setDeskripsi(deskripsi);
        currentDonation.setNamaProyek(judul);
        currentDonation.setTarget(target);
        currentDonation.setSisaWaktu(sisaWaktu);
        currentDonation.setGambar(gambar);
    }

    public static Donation getCurrentDonation() {
        return currentDonation;
    }
}
