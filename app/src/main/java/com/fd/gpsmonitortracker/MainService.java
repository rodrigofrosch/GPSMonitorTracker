package com.fd.gpsmonitortracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class MainService extends Service {

    private static double COUNTER1 = 0;
    private static boolean ISRUNNING = false;
    private static int DELAY = 15 * 1000;
    private static String TAG = "Main Service: ";
    private static ArrayList<Location> locations_load;
    private static Location location_now;
    private static DataOpenHelper data = null;
    private static Thread tr;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        this.LoadData();

        ISRUNNING = true;
        //get location now
        location_now = new Location();
        Log.d(TAG, "Started");

        /**
         * new Thread to check in or out location
         */
        tr = new Thread() {
            public void run () {

                while (COUNTER1 < Double.MAX_VALUE) {

                    startService(new Intent(getBaseContext(), GPSService.class));
                    stopService(new Intent(getBaseContext(), GPSService.class));

                    location_now.setLatitude(Utils.LATITUDE);
                    location_now.setLongitude(Utils.LONGITUDE);

                    if (locations_load.size() != 0){
                        for (Location location : locations_load){
                            if (location.inRadius(location, location_now)){
                                Log.d("+++++++++++++++++++++++++++++", location.getName() + " in radius");
                                if(location.getLastMsgSend().equals("OUT") || !location.getLastMsgSend().equals("IN")) {
                                    Log.d("+++++++++++++++++++++++++++++", location.getName() + " - last msg send: " + location.getLastMsgSend());
                                    location.setLastMsgSend("IN");
                                    sendMessageText(location.getName(), location.getLastMsgSend());
                                    updateLastMsgSendLocation(location.getName(), location.getLastMsgSend());
                                }
                            } else {
                                Log.d("+++++++++++++++++++++++++++++", location.getName() + " out radius");
                                if (location.getLastMsgSend().equals("IN") || !location.getLastMsgSend().equals("OUT")) {
                                    Log.d("+++++++++++++++++++++++++++++", location.getName() + " - last msg send: IN");
                                    location.setLastMsgSend("OUT");
                                    sendMessageText(location.getName(), location.getLastMsgSend());
                                    updateLastMsgSendLocation(location.getName(), location.getLastMsgSend());
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "Nenhum local definido na base de dados");
                    }

                    COUNTER1++;
                    if (COUNTER1 >= Double.MAX_VALUE) {
                        COUNTER1 = 0;
                    }
                    SystemClock.sleep(DELAY);
                }
            }


        };
        tr.start();

        return START_STICKY;
    }

    private void LoadData() {
        data = new DataOpenHelper(this);
        locations_load = data.getAllLocations();
        data.close();
        data= null;
    }


    private boolean updateLastMsgSendLocation(String location, String direction){
        data = new DataOpenHelper(this);
        if (data.updateLastMsgSendLocation(location, direction)){
            data.close();
            data = null;
            return true;
        } else {
            data.close();
            data = null;
            return false;
        }
    }

    private String getMessageByLocation(String location, String direction) {
        data = new DataOpenHelper(this);
        String msg = data.getMessageByLocation(location, direction);
        data = null;
        return msg;
    }

    private void sendMessageText(String location, String direction) {
        Log.d("+++++++++++++++++++++++++++++", "sendMessageText START " + location);
        data = new DataOpenHelper(this);

        ArrayList<String> numbers = data.getCellularByLocationByNotify(location);
        for (String number : numbers) {
            Sms.sendSms(number, getMessageByLocation(location, direction));
            Log.d("+++++++++++++++++++++++++++++", "SMS send to: " + number);
        }
        data = null;
        Log.d("+++++++++++++++++++++++++++++", "sendMessageText END");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ISRUNNING = false;
        Toast.makeText(this, getString(R.string.msg_service_stop), Toast.LENGTH_LONG).show();
        Log.d(TAG, "Destroyed");
    }

    public static boolean checkService() {
        if (ISRUNNING) {
            return true;
        } else {
            return false;
        }

    }
}