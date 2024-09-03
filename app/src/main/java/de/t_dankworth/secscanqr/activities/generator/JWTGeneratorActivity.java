package de.t_dankworth.secscanqr.activities.generator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;


import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.activities.MainActivity;
import de.t_dankworth.secscanqr.util.GeneralHandler;

/**
 * Created by Thore Dankworth
 * Last Update: 01.05.2020
 * Last Update by Thore Dankworth
 * This class is all about the JWT to QR-Code Generate Activity.
 */

public class JWTGeneratorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    ArrayList<EditText> statementName, statementCont;
    int format;
    EditText sk;
    ArrayList<String> text2Qr = new ArrayList<>();
    String jwtRes;
    LinearLayout ll;
    Button btnGenerate, btnNewStat;
    private static final String STATE_TEXT = MainActivity.class.getName();
    private int nextId = 1;
    private int initialized = 1;
    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        statementName = new ArrayList<>();
        statementCont = new ArrayList<>();
        super.onCreate(savedInstanceState);
        GeneralHandler generalHandler = new GeneralHandler(this);
        generalHandler.loadTheme();
        setContentView(R.layout.activity_jwt_generator);
        statementName.add(findViewById(R.id.statementName));
        statementCont.add(findViewById(R.id.statementContent));
        sk = findViewById(R.id.secretKey);

        btnGenerate = (Button) findViewById(R.id.btnGenerateID);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertValuesIntoArray();
                if(sk.getText().toString().length() >= 256 || sk.getText().toString().isEmpty()) {
                    text2Qr.add(txtStrTrim(sk));
                    if(text2Qr.get(0).isEmpty()){
                        Toast.makeText(getApplicationContext(), "type something first!", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            buildJWT();
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                        openResultActivity();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(), "the secret key must be 256 characters long!" + sk.getText().toString().length(), Toast.LENGTH_SHORT).show();

            }
        });

        btnNewStat = (Button) findViewById(R.id.btnGenerateStatements);
        int i = 0, j = 0;
        ll = (LinearLayout) findViewById(R.id.linearLayout);
        btnNewStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText temp = findViewById(R.id.statementName);
                EditText temp2 = findViewById(R.id.statementContent);
                EditText editText1 = new EditText(temp.getContext());
                EditText editText2 = new EditText(temp2.getContext());
                editText1.setId(nextId);
                editText2.setId(nextId + 1);
                editText1.setHint("statement name");
                editText2.setHint("statement content");
                editText1.setTextColor(temp.getTextColors());
                editText2.setTextColor(temp.getTextColors());

                ll.addView(editText1);
                ll.addView(editText2);
                nextId += 1;
                initializeInputFields();
            }
        });
        //Setup the Spinner Menu for the different formats
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.text_formats_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        System.out.println("a");
        if(savedInstanceState != null){
            text2Qr = (ArrayList<String>) savedInstanceState.get(STATE_TEXT);
            recoverOldValues();
        }
    }

    /**
     * This method saves all data before the Activity will be destroyed
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putStringArrayList(STATE_TEXT, text2Qr);
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
        statementName.add(findViewById(R.id.statementName));
        statementCont.add(findViewById(R.id.statementContent));
        sk = findViewById(R.id.secretKey);
        for (int i = initialized; i < nextId; i++) {
            if(!txtStrTrim(findViewById(i)).isEmpty()){
                if (i % 2 == 0) {
                    statementCont.add(findViewById(i));
                }
                else {
                    statementName.add(findViewById(i));
                }
            }
        }
        initialized = nextId;
    }


    /**
     * The values of the input fields will be placed into a string array
     */
    private void insertValuesIntoArray(){
        text2Qr.add(txtStrTrim(findViewById(R.id.statementName)));
        text2Qr.add(txtStrTrim(findViewById(R.id.statementContent)));
        for (int i = 1; i < nextId; i++) {
            String text = txtStrTrim(findViewById(i));
            if (!text.isEmpty())
                text2Qr.add(text);
        }
        initialized = nextId;
    }
    private String txtStrTrim(EditText item){
        return item.getText().toString().trim();
    }
    /**
     * This will write the values of the array into the input fields
     * Needed if screen gets rotated
     */
    private void recoverOldValues(){
        int len = statementName.size();
        for (int i = 0; i < len; i++) {
            if (i % 2 == 0)
                statementCont.get(i).setText(text2Qr.get(i));
            else
                statementCont.get(i).setText(text2Qr.get(i));
        }
    }

    /**
     * This method builds the final string which gets transformed into a qr-code
     */
    private void buildJWT() throws NoSuchAlgorithmException {
        JwtBuilder objJWT = Jwts.builder();
        int len = statementName.size();
        for (int i = 0; i < len; i++) {
            objJWT.claim(txtStrTrim(statementName.get(i)), txtStrTrim(statementCont.get(i)));
        }
        objJWT.issuedAt(new Date());
        SecretKey secretKey;
        if(txtStrTrim(sk).length() > 256)
            secretKey = new SecretKeySpec(txtStrTrim(sk).getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        else{
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256);
            // Generate the secret key
            secretKey = keyGen.generateKey();
        }
        objJWT.signWith(secretKey);
        jwtRes = objJWT.compact();
    }

    /**
     *  This method will launch a new Activity where the generated QR-Code will be displayed.
     */
    private void openResultActivity(){
        Intent intent = new Intent(this, GeneratorResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CODE", jwtRes);
        bundle.putInt("FORMAT", format);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
