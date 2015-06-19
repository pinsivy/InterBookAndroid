package com.interbook.android.interbookandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import BLL.SessionManager;
import Model.Util;
import Parser.HttpManager;
import Parser.XMLParser;


public class MainActivity extends Activity {

    // Email, password edittext
    EditText txtUsername, txtPassword;

    // Session Manager Class
    SessionManager session;
    List<Util> lu;
    List<getUtil> tasks;
    ProgressBar pb;

    String regId;
    GoogleCloudMessaging gcm;
    Context context;
    TextView outputRegid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        outputRegid = (TextView) findViewById(R.id.outputRegid);
        if (TextUtils.isEmpty(regId)) {
            // Récupération du registerId du terminal ou enregistrement de ce dernier
            regId = registerGCM();
            outputRegid.append(regId);
            Log.d("", "GCM RegId: " + regId);
        } else {
            outputRegid.append(session.getRegistrationId());
            Toast.makeText(getApplicationContext(), "Déjà enregistré sur le GCM Server!", Toast.LENGTH_LONG).show();
        }

        if(session.isLoggedIn())
        {
            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(i);
        }


        txtUsername = (EditText) findViewById(R.id.login);
        txtPassword = (EditText) findViewById(R.id.password);

        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


    }


    public void BoutonSeConnecter(View v){

        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

        if(username.trim().length() > 0 && password.trim().length() > 0){

            if (isOnline()) {
                tasks = new ArrayList<>();
                getUtil getu = new getUtil();
                getu.execute("post", "http://interbook-dev.com/BLL/IBWS.asmx/GetUtilByEmailMdp", "particulier=true&email=ronan.pinsivy@gmail.com&mdp=130189");
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Please enter username and password", Toast.LENGTH_LONG).show();
        }
    }

    public String registerGCM() {
        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(regId)) {
            registerInBackground();
            Log.d("","registerGCM - enregistrement auprès du GCM server OK - regId: " + regId);
        } else {
            Toast.makeText(getApplicationContext(), "RegId existe déjà. RegId: " + regId, Toast.LENGTH_LONG).show();
        }
        return regId;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString("regId", "");
        if (registrationId.isEmpty()) {
            Log.i("", "registrationId non trouvé.");
            return "";
        }
        return registrationId;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register("200212735020");
                    msg = "Terminal enregistré, register ID=" + regId;
                    // On enregistre le registerId dans les SharedPreferences
                    session.storeRegistrationId(regId);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("","Error: " + msg);
                }
                return msg;
            }
        }.execute(null, null, null);
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

    private class getUtil extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected String doInBackground(String... params) {
            String content = "";
            if (params[0] == "get")
                content = HttpManager.getData(params[1]);
            else if (params[0] == "post")
                content = HttpManager.postData(params[1], params[2]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {

                List<List<String>> genericList = XMLParser.parseFeed("UtilSimple", new String[]{"idu"}, result);
                lu = new ArrayList<>();

                for(List<String> lo : genericList) {
                    lu.add(new Util(lo.get(0)));
                }

                signIn();
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
        }
    }

    protected void signIn() {
        if(lu != null)
        {
            session.createLoginSession("Android Hive", "anroidhive@gmail.com");

            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(i);
            finish();
        } else{
            Toast.makeText(getApplicationContext(), "Username/Password is incorrect", Toast.LENGTH_LONG).show();
        }
    }
}
