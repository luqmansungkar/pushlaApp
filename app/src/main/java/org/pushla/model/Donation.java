package org.pushla.model;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import org.pushla.tes.tespushla.Donate;
import org.pushla.tes.tespushla.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Anjar_Ibnu on 29/03/2015.
 */
public class Donation extends Proyek{
    private boolean xl;
    private ArrayList<Integer> listNominal;
    private int pendukung;
    private boolean isRunning;

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

    public int getReceivedDonation() {
        return receivedDonation;
    }

    public void addReceivedSonation()
    {
        this.receivedDonation++;
    }

    public void resetReceivedDonation()
    {
        this.receivedDonation = 0;
    }

    public boolean isReceivedCompletely()
    {
        return this.receivedDonation == listNominal.size();
    }

    public void startTimer(long time, final Donate donate)
    {
        isRunning = true;
        final Handler h = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 0){
                    showFailedDialog(donate);
                }else{
//                    showErrorDialog();
                }
            }
        };
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                resetReceivedDonation();
                h.sendEmptyMessage(0);
            }
        }, time);
    }

    private void showFailedDialog(Donate donate)
    {
        if(isRunning)
        donate.showFailedMessage();
    }

    public void stopTimer()
    {
        isRunning = false;
    }
}
