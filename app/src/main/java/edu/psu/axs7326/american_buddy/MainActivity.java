package edu.psu.axs7326.american_buddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.LocaleManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE=100;
    private RelativeLayout homeRL;
    private TextView current_temp,feels_like,current_time,current_date,current_location,current_condition;
    private ImageView imageView ;
    private EditText city_name;
    private ImageView condition_image;
    private LocationManager location_manager;
    private FloatingActionButton search_button;

    private Button american_slang_button,converter_button;
    private int PERMISSION_CODE = 1;
    private String current_city_name;

    private SharedPref sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = new SharedPref(this);

        //load theme preference
        if (sharedPreferences.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);


        current_temp=findViewById(R.id.idTVCurrent_Temp);
        feels_like=findViewById(R.id.idTVFeels_like);
        current_time=findViewById(R.id.idTVTime);
        current_date=findViewById(R.id.idTVDate);
        current_location=findViewById(R.id.idTVLocation);
        current_condition=findViewById(R.id.idTVCondition);
        city_name=findViewById(R.id.enter_city);
        condition_image=findViewById(R.id.idRLCondition_Image);
        search_button=findViewById(R.id.search_button);
        american_slang_button=findViewById(R.id.american_slang);
        converter_button=findViewById(R.id.converter);
        imageView = findViewById(R.id.idRLCondition_Image);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        displayWeather();
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city= city_name.getText().toString();
//                String[] arrOfStr = city.split(",", 2);
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter city Name",Toast.LENGTH_SHORT).show();
                }else{
//                    if(arrOfStr[1].isEmpty())
                        getWeatherInfo(city,"","");
//                    else
//                    getWeatherInfo(arrOfStr[0],arrOfStr[1],"");
                }
            }
        });

        american_slang_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SlangActivity.class);
                startActivity(intent);
            }
        });

        converter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ConverterActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        setTitle("American Buddy");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }


    private void displayWeather() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                getWeatherInfo(addresses.get(0).getLocality(), addresses.get(0).getAdminArea(), addresses.get(0).getCountryCode());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
        }else
        {
            askPermission();
        }
    }
    private void askPermission(){
        ActivityCompat.requestPermissions(MainActivity.this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                displayWeather();
            }
            else {
                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getWeatherInfo(String cityName,String state,String country){

        String url="http://api.weatherapi.com/v1/forecast.json?key=4b561d8dab8c4fc7bce205943233103&q="+cityName+","+state+"&days=1&aqi=no&alerts=no";
        if(state.isEmpty()&&country.isEmpty()) {
            url = "http://api.weatherapi.com/v1/forecast.json?key=4b561d8dab8c4fc7bce205943233103&q=" + cityName + "&days=1&aqi=no&alerts=no";
            Log.d("TAG","TRY KARTE RAHO"+url);
        }

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
//          Log.d("TAG","TRY KARTE RAHO");
            try {
                String temp_c= response.getJSONObject("current").getString("temp_c");
                String temp_f= response.getJSONObject("current").getString("temp_f");
                String feelslike_c= response.getJSONObject("current").getString("feelslike_c");
                String feelslike_f= response.getJSONObject("current").getString("feelslike_f");
                String condition=response.getJSONObject("current").getJSONObject("condition").getString("text");
                String api_city=response.getJSONObject("location").getString("name");
                String api_country=response.getJSONObject("location").getString("country");
                String api_state=response.getJSONObject("location").getString("region");
                if(api_state.isEmpty())
                    current_location.setText(api_city+","+api_country);
                else
                    current_location.setText(api_city+","+api_state+","+api_country);

                current_condition.setText(condition);


                if(sharedPreferences.loadDisplay()){
                    current_temp.setText("Temp: "+temp_f+"°F/"+temp_c+"°C");
                    feels_like.setText("Feels like: "+feelslike_f+"°F/"+feelslike_c+"°C");
                } else {
                    current_temp.setText("Temp: "+temp_c+"°C/"+temp_f+"°F");
                    feels_like.setText("Feels like: "+feelslike_c+"°C/"+feelslike_f+"°F");
                }

//                String pattern = "E, dd MMMM yyyy";
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//                String date = simpleDateFormat.format(new Date());
//                current_date.setText(date);

                String timezoneId = response.getJSONObject("location").getString("tz_id");
                TimeZone timeZone = TimeZone.getTimeZone(timezoneId);
                String datePattern = "E, dd MMMM yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
                dateFormat.setTimeZone(timeZone);
                String date = dateFormat.format(new Date());
                current_date.setText(date);

                DateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
                timeFormat.setTimeZone(timeZone);
                String timeString = timeFormat.format(new Date()).toString();
                current_time.setText(timeString);

                Calendar calendar = Calendar.getInstance(timeZone);
                calendar.setTime(new Date());

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if (hour >= 18 || hour < 6) {
                    Log.d("TAG", Integer.toString(hour));
                    imageView.setImageResource(R.drawable.night);
                } else {
                    imageView.setImageResource(R.drawable.sunny);
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, error -> Toast.makeText(MainActivity.this,"Please enter valid city name..",Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jor);
    }
}