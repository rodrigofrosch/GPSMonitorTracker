package com.fd.gpsmonitortracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationsFragment extends Fragment {

    ExpandableListAdapterLocation listAdapterLocation;
    ExpandableListView myExpandableListLocations;
    List<String> listLocationDataHeader;
    HashMap<String, List<String>> listLocationDataChild;
    DataOpenHelper data;
    int CountItem = 0;
    int groupPosition;
    int childPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View locations = inflater.inflate(R.layout.fragment_locations, container, false);

        //get expandable list view
        myExpandableListLocations = (ExpandableListView)locations.findViewById(R.id.expandableListViewLocations);

        //load data for database for expandable lists
        loadExpandableListLocation();
        //create list adapter
        listAdapterLocation = new ExpandableListAdapterLocation(locations.getContext(), listLocationDataHeader, listLocationDataChild);

        // setting list adapter
        myExpandableListLocations.setAdapter(listAdapterLocation);

        expandListLocations();

        myExpandableListLocations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                    final ExpandableListAdapter adapter = ((ExpandableListView) parent).getExpandableListAdapter();

                    groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    childPosition = ExpandableListView.getPackedPositionChild(id);


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage(R.string.locations_popup_decision);
                    alertDialogBuilder.setNeutralButton(R.string.delete_button,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (deleteLocation(adapter.getGroup(groupPosition).toString())){
                                        Toast.makeText(getActivity(), getString(R.string.location_toast_delete_sucess), Toast.LENGTH_SHORT).show();
                                        SystemClock.sleep(Toast.LENGTH_SHORT * 1000);

                                        //reload
                                        Intent refresh = new Intent(getActivity(), MainActivity.class);
                                        try {
                                            getActivity().finish();
                                        } catch (Throwable throwable) {
                                            throwable.printStackTrace();
                                        }
                                        startActivity(refresh);

                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.location_toast_delete_failed), Toast.LENGTH_SHORT).show();
                                    }
                                    return;
                                }
                            });
                    /*
                    alertDialogBuilder.setNegativeButton(R.string.cancel_button,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    return;
                                }
                            });
                    * */
                    alertDialogBuilder.setPositiveButton(R.string.update_button,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent positveActivity = new Intent(getActivity(), NewLocationActivity.class);
                                    positveActivity.putExtra("LocationName", adapter.getGroup(groupPosition).toString());
                                    positveActivity.putExtra("LastActivity", "update");

                                    try {
                                        getActivity().finish();
                                    } catch (Throwable throwable) {
                                        throwable.printStackTrace();
                                    }
                                    startActivity(positveActivity);
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    // You now have everything that you would as if this was an OnChildClickListener()
                    // Add your logic here.


                    // Return true as we are handling the event.
                    return true;
                }

                return false;
            }
        });

        return locations;
    }

    private boolean deleteLocation(String s) {
        data = new DataOpenHelper(getActivity());
        if (data.deleteLocationByName(s))
            return true;
        else
            return false;
    }

    private void expandListLocations() {
        for (int i = 0; i < listLocationDataHeader.size(); i++){
            myExpandableListLocations.isGroupExpanded(i);
        }
    }

    private void loadExpandableListLocation() {
        data = new DataOpenHelper(getActivity());
        listLocationDataHeader = new ArrayList<String>();
        listLocationDataChild = new HashMap<String, List<String>>();
        ArrayList<Location> allLocations = data.getAllLocations();
        List<String> childs = null;
        listLocationDataHeader = data.getAllLocationsName();
        listLocationDataChild = new HashMap<String, List<String>>();
        List<String> numbers;
        String numbersConcat = "";

        String msgIn = null;
        String msgOut = null;
        for (int i = 0; i < listLocationDataHeader.size(); i++) {
            int count = 0;
            for (Location location : allLocations) {
                if(listLocationDataHeader.get(i).equals(location.getName())) {
                    childs = new ArrayList<String>();
                    msgIn = (location.getMsgIn() != 0) ? " Yes" : " No";
                    msgOut = (location.getMsgOut() != 0) ? " Yes" : " No";
                    childs.add(getString(R.string.location_template_item_latitude) + " " + location.getLatitude());
                    childs.add(getString(R.string.location_template_item_longitude) + " " + location.getLongitude());
                    childs.add(getString(R.string.location_template_item_radius) + " " + location.getRadius());
                    numbers = data.getNumbersNotifyLocation(listLocationDataHeader.get(i));
                    if (numbers.size() > 0) {
                        numbersConcat = getString(R.string.location_template_item_numbers_to_notify) + " ";
                        int aux = 0;
                        for (String n : numbers) {
                            aux++;
                            if (!(aux == numbers.size())) {
                                numbersConcat += n.toString() + ", ";
                            } else {
                                numbersConcat += n.toString() + "; ";
                                aux = 0;
                            }
                        }
                    }
                    count++;
                    if (!numbersConcat.isEmpty() || numbers.size() == 0) {
                        if (data.getNumbersNotifyLocation(listLocationDataHeader.get(i)).size() == 0){
                            childs.add(getString(R.string.location_template_item_numbers_to_notify) + " " + getString(R.string.location_template_item_no_numbers_to_notify));
                        } else {
                            childs.add(numbersConcat);
                        }
                    }
                    numbersConcat = "";
                    childs.add(getString(R.string.location_template_item_last_msg_send) + " " + location.getLastMsgSend());
                    childs.add(getString(R.string.location_template_item_msg_in) + " " + msgIn);
                    childs.add(getString(R.string.location_template_item_msg_out) + " " + msgOut);
                    listLocationDataChild.put(listLocationDataHeader.get(i), childs);
                }

            }

        }
        CountItem = listLocationDataHeader.size();
        data = null;
    }
}
