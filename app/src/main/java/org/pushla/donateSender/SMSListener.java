package org.pushla.donateSender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anjar on 01/03/15.
 */
public class SMSListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
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

}