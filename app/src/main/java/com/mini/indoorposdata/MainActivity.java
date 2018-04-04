package com.mini.indoorposdata;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    ListView list;
    String wifis[];
    CSVWriter writer=null;
    FileWriter mFileWriter = null;
    String filePath;
    String room,inOrOut="null";
    int stopflag=1;

    ArrayList<String> wifiList;

    EditText roomname,inout;
    Button start,stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  RequestRunTimePermission();

        stop = (Button) findViewById(R.id.stop);
        start = (Button) findViewById(R.id.start);
        roomname =(EditText) findViewById(R.id.room);
        inout = (EditText) findViewById(R.id.inout);
        //String wifinames = getIntent().getStringExtra("wifis");
        String wifinames= "80:E8:6F:0A:92:B6,AC:F1:DF:09:B8:29,00:1C:F0:CB:ED:69,80:E8:6F:0A:90:06,1C:BD:B9:26:22:C1,2C:33:7A:58:42:9E,80:E8:6F:0A:90:46,80:58:F8:37:E9:1C,98:DE:D0:AB:91:A6,00:21:91:97:88:1A,7E:46:85:FF:30:2A,C8:3A:35:5D:55:08,80:E8:6F:0B:E7:76,80:E8:6F:0A:CC:EE,C8:D3:A3:25:1B:8D,80:E8:6F:0B:E9:A6,C8:D3:A3:25:2A:2C,C8:D3:A3:25:4D:26,80:E8:6F:0A:90:A6,80:7A:BF:67:12:7F,80:E8:6F:0B:EB:96,80:E8:6F:0B:E4:F6,6C:19:8F:C8:BD:50,80:E8:6F:0B:E8:FE,54:14:73:57:E4:D7,80:E8:6F:0B:E6:36,1E:56:FE:F8:00:0F,80:E8:6F:0B:EA:06,80:E8:6F:0B:EC:16,50:8F:4C:9B:60:B7,80:E8:6F:0B:EA:BE,80:E8:6F:0B:E4:DE,A0:AB:1B:6A:14:E8,80:E8:6F:0B:E8:2E,C2:85:4C:A1:D3:5C,80:E8:6F:0B:EA:76,2A:3E:8E:B8:D3:AD,CC:B0:DA:35:BB:0F,D0:F8:8C:E1:19:A5,80:E8:6F:0B:E8:D6,36:78:D7:7B:61:50,80:E8:6F:0B:E4:BE,00:26:5A:72:06:B0";
        room = getIntent().getStringExtra("room");
        makeList(wifinames.toLowerCase());
        list=(ListView)findViewById(R.id.list);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopflag=0;
                room = roomname.getText().toString();
                inOrOut = inout.getText().toString();
                scan();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    stopflag=1;
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //startScan();

        String baseDir = getExternalFilesDir(null).getAbsoluteFile()+"/";
        String fileName = "WifiData.csv";
        filePath = baseDir +/*File.separator +*/ fileName;
        File f = new File(filePath );

        if(f.exists() && !f.isDirectory()){
            try {
                mFileWriter = new FileWriter(filePath , true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer = new CSVWriter(mFileWriter);
        }
        else {
            try {
                writer = new CSVWriter(new FileWriter(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String[] data = new String[wifiList.size()+2];
        for(int i=0;i<wifiList.size();i++)
        {
            data[i]= wifiList.get(i);
        }
        data[wifiList.size()]="InOut";
        data[wifiList.size() + 1]="Room";
        writer.writeNext(data);
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scan();
    }

    void startScan(){
        String baseDir = getExternalFilesDir(null).getAbsoluteFile()+"/";
        String fileName = "WifiData.csv";
        filePath = baseDir +/*File.separator +*/ fileName;
        File f = new File(filePath );

        if(f.exists() && !f.isDirectory()){
            try {
                mFileWriter = new FileWriter(filePath , true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer = new CSVWriter(mFileWriter);
        }
        else {
            try {
                writer = new CSVWriter(new FileWriter(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String[] data = new String[wifiList.size()+2];
        for(int i=0;i<wifiList.size();i++)
        {
            data[i]= wifiList.get(i);
        }
        data[wifiList.size()]="InOut";
        data[wifiList.size() + 1]="Room";
        writer.writeNext(data);
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scan();
    }

    void makeList(String wifinames){
        int st=0;
        wifiList=new ArrayList<String>();
        for(int i=0;i<wifinames.length();i++) {
            if(wifinames.charAt(i)==','){
                String temp=wifinames.substring(st,i);
                wifiList.add(temp);
                st=i+1;
            }
        }
        wifiList.add(wifinames.substring(st));
    }

    protected void onPause() {
        unregisterReceiver(wifiReciever);
        System.out.println("pause called");
        super.onPause();
    }

    @Override
    protected void onStop() {
        System.out.println("stop called");
        super.onStop();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        scan();
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
            ArrayList <String> filtered =new ArrayList<String>();
            int[] wifiLevels =new int[wifiList.size()];
            for(int i=0;i<wifiList.size();i++)
                wifiLevels[i]=100;
            int count=0;
            for (ScanResult scanResult : wifiScanList) {
                int temp=0;
                for(int i=0;i<wifiList.size();i++){
                    if(wifiList.get(i).trim().toString().equals(scanResult.BSSID)){
                        temp=1;
                        break;
                    }
                }
                if(temp==1) {
                    int l = scanResult.level;
                    int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
                    wifiLevels[wifiList.indexOf(scanResult.BSSID)]=l;
                    String str = scanResult.SSID +" MAC: "+scanResult.BSSID+" Strength: " + l;
                    filtered.add(str);
                    count++;
                    System.out.println("Name: " + scanResult.SSID + " Strength: " + l);
                }
            }
            list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),R.layout.list_item,R.id.label, filtered));

            try {
                mFileWriter = new FileWriter(filePath , true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer = new CSVWriter(mFileWriter);

            String[] data = new String[wifiList.size()+2];
            for(int i=0;i<wifiList.size();i++)
            {
                data[i]= wifiLevels[i]+"";
            }
            data[wifiList.size()]=inOrOut;
            data[wifiList.size() + 1]=room;
            writer.writeNext(data);

            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(stopflag==0) {

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

    }

    boolean wifiMatch(String name){
        int temp=0;
        for(int i=0;i<wifiList.size();i++){
            if(wifiList.get(i).trim().toString().equals(name)){
                temp=1;
            }
        }
        if(temp==0) {
            return false;
        }
        else
        {
            return true;
        }
    }

}