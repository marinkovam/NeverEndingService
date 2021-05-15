package com.example.neverendingservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NetworkChangeBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkCheckClass netCheck = new NetworkCheckClass(context);

        if(netCheck.networkCheck() == true){
            Toast.makeText(context,"Network_Change_Broadcast : CONNECTED",Toast.LENGTH_SHORT);
            Log.d("NETWORK_CHANGE_BROAD","Connected");

        }
        else if(netCheck.networkCheck() == false){
            Toast.makeText(context,"Network_Change_Broadcast : DISCONNECTED",Toast.LENGTH_SHORT);
            Log.d("NETWORK_CHANGE_BROAD","Disconnected");

        }
    }
}
