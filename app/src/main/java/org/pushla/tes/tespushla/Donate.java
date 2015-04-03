package org.pushla.tes.tespushla;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.pushla.donateSender.Operator;
import org.pushla.model.Donation;
import org.pushla.util.ReportSender;
import org.pushla.util.URLImageParser;

import java.util.ArrayList;
import java.util.HashMap;


public class Donate extends ActionBarActivity{
//    private Bitmap gambar;
//    private String judul, author;

    public static final String EXTRA_JUDUL = "judul";
    public static final String EXTRA_AUTHOR = "author";

    private ImageButton donateButton;

    private Drawable donateOn;

    private ArrayList<ImageButton> listButtonNominal;
    private ArrayList<Drawable> listButtonOff, listButtonOn;

    private HashMap<Integer, Integer> mapId;

    private TextView txtJudul, txtAuthor, txtPendukung, txtTerkumpul, txtTarget, txtSisaWaktu;
    private WebView txtDeskripsi;

    private boolean buttonPushActive = false;
    final Context context = this;

    private Button button_cancel, button_donate_ok;

    public static Dialog dialog, dialogLoading, dialogGagal;

    private boolean isLoadingDonation = false;
    private Listener smsListener;
    public static Donate donate;
    public static Bundle donateBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donate);

        donateBundle = savedInstanceState;

//        this.judul = getIntent().getStringExtra(EXTRA_JUDUL).trim();
//        this.author = getIntent().getStringExtra(EXTRA_AUTHOR).trim();

        this.txtJudul = (TextView)findViewById(R.id.textTitle);
        this.txtAuthor = (TextView)findViewById(R.id.textOrganizer);
        this.txtPendukung = (TextView)findViewById(R.id.jumlahPendukung);
        this.txtTerkumpul =(TextView)findViewById(R.id.from);
        this.txtTarget = (TextView)findViewById(R.id.total);
        this.txtSisaWaktu = (TextView)findViewById(R.id.sisaWaktu);
        this.txtDeskripsi = (WebView) findViewById(R.id.deskripsi);

        Donation curDonation = ResourceManager.getCurrentDonation();
        txtJudul.setText(curDonation.getNamaProyek());
        txtAuthor.setText(curDonation.getAuthor());
        this.txtPendukung.setText(""+curDonation.getPendukung());
        this.txtTerkumpul.setText(""+curDonation.getTerkumpul());
        this.txtTarget.setText("dari "+curDonation.getTarget());
        this.txtSisaWaktu.setText(""+curDonation.getSisaWaktu());
        System.out.println("Deskripsi " + curDonation.getDeskripsi());
//        this.txtDeskripsi.setText(Html.fromHtml(curDonation.getDeskripsi(), new URLImageParser(txtDeskripsi, this), null));
        this.txtDeskripsi.loadData(curDonation.getDeskripsi(), "text/html; charset=UTF-8", null);
//        this.gambar = ResourceManager.getGambar(judul, this);
        Bitmap gambar = curDonation.getGambar();
//        System.out.println("gambar = " + gambar);
        //ganti background sesuai proyek
//        RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.start_point);
        ImageView img = (ImageView) findViewById(R.id.img_donate);
        if(gambar != null)
            img.setImageDrawable(new BitmapDrawable(getResources(), gambar));
//        rLayout.setBackground(new BitmapDrawable(getResources(),gambar));

        donateButton = (ImageButton) findViewById(R.id.donateButton);
        donateButton.setOnClickListener(new SendPulsaAction(this));

        listButtonNominal = new ArrayList<>();
        listButtonOff = new ArrayList<>();
        listButtonOn = new ArrayList<>();
        mapId = new HashMap<>();

        listButtonOff.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_2500)));
        listButtonOff.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_5000)));
        listButtonOff.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_7500)));
        listButtonOff.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_10000)));

        listButtonOn.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_2500_on)));
        listButtonOn.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_5000_on)));
        listButtonOn.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_7500_on)));
        listButtonOn.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_10000_on)));

        donateOn = new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_donate));

//        for(int ii=0; ii<listButtonNominal.size(); ii++)
//        {
//            ImageButton b = listButtonNominal.get(ii);
////            System.out.println("Nambahin Listener!");
//            b.setOnClickListener(new NominalButtonListener());
//        }

        mapId.put(R.id.button_6, 0);
        mapId.put(R.id.button_10, 1);
        mapId.put(R.id.button_15, 2);
        mapId.put(R.id.button_20, 3);

        donateButton.setImageDrawable(donateOn);
        buttonPushActive = true;

        smsListener = new Listener();
        donate = this;
    }

    @Override
    protected void onResume() {
        registerReceiver(smsListener, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(smsListener);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_donate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayMessage(String message)
    {
        Toast.makeText(this, message,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if(isLoadingDonation) return;
        super.onBackPressed();
    }

    class SendPulsaAction implements View.OnClickListener {
        private Donate parent;

        public SendPulsaAction(Donate parent) {
            this.parent = parent;
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == donateButton.getId())
            {
                if(!buttonPushActive) return;
                try {
                    dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.donate_popup);
                    //dialog.setTitle("Pilih jumlah donasi");
                    dialog.show();

                    listButtonNominal.clear();
                    listButtonNominal.add((ImageButton)dialog.findViewById(R.id.button_6));
                    listButtonNominal.add((ImageButton)dialog.findViewById(R.id.button_10));
                    listButtonNominal.add((ImageButton)dialog.findViewById(R.id.button_15));
                    listButtonNominal.add((ImageButton)dialog.findViewById(R.id.button_20));
                    for(int ii=0; ii<listButtonNominal.size(); ii++)
                    {
                        ImageButton b = listButtonNominal.get(ii);
//            System.out.println("Nambahin Listener!");
                        b.setOnClickListener(new NominalButtonListener());
                    }

//                    if(button_cancel == null) {
                        button_cancel = (Button)dialog.findViewById(R.id.button_cancel);
                        button_cancel.setOnClickListener(new PopUpDonationButton(parent));
//                    }
//                    if(button_donate_ok == null) {
                        button_donate_ok = (Button) dialog.findViewById(R.id.button_donate_ok);
                        button_donate_ok.setOnClickListener(new PopUpDonationButton(parent));
//                    }
                } catch (Exception e) {
                    parent.displayMessage("Transfer pulsa gagal.\n");
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    class NominalButtonListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            System.out.println("Klik nominal button");
            int nominal = 2500;
            if(mapId.containsKey(v.getId()))
            {
                System.out.println("Key => " + mapId.get(v.getId()));
                donateButton.setImageDrawable(donateOn);
                for(int ii=0; ii<listButtonNominal.size(); ii++)
                {
                    ImageButton b = listButtonNominal.get(ii);
                    b.setImageDrawable(listButtonOff.get(ii));
                }
                int temp = mapId.get(v.getId());
                listButtonNominal.get(temp).setImageDrawable(listButtonOn.get(temp));
//                donateButton.setImageDrawable(donateOn);
//                buttonPushActive = true;
            }

            if(v.getId() == R.id.button_6)
            {
                if(ResourceManager.getCurrentDonation().isXl())
                {
                    nominal = 2500;
                }
                else
                {
                    nominal = 6000;
                }
                ResourceManager.setCurrentNominalDonation(nominal);
            }
            else if(v.getId() == R.id.button_10)
            {
                if(ResourceManager.getCurrentDonation().isXl())
                {
                    nominal = 5000;
                }
                else
                {
                    nominal = 10000;
                }
                ResourceManager.setCurrentNominalDonation(nominal);
            }
            else if(v.getId() == R.id.button_15)
            {
                if(ResourceManager.getCurrentDonation().isXl())
                {
                    nominal = 7500;
                }
                else
                {
                    nominal = 15000;
                }
                ResourceManager.setCurrentNominalDonation(nominal);
            }
            else if(v.getId() == R.id.button_20)
            {
                if(ResourceManager.getCurrentDonation().isXl())
                {
                    nominal = 1000;
                }
                else
                {
                    nominal = 20000;
                }
                ResourceManager.setCurrentNominalDonation(nominal);
            }
        }
    }

    class PopUpDonationButton implements View.OnClickListener{
        private SmsManager smsManager;
        private Donate parent;

        public PopUpDonationButton(Donate parent) {
            this.smsManager = SmsManager.getDefault();
            this.parent = parent;
            ResourceManager.setCurrentNominalDonation(2500);
        }
        @Override
        public void onClick(View v) {
            System.out.println("Pop Up Button Pressed");
            if(v.getId() == button_cancel.getId())
            {
                dialog.cancel();
            }
            else if(v.getId() == button_donate_ok.getId())
            {
                dialog.dismiss();


                dialogLoading = new Dialog(context);
                dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogLoading.setContentView(R.layout.donate_process);
                dialogLoading.setCancelable(false);
                //dialog.setTitle("Pilih jumlah donasi");
                dialogLoading.show();
//                parent.setContentView(R.layout.donation_loading);
                isLoadingDonation = true;

                ArrayList<Integer> temp = ResourceManager.getCurrentDonation().getListNominal();
                for(int ii = 0; ii<temp.size(); ii++)
                {
                    String nomorTujuan = Operator.getDestinationNumber(parent);
                    int operatorCode = Operator.getDeviceOperator(parent.getApplicationContext());
                    String nominal = "" + Operator.getTotalDonasi(temp.get(ii), parent.getApplicationContext());
                    String operatorNumber = Operator.getOperatorNumber(operatorCode, nomorTujuan, nominal);
                    String smsContent = Operator.getSMSContent(operatorCode, nomorTujuan, nominal);
                    smsManager.sendTextMessage(operatorNumber, null, smsContent, null, null);
                }
            }

        }
    }

    public static class Listener extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
//            showFailedMessage();
            // TODO Auto-generated method stub
            System.out.println("Menerima pesan");
            if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
                SmsMessage[] msgs = null;
                if (bundle != null){
                    //---retrieve the SMS message received---
                    try{
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for(int i=0; i<msgs.length; i++){
                            msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                            int msg_from = Integer.parseInt(msgs[i].getOriginatingAddress());
                            String msgBody = msgs[i].getMessageBody();
                            //System.out.println("" + msg_from + "\n" + msgBody);
                            if(Operator.listOperatorNumber.contains(msg_from))
                            {
                                sendResponse(context, msg_from, msgBody);
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

        private void sendResponse(Context context, int sender, String body)
        {
            System.out.println("body = " + body);
            if(body.toLowerCase().contains("maaf"))
            {
                System.out.println("Harusnya gagal nih");
                showFailedMessage();
                return;
            }
            SmsManager smsManager = SmsManager.getDefault();
            String response = "";
            //if sender is INDOSAT
            if(sender == Operator.INDOSAT_NUMBER) {
                String[] temp = body.split("(?<=[.!?])\\s* ");
                String token = "";
                for (int ii = 0; ii < temp.length && token.isEmpty(); ii++) {
                    String foo = temp[ii];
                    String[] fooTemp = foo.split(" ");
                    boolean hasToken = false;
                    for (int jj = 0; jj < fooTemp.length; jj++) {
                        if (fooTemp[jj].equalsIgnoreCase("token")) hasToken = true;
                    }

                    if (hasToken) {
                        for (int jj = 0; jj < fooTemp.length && token.isEmpty(); jj++) {
                            int tempResult = parseInt(fooTemp[jj]);
                            if (tempResult != -1) token += tempResult;
                        }
                    }
                }
                if(token.isEmpty()) return;
                response = "OK " + token;
            }
            else if(sender == Operator.XL_NUMBER)
            {
                if(body.contains("ketik Y") || body.contains("Ketik Y"))
                {
                    response = "Y";
                }
                else
                {
                    return;
                }
            }
            else if(sender == Operator.TELKOMSEL_NUMBER)
            {
                if(body.contains("Ketik YA"))
                {
                    response = "YA";
                }
                else
                {
                    return;
                }
            }
            else
            {
                return;
            }
            smsManager.sendTextMessage(""+sender, null, response, null, null);
            Toast.makeText(context, "Transfer berhasil :)",
                    Toast.LENGTH_LONG).show();
            //coba2 kirim report ke server
            sendResponse(ResourceManager.getCurrentDonation());
        }

        private void sendResponse(Donation d)
        {
            ReportSender reportSender = new ReportSender(ResourceManager.getEmail(), d.getNominal()+"", d.getId());
            reportSender.execute();
        }

        private int parseInt(String str)
        {
            try
            {
                return Integer.parseInt(str);
            }
            catch(Exception e)
            {

            }
            return -1;
        }

        private void showFailedMessage()
        {
//            donate.onCreate(donateBundle);
            if(dialogLoading != null) dialogLoading.dismiss();
            dialogGagal = new Dialog(donate);
            dialogGagal.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogGagal.setContentView(R.layout.donate_failed);
//            dialog.setTitle("Pilih jumlah donasi");
            dialogGagal.show();
        }
    }
}