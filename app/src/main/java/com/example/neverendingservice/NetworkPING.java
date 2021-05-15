package com.example.neverendingservice;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NetworkPING extends AsyncTask<Void,Void,String> {

    public static String ping;
    public String host;
    public int packetSize;
    public int jobPeriod;
    public BufferedReader input;
    public int count;
    public  String line;

    @Override
    protected String doInBackground(Void... voids) {

        try {
            String jsonData = getJSON.getBackendJson();
            JSONArray jsonArray = new JSONArray(jsonData);

            for(int i =0; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                host = jsonObject.getString("host");
                count = jsonObject.getInt("count");
                packetSize = jsonObject.getInt("packetSize");
                jobPeriod = jsonObject.getInt("jobPeriod");

                for(int j=0; j<=(int)(600/jobPeriod);j++){
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

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Ping;
    }
}

