package com.interbook.android.interbookandroid;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import BLL.SessionManager;

/**
 * Created by dell on 15/06/2015.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    SessionManager session;

    @Override
    public void onReceive(Context context, Intent intent) {

        session = new SessionManager(context);
        if(!session.getInPref("regid").isEmpty()) {
            ComponentName comp = new ComponentName(context.getPackageName(),
                    GcmIntentService.class.getName());
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        }
    }
}
