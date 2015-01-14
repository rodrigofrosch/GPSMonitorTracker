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

public class MessagesFragment extends Fragment {

    ExpandableListAdapterMessage listAdapterMessage;
    ExpandableListView myExpandableListMessages;
    List<String> listMessageDataHeader;
    HashMap<String, List<String>> listMessageDataChild;
    DataOpenHelper data;
    int CountItem = 0;
    int groupPosition;
    int childPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View messages = inflater.inflate(R.layout.fragment_messages, container, false);

        //get expandable list view
        myExpandableListMessages = (ExpandableListView)messages.findViewById(R.id.expandableListViewMessages);

        //load data for database for expandable lists
        loadExpandableListMessage();
        //create list adapter
        listAdapterMessage = new ExpandableListAdapterMessage(messages.getContext(), listMessageDataHeader, listMessageDataChild);
        // setting list adapter
        myExpandableListMessages.setAdapter(listAdapterMessage);

        expandListMessages();

        myExpandableListMessages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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

                                    if (deleteMessage(adapter.getChild(groupPosition, childPosition).toString().replace("Text: ", ""))){
                                        Toast.makeText(getActivity(), getString(R.string.message_toast_delete_sucess), Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(getActivity(), getString(R.string.message_toast_delete_failed), Toast.LENGTH_SHORT).show();
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
                                    Intent positveActivity = new Intent(getActivity(), NewMessageActivity.class);
                                    String location = adapter.getGroup(groupPosition).toString().replace("IN", "").replace("OUT", "").trim();
                                    String direction = adapter.getGroup(groupPosition).toString().replace(location, "").trim();
                                    positveActivity.putExtra("location", location);
                                    positveActivity.putExtra("direction", direction);
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


        return messages;
    }

    private boolean deleteMessage(String msg) {
        data = new DataOpenHelper(getActivity());
        if (data.deleteMessage(msg) != 0){
            return true;
        }
        else
            return false;
    }

    private void expandListMessages() {
        for (int i = 0; i < CountItem; i++){
            myExpandableListMessages.expandGroup(i, true);
        }
    }

    private void loadExpandableListMessage() {
        data = new DataOpenHelper(getActivity());
        ArrayList<Message> allMessages = data.getAllMessages();
        List<String> childs = null;
        listMessageDataHeader = new ArrayList<String>();
        String header;

        listMessageDataChild = new HashMap<String, List<String>>();

        for (Message message: allMessages){
            header = message.getLocation().toUpperCase() + " " + message.getDirection().toUpperCase();
            childs = new ArrayList<String>();
            //childs.add(Integer.toString(message.getId()));
            childs.add(getString(R.string.message_template_item_text) + " " + message.getMsg());
            listMessageDataHeader.add(header);
            listMessageDataChild.put(header, childs);
        }


        CountItem = listMessageDataHeader.size();
    }
}
