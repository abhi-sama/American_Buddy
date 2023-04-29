package edu.psu.axs7326.american_buddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.psu.axs7326.american_buddy.db.SlangViewModel;

public class ConverterActivity extends AppCompatActivity{


    private Button unitConvert,clearValue;
    private EditText editInput1,editInput2;
    private TextView textInput1,textInput2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
//        getSupportActionBar().hide();
        Spinner selected_unit =findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.unit_dropdown, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selected_unit.setAdapter(adapter);

        unitConvert = findViewById(R.id.unitConvert);
        clearValue = findViewById(R.id.clear);
        editInput1=(EditText)findViewById(R.id.idEditUnit1);
        editInput2=(EditText)findViewById(R.id.idEditUnit2);
        textInput1=(TextView) findViewById(R.id.idUnit1);
        textInput2=(TextView) findViewById(R.id.idUnit2);
        selected_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (selected_unit.getSelectedItem().toString())
                {
                    case "Fahrenheit-Celsius":
                        textInput1.setText("Fahrenheit");
                        textInput2.setText("Celsius");
                        editInput1.setText("");
                        editInput2.setText("");
                        break;
                    case "Miles-Kilometers":
                        textInput1.setText("Miles");
                        textInput2.setText("Kilometers");
                        editInput1.setText("");
                        editInput2.setText("");
                        break;

                    case "Ounces-Litres":
                        textInput1.setText("Ounces");
                        textInput2.setText("Litres");
                        editInput1.setText("");
                        editInput2.setText("");
                        break;

                    case "Pounds-Kilo":
                        textInput1.setText("Pounds");
                        textInput2.setText("Kilo");
                        editInput1.setText("");
                        editInput2.setText("");
                        break;

                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }

        });
        unitConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selected_unit.getSelectedItem().toString())
                {
                    case "Fahrenheit-Celsius":

                        if(!editInput1.getText().toString().equals("")&&!editInput2.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(),"Please clear out the values",Toast.LENGTH_SHORT).show();
                        }
                        else if(!editInput1.getText().toString().equals("")){
                            double chosenNumber = Double.parseDouble(editInput1.getText().toString());
                            double convertedNumber = (chosenNumber - 32) * 5/9;
                            editInput2.setText("");
                            editInput2.setText(String.format("%.2f", convertedNumber));

                        }
                        else if(!editInput2.getText().toString().equals("")) {
                            double chosenNumber = Double.parseDouble(editInput2.getText().toString());
                            double convertedNumber = (chosenNumber * 9/5) + 32;
                            editInput1.setText("");
                            editInput1.setText(String.format("%.2f", convertedNumber));
                        }
                        break;
                    case "Miles-Kilometers":
                        if(!editInput1.getText().toString().equals("")&&!editInput2.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(),"Please clear out value",Toast.LENGTH_SHORT).show();
                        }
                        else if(!editInput1.getText().toString().equals("")){
                            double chosenNumber = Double.parseDouble(editInput1.getText().toString());
                            if(chosenNumber<0) {
                                Toast.makeText(getApplicationContext(),"Entered unit value cannot be in negative.",Toast.LENGTH_SHORT).show();
                                break;
                            }
                            double convertedNumber = chosenNumber * 1.60934;
                            editInput2.setText("");
                            editInput2.setText(String.format("%.2f", convertedNumber));
                        }
                        else if(!editInput2.getText().toString().equals("")) {
                            double chosenNumber = Double.parseDouble(editInput2.getText().toString());
                            if(chosenNumber<0) {
                                Toast.makeText(getApplicationContext(),"Entered unit value cannot be in negative.",Toast.LENGTH_SHORT).show();
                                break;
                            }
                            double convertedNumber = chosenNumber / 1.60934;
                            editInput1.setText("");
                            editInput1.setText(String.format("%.2f", convertedNumber));
                        }
                        break;

                    case "Ounces-Litres":
                        if(!editInput1.getText().toString().equals("")&&!editInput2.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(),"Please clear out value",Toast.LENGTH_SHORT).show();
                        }
                        else if(!editInput1.getText().toString().equals("")){
                            double chosenNumber = Double.parseDouble(editInput1.getText().toString());
                            if(chosenNumber<0) {
                                Toast.makeText(getApplicationContext(),"Entered unit value cannot be in negative.",Toast.LENGTH_SHORT).show();
                                break;
                            }
                            double convertedNumber = chosenNumber / 33.814;
                            editInput2.setText("");
                            editInput2.setText(String.format("%.2f", convertedNumber));
                        }
                        else if(!editInput2.getText().toString().equals("")) {
                            double chosenNumber = Double.parseDouble(editInput2.getText().toString());
                            if(chosenNumber<0) {
                                Toast.makeText(getApplicationContext(),"Entered unit value cannot be in negative.",Toast.LENGTH_SHORT).show();
                                break;
                            }
                            double convertedNumber = chosenNumber * 33.814;
                            editInput1.setText("");
                            editInput1.setText(String.format("%.2f", convertedNumber));
                        }
                        break;

                    case "Pounds-Kilo":
                        if(!editInput1.getText().toString().equals("")&&!editInput2.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(),"Please clear out value",Toast.LENGTH_SHORT).show();
                        }
                        else if(!editInput1.getText().toString().equals("")){
                            double chosenNumber = Double.parseDouble(editInput1.getText().toString());
                            if(chosenNumber<0) {
                                Toast.makeText(getApplicationContext(),"Entered unit value cannot be in negative.",Toast.LENGTH_SHORT).show();
                                break;
                            }
                            double convertedNumber = chosenNumber * 0.453592;
                            editInput2.setText("");
                            editInput2.setText(String.format("%.2f", convertedNumber));
                        }
                        else if(!editInput2.getText().toString().equals("")) {
                            double chosenNumber = Double.parseDouble(editInput2.getText().toString());
                            if(chosenNumber<0) {
                                Toast.makeText(getApplicationContext(),"Entered unit value cannot be in negative.",Toast.LENGTH_SHORT).show();
                                break;
                            }
                            double convertedNumber = chosenNumber / 0.453592;
                            editInput1.setText("");
                            editInput1.setText(String.format("%.2f", convertedNumber));
                        }
                        break;
                }
            }
        });

        clearValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editInput1.getText().clear();
                editInput2.getText().clear();
            }
        });

    }


}
