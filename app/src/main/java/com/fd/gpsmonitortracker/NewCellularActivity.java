package com.fd.gpsmonitortracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class NewCellularActivity extends ActionBarActivity {

    DataOpenHelper data;
    EditText editTextName, editTextNumber;
    Button btnSaveCellullar;
    CheckBox ckbIsNotify;
    String parameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cellular);

        editTextName = (EditText) findViewById(R.id.editTextCellularName);
        editTextNumber = (EditText) findViewById(R.id.editTextCellularNumber);
        ckbIsNotify = (CheckBox) findViewById(R.id.ckbCellularIsNotify);
        btnSaveCellullar = (Button) findViewById(R.id.btnSaveCellullar);

        loadParameter();

        if (parameter != null){
            btnSaveCellullar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!editTextNumber.getText().toString().matches("") || !editTextName.getText().toString().matches("")){
                        data = new DataOpenHelper(getApplicationContext());
                        String notify = "";
                        if (ckbIsNotify.isChecked())
                            notify = "yes".toUpperCase();
                        else
                            notify = "no".toUpperCase();
                        if (data.updateCellular(null, editTextName.getText().toString().toUpperCase(), editTextNumber.getText().toString(), notify)){
                            Toast.makeText(getApplicationContext(), getString(R.string.cellular_update_success), Toast.LENGTH_LONG).show();
                            SystemClock.sleep(Toast.LENGTH_LONG*1000);
                            //go to MainActivity
                            Intent main;
                            main = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(main);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.cellular_update_failed), Toast.LENGTH_LONG).show();
                            editTextName.setText("");
                            editTextNumber.setText("");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.cellular_new_validation), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    private void loadParameter() {
        try {
            Bundle b = getIntent().getExtras();
            parameter = b.getString("CellularName");
            if (parameter != "") {
                setTitle(getString(R.string.title_activity_update_location));
                loadCellular(parameter);
                getIntent().removeExtra("CellularName");
            }
        }
        catch (Exception e){
            return;
        }
    }

    private void loadCellular(String parameter) {
        data = new DataOpenHelper(getApplicationContext());
        Cellular cellular = data.getCellularByName(parameter);
        editTextName.setText(cellular.getName());
        editTextNumber.setText(cellular.getNumber());
        ckbIsNotify.setChecked((cellular.getIsNotify() == "YES") ? false : true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_cellular, menu);
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

    public boolean isTelefone(String numeroTelefone) {
        String formato = "\\d";
        if((numeroTelefone == null) || (numeroTelefone.length()>11) || (!numeroTelefone.matches(formato)))
            return false;
        else
            return true;
    }

    private boolean passValidateNull(){
        if (!editTextName.getText().toString().trim().isEmpty() &&
                !editTextNumber.getText().toString().trim().isEmpty()){
            return true;
        } else return false;
    }


    public void saveCellular(View view){
        if (passValidateNull()){
            if (isTelefone(editTextNumber.getText().toString())) {
                data = new DataOpenHelper(getApplicationContext());
                String notify = "";
                if (ckbIsNotify.isChecked())
                    notify = "yes".toUpperCase();
                else
                    notify = "no".toUpperCase();
                if (data.insertCellular(editTextName.getText().toString().toUpperCase(), editTextNumber.getText().toString(), notify)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.cellular_new_success), Toast.LENGTH_LONG).show();
                    SystemClock.sleep(Toast.LENGTH_LONG * 1000);
                    //go to MainActivity
                    Intent main;
                    main = new Intent(this, MainActivity.class);
                    startActivity(main);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.cellular_new_failed), Toast.LENGTH_LONG).show();
                    editTextName.setText("");
                    editTextNumber.setText("");
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.cellular_new_validation_numbers), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.cellular_new_validation), Toast.LENGTH_LONG).show();
        }
    }
}
