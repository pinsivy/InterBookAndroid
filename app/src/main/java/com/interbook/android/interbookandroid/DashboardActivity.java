package com.interbook.android.interbookandroid;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;

import BLL.SessionManager;


public class DashboardActivity extends Activity {

    // Session Manager Class
    SessionManager session;
    ProgressBar progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();


        HashMap<String, String> user = session.getUserDetails();
        String email = user.get(SessionManager.KEY_EMAIL);

        TextView lblName = (TextView) findViewById(R.id.emailText);
        lblName.setText(Html.fromHtml("Email: <b>" + email + "</b>"));

        progressLoading = (ProgressBar) findViewById(R.id.progressLoading);
        //progressLoading.setVisibility(View.VISIBLE);
    }

    public void BoutonSeDeconnecter(View v){
        session.logoutUser();
    }
}
