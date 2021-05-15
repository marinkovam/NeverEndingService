package com.example.neverendingservice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkCheckClass {
    public boolean connection;
    public Context mContext;

    public NetworkCheckClass(Context context) {
        mContext=context;
    }

    public boolean networkCheck(){

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null)
        {
            //connetion = true;
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                connection = true;}
            else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                connection = true;
            }
        }
        else {
            connection = false;
        }
        return  connection;
    }
}

