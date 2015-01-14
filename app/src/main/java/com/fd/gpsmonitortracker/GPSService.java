package com.fd.gpsmonitortracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GPSService extends Service {

    private static boolean ISRUNNING = false;
    private static String TAG = "GPS Service: ";
    GPSTracker gps;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.ISRUNNING = true;
        Log.d(TAG, "Started to get location");

        gps = new GPSTracker(GPSService.this);
        if (gps.canGetLocation()) {
            Utils.LATITUDE = Double.toString(gps.getLatitude());
            Utils.LONGITUDE = Double.toString(gps.getLongitude());
            Log.d(this.TAG, " Lat: " + Utils.LATITUDE + ", Long: " + Utils.LONGITUDE);
        } else {
            gps.showSettingsAlert();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ISRUNNING = false;
        gps.stopUsingGPS();
        gps = null;
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