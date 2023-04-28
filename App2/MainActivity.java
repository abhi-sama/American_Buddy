package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.gtappdevelopers.weatherapp.WeatherRVAdapter;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, temperatureTV, conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV;
    private ArrayListM<WeatherRVModal> weatherRVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter;

    private LocationManager locationManager;
    private int PERMISSION_CODE = 1 ;

    private String cityName;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);


        locationManger = (LocationManager) getSystemService(Context.LOCATION_SERIVCE);
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
          ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(),location.getLatitude());
        getWeatherInfo(cityName);

        searchIV.setOnClickListener(new View.OnClickListener(){
            @Override
               public void onClick(View v) {
               String city = cityEdt.getText().toString();
               if(city.isEmpty()) {
                   Toast.makeText(MainActivity.this, "Please enter city NAME", Toast.LENGTH_SHORT).show();
               }else {
                   cityNameTV.setText(cityName);
                   getWeatherInfo(city);
               }
            }
        });

    }

    @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
          if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
              Toast.makeText(this,"Permissions granted..", Toast.LENGTH_SHORT).show();
          }else {
            Toast.makeText(this, "Please provide the permission", Toast.LENGTH_SHORT).show();
            finish();
          }
        }
    }

        private String getCityName(double longitube, double latitude){
            String cityName = "Not found";
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                List<Address> addresses = gcd.getFromLocation(latitude, longitube, 10);

                for(Address adr : addresses){
                    if(adr!= null){
                        String city = adr.getLocality();
                        if(city!=null && !city.equals("")){
                            cityName = city;
                        }else {
                            Log.d("TGA", "CITY NOT FOUND");
                            Toast.makeText(this,"User City Not Found..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            return cityName;
        }

        private void getWeatherInfo(String cityName) {
            String url = "http://api.weatherapi.com/v1/forecast.json?key=e6a8abccdce4449a9dc180759230204&q=" + cityName + " &days=1&aqi=yes&alerts=yes";
            cityNameTV.setText(cityName);
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

            JsonObjectRequest jsonObjectRequest = new JsonOnjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>())
            @Override
            public void onResponse(JSONObject response){
            loadingPB.setVisibility(View.GONE);
            homeRL.setVisibility(View.VISIBLE);
            weatherRVModalArrayList.clear();

            try{
                String temperature=response.getJSONObject("currect").getString("temp_c")
                temperatureTV.setText(temperature+"°c");
                int isDay = response.getJSONObject("currect").getInt("is_day");
                String condition = response.getJSONObject("currect").getJSONObject("condition").getString("text");
                String conditionIcon = response.getJSONObject("currect").getJSONObject("condition").getString("icon");
                Picasson.get().load("http:".concat(conditionIcon)).into(iconIV);
                conditionTV.setText(condition);
                if(isDay==1){
                    //morning image URL need
                    picasso.get().load("").into(backIV);
                }else {
                    //night image URL need
                    picasso.get().load("").into(backIV);
                }
                JSONObject forecastObj = response.getJSONObject("forecast");
                JSONObject forecastO = forecaseObj.getJSONArray("forecastday").getJSONObject(0);
                JSONObject hourArray = forecaseObj.getJSONArray("hour");

                for(int i=0; i<hourArry.length(); i++){
                    JSONOject hourObj = hourArry.getJSONObject(i);
                    String time = hourObj.getString("time");
                    String temper = hourObj.getString("temp_c");
                    String img = hourObj.getJSONOject("condition").getString("icon");
                    String wind = hourObj.getString("wind_kph");
                    weatherRVModalArrayList.add(new WeatherRVModal(time,temp,img,wind));
                }
                weatherRVAdapter.notifyDataSetChanged();


            } catch(JSONException e){
                e.printStackTrace();
              }

            }
        }, new Response.ErrorListener(){
           @Override
           public void onErrorResponse(VolleyError error){
               Toast.makeText(MainActivity.this,"Please enter valid city name",Toast.LENGTH_SHORT).show();
           }
        });

        requestQueue.add(jsonObjectRequest);
    }
}