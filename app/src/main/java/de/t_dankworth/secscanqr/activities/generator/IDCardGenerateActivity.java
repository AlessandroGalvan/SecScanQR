package de.t_dankworth.secscanqr.activities.generator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.activities.MainActivity;
import de.t_dankworth.secscanqr.util.GeneralHandler;

/**
 * Created by Thore Dankworth
 * Last Update: 01.05.2020
 * Last Update by Thore Dankworth
 * This class is all about the VCard to QR-Code Generate Activity.
 */

public class IDCardGenerateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText documentCode, documentNumber, documentName, surname, name, birthplace, birthdate, gender, height, nationality, emissionDate, expiry, residence, fiscalCode;
    int format;
    String[] text2Qr = new String[14];
    String ID;
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
        setContentView(R.layout.activity_id_generator);
        initializeInputFields();
        btnGenerate = (Button) findViewById(R.id.btnGenerateID);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertValuesIntoArray();
                if(text2Qr[0].isEmpty() && text2Qr[1].isEmpty()){
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_fn_or_name_first), Toast.LENGTH_SHORT).show();
                } else {
                    buildID();
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
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putStringArray(STATE_TEXT, text2Qr);
    }

    /**
     * Generates the chosen format from the spinner menu
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String compare = parent.getItemAtPosition(position).toString();
        // ottimizzato
        format = 9;
        if(compare.equals("AZTEC")){
            format = 10;
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
        documentCode = (EditText) findViewById(R.id.documentCode);
        documentNumber = (EditText) findViewById(R.id.documentNumber);
        documentName = (EditText) findViewById(R.id.documentName);
        surname = (EditText) findViewById(R.id.surname);
        name = (EditText) findViewById(R.id.name);
        birthplace = (EditText) findViewById(R.id.birthplace);
        birthdate = (EditText) findViewById(R.id.birthdate);
        gender = (EditText) findViewById(R.id.gender);
        height = (EditText) findViewById(R.id.height);
        nationality = (EditText) findViewById(R.id.nationality);
        emissionDate = (EditText) findViewById(R.id.emissionDate);
        expiry = (EditText) findViewById(R.id.expiry);
        residence = (EditText) findViewById(R.id.residence);
        fiscalCode = (EditText) findViewById(R.id.fiscalCode);

    }


    /**
     * The values of the input fields will be placed into a string array
     */
    private void insertValuesIntoArray(){
        text2Qr[0] = txtStrTrim(documentCode);
        text2Qr[1] = txtStrTrim(documentNumber);
        text2Qr[2] = txtStrTrim(documentName);
        text2Qr[3] = txtStrTrim(surname);
        text2Qr[4] = txtStrTrim(name);
        text2Qr[5] = txtStrTrim(birthplace);
        text2Qr[6] = txtStrTrim(birthdate);
        text2Qr[7] = txtStrTrim(gender);
        text2Qr[8] = txtStrTrim(height);
        text2Qr[9] = txtStrTrim(nationality);
        text2Qr[10] = txtStrTrim(emissionDate);
        text2Qr[11] = txtStrTrim(expiry);
        text2Qr[12] = txtStrTrim(residence);
        text2Qr[13] = txtStrTrim(fiscalCode);
    }
    private String txtStrTrim(EditText item){
        return item.getText().toString().trim();
    }
    /**
     * This will write the values of the array into the input fields
     * Needed if screen gets rotated
     */
    private void recoverOldValues(){
        // Assuming you have TextViews or EditTexts for each variable
        documentCode.setText(text2Qr[0]);
        documentNumber.setText(text2Qr[1]);
        documentName.setText(text2Qr[2]);
        surname.setText(text2Qr[3]);
        name.setText(text2Qr[4]);
        birthplace.setText(text2Qr[5]);
        birthdate.setText(text2Qr[6]);
        gender.setText(text2Qr[7]);
        height.setText(text2Qr[8]);
        nationality.setText(text2Qr[9]);
        emissionDate.setText(text2Qr[10]);
        expiry.setText(text2Qr[11]);
        residence.setText(text2Qr[12]);
        fiscalCode.setText(text2Qr[13]);

    }

    /**
     * This method builds the final string which gets transformed into a qr-code
     */
    private void buildID() {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i <= 13; i++) {
            if (!text2Qr[i].isEmpty()) {
                switch (i) {
                    case 0:
                        temp.append("Document code: ").append(text2Qr[0]).append("\n");
                        break;
                    case 1:
                        temp.append("Document number: ").append(text2Qr[1]).append("\n");
                        break;
                    case 2:
                        temp.append("Document name: ").append(text2Qr[2]).append("\n");
                        break;
                    case 3:
                        temp.append("Surname: ").append(text2Qr[3]).append("\n");
                        break;
                    case 4:
                        temp.append("Name: ").append(text2Qr[4]).append("\n");
                        break;
                    case 5:
                        temp.append("Birthplace: ").append(text2Qr[5]).append("\n");
                        break;
                    case 6:
                        temp.append("Birth date: ").append(text2Qr[6]).append("\n");
                        break;
                    case 7:
                        temp.append("Gender: ").append(text2Qr[7]).append("\n");
                        break;
                    case 8:
                        temp.append("Height: ").append(text2Qr[8]).append("\n");
                        break;
                    case 9:
                        temp.append("Nationality: ").append(text2Qr[9]).append("\n");
                        break;
                    case 10:
                        temp.append("Emission Date: ").append(text2Qr[10]).append("\n");
                        break;
                    case 11:
                        temp.append("Expiry: ").append(text2Qr[11]).append("\n");
                        break;
                    case 12:
                        temp.append("Residence: ").append(text2Qr[12]).append("\n");
                        break;
                    case 13:
                        temp.append("Fiscal code: ").append(text2Qr[13]).append("\n");
                        break;
                }
            }
        }
        ID = temp.toString();
    }

    /**
     *  This method will launch a new Activity where the generated QR-Code will be displayed.
     */
    private void openResultActivity(){
        Intent intent = new Intent(this, GeneratorResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CODE", ID);
        bundle.putInt("FORMAT", format);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
