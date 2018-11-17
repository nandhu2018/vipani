package com.gigaappz.vipani;

/**
 * Created by DELL on 12-Oct-18.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.gigaappz.vipani.activity.AlertDialogActivity;
import com.gigaappz.vipani.activity.Flash;
import com.gigaappz.vipani.activity.NewMarketActivity;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

public class CheckConnectivity extends BroadcastReceiver{

    @Override
    public void onReceive(final Context context, Intent arg1) {

        boolean isConnected = arg1.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if(isConnected){
            Toast.makeText(context, "Internet Connection Lost", Toast.LENGTH_LONG).show();


        }
        else{
            Toast.makeText(context, "Internet Connected", Toast.LENGTH_LONG).show();
            /*Intent i = new Intent(context, Flash.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);*/

        }
    }
}