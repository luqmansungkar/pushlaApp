package org.pushla.donateSender;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import java.util.ArrayList;

/**
 * Created by anjar on 01/03/15.
 */
public class Operator {
    public static final int INDOSAT = 1;
    public static final int XL = 2;
    public static final int TELKOMSEL = 3;
    public static final int INDOSAT_NUMBER = 151;
    public static final int XL_NUMBER = 168;
    public static final int TELKOMSEL_NUMBER = 858;
    public static final int UNKNOWN = -1;
    public static final String PREFS_NAME = "PUSHLA_SETTING";
    public static final String STORED_PHONE_NUMBER = "PHONE_NUMBER";
    public static final String STORED_OPERATOR_NAME = "OPERATOR_NAME";
    public static final String STORED_USER_NAME = "STORED_USER_NAME";
    public static final String STORED_PASSWORD = "STORED_PASSWORD";
    public static ArrayList<Integer> listOperatorNumber;

    public static void init()
    {
        if(listOperatorNumber == null)
        {
            listOperatorNumber = new ArrayList<>();
            listOperatorNumber.add(Operator.INDOSAT_NUMBER);
            listOperatorNumber.add(Operator.XL_NUMBER);
            listOperatorNumber.add(Operator.TELKOMSEL_NUMBER);
        }
    }

    public static String getSMSContent(int operatorCode, String nomorTujuan, String nominal)
    {
        String result = "";
        if(operatorCode == Operator.INDOSAT)
        {
            result = "Transferpulsa " + nomorTujuan + " " + nominal;
        }
        else if(operatorCode == Operator.XL)
        {
            result = "BAGI " + nomorTujuan + " " + nominal;
        }
        else if(operatorCode == Operator.TELKOMSEL)
        {
            result = "TPULSA " + nominal;
        }
        return result;
    }

    public static String getOperatorNumber(int operatorCode, String destNumber)
    {
        String result = "";
        if(operatorCode == Operator.INDOSAT)
        {
            result = "" + Operator.INDOSAT_NUMBER;
        }
        else if(operatorCode == Operator.XL)
        {
            result = "" + Operator.XL_NUMBER;
        }
        else if(operatorCode == Operator.TELKOMSEL)
        {
            result = destNumber;
        }
        return result;
    }

    public static int getOperatorFromName(String operatorName)
    {
        if(operatorName.equalsIgnoreCase("Telkomsel"))
        {
            return Operator.TELKOMSEL;
        }
        else if(operatorName.equalsIgnoreCase("Indosat"))
        {
            return Operator.INDOSAT;
        }
        else if(operatorName.equalsIgnoreCase("XL"))
        {
            return Operator.XL;
        }
        return Operator.UNKNOWN;
    }

    public static int getDeviceOperator(Context context)
    {
        try
        {
            SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String operatorName = settings.getString(STORED_OPERATOR_NAME, "");
            if(operatorName.equalsIgnoreCase("Telkomsel"))
            {
                return Operator.TELKOMSEL;
            }
            else if(operatorName.equalsIgnoreCase("Indosat"))
            {
                return Operator.INDOSAT;
            }
            else if(operatorName.equalsIgnoreCase("XL"))
            {
                return Operator.XL;
            }
        }
        catch(Exception e){}
        return Operator.UNKNOWN;
    }

    public static void storeRegistrationData(Context context, String operator)
    {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
//        editor.putString(STORED_PHONE_NUMBER, phoneNumber);
//        editor.putString(STORED_PASSWORD, password);
//        editor.putString(STORED_USER_NAME, username);
        editor.putString(STORED_OPERATOR_NAME, operator);
        editor.commit();
    }

    public static String readOperatorName(Context context)
    {
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getSimOperatorName();
    }

    public static String getDestinationNumber(Context context)
    {
        String result = "-1";
        int operator = Operator.getDeviceOperator(context);
        if(operator == Operator.INDOSAT)
        {
            //result = "085725783863";
            result = "085729685018";
        }
        else if(operator == Operator.TELKOMSEL)
        {
            result = "082226595325";
        }
        else if(operator == Operator.XL)
        {
            result = "087837960462";
        }
        return result;
    }
}

