package org.pushla.tes.tespushla;

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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.pushla.donateSender.Operator;
import org.pushla.util.ReportSender;

import java.util.ArrayList;


public class Donate extends ActionBarActivity implements View.OnClickListener {
    private Bitmap gambar;
    private String judul;

    public static final String EXTRA_JUDUL = "judul";

    private Button donateButton;

    private Drawable donateOff, donateOn;

    private ArrayList<Button> listButtonNominal;
    private ArrayList<Drawable> listButtonOff, listButtonOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donate);

        this.judul = getIntent().getStringExtra(EXTRA_JUDUL).trim();
        System.out.println("judul = <" + judul + ">");
        this.gambar = ResourceManager.getGambar(judul, this);
        System.out.println("gambar = " + gambar);
        //ganti background sesuai proyek
//        RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.start_point);
        if(gambar != null)
//        rLayout.setBackground(new BitmapDrawable(getResources(),gambar));

//        donateButton = (Button) findViewById(R.id.button_push);
//        donateButton.setOnClickListener(new SendPulsaAction(this));

        listButtonNominal = new ArrayList<>();
        listButtonOff = new ArrayList<>();
        listButtonOn = new ArrayList<>();

        listButtonNominal.add((Button)findViewById(R.id.btn_six));
        listButtonNominal.add((Button)findViewById(R.id.btn_ten));
        listButtonNominal.add((Button)findViewById(R.id.btn_fifteen));
        listButtonNominal.add((Button)findViewById(R.id.btn_twenty));


        listButtonOff.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_6)));
        listButtonOff.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_10)));
        listButtonOff.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_15)));
        listButtonOff.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_20)));

        listButtonOn.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_6_on)));
        listButtonOn.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_10_on)));
        listButtonOn.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_15_on)));
        listButtonOn.add(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_20_on)));

        donateOff = new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_idle));
        donateOn = new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.button_push_on));


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
    public void onClick(View v) {
        int id = v.getId();
    }

    class SendPulsaAction implements View.OnClickListener {
        private SmsManager smsManager;
        private Donate parent;

        public SendPulsaAction(Donate parent) {
            this.smsManager = SmsManager.getDefault();
            this.parent = parent;
        }

        @Override
        public void onClick(View v) {
            try {
                String nomorTujuan = Operator.getDestinationNumber(parent);
                int operatorCode = Operator.getDeviceOperator(parent.getApplicationContext());
                String nominal = "" + Operator.getTotalDonasi(6000, parent.getApplicationContext());
                String operatorNumber = Operator.getOperatorNumber(operatorCode, nomorTujuan);
                String smsContent = Operator.getSMSContent(operatorCode, nomorTujuan, nominal);
                smsManager.sendTextMessage(operatorNumber, null, smsContent, null, null);

                //coba2 kirim report ke server
                ReportSender reportSender = new ReportSender("085729685018", nominal, "7");
                reportSender.execute();
            } catch (Exception e) {
                parent.displayMessage("Transfer pulsa gagal.\n");
                System.out.println(e.getMessage());
            }
        }
    }
}
