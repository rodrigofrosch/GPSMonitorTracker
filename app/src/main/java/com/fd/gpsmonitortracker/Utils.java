package com.fd.gpsmonitortracker;

import android.content.Context;

public class Utils {
    public String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }
    public String removeCaracteresFromString(String str, String charsRemove, String delimiter) {

        if (charsRemove!=null && charsRemove.length()>0 && str!=null) {

            String[] remover = charsRemove.split(delimiter);

            for(int i =0; i < remover.length ; i++) {
                //System.out.println("i: " + i + " ["+ remover[i]+"]");
                if (str.indexOf(remover[i]) != -1){
                    str = str.replace(remover[i], "");
                }
            }
        }

        return str;
    }

    public static String LATITUDE = "-1";

    public static String LONGITUDE = "-1";

    public static String LAST_MSG = "in";
}
