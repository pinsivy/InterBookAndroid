package com.interbook.android.interbookandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import BLL.SessionManager;
import Model.Util;
import Parser.HttpManager;
import Parser.XMLParser;


public class MainActivity extends Activity {

    EditText txtEmail, txtPassword;
    String email, password;

    SessionManager session;
    ProgressBar pb;
    Button b;

    String regId;
    Util u;
    GoogleCloudMessaging gcm;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        b = (Button) findViewById(R.id.buttonSeconnecter);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        if(session.isLoggedIn())
        {
            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(i);
        }

        txtEmail = (EditText) findViewById(R.id.login);
        txtPassword = (EditText) findViewById(R.id.password);

    }

    public void BoutonSeConnecter(View v){

        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();

        if(email.trim().length() > 0 && password.trim().length() > 0){

            if (isOnline()) {
                new getUtilInBackground().execute();
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Please enter username and password", Toast.LENGTH_LONG).show();
        }
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private class getUtilInBackground extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            b.setText("");
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String secret = "interbook";
                String message = password;
                Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
                sha256_HMAC.init(secret_key);
                String hash = Base64.encodeToString(sha256_HMAC.doFinal(message.getBytes()),Base64.DEFAULT).trim();
                return HttpManager.postData("http://interbook-dev.com/BLL/IBWS.asmx/GetUtilByEmailMdp", "particulier=true&email=" + email + "&mdp=" + hash);
            }
            catch (Exception e){
                return "Error";
            }

        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {

                List<List<String>> genericList = XMLParser.parseFeed("UtilSimple", new String[]{"IdU"}, result);
                if(genericList.size() != 0) {
                    u = new Util(genericList.get(0).get(0));
                }
                signIn();
            }
        }
    }

    protected void signIn() {
        if(u != null)
        {
            session.createLoginSession(email, u.getId());

            if (TextUtils.isEmpty(regId)) {
                gcm = GoogleCloudMessaging.getInstance(this);
                regId = session.getInPref("regid");

                if (TextUtils.isEmpty(regId)) {
                    new registerInBackground().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "RegId existe deja. RegId: " + regId, Toast.LENGTH_LONG).show();
                }
            }
        } else{
            b.setText("Se connecter");
            pb.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Username/Password is incorrect", Toast.LENGTH_LONG).show();
        }
    }

    private class registerInBackground extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regId = gcm.register("200212735020");
                return HttpManager.postData("http://interbook-dev.com/BLL/IBWS.asmx/GetUtilAndroidByRegidIdu", "registerid=" + regId + "&idu=" + u.getId());

            } catch (IOException ex) {
                return "Error :" + ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {

                List<List<String>> genericList = XMLParser.parseFeed("UtilAndroidSimple", new String[]{"Id_Util_Android"}, result);

                if(genericList.size() == 0){
                    new registerInBackgroundFinal().execute();
                }
            }
        }
    }

    private class registerInBackgroundFinal extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return HttpManager.postData("http://interbook-dev.com/BLL/IBWS.asmx/InsertLine_Util_Android", "id_Util_Android=0&registerid=" + regId + "&idu=" + session.getInPref("idu"));
        }

        @Override
        protected void onPostExecute(String result) {
            session.storeInPref("regid", regId);

            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(i);
            finish();
        }
    }
}
