package org.pushla.model;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import org.pushla.tes.tespushla.Donate;
import org.pushla.tes.tespushla.R;
import org.pushla.tes.tespushla.ResourceManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Anjar_Ibnu on 29/03/2015.
 */
public class Donation extends Proyek{
    private boolean xl;
    private int pendukung;
    private boolean isRunning;
    private int nominal = -1;

    private int receivedDonation = 0;

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


    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    public int getNominal()
    {
//        int hasil = 0;
//        for (int i = 0; i < listNominal.size(); i++) {
//            hasil += listNominal.get(i);
//        }
//        return hasil;
        return nominal;
    }

    public int getReceivedDonation() {
        return receivedDonation;
    }

    public void addReceivedSonation()
    {
//        this.receivedDonation++;
        this.receivedDonation = nominal;
    }

    public void resetReceivedDonation()
    {
        this.receivedDonation = 0;
    }

    public boolean isReceivedCompletely()
    {
//        return this.receivedDonation == listNominal.size();
        return this.receivedDonation == this.nominal;
    }

    public void startFakeTimer(long time, final Donate donate)
    {
        isRunning = true;
        final Handler h = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 0){
                    donate.showSuccessPage();
                }else{
//                    showErrorDialog();
                }
            }
        };
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
//                resetReceivedDonation();
                h.sendEmptyMessage(0);
            }
        }, time);
    }

    private void showFailedDialog(Donate donate)
    {
        if(isRunning)
        donate.showFailedMessage(true);
    }

    public void stopTimer()
    {
        isRunning = false;
    }

    public SuccessDonation toSuccessDonation()
    {
        SuccessDonation tmp = new SuccessDonation();
        tmp.setWaktu(ResourceManager.getCurrentTime());
        tmp.setId(Integer.parseInt(getId()));
        tmp.setNominal(getNominal());
        tmp.setJudul(getNamaProyek());
        return tmp;
    }
}
