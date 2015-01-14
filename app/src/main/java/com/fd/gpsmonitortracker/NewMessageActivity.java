package com.fd.gpsmonitortracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class NewMessageActivity extends ActionBarActivity {

    EditText editTextMessageText;
    Button btnSave;
    Spinner spinnerLocation, spinnerDirection;
    DataOpenHelper data;
    String parameter1;
    String parameter2;
    String messageLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);



        btnSave = (Button) findViewById(R.id.btnSaveMessage);
        editTextMessageText = (EditText) findViewById(R.id.editTextNewMessageText);
        spinnerLocation = (Spinner) findViewById(R.id.spinnerLocation);
        spinnerDirection = (Spinner) findViewById(R.id.spinnerDirection);

        loadParameter();

        loadSpinners();


        //UPDATE
        if (parameter1 != null && parameter2 != null){
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(passValidateNull()){
                        data = new DataOpenHelper(getApplicationContext());

                        if(data.updateMessage(null, editTextMessageText.getText().toString(),
                                spinnerLocation.getSelectedItem().toString(), spinnerDirection.getSelectedItem().toString())){
                            Toast.makeText(getApplicationContext(),  getString(R.string.message_new_message_success), Toast.LENGTH_SHORT).show();
                            SystemClock.sleep(Toast.LENGTH_SHORT * 1000);
                            Intent refresh = new Intent(getApplicationContext(), MainActivity.class);
                            try {
                                finalize();
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                            startActivity(refresh);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.message_new_message_failed), Toast.LENGTH_LONG).show();
                            editTextMessageText.setText("");
                            spinnerDirection.setSelected(false);
                            spinnerLocation.setSelected(false);
                        }

                        data.close();
                        data = null;
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.message_new_message_validation), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    private void loadParameter() {
        try {
            Bundle b = getIntent().getExtras();
            parameter1 = b.getString("location");
            parameter2 = b.getString("direction");

            if (parameter1 != "") {
                setTitle(getString(R.string.title_activity_update_message));
                loadMessage(parameter1, parameter2);
                getIntent().removeExtra("location");
                getIntent().removeExtra("direction");
            }
        }
        catch (Exception e){
            return;
        }
    }

    private boolean passValidateNull(){
        //editTextMessageText
        //spinnerLocation
        //spinnerDirection
        return ((!editTextMessageText.getText().toString().trim().isEmpty() &&
                (!spinnerLocation.getSelectedItem().toString().equals(getString(R.string.text_spinner_select).toString())) &&
                (!spinnerDirection.getSelectedItem().toString().equals(getString(R.string.text_spinner_select).toString())))) ? true : false;
    }

    private void loadMessage(String location, String direction) {
        data = new DataOpenHelper(getApplicationContext() );
        messageLoad = data.getMessageByLocation(location, direction);
        data = null;
        editTextMessageText.setText(messageLoad);
    }

    private void loadSpinners() {
        List<String> list2;

        list2 = new ArrayList<String>();
        list2.add(getString(R.string.text_spinner_select));
        list2.add("IN");
        list2.add("OUT");

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDirection.setAdapter(dataAdapter2);

        List<String> list3;
        list3 = new ArrayList<String>();
        data = new DataOpenHelper(getApplicationContext());
        ArrayList<String> list;
        list = data.getAllLocationsName();
        if (list == null) {
            list.add("Locations not found");
        } else {
            for (int i = 0; i <list.size() + 1; i++) {
                if(i == 0)
                    list3.add(getString(R.string.text_spinner_select));
                else
                    list3.add(list.get(i - 1));
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list3);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLocation.setAdapter(dataAdapter);
        }

    }


    public void saveMessage(View view){
        if(passValidateNull()){
            data = new DataOpenHelper(getApplicationContext());
            if (!checkMessageInOrOutExists(spinnerLocation.getSelectedItem().toString(), spinnerDirection.getSelectedItem().toString())) {
                if (data.insertMessage(editTextMessageText.getText().toString(),
                        spinnerLocation.getSelectedItem().toString(), spinnerDirection.getSelectedItem().toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.message_new_message_success), Toast.LENGTH_SHORT).show();
                    SystemClock.sleep(Toast.LENGTH_SHORT * 1000);
                    Intent refresh = new Intent(this, MainActivity.class);
                    try {
                        finalize();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    startActivity(refresh);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.message_new_message_failed), Toast.LENGTH_LONG).show();
                    editTextMessageText.setText("");
                    spinnerDirection.setSelected(false);
                    spinnerLocation.setSelected(false);
                }
                data.close();
                data = null;
            } else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.message_toast_message_exists) + spinnerLocation.getSelectedItem().toString() + " " + spinnerDirection.getSelectedItem().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.message_new_message_validation), Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkMessageInOrOutExists(String location, String direction) {
        data = new DataOpenHelper(getApplicationContext());
        if (data.getMessageByLocation(location, direction) != "")
            return true;
        else
            return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_message, menu);
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
