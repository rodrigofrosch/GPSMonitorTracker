package com.fd.gpsmonitortracker;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by frog on 30/12/14.
 */
public class DataXML {


    private ArrayList<Message> messages;
    private ArrayList<Location> locations;
    private ArrayList<Cellular> cellulars;
    private String headerXML = "<?xml version='1.0' encoding='UTF-8'?>\n";
    private String tagItemsOpen = "<items>\n";
    private String tagItemClose = "</items>";

    public String export(ArrayList<Message> messages, ArrayList<Location> locations, ArrayList<Cellular> cellulars) throws IOException {

        this.messages = messages;
        this.locations = locations;
        this.cellulars = cellulars;


        File path = Environment.getExternalStorageDirectory();
        String dir = "GPSMonitorTracker";
        String absolutePath;



        File directory = new File(path, dir);
        Log.d("", directory.getAbsolutePath());
        absolutePath = directory.getAbsolutePath();
        directory.mkdirs();




        //write the bytes in file
        if(directory.exists())
        {
            File file = new File(absolutePath + File.separator + "gmt_data.xml");
            if (!file.exists()) {
                file.createNewFile();
            }
            if (file.exists()) {
                OutputStream fo = null;
                try {
                    String dataXml = generateXML();
                    byte[] content = dataXml.getBytes();
                    fo = new FileOutputStream(file);
                    fo.write(content);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



        return absolutePath + File.separator + "gmt_data.xml";
    }


    private String generateXML(){
        String loc = generateTagWithAttrLocations(locations);
        String msg = generateTagWithAttrMessages(messages);
        String cel = generateTagWithAttrCellulars(cellulars);


        return headerXML + tagItemsOpen + loc + msg + cel + tagItemClose;
    }

    private String generateTagWithAttrCellulars(ArrayList<Cellular> cellulars) {
        String cellularsTag = "\t<cellulars>\n";
        for (int i = 0; i < cellulars.size(); i++){
            cellularsTag += String.format(
                            //"\t<cellular id='%s'>\n" +
                            "\t\t<cellular>\n" +
                            "\t\t\t<cellularName>%s</cellularName>\n" +
                            "\t\t\t<number>%s</number>\n" +
                            "\t\t\t<notify>%s</notify>\n" +
                            "\t\t</cellular>\n",
                    //Integer.toString(cellulars.get(i).getId()),
                    cellulars.get(i).getName(),
                    cellulars.get(i).getNumber(),
                    cellulars.get(i).getIsNotify());
        }
        cellularsTag += "\t</cellulars>\n";
        return cellularsTag;
    }

    private String generateTagWithAttrMessages(ArrayList<Message> messages) {
        String messagesTag = "\t<messages>\n";
        for (int i = 0; i < messages.size(); i++){
            messagesTag += String.format(
                            "\t\t<message>\n" +
                            "\t\t\t<text>%s</text>\n" +
                            "\t\t\t<messageLocation>%s</messageLocation>\n" +
                            "\t\t\t<direction>%s</direction>\n" +
                            "\t\t</message>\n",
                    //Integer.toString(messages.get(i).getId()),
                    messages.get(i).getMsg(),
                    messages.get(i).getLocation(),
                    messages.get(i).getDirection());
        }
        messagesTag += "\t</messages>\n";
        return messagesTag;
    }

    private String generateTagWithAttrLocations(ArrayList<Location> locations) {
        String locationsTag = "\t<locations>\n";
        for (int i = 0; i < locations.size(); i++){
            locationsTag += String.format(
                            "\t\t<location>\n" +
                            //"\t\t<locationId='%s'>\n" +
                            "\t\t\t<locationName>%s</locationName>\n" +
                            "\t\t\t<radius>%s</radius>\n" +
                            "\t\t\t<latitude>%s</latitude>\n" +
                            "\t\t\t<longitude>%s</longitude>\n" +
                            "\t\t\t<altitude>%s</altitude>\n" +
                            "\t\t\t<last_msg_send>%s</last_msg_send>\n" +
                            //"\t\t\t<msg_in>%s</msg_in>\n" +
                            //"\t\t\t<msg_out>%s</msg_out>\n" +
                            "\t\t\t<notify>%s</notify>\n" +
                            "\t\t</location>\n",
                    //Integer.toString(locations.get(i).getId()),
                    locations.get(i).getName(),
                    locations.get(i).getRadius(),
                    locations.get(i).getLatitude(),
                    locations.get(i).getLongitude(),
                    locations.get(i).getAltitude(),
                    locations.get(i).getLastMsgSend(),
                    //locations.get(i).getMsgIn(),
                    //locations.get(i).getMsgOut(),
                    locations.get(i).getIsNotify());
        }
        locationsTag += "\t</locations>\n";
        return locationsTag;
    }

}

