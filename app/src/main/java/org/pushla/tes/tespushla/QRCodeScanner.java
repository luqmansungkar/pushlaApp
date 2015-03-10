package org.pushla.tes.tespushla;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class QRCodeScanner extends ActionBarActivity {
    private Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);

        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_WIDTH", 1000);
                intent.putExtra("SCAN_HEIGHT", 1000);
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                //customize the prompt message before scanning
                intent.putExtra("PROMPT_MESSAGE", "Scanner Start!");
                startActivityForResult(intent, 0);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qrcode_scanner, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0)
        {
            System.out.println("Sudah mendapatkan hasil");
            String contents = data.getStringExtra("SCAN_RESULT");
            System.out.println("contents = " + contents);
            if (contents != null) {
                System.out.println(contents);
                Toast.makeText(getApplicationContext(),contents,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, Donate.class);
                intent.putExtra(Donate.EXTRA_JUDUL, contents);
                startActivity(intent);
            } else {
                System.out.println("Tidak menemukan konten");
                Toast.makeText(getApplicationContext(),"Tidak menemukan konten",Toast.LENGTH_LONG).show();
            }
        }
    }
}
