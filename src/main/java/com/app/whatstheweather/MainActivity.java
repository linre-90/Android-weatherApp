package com.app.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    Button searchBtn;
    TextView city;
    TextView result;
    final String urlStart = "https://SOME_API_ADDRES";
    final String urlEnd = "API_KEY_GOES_HERE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchBtn = findViewById(R.id.button);
        city = findViewById(R.id.city);
        result = findViewById(R.id.textView_weather);
        new downloadWeather().execute(urlStart+city.getText().toString()+urlEnd);
    }
    private class downloadWeather extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection;
            String result = "";
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while (data !=-1){
                    char current = (char)data;
                    result += current;
                    data = reader.read();
                }
                reader.close();
                inputStream.close();
                connection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return "not";
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!s.equals("not")) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String weatherInfo = jsonObject.getString("weather");
                    JSONObject weatherInfoTemperature = jsonObject.getJSONObject("main");
                    JSONArray array = new JSONArray(weatherInfo);
                    JSONObject jsonPart = array.getJSONObject(0);
                    double temperature = Double.parseDouble(weatherInfoTemperature.getString("temp"));
                    result.setText("Weather in " + city.getText().toString() + " is " + jsonPart.getString("main")
                    + " ("+jsonPart.getString("description")+")" + ". Temperature: " + String.format("%.1f",temperature - 273.15));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(MainActivity.this, "Invalid city!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void onClick(View v) {
        new downloadWeather().execute(urlStart+city.getText().toString()+urlEnd);
        // closes keyboard
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            mgr.hideSoftInputFromWindow(city.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
