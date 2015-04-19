package org.pushla.tes.tespushla;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.pushla.model.Donation;
import org.pushla.model.SuccessDonation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anjar on 01/03/15.
 */
public class ResourceManager {
    private static final String PREFS = "pref";
    private static final String KEY_LIST_GAMBAR = "list gambar";
    private static final String KEY_DONASI_SUKSES = "donasi_sukses";
    private static Donation currentDonation;
    private static Map<String, String> hashNamaProyek;
    public static final String PREFS_LOGIN = "prefs";
    public static void saveGambar(String namaFile, Bitmap gambar, Context activity, boolean delete)
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

    public static Bitmap getGambar(String namaFile, Context activity)
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

    private static boolean isSaved(String namaFile, Context activity)
    {
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        boolean saved = sharedPref.getBoolean(namaFile, false);
        return saved;
    }

    private static void setSaved(String namaFile, Context activity)
    {
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(namaFile, true);
        editor.commit();
    }

    public static ArrayList<String> getListGambar(Context activity)
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

    public static void setListGambar(Context activity, String listGambar)
    {
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_LIST_GAMBAR, listGambar);
        editor.commit();
    }

    public static String getEmail(Context context)
    {
//        return "ginanjar.ibnu";
        SharedPreferences sp = context.getSharedPreferences(PREFS_LOGIN, Context.MODE_PRIVATE);
        return sp.getString("email","-1");
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

    public static void setCurrentNominalDonation(int nominal)
    {
        currentDonation.setNominal(nominal);
    }
    public static Donation getCurrentDonation() {
        return currentDonation;
    }

    public static void addNamaProyek(String id, String nama)
    {
        if(hashNamaProyek == null) hashNamaProyek = new HashMap<>();
        hashNamaProyek.put(id, nama);
    }

    public static String getNamaProyek(String id)
    {
        if(hashNamaProyek == null) return null;
        return hashNamaProyek.get(id);
    }

    //menangani kalo pas donasi malah jadi offline
    public static ArrayList<SuccessDonation> getListDonasiSukses(Context activity)
    {
        ArrayList<SuccessDonation> hasil = new ArrayList<>();
        SharedPreferences sharedPref = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String listDonasi = sharedPref.getString(KEY_DONASI_SUKSES, "");
        String[]arrayDonasi = listDonasi.split("|");
        for(int ii=0; ii<arrayDonasi.length; ii++)
        {
            String currentDonation = arrayDonasi[ii];
            String[] tempDonation = currentDonation.split(",");
            if(tempDonation.length >= 4)
            {
                SuccessDonation sd = new SuccessDonation();
                sd.setNominal(Integer.parseInt(tempDonation[0]));
                sd.setJudul(tempDonation[1]);
                sd.setId(Integer.parseInt(tempDonation[2]));
                sd.setWaktu(tempDonation[3]);
                hasil.add(sd);
            }
        }
        return hasil;
    }

    public static void addDonasiSukses(SuccessDonation sd, Context context)
    {
        ArrayList<SuccessDonation> listSuccess = getListDonasiSukses(context);
        listSuccess.add(sd);
        setListDonasiSukses(context, listSuccess);
    }

    public static void setListDonasiSukses(Context context, ArrayList<SuccessDonation> listSukses)
    {
        String hasil = "";
        for(int ii=0; ii<listSukses.size(); ii++)
        {
            SuccessDonation sd = listSukses.get(ii);
            if(sd!=null)
            {
                String tempDonation = "";
                tempDonation += sd.getNominal() + ",";
                tempDonation += sd.getJudul() + ",";
                tempDonation += sd.getId() + ",";
                tempDonation += sd.getWaktu();
                hasil += tempDonation;
                if(ii< listSukses.size()-1)
                {
                    hasil += "|";
                }
            }
        }

        SharedPreferences sharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_DONASI_SUKSES, hasil);
        editor.commit();
    }

    public static String getCurrentTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
}
