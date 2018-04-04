package com.mini.indoorposdata;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    ListView list;
    WifiManager wifiManager;
    ArrayList<String> wifis;
    ArrayList<String> wifiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        list=(ListView)findViewById(R.id.list);
        String wifinames= getIntent().getStringExtra("wifis");
        String room = getIntent().getStringExtra("room");
        makeList(wifinames);
        new CountDownTimer(1000, 500) {

            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                //mTextField.setText("done!");
                scan();
            }
        }.start();

    }

    void makeList(String wifinames){
        int st=0;
        wifiList=new ArrayList<String>();
        for(int i=0;i<wifinames.length();i++) {
            if(wifinames.charAt(i)==':'){
                String temp=wifinames.substring(st,i);
                wifiList.add(temp);
                st=i+1;
            }
        }
        wifiList.add(wifinames.substring(st));
    }


    void scan(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();

        List<ScanResult> wifiScanList = wifiManager.getScanResults();
        ArrayList <String> filtered =new ArrayList<String>();
        int count=0;
        for (ScanResult scanResult : wifiScanList) {
            int temp=0;
            for(int i=0;i<wifiList.size();i++){
                if(wifiList.get(i).trim().toString().equals(scanResult.SSID)){
                    temp=1;
                    break;
                }
            }
            if(temp==1) {
                int l = scanResult.level;
                int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
                String str = scanResult.SSID + " Strength: " + l;
                filtered.add(str);
                count++;
                System.out.println("Name: " + scanResult.SSID + " Strength: " + l);
            }
        }
        list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),R.layout.list_item,R.id.label, filtered));

        new CountDownTimer(500, 500) {

            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                scan();
            }
        }.start();
    }
}
