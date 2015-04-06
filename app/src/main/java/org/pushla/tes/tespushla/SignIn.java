package org.pushla.tes.tespushla;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.pushla.donateSender.Operator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luqman on 29/03/2015.
 */
public class SignIn extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult>{

    private static final int RC_SIGN_IN = 0;
    String nama,email,usernme;

    private GoogleApiClient mGoogleApiClient;

    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private SignInButton btnSignIn;

    private Button btnRevoke;

    private ProgressDialog pDialog, qDialog;
    private SharedPreferences sp;
    public static final String PREFS = "prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);

        //btnSignIn.setColorScheme(SignInButton.COLOR_LIGHT);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGplus();
            }
        });

        qDialog = new ProgressDialog(SignIn.this);
        qDialog.setMessage("Connecting...");
        qDialog.setCancelable(false);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    private void revoke(){
        if (mGoogleApiClient.isConnected()){
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            mGoogleApiClient.connect();
                        }
                    });
        }
    }

    protected void onStart(){
        super.onStart();
        Log.d("debug: ","on Start");
        mGoogleApiClient.connect();
    }

    protected void onStop(){
        super.onStop();
        Log.d("debug: ","on Stop");
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
            mSignInClicked = false;
        }
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent){
        Log.d("debug: ","on Activity Result");
        if (requestCode == RC_SIGN_IN){
            if (responseCode != RESULT_OK){
                mSignInClicked = false;
                Log.d("debug: ","Sesuatu 1");
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()){

                qDialog.show();
                mGoogleApiClient.connect();
                Log.d("debug: ","Sesuatu 2");
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("debug: ","on Connected");
        if (qDialog.isShowing()){
            qDialog.dismiss();
        }
        mSignInClicked = false;
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
        Toast.makeText(this, "User is connected!", Toast.LENGTH_SHORT).show();

        if (mGoogleApiClient.isConnected()) {
            Log.d("debug: ","on Connected masuk if");
            getProfileInformation();
            new addUser().execute();
        }else{
            Toast.makeText(SignIn.this,"Gagal sign in",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("debug: ","on Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("debug: ","on Connection Failed");
        if (qDialog.isShowing()){
            qDialog.dismiss();
        }
        if (!connectionResult.hasResolution()){
            //GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            Toast.makeText(SignIn.this,"Koneksi gagal, periksa koneksi internet anda", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mIntentInProgress){
            mConnectionResult = connectionResult;
            if (mSignInClicked){
                resolveSignInError();
            }
        }
    }

    private void signInWithGplus(){
        //mGoogleApiClient.connect();
        Log.d("debug: ","Sign in with g plus");
        if (!mGoogleApiClient.isConnecting()){
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    private void resolveSignInError(){
        Log.d("debug: ","resolve sign in error");
        if (mConnectionResult.hasResolution()){
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);

            }catch (IntentSender.SendIntentException e){
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void getProfileInformation(){
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null){
            //if(true){
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                nama = currentPerson.getDisplayName();
                email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                String[] split = email.split("@");
                usernme = split[0];

            }else{
                Toast.makeText(getApplicationContext(), "Person information is null", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {

    }

    private class addUser extends AsyncTask<Void, Void, Void> {
        String response = "";
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SignIn.this);
            pDialog.setMessage("Logging in...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();

            String url = "http://pushla.org/server/user/tambahuser";

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("kunci","CMUeRSv4TKDFbJhN8VcWRrcfuWYJNJ"));
            nameValuePairs.add(new BasicNameValuePair("id_user",email));
            nameValuePairs.add(new BasicNameValuePair("email",email));
            nameValuePairs.add(new BasicNameValuePair("username",usernme));
            nameValuePairs.add(new BasicNameValuePair("nama",nama));
            nameValuePairs.add(new BasicNameValuePair("operator", Operator.readOperatorName(SignIn.this)));

            response = sh.makeServiceCall(url, ServiceHandler.POST, nameValuePairs);

            Log.d("Response: ", "> " + response);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            JSONObject hasil;
            String status = "";
            try {
                hasil = new JSONObject(response);
                status = hasil.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("add user: ","> "+status);

            if (status.equalsIgnoreCase("sukses")){
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                    sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("nama",nama);
                    editor.putString("email",email);
                    editor.putBoolean("loggedIn",true);
                    editor.commit();
                    Intent mainIntent = new Intent(SignIn.this,MainActivity.class);
                    System.out.println("Banyaknya proyek = " + SplashScreen.listProyek.size());
                    SignIn.this.finish();
                    SignIn.this.startActivity(mainIntent);
                }
            }else{
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                    Log.d("response: ", "> nama: "+nama+", email: "+email+", username: "+usernme+", operator: "+Operator.readOperatorName(SignIn.this));
                    Toast.makeText(SignIn.this,"Gagal mendaftarkan user, periksa koneksi internet anda", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
