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

public class BookGenerateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText isbn, title, author, genre, coverURL, release, price;
    int format;
    String[] text2Qr = new String[7];
    String book;
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
        setContentView(R.layout.activity_book_generate);
        initializeInputFields();
        btnGenerate = (Button) findViewById(R.id.btnGenerateBook);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertValuesIntoArray();
                if(text2Qr[0].isEmpty() && text2Qr[1].isEmpty()){
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_fn_or_name_first), Toast.LENGTH_SHORT).show();
                } else {
                    buildBook();
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
        isbn = (EditText) findViewById(R.id.isbn);
        title = (EditText) findViewById(R.id.title);
        release = (EditText) findViewById(R.id.release);
        author = (EditText) findViewById(R.id.author);
        coverURL = (EditText) findViewById(R.id.coverURL);
        genre = (EditText) findViewById(R.id.genre);
        price = (EditText) findViewById(R.id.price);
    }


    /**
     * The values of the input fields will be placed into a string array
     */
    private void insertValuesIntoArray(){
        text2Qr[0] = txtStrTrim(isbn);
        text2Qr[1] = txtStrTrim(title);
        text2Qr[2] = txtStrTrim(release);
        text2Qr[3] = txtStrTrim(author);
        text2Qr[4] = txtStrTrim(coverURL);
        text2Qr[5] = txtStrTrim(genre);
        text2Qr[6] = txtStrTrim(price);
    }
    private String txtStrTrim(EditText item){
        return item.getText().toString().trim();
    }
    /* RICOSTRUITA
    private void insertValuesIntoArray(){
        text2Qr[0] = tfName.getText().toString().trim();
        text2Qr[1] = tfFirstName.getText().toString().trim();
        text2Qr[2] = tfBday.getText().toString().trim();
        text2Qr[3] = tfOrg.getText().toString().trim();
        text2Qr[4] = tfPhoto.getText().toString().trim();
        text2Qr[5] = tfWeb.getText().toString().trim();
        text2Qr[6] = tfEmail.getText().toString().trim();
        text2Qr[7] = tfMobil.getText().toString().trim();
        text2Qr[8] = tfTeleWork.getText().toString().trim();
        text2Qr[9] = tfTelePrivate.getText().toString().trim();
        text2Qr[10] = tfStreet.getText().toString().trim();
        text2Qr[11] = tfCity.getText().toString().trim();
        text2Qr[12] = tfState.getText().toString().trim();
        text2Qr[13] = tfPLZ.getText().toString().trim();
        text2Qr[14] = tfCountry.getText().toString().trim();
        text2Qr[15] = tfNote.getText().toString().trim();
    }
    */
    /**
     * This will write the values of the array into the input fields
     * Needed if screen gets rotated
     */
    private void recoverOldValues(){
        isbn.setText(text2Qr[0]);
        title.setText(text2Qr[1]);
        release.setText(text2Qr[2]);
        author.setText(text2Qr[3]);
        coverURL.setText(text2Qr[4]);
        genre.setText(text2Qr[5]);
        price.setText(text2Qr[6]);
    }

    /**
     * This method builds the final string which gets transformed into a qr-code
     */
    private void buildBook() {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i <= 6; i++) {
            if (!text2Qr[i].isEmpty()) {
                switch (i) {
                    case 0:
                        temp.append("ISBN: ").append(text2Qr[0]).append("\n");
                        break;
                    case 1:
                        temp.append("Title: ").append(text2Qr[1]).append("\n");
                        break;
                    case 2:
                        temp.append("Release date: ").append(text2Qr[2]).append("\n");
                        break;
                    case 3:
                        temp.append("Author: ").append(text2Qr[3]).append("\n");
                        break;
                    case 4:
                        temp.append("Cover URL: ").append(text2Qr[4]).append("\n");
                        break;
                    case 5:
                        temp.append("Genre: ").append(text2Qr[5]).append("\n");
                        break;
                    case 6:
                        temp.append("Price: ").append(text2Qr[6]).append("\n");
                        break;
                }
            }
        }
        book = temp.toString();
    }

    /**
     *  This method will launch a new Activity where the generated QR-Code will be displayed.
     */
    private void openResultActivity(){
        Intent intent = new Intent(this, GeneratorResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CODE", book);
        bundle.putInt("FORMAT", format);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
