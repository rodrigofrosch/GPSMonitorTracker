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

public class CellularsFragment extends Fragment {

    private ExpandableListAdapterCellular listAdapterCellular;
    private ExpandableListView myExpandableListCellulars;
    private List<String> listCellularDataHeader;
    private HashMap<String, List<String>> listCellularDataChild;
    private DataOpenHelper data;
    private static int CountItem = 0;
    private int groupPosition;
    private int childPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View cellulars = inflater.inflate(R.layout.fragment_cellulars, container, false);

        //get expandable list view
        myExpandableListCellulars = (ExpandableListView)cellulars.findViewById(R.id.expandableListViewCellulars);

        //load data for database for expandable lists
        loadExpandableListCellulars();
        //create list adapter
        listAdapterCellular = new ExpandableListAdapterCellular(cellulars.getContext(), listCellularDataHeader, listCellularDataChild);
        // setting list adapter
        myExpandableListCellulars.setAdapter(listAdapterCellular);

        expandListCellulars();

        myExpandableListCellulars.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
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
                                    try {
                                        if (deleteCellular(adapter.getGroup(groupPosition).toString())){
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
                                    } catch (Exception e) {
                                        //Toast.makeText(getActivity(), getString(R.string.location_toast_delete_failed) + " Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
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
                                    Intent positveActivity = new Intent(getActivity(), NewCellularActivity.class);
                                    positveActivity.putExtra("CellularName", adapter.getGroup(groupPosition).toString());
                                    getActivity().finish();
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


        return cellulars;
    }

    private boolean deleteCellular(String name) throws Exception {
        data = new DataOpenHelper(getActivity());
        if (data.deleteCellularByName(name))
            return true;
        else
            return false;
    }

    private void expandListCellulars() {
        for (int i = 0; i < CountItem; i++){
            myExpandableListCellulars.expandGroup(i);
        }
    }

    private void loadExpandableListCellulars() {
        data = new DataOpenHelper(getActivity());
        ArrayList<Cellular> cellulars = data.getAllCellulars();
        List<String> childs;
        listCellularDataHeader = data.getAllCellularsName();
        listCellularDataChild = new HashMap<String, List<String>>();

        for (int i = 0; i < listCellularDataHeader.size(); i++) {
            for (Cellular cellular : cellulars){
                childs = new ArrayList<String>();
                //childs.add(getString(R.string.cellular_template_item_id) + " " + Integer.toString(cellular.getId()));
                childs.add(getString(R.string.cellular_template_item_number) + " " + cellular.getNumber());
                childs.add(getString(R.string.cellular_template_item_is_notify) + " " + cellular.getIsNotify());

                listCellularDataChild.put(cellular.getName(), childs);
            }
        }
        CountItem = listCellularDataHeader.size();
    }
}
