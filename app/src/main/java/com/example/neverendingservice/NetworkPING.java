package com.example.neverendingservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.content.Context.MODE_PRIVATE;

public class NetworkPING extends AsyncTask<Void,Void,String> {

    public static String ping;
    public String host;
    public int packetSize;
    public int jobPeriod;
    public BufferedReader input;
    public int count;
    public  String line;
    private static String TAG = "networkping";

    public String resultOk;
    public NetworkCheckClass networkCheck;
    public Context mContext;
    private SharedPreferences preferences;

    public NetworkPING(Context context) {
        mContext=context;
    }


    @Override
    protected String doInBackground(Void... voids) {

        networkCheck = new NetworkCheckClass(mContext.getApplicationContext());


        try {
            String jsonData = getJSON.getBackendJson();
            JSONArray jsonArray = new JSONArray(jsonData);

            for(int i =0; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                host = jsonObject.getString("host");
                count = jsonObject.getInt("count");
                packetSize = jsonObject.getInt("packetSize");
                jobPeriod = jsonObject.getInt("jobPeriod");

                for(int j=0; j<=600/jobPeriod;j++){
                    ping = makePING(host,count,packetSize);

                    Log.i("MAKE_PING",ping);
                    try{
                        Thread.sleep(jobPeriod*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ping;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    public String makePING(String Host , int Count, int PacketSize){
        String Ping = "";

        try{

            String pingCmd = "ping -s " + PacketSize + " -c "+ Count + " " + Host;
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(pingCmd);
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            line = " ";
            while ((line = input.readLine()) != null){
                Ping += line;

            }
            input.close();

            Log.i("MAKE_PING",Ping);
            preferences = mContext.getSharedPreferences("com.example.qoscheckapp.ActiveServiceRunning", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if(networkCheck.networkCheck()){
                postJSON.postBackendJson(Ping);
                if(preferences.getString("result1",null)!= null){
                    postJSON.postBackendJson(preferences.getString("result1",null));
                    editor.putString("result1",null);
                }
                if(preferences.getString("result2",null)!= null){
                    postJSON.postBackendJson(preferences.getString("result2",null));
                    editor.putString("result2",null);
                }
                else{
                    if((preferences.getString("result1",null)!=null ) && (preferences.getString("result1",null)!=null)
                            || (preferences.getString("result1",null)==null ) && (preferences.getString("result1",null)==null) ){
                        editor.putString("result1",Ping);
                    }else {
                        editor.putString("result2",Ping);
                    }
                    editor.apply();
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Ping;
    }
}
