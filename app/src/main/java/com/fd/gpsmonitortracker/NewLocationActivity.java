package com.fd.gpsmonitortracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class NewLocationActivity extends ActionBarActivity {

    private DataOpenHelper data;
    private Button btnSaveLocation, btnGetLocation, btnEditNumbers, btnCancel;
    private EditText editTextName, editTextLatitude, editTextLongitude, editTextRadius;
    private CheckBox ckbNotify;
    private String parameter;
    private ExpandableListView myExpandableListCellularsLocations;
    private ExpandableListAdapterCellularsLocations listAdapterCellularsLocations;
    private List<String> listCelsLocsDataHeader;
    private HashMap<String, List<String>> listCelsLocsDataChild;
    private static int CountItem = 0;
    private Intent main;
    private ArrayList<CharSequence> listNumbersParameterSend;
    private ArrayList<String> allNumbers;
    private ArrayList<Location> location;
    private ArrayList<CharSequence> parameterList;
    private String parameterLastActivity = "";
    private GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location);

        myExpandableListCellularsLocations = (ExpandableListView)findViewById(R.id.expandableListViewCellularsLocations);
        btnGetLocation = (Button) findViewById(R.id.btnGetLocation);
        btnSaveLocation = (Button) findViewById(R.id.btnSaveLocation);
        editTextName = (EditText) findViewById(R.id.editTextLocationName);
        editTextLatitude = (EditText) findViewById(R.id.editTextLocationLatitude);
        editTextLongitude = (EditText) findViewById(R.id.editTextLocationLongitude);
        editTextRadius = (EditText) findViewById(R.id.editTextLocationRadius);
        ckbNotify = (CheckBox) findViewById(R.id.checkBoxLocationIsNotify);
        btnEditNumbers = (Button) findViewById(R.id.btnEditNumbers);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main;
                main = new Intent(getApplicationContext(), MainActivity.class);
                finish();
                startActivity(main);
            }
        });

        calculateWidthButtons();

        loadParameter();

        if (parameter != null) {
            //load data for database for expandable lists
            loadExpandableListCelsLocs();
            //create list adapter
            listAdapterCellularsLocations = new ExpandableListAdapterCellularsLocations(getApplicationContext(), listCelsLocsDataHeader, listCelsLocsDataChild);
            // setting list adapter
            myExpandableListCellularsLocations.setAdapter(listAdapterCellularsLocations);

            expandListCellularsLocations();
        }
    }

    private void calculateWidthButtons() {
        int width = getWindowManager().getDefaultDisplay().getWidth()/2;
        btnCancel.setWidth(width - 5);
        btnSaveLocation.setWidth(width-5);
    }

    private void expandListCellularsLocations() {
        for (int i = 0; i < CountItem; i++){
            myExpandableListCellularsLocations.expandGroup(i);
        }
    }

    private void loadExpandableListCelsLocs() {
        data = new DataOpenHelper(getApplicationContext());
        listCelsLocsDataChild = new HashMap<String, List<String>>();
        listCelsLocsDataHeader = new ArrayList<String>();
        String header;
        if (parameterLastActivity == "update") {
            allNumbers = new ArrayList<>();
            ArrayList<String> listAux =data.getNumbersNotifyLocation(parameter);
            String concatList = "";
            allNumbers = new ArrayList<>();
            for (int i =0; i < listAux.size(); i++){
                if (i < listAux.size()-1)
                    concatList += listAux.get(i).toString() + ", ";
                if (i == listAux.size()-1)
                    concatList += listAux.get(i).toString() + "; ";
            }
            allNumbers.add(concatList);
            header = getString(R.string.location_tv_numbers_to_notify).toString();
            listCelsLocsDataHeader.add(header);
            listCelsLocsDataChild.put(header, allNumbers);
        } else {
            allNumbers = new ArrayList<>();
            ArrayList<String> listAux = new ArrayList<>();
            String aux = "";
            for (CharSequence l : parameterList){
                if (!aux.equals(l.toString().replace("[", "").replace("]", ""))) {
                    aux = l.toString().replace("[", "").replace("]", "");
                    listAux.add(aux);
                }
            }
            String concatList = "";
            for (int i =0; i < listAux.size(); i++){
                if (i < listAux.size()-1)
                    concatList += listAux.get(i).toString() + ", ";
                if (i == listAux.size()-1)
                    concatList += listAux.get(i).toString() + "; ";
            }
            allNumbers.add(concatList);
            header = getString(R.string.location_tv_numbers_to_notify).toString();
            listCelsLocsDataHeader.add(header);
            listCelsLocsDataChild.put(header, allNumbers);
        }
        data = null;
        CountItem = listCelsLocsDataHeader.size();
    }

    private void loadParameter() {
        try {

            Bundle b = getIntent().getExtras();
            parameter = b.getString("LocationName");
            parameterList = new ArrayList<>();
            if (b.getString("LastActivity").toString().equals("update")) {
                parameterLastActivity = "update";
                getIntent().removeExtra("LastActivity");
            }
            if (b.getString("LastActivity").toString().equals("addCellularLocation")) {
                parameterLastActivity = "addCellularLocation";
                getIntent().removeExtra("LastActivity");
            }
            if (parameterList != null){
                parameterList = b.getCharSequenceArrayList("numbers");
            }
            if (!parameter.equals("")) {
                setTitle(getString(R.string.title_activity_update_location));
                loadLocation(parameter);
                getIntent().removeExtra("LocationName");
                getIntent().removeExtra("numbers");
            }


        }
        catch (Exception e){
            return;
        }
    }

    private void loadLocation(String parameter) {
        data = new DataOpenHelper(getApplicationContext());
        location = data.getAllLocationsByName(parameter);
        editTextName.setText(location.get(0).getName());
        editTextLatitude.setText(location.get(0).getLatitude());
        editTextLongitude.setText(location.get(0).getLongitude());
        editTextRadius.setText(location.get(0).getRadius());

        if (location.get(0).getIsNotify() != "YES") {
            ckbNotify.setChecked(true);
        } else {
            ckbNotify.setChecked(false);
        }
        btnSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLocation();
            }
        });
        data = null;
    }

    private void updateLocation(){
        data = new DataOpenHelper(getApplicationContext());
        if (passValidateNull()) {
            if (data.updateLocationByName(
                    editTextName.getText().toString().toUpperCase(),
                    editTextRadius.getText().toString(),
                    editTextLatitude.getText().toString(),
                    editTextLongitude.getText().toString(),
                    (ckbNotify.isChecked()) ? "YES" : "NO"
            )) {

                if (listAdapterCellularsLocations.getChildrenCount(0) > 0) {
                    int idCellular;
                    int idLocation = data.getLocationIdByName(parameter);

                    ArrayList<String> list = parameterListToList(parameterList);
                    String aux = "";
                    for (int i = 0; i < list.size(); i++) {
                        aux = list.get(i).toString().replace("[[", "").replace("]]", "");
                        Log.d("ParameterList---------", aux);
                        idCellular = data.getCellularIdByNumber(aux);
                        if (data.getResultIsNullInCellularsLocations(idCellular, idLocation)< 0) {
                            data.insertCellularsLocationsByIds(idCellular, idLocation);
                        }
                    }
                }
                Toast.makeText(this, getString(R.string.location_toast_update_sucess), Toast.LENGTH_SHORT).show();
                SystemClock.sleep(Toast.LENGTH_SHORT * 1000);
                Intent main;
                main = new Intent(this, MainActivity.class);
                startActivity(main);
                return;
            } else {
                Toast.makeText(this, getString(R.string.location_toast_update_failed), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.validation_null_location_msg), Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<String> parameterListToList(ArrayList<CharSequence> parameterList) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(parameterList.toString().split(", ")));
        return list;
    }

    private boolean passValidateNull(){
        if (!editTextLatitude.getText().toString().trim().isEmpty() &&
                !editTextLongitude.getText().toString().trim().isEmpty() &&
                !editTextRadius.getText().toString().trim().isEmpty() &&
                !editTextName.getText().toString().trim().isEmpty()){
            return true;
        } else return false;
    }

    public void saveLocation(View view){
        data = new DataOpenHelper(getApplicationContext());
        String notify = (ckbNotify.isChecked())? "YES" : "NO";
        if (passValidateNull()) {
            if (!editTextName.getText().toString().matches("")
                    || !editTextRadius.getText().toString().matches("")
                    || !editTextLatitude.getText().toString().matches("")
                    || !editTextLongitude.getText().toString().matches("")) {
                if (data.insertLocation(editTextName.getText().toString().toUpperCase(),
                        editTextRadius.getText().toString(),
                        editTextLatitude.getText().toString(),
                        editTextLongitude.getText().toString(),
                        "",
                        notify)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.location_toast_new_save_success), Toast.LENGTH_LONG).show();
                    Intent main;
                    main = new Intent(this, MainActivity.class);
                    startActivity(main);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.cellular_new_failed), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.cellular_validation_alert), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.validation_null_location_msg), Toast.LENGTH_SHORT).show();
        }

    }

    public void editNumbers(View view) throws Throwable {
        listNumbersParameterSend = new ArrayList<CharSequence>();
        ArrayList<CharSequence> listLocation = new ArrayList<>();
        for (String s : allNumbers){
            listNumbersParameterSend.add((CharSequence) s.toString());
        }
        listLocation.add(location.get(0).getName());
        try {
            finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        main = new Intent(this, AddCellularLocationActivity.class);
        main.putCharSequenceArrayListExtra("numbers", listNumbersParameterSend);
        main.putCharSequenceArrayListExtra("LocationName", listLocation);
        finish();
        startActivity(main);
    }

    public void GetLocation(View view){
        GPSTracker gps = new GPSTracker(this);
        try {
            editTextLatitude.setText(String.valueOf(Double.parseDouble(String.valueOf(gps.getLocation().getLatitude()))).toString());
            editTextLongitude.setText(String.valueOf(Double.parseDouble(String.valueOf(gps.getLocation().getLongitude()))).toString());
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
