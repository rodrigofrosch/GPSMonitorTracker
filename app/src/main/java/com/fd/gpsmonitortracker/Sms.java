package com.fd.gpsmonitortracker;

import android.telephony.SmsManager;


public class Sms {
    static void sendSms(String number, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);
    }
}