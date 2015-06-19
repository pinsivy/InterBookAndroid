package com.interbook.android.interbookandroid;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import BLL.SessionManager;


public class DashboardActivity extends ActionBarActivity {

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        TextView lblName = (TextView) findViewById(R.id.emailText);

        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        String email = user.get(SessionManager.KEY_NAME);

        // displaying user data
        lblName.setText(Html.fromHtml("Email: <b>" + email + "</b>"));

        lblName = (TextView) findViewById(R.id.emailText);
    }

    public void BoutonSeDeconnecter(View v){
        session.logoutUser();
    }
}
