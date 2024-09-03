
package de.t_dankworth.secscanqr.activities.generator;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.activities.MainActivity;
import de.t_dankworth.secscanqr.util.GeneralHandler;

/**
 * Created by Thore Dankworth
 * Last Update: 01.05.2020
 * Last Update by Thore Dankworth
 *
 * This class is all about the VCard to QR-Code Generate Activity.
 */

public class VCardGeneratorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText tfFirstName, tfName, tfOrg, tfTeleWork, tfTelePrivate, tfMobil, tfEmail, tfWeb, tfStreet, tfPLZ, tfCity, tfState, tfCountry, tfPhoto, tfBday, tfNote;
    int format;
    String[] text2Qr = new String[16];
    String vcardCode;
    Button btnGenerate;
    private static final String STATE_TEXT = MainActivity.class.getName();

    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneralHandler generalHandler = new GeneralHandler(this);
        generalHandler.loadTheme();
        setContentView(R.layout.activity_vcard_generator);
        initializeInputFields();
        btnGenerate = (Button) findViewById(R.id.btnGenerateVCard);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertValuesIntoArray();
                if(text2Qr[0].isEmpty() && text2Qr[1].isEmpty()){
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_fn_or_name_first), Toast.LENGTH_SHORT).show();
                } else {
                    buildVCardCode();
                    openResultActivity();
                }
            }
        });
        //Setup the Spinner Menu for the different formats
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.text_formats_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



        if(savedInstanceState != null){
            text2Qr = (String[]) savedInstanceState.get(STATE_TEXT);
            recoverOldValues();
        }

    }

    /**
     * This method saves all data before the Activity will be destroyed
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putStringArray(STATE_TEXT, text2Qr);
    }

    /**
     * Generates the chosen format from the spinner menu
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String compare = parent.getItemAtPosition(position).toString();
        if(compare.equals("AZTEC")){
            format = 10;
        }
        else if(compare.equals("QR_CODE")){
            format = 9;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        format = 9;
    }

    /**
     * Initializes all input fields
     */
    private void initializeInputFields(){
        tfFirstName = findViewById(R.id.txtFN);
        tfName = findViewById(R.id.txtName);
        tfBday = findViewById(R.id.txtBday);
        tfOrg =  findViewById(R.id.txtOrg);
        tfPhoto =  findViewById(R.id.txtPhotoUri);
        tfTeleWork =  findViewById(R.id.txtTeleWork);
        tfTelePrivate =  findViewById(R.id.txtTelePrivat);
        tfMobil =  findViewById(R.id.txtMobil);
        tfEmail =  findViewById(R.id.txtEmail);
        tfWeb =  findViewById(R.id.txtWeb);
        tfStreet =  findViewById(R.id.txtStreet);
        tfPLZ =  findViewById(R.id.txtPLZ);
        tfCity =  findViewById(R.id.txtCity);
        tfState =  findViewById(R.id.txtState);
        tfCountry =  findViewById(R.id.txtCountry);
        tfNote =  findViewById(R.id.txtNote);
    }

    /**
     * The values of the input fields will be placed into a string array
     */
    private void insertValuesIntoArray(){
        text2Qr[0] = txtStrTrim(tfName);
        text2Qr[1] = txtStrTrim(tfFirstName);
        text2Qr[2] = txtStrTrim(tfBday);
        text2Qr[3] = txtStrTrim(tfOrg);
        text2Qr[4] = txtStrTrim(tfPhoto);
        text2Qr[5] = txtStrTrim(tfWeb);
        text2Qr[6] = txtStrTrim(tfEmail);
        text2Qr[7] = txtStrTrim(tfMobil);
        text2Qr[8] = txtStrTrim(tfTeleWork);
        text2Qr[9] = txtStrTrim(tfTelePrivate);
        text2Qr[10] = txtStrTrim(tfStreet);
        text2Qr[11] = txtStrTrim(tfCity);
        text2Qr[12] = txtStrTrim(tfState);
        text2Qr[13] = txtStrTrim(tfPLZ);
        text2Qr[14] = txtStrTrim(tfCountry);
        text2Qr[15] = txtStrTrim(tfNote);
    }
    private String txtStrTrim(EditText item){
        return item.getText().toString().trim();
    }
    /**
     * This will write the values of the array into the input fields
     * Needed if screen gets rotated
     */
    private void recoverOldValues(){
        tfName.setText(text2Qr[0]);
        tfFirstName.setText(text2Qr[1]);
        tfBday.setText(text2Qr[2]);
        tfOrg.setText(text2Qr[3]);
        tfPhoto.setText(text2Qr[4]);
        tfWeb.setText(text2Qr[5]);
        tfEmail.setText(text2Qr[6]);
        tfMobil.setText(text2Qr[7]);
        tfTeleWork.setText(text2Qr[8]);
        tfTelePrivate.setText(text2Qr[9]);
        tfStreet.setText(text2Qr[10]);
        tfCity.setText(text2Qr[11]);
        tfState.setText(text2Qr[12]);
        tfPLZ.setText(text2Qr[13]);
        tfCountry.setText(text2Qr[14]);
        tfNote.setText(text2Qr[15]);
    }

    /**
     * This method builds the final string which gets transformed into a qr-code
     */
    private void buildVCardCode(){
        StringBuilder temp = new StringBuilder();
        temp.append("BEGIN:VCARD\nVERSION:2.1\nN:").
                append(text2Qr[0]).
                append(";").
                append(text2Qr[1]).
                append("\n");
        for (int i = 2; i <= 15; i++) {
            if(!text2Qr[i].isEmpty())
                switch (i){
                    case 2:
                        temp.append("BDAY:").append(text2Qr[2]).append("\n");
                        break;
                    case 3:
                        temp.append("ORG:").append(text2Qr[3]).append("\n");
                        break;
                    case 4:
                        temp.append("PHOTO;VALUE=URI:").append(text2Qr[4]).append("\n");
                        break;
                    case 5:
                        temp.append("URL:").append(text2Qr[5]).append("\n");
                        break;
                    case 6:
                        temp.append("EMAIL;TYPE=INTERNET:").append(text2Qr[6]).append("\n");
                        break;
                    case 7:
                        temp.append("TEL;CELL:").append(text2Qr[7]).append("\n");
                        break;
                    case 8:
                        temp.append("TEL;WORK;VOICE:").append(text2Qr[8]).append("\n");
                        break;
                    case 9:
                        temp.append("TEL;HOME;VOICE:").append(text2Qr[9]).append("\n");
                        break;
                    case 10:
                        temp.append("ADR:;;").append(text2Qr[10]).append(";");
                        i++;
                    case 11:
                        for (;i <= 13; i++) {
                            temp.append(text2Qr[i]).append(";");
                        }
                    case 14:
                        temp.append(text2Qr[11]).append("\n");
                        i++;
                        break;
                    case 15:
                        temp.append("NOTE:").append(text2Qr[15]).append("\n");
                }
        }
        vcardCode = temp.append("END:VCARD").toString();
    }

    /**
     *  This method will launch a new Activity were the generated QR-Code will be displayed.
     */
    private void openResultActivity(){
        Intent intent = new Intent(this, GeneratorResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CODE", vcardCode);
        bundle.putInt("FORMAT", format);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
