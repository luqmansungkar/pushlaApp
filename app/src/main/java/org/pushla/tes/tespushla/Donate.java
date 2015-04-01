package org.pushla.tes.tespushla;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.pushla.donateSender.Operator;
import org.pushla.util.ReportSender;

import java.util.ArrayList;
import java.util.HashMap;


public class Donate extends ActionBarActivity{
    private Bitmap gambar;
    private String judul, author;

    public static final String EXTRA_JUDUL = "judul";
    public static final String EXTRA_AUTHOR = "author";

    private ImageButton donateButton;

    private Drawable donateOff, donateOn;

    private ArrayList<ImageButton> listButtonNominal;
    private ArrayList<Drawable> listButtonOff, listButtonOn;

    private HashMap<Integer, Integer> mapId;

    private TextView txtJudul, txtAuthor;

    private boolean buttonPushActive = false;
    final Context context = this;

    private Button button_cancel, button_donate_ok;

    private Dialog dialog;

    private boolean isLoadingDonation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donate);

        this.judul = getIntent().getStringExtra(EXTRA_JUDUL).trim();
        this.author = getIntent().getStringExtra(EXTRA_AUTHOR).trim();

        this.txtJudul = (TextView)findViewById(R.id.textTitle);
        this.txtAuthor = (TextView)findViewById(R.id.textOrganizer);

        txtJudul.setText(judul);
        txtAuthor.setText(author);
        System.out.println("judul = <" + judul + ">");
        this.gambar = ResourceManager.getGambar(judul, this);
        System.out.println("gambar = " + gambar);
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
        }
    }

    class PopUpDonationButton implements View.OnClickListener{
        private SmsManager smsManager;
        private Donate parent;

        public PopUpDonationButton(Donate parent) {
            this.smsManager = SmsManager.getDefault();
            this.parent = parent;
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
                String nomorTujuan = Operator.getDestinationNumber(parent);
                int operatorCode = Operator.getDeviceOperator(parent.getApplicationContext());
                String nominal = "" + Operator.getTotalDonasi(6000, parent.getApplicationContext());
                String operatorNumber = Operator.getOperatorNumber(operatorCode, nomorTujuan);
                String smsContent = Operator.getSMSContent(operatorCode, nomorTujuan, nominal);
                smsManager.sendTextMessage(operatorNumber, null, smsContent, null, null);
                parent.setContentView(R.layout.donation_loading);
                isLoadingDonation = true;
            }
        }
    }
}