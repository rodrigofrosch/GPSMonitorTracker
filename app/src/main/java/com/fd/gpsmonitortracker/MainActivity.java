package com.fd.gpsmonitortracker;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private DataOpenHelper data;
    private static String selectTab;
    private static String resultExportData = null;
    private static long loop = 0;
    private static final int FILE_SELECT_CODE = 0;
    private ArrayList<Message> messagesFromXml = null;
    private ArrayList<Location> locationsFromXml = null;
    private ArrayList<Cellular> cellularsFromXml = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //implementar
        startService(checkServiceSettingsAutoStart());
        Log.d("MainActivity", "StartService");


        loadParameter();

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    private void loadParameter() {
        try {
            Bundle b = getIntent().getExtras();
            selectTab = b.getString("tab");
            getIntent().removeExtra("tab");
        }
        catch (Exception e){
            return;
        }
    }

    private boolean checkServiceSettingsAutoStart() {
        return true;
    }

    private void startService(boolean autoStart) {
        //init MainService
        startService(new Intent(getBaseContext(), MainService.class));
    }

    public void newCellular(View view)
    {
        Intent newCellular;
        newCellular = new Intent(this, NewCellularActivity.class);
        finish();
        startActivity(newCellular);
    }

    public void newLocation(View view)
    {
        Intent newLocation;
        newLocation = new Intent(this, NewLocationActivity.class);
        finish();
        startActivity(newLocation);
    }

    public void newMessage(View view)
    {
        Intent newMessage;
        newMessage = new Intent(this, NewMessageActivity.class);
        finish();
        startActivity(newMessage);
    }

    public void importData(View vew){
        Log.d("*******************", "Method importData");
        //TESTE
        //loadReceiveXml("");


        //to tests this as comment marked
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/");
        intent.setDataAndType(uri, "resource/folder");
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),FILE_SELECT_CODE);
        } catch (Exception e) {
            // Potentially direct the user to the Market with a Dialog
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("Navigator files", "File Uri: " + uri.toString());
                    // Get the path
                    String path = null;
                    try {
                        path = FileUtils.getPath(this, uri);
                        loadReceiveXml(path);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadReceiveXml(String filePath) {
        Log.d("*******************", "Method loadReceiveXml");
        filePath = Environment.getExternalStorageDirectory().getPath() + "/GPSMonitorTracker/gmt_data.xml";
        if (filePath != null) {
            XmlPullParserFactory pullParserFactory;
            try {
                pullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = pullParserFactory.newPullParser();
                InputStream in_s = new FileInputStream(filePath);
                importDataXMLToDataBase(in_s);
            } catch (XmlPullParserException e) {
                Log.d("XmlPullParserException", e.getMessage());
                return;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d("FileNotFoundException", e.getMessage());
                return;
            }
        }
    }

    private void importDataXMLToDataBase(InputStream is) throws XmlPullParserException, IOException {
        Log.d("*******************", "InpuStream " + is.toString());
        Log.d("*******************", "Method importDataXMLToDataBase");
        Message message = null;
        Location location = null;
        Cellular cellular = null;


        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;

        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();

            Log.d("*******************", "START While read parse xml");
            while (eventType != XmlPullParser.END_DOCUMENT){

                String tagName = null;
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        messagesFromXml = new ArrayList();
                        locationsFromXml = new ArrayList();
                        cellularsFromXml = new ArrayList();
                        break;
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if (tagName.equals("locations")) {
                            Log.d("-------------------------------", "<" + tagName + ">");
                        }
                        if (tagName.equals("location")) {
                            location = new Location();
                            Log.d("------------------------------------", "<" + tagName + ">");
                        }
                        if (location != null) {
                            if (tagName.equals("locationName")) {
                                location.setName(parser.nextText());
                                Log.d("-----------------------------------------", tagName + " : " + location.getName());
                            } else if (tagName.equals("radius")) {
                                location.setRadius(parser.nextText());
                                Log.d("-----------------------------------------", tagName + " : " + location.getRadius());
                            } else if (tagName.equals("latitude")) {
                                location.setLatitude(parser.nextText());
                                Log.d("-----------------------------------------", tagName + " : " + location.getLatitude());
                            } else if (tagName.equals("longitude")) {
                                location.setLongitude(parser.nextText());
                                Log.d("-----------------------------------------", tagName + " : " + location.getLongitude());
                            } else if (tagName.equals("altitude")) {
                                location.setAltitude(parser.nextText());
                                Log.d("-----------------------------------------", tagName + " : " + location.getAltitude());
                            } /*else if (tagName.equals("last_msg_send")) {
                                location.setLastMsgSend(parser.nextText());
                                Log.d("-----------------------------------------", tagName + " : " + location.getLastMsgSend());
                            } else if (tagName.equals("msg_in")) {
                                //location.setMsgIn(Integer.parseInt(parser.nextText()));
                                Log.d("-----------------------------------------", tagName + " : ");
                            } else if (tagName.equals("msg_out")) {
                                //location.setMsgOut(Integer.parseInt(parser.nextText()));
                                Log.d("-----------------------------------------", tagName + " : ");
                            }*/ else if (tagName.equals("notify")) {
                                location.setIsNotify(parser.nextText());
                                Log.d("-----------------------------------------", tagName + " : " + location.getIsNotify());
                            }
                        }
                        if (tagName.equals("messages")) {
                            Log.d("-------------------------------", "<" + tagName + ">");
                        }
                        if (tagName.equals("message")){
                            message = new Message();
                            Log.d("------------------------------------", "<" + tagName + ">");
                        }

                        if (message != null){
                            if (tagName.equals("text")){
                                message.setMsg(parser.nextText());
                                Log.d("-----------------------------------------", tagName + " : " + message.getMsg());
                            } else if (tagName.equals("messageLocation")){
                                message.setLocation(parser.nextText());
                                Log.d("-----------------------------------------", tagName + " : " + message.getLocation());
                            } else if (tagName.equals("direction")){
                                message.setDirection(parser.nextText());
                                Log.d("-----------------------------------------", tagName + " : " + message.getDirection());
                            }
                        }


                        if (tagName.equals("cellulars")) {
                            Log.d("-------------------------------", "<" + tagName + ">");
                        }

                        if (tagName.equals("cellular")){
                            cellular = new Cellular();
                            Log.d("------------------------------------", "<" + tagName + ">");
                        }
                        if (cellular != null){
                            if (tagName.equals("cellularName")){
                                cellular.setName(parser.nextText());
                                Log.d("-----------------------------------------", tagName + " : " + cellular.getName());
                            } else if (tagName.equals("number")){
                                cellular.setNumber(parser.nextText());
                                Log.d("-----------------------------------------", tagName + " : " + cellular.getNumber());
                            } else if (tagName.equals("notify")){
                                cellular.setIsNotify(parser.nextText());
                                Log.d("-----------------------------------------", tagName + " : " + cellular.getIsNotify());
                            }
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();
                        if (tagName.equalsIgnoreCase("location") && tagName != null && location != null){
                            locationsFromXml.add(location);
                            Log.d("------------------------------------", "</" + tagName + ">");
                            Log.d("------------------------------------", tagName.toUpperCase() + " add succesfully");
                            location = null;
                        }
                        if (tagName.equals("locations") && tagName != null){
                            Log.d("-------------------------------", "</" + tagName + ">");
                        }


                        if (tagName.equalsIgnoreCase("message") && tagName != null){
                            messagesFromXml.add(message);
                            Log.d("------------------------------------", "</" + tagName + ">");
                            Log.d("------------------------------------", tagName.toUpperCase() + " add succesfully");
                            message = null;
                        }
                        if (tagName.equals("messages") && tagName != null){
                            Log.d("-------------------------------", "</" + tagName + ">");
                        }

                        if (tagName.equalsIgnoreCase("cellular") && tagName != null){
                            cellularsFromXml.add(cellular);
                            Log.d("------------------------------------", "</" + tagName + ">");
                            Log.d("------------------------------------", tagName.toUpperCase() + " add succesfully");
                            cellular = null;
                        }
                        if (tagName.equals("cellulars") && tagName != null){
                            Log.d("-------------------------------", "</" + tagName + ">");
                        }

                        break;
                }
                eventType = parser.next();
            }

            Log.d("*******************", "END While read parse xml");

            try {
                data = new DataOpenHelper(getApplicationContext());
                data.LoadXml(locationsFromXml, messagesFromXml, cellularsFromXml);
                Toast.makeText(getApplicationContext(), "Import data successfully", Toast.LENGTH_SHORT).show();
                Log.d("--------- Total locals in list/DB", Integer.toString(locationsFromXml.size()));
                Log.d("--------- Total messages in list/DB", Integer.toString(messagesFromXml.size()));
                Log.d("--------- Total cellulars in list/DB", Integer.toString(cellularsFromXml.size()));
                //Sucess
                messagesFromXml = null;
                cellularsFromXml = null;
                locationsFromXml = null;
                data = null;
            } catch (Exception e){
                e.printStackTrace();
                Log.d("--------- NOT INSERT INTO DATABASE", "");
            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Message m: messagesFromXml){
                    Log.d("******Message add", m.getMsg());
                }
                for (Cellular c : cellularsFromXml){
                    Log.d("******Cellular add", c.getNumber());
                }
                for (Location l : locationsFromXml){
                    Log.d("******Locations add", l.getName());
                }
            }
        });
        */


    }

    public void exportData(View vew) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                data = new DataOpenHelper(getApplicationContext());
                DataXML xml = new DataXML();
                try {
                    resultExportData = xml.export(data.getAllMessages(), data.getAllLocations(), data.getAllCellulars());
                    //Toast.makeText(new MainActivity(), R.string.message_dataxmlservice_generate_success +
                    //      " " + resultExportData, Toast.LENGTH_LONG).show();
                    if (resultExportData != null) {
                        android.os.Message msg = handler.obtainMessage();
                        msg.arg1 = 1;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                data = null;
            }
        }).start();
    }

    private final Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(getApplicationContext(), getString(R.string.message_dataxmlservice_generate_success) +
                        " " + resultExportData, Toast.LENGTH_LONG).show();
        }
    };

    public void checkService(View view){
        if (MainService.checkService()){
            Toast.makeText(this, getString(R.string.msg_check_service_running), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.msg_check_service_not_running), Toast.LENGTH_LONG).show();
        }
    }

    private boolean updateSettings(String autoStart) {
        data = new DataOpenHelper(getApplicationContext());
        return data.updateSettings(autoStart);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new LocationsFragment();
                case 1:
                    return new MessagesFragment();
                case 2:
                    return new CellularsFragment();
                case 3:
                    return new SettingsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            if (selectTab == "messages") {
                rootView = inflater.inflate(R.layout.fragment_messages
                        , container, false);
            }else {
                rootView = inflater.inflate(R.layout.fragment_locations, container, false);
            }
            return rootView;
        }
    }

}
