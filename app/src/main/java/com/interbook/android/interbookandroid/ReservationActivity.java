package com.interbook.android.interbookandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.server.Message;

import java.util.List;

import BLL.SessionManager;
import Model.Reservation;
import Parser.HttpManager;
import Parser.XMLParser;


public class ReservationActivity extends Activity {

    SessionManager session;
    Message newMessage;
    Reservation resa;
    ProgressBar progressLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        session = new SessionManager(getApplicationContext());
        progressLoading = (ProgressBar) findViewById(R.id.progressLoading);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newMessage = null;
            } else {
                TextView txtContact = (TextView) findViewById(R.id.TxtContact);
                newMessage = (Message)extras.get("argument");
                txtContact.setText("Souhaitez-vous travailler avec " + newMessage.getData().get("nomEntreprise") + " du " + newMessage.getData().get("debut") + " au " + newMessage.getData().get("fin") + " ?");
                new getResaInBackground().execute();
            }
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

    public void BoutonAccepter(View v) {
        if (isOnline()) {
            resa.setIdEtatReservation("3");
            new updateResaInBackground().execute();
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }
    }

    public void BoutonRefuser(View v){
        if (isOnline()) {
            resa.setIdEtatReservation("2");
            new updateResaInBackground().execute();
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }
    }

    public void BoutonReponse(View v){
        if (isOnline()) {
            new insertContactInBackground().execute();
            new insertContact2InBackground().execute();
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }
    }

    private class getResaInBackground extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpManager.postData("http://interbook-dev.com/BLL/IBWS.asmx/GetReservationByIdr", "idr=" + newMessage.getData().get("idr"));
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {

                List<List<String>> genericList = XMLParser.parseFeed("ReservationSimple", new String[]{"IdU","id_Reservation","idUEntreprise","idUEmploye","debut","fin","id_EtatReservation"}, result);
                if(genericList.size() != 0) {
                    resa = new Reservation(genericList.get(0).get(0), genericList.get(0).get(1), genericList.get(0).get(5), genericList.get(0).get(2), genericList.get(0).get(3), genericList.get(0).get(4));
                }

                progressLoading.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class updateResaInBackground extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpManager.postData("http://interbook-dev.com/BLL/IBWS.asmx/InsertLine_Reservation",
                    "id_Reservation=" + resa.getId() +
                    "&idUEntreprise=" + resa.getIduEntreprise() +
                    "&debut=01-01-2015" +// resa.getDebut() +
                    "&fin=2015-01-01" + //resa.getFin() +
                    "&id_EtatReservation=" + resa.getIdEtatReservation() +
                    "&idUEmploye=" + resa.getIduEmploye()
            );
        }

        @Override
        protected void onPostExecute(String result) {
            progressLoading.setVisibility(View.INVISIBLE);
            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(i);
            finish();
        }
    }

    private class insertContactInBackground extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpManager.postData("http://interbook-dev.com/BLL/IBWS.asmx/InsertLine_Util_Contact",
                    "id_Util_Contact=0" +
                            "&iduFrom=" + resa.getIduEntreprise() +
                            "&iduTo=" + resa.getIduEmploye()
            );
        }
    }

    private class insertContact2InBackground extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpManager.postData("http://interbook-dev.com/BLL/IBWS.asmx/InsertLine_Util_Contact",
                    "id_Util_Contact=0" +
                            "&iduFrom=" + resa.getIduEmploye() +
                            "&iduTo=" + resa.getIduEntreprise()
            );
        }

        @Override
        protected void onPostExecute(String result) {
            progressLoading.setVisibility(View.INVISIBLE);
            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(i);
            finish();
        }
    }
}
