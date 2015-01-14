package com.fd.gpsmonitortracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class AddCellularLocationActivity extends ActionBarActivity {

    private DataOpenHelper data;
    private ExpandableListView myExpandableListAddCellularsLocations;
    private ExpandableListAdapterAddCellularsLocations listAdapterAddCellularsLocations;
    private List<String> listAddCelsLocsDataHeader;
    private HashMap<String, List<String>> listAddCelsLocsDataChild;
    private Spinner spinnerNumbersToAddCellularsLocations;
    private static int CountItem = 0;
    private int groupPosition;
    private int childPosition;
    private Button btnAddCellularLocation;
    private String parameterLocation;
    private List<String> listNumbersBeforeSave = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cellular_location);

        spinnerNumbersToAddCellularsLocations = (Spinner)findViewById(R.id.spinnerNumbersToAddCellularsLocations);
        myExpandableListAddCellularsLocations = (ExpandableListView)findViewById(R.id.expandableListViewAddCellularsLocations);
        btnAddCellularLocation = (Button)findViewById(R.id.btnAddCellularLocation);

        expandableClean();

        loadParameter();

        btnAddCellularLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<CharSequence> list = new ArrayList<CharSequence>();
                for (String l : listNumbersBeforeSave){
                    list.add(l.toString());
                }
                Intent positveActivity = new Intent(getApplicationContext(), NewLocationActivity.class);
                positveActivity.putExtra("LocationName",  parameterLocation.toString());
                positveActivity.putExtra("LastActivity", "addCellularLocation");
                positveActivity.putCharSequenceArrayListExtra("numbers", list);
                try {
                    finish();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                startActivity(positveActivity);
            }
        });

        loadSpinnerNumbers();

        spinnerNumbersToAddCellularsLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (!spinnerNumbersToAddCellularsLocations.getSelectedItem().toString().equals(getString(R.string.text_spinner_select))) {
                    ArrayList<CharSequence> list;
                    ArrayList<CharSequence> listOld = new ArrayList<CharSequence>();
                    for (int i = 0; i < listAdapterAddCellularsLocations.getChildrenCount(0); i++){
                        listOld.add(listAdapterAddCellularsLocations.getChild(0, i).toString());
                    }
                    boolean contains = false;

                    Log.d("================================================ Total de childrens", listAdapterAddCellularsLocations.getChild(0, 0).toString());

                    List<String> convertList = new ArrayList<String>(Arrays.asList(listNumbersBeforeSave.get(0).toString().split(", ")));


                    for (int i = 0; i < convertList.size(); i++) {
                        if (convertList.get(i).toString().replace("[", "").replace("]", "").equals(spinnerNumbersToAddCellularsLocations.getSelectedItem().toString())) {
                            contains = true;
                            break;
                        }
                    }

                    list = listOld;
                    if (!contains){
                        list.add(spinnerNumbersToAddCellularsLocations.getSelectedItem().toString());
                    }
                    else return;
                    Intent positveActivity = new Intent(getApplicationContext(), AddCellularLocationActivity.class);
                    positveActivity.putExtra("LocationName", parameterLocation);
                    positveActivity.putCharSequenceArrayListExtra("numbers", list);

                    try {
                        finish();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    startActivity(positveActivity);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (listNumbersBeforeSave != null) {
            //load data for database for expandable lists
            loadExpandableListAddCelsLocs();
            //create list adapter
            listAdapterAddCellularsLocations = new ExpandableListAdapterAddCellularsLocations(getApplicationContext(), listAddCelsLocsDataHeader, listAddCelsLocsDataChild);
            // setting list adapter
            myExpandableListAddCellularsLocations.setAdapter(listAdapterAddCellularsLocations);

            expandListAddCellularsLocations();

            myExpandableListAddCellularsLocations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                        final ExpandableListAdapter adapter = ((ExpandableListView) parent).getExpandableListAdapter();

                        groupPosition = ExpandableListView.getPackedPositionGroup(id);
                        childPosition = ExpandableListView.getPackedPositionChild(id);


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getApplicationContext());
                        alertDialogBuilder.setMessage(R.string.locations_popup_decision);
                        alertDialogBuilder.setNeutralButton(R.string.delete_button,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Logic hear
                                    }
                                });
                        alertDialogBuilder.setPositiveButton(R.string.update_button,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                       //Logic hear
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void expandableClean() {
        for (int i = 0; i < myExpandableListAddCellularsLocations.getChildCount(); i++){
            myExpandableListAddCellularsLocations.removeViewAt(i);
        }
    }

    private void loadSpinnerNumbers() {
        //spinnerNumbersToAddCellularsLocations
        data = new DataOpenHelper(getApplicationContext());
        List<Cellular> cellulars = data.getAllCellulars();
        ArrayList<String> list = new ArrayList<>();

        if (cellulars == null) {
            list.add("Cellulars not found");
        } else {

            for (int i = 0; i <cellulars.size() + 1; i++) {
                if(i == 0)
                    list.add(getString(R.string.text_spinner_select));
                else
                    list.add(cellulars.get(i-1).getNumber());
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerNumbersToAddCellularsLocations.setAdapter(dataAdapter);
        }
        data = null;

    }

    private void expandListAddCellularsLocations() {
        for (int i = 0; i < CountItem; i++){
            myExpandableListAddCellularsLocations.expandGroup(i);
        }
    }

    private void addListCellularsLocations (View view){

    }

    private void loadExpandableListAddCelsLocs() {
        List<String> allNumbers;
        String header = getString(R.string.location_tv_numbers_to_notify).toString();
        listAddCelsLocsDataHeader = new ArrayList<>();
        listAddCelsLocsDataChild = new HashMap<>();
        if (listNumbersBeforeSave != null) {
            allNumbers = new ArrayList<>();
            for (int i =0; i < listNumbersBeforeSave.size(); i++){
                allNumbers.add(listNumbersBeforeSave.get(i).replace("[", "").replace("]", ""));
            }
            listAddCelsLocsDataHeader.add(header);
            listAddCelsLocsDataChild.put(header, allNumbers);
            CountItem = listAddCelsLocsDataHeader.size();
        } else {
            allNumbers = new ArrayList<>();
            allNumbers.add( getString(R.string.location_template_item_no_numbers_to_notify));
            listAddCelsLocsDataHeader.add(header);
            listAddCelsLocsDataChild.put(header, allNumbers);
        }
    }

    private void loadParameter() {
        try {
            Bundle b = getIntent().getExtras();
            //parameterLocation = (String) b.get("locationName");

            listNumbersBeforeSave.add(String.valueOf(b.getCharSequenceArrayList("numbers")).replace("; ", ""));
            if (b.getCharSequenceArrayList("LocationName") != null) {
                parameterLocation = b.getCharSequenceArrayList("LocationName").get(0).toString();
                Log.d("oooooooooooooooooooooooooooooooooooo Location name", parameterLocation);
                b.remove("LocationName");
            }
            if (b.get("LocationName") != null){
                parameterLocation = b.get("LocationName").toString();
                Log.d("oooooooooooooooooooooooooooooooooooo Location name", parameterLocation);

            }
            if (!listNumbersBeforeSave.isEmpty()) {
                setTitle(getString(R.string.cellular_location_list_cellulars));
                getIntent().removeExtra("numbers");
            }
            if (!parameterLocation.isEmpty())
                getIntent().removeExtra("LocationName");
        }
        catch (Exception e){
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_cellular_location, menu);
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
