package org.pushla.tes.tespushla;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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


public class Donate extends ActionBarActivity {
    private Bitmap gambar;
    private String judul;

    public static final String EXTRA_JUDUL = "judul";

    private Button donateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donate);

        this.judul = getIntent().getStringExtra(EXTRA_JUDUL).trim();
        System.out.println("judul = <" + judul + ">");
        this.gambar = ResourceManager.getGambar(judul, this);
        System.out.println("gambar = " + gambar);
        //ganti background sesuai proyek
        RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.start_point);
        if(gambar != null)
        rLayout.setBackground(new BitmapDrawable(getResources(),gambar));

        donateButton = (Button) findViewById(R.id.button_push);
        donateButton.setOnClickListener(new SendPulsaAction(this));
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
                String nominal = "1000";
                String operatorNumber = Operator.getOperatorNumber(operatorCode, nomorTujuan);
                String smsContent = Operator.getSMSContent(operatorCode, nomorTujuan, nominal);
                smsManager.sendTextMessage(operatorNumber, null, smsContent, null, null);
            } catch (Exception e) {
                parent.displayMessage("Transfer pulsa gagal.\n");
                System.out.println(e.getMessage());
            }
        }
    }
}
