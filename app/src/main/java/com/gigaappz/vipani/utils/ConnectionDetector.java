package com.gigaappz.vipani.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {
    private Context context;

    public ConnectionDetector(Context context){
        this.context = context;
    }

    public boolean isConnectingToInternet(){
        boolean isConnected = false;
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            isConnected = info != null &&  info.isConnectedOrConnecting();

        }
        return isConnected;
    }
}
