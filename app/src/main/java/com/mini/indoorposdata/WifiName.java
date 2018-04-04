package com.mini.indoorposdata;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class WifiName extends AppCompatActivity {


    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    ListView scannedList;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_name);

        //final EditText wifis=(EditText)findViewById(R.id.wifis);
        final EditText room=(EditText)findViewById(R.id.room);
        Button button=(Button)findViewById(R.id.ok);
        Button scan=(Button)findViewById(R.id.scan);
        scannedList = (ListView) findViewById(R.id.scannedList);

        //wifis.setText("AB:Harshit_Singh:F-802:F-902");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SparseBooleanArray checked = scannedList.getCheckedItemPositions();
                ArrayList<String> selectedItems = new ArrayList<String>();
                for (int i = 0; i < checked.size(); i++) {
                    int position = checked.keyAt(i);
                    if (checked.valueAt(i))
                        selectedItems.add((String) adapter.getItem(position));
                }

                String[] outputStrArr = new String[selectedItems.size()];
                String wifis="";
                for (int i = 0; i < selectedItems.size(); i++) {
                    outputStrArr[i] = selectedItems.get(i);
                    String temp=outputStrArr[i].substring(0,17);
                    wifis+=temp;
                    if(i!=(selectedItems.size()-1))
                    {
                        wifis+=",";
                    }
                    System.out.println("fsfsdf: "+temp);

                }

                finish();
                Intent i=new Intent(WifiName.this,MainActivity.class);
                i.putExtra("wifis",wifis);
                i.putExtra("room",room.getText().toString());
                startActivity(i);
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();

            }
        });

        scan();

    }

    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    void scan(){
        mainWifiObj = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        mainWifiObj.startScan();
    }



    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
            ArrayList<String> filtered =new ArrayList<String>();
            int count=0;
            for (ScanResult scanResult : wifiScanList) {
                    int l = scanResult.level;
                    String str = scanResult.BSSID +" Name: "+scanResult.SSID+" Strength: " + l;
                    filtered.add(str);
                    count++;
                    System.out.println("Name: " + scanResult.SSID + " Strength: " + l);
            }
            scannedList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,filtered);

            scannedList.setAdapter(adapter);
        }

    }
}
