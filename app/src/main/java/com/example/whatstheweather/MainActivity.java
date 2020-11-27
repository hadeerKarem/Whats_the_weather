package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editTextCity;
    TextView textViewWeather;
    boolean error = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = (EditText) findViewById(R.id.editTextCity);
        textViewWeather = (TextView) findViewById(R.id.textViewWeather);
    }

    public void buttonClicked(View view) {
        try {
            //calling thread task
            DownloadTask task = new DownloadTask();

            String encodedCityName = URLEncoder.encode(editTextCity.getText().toString(), "UTF-8");

        task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName
                + "&appid=6a1c976ef29be53a9ba3d754874740ec");

        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(editTextCity.getWindowToken(), 0);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could Not found Weather :(", Toast.LENGTH_LONG).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream =urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                error = true;
 //                Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (error) {
                textViewWeather.setText("");
                Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
                error = false;
            }
            else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String weatherInfo = jsonObject.getString("weather");
                    Log.i("weather content", weatherInfo);

                    JSONArray jsonArray = new JSONArray(weatherInfo);
                    String message = "";

                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject jsonObjectPart =jsonArray.getJSONObject(i);

                        String main = jsonObjectPart.getString("main");
                        String description = jsonObjectPart.getString("description");

                        if (!main.equals("") && !description.equals("")){
                            message += main + ": " + description + "\r\n";
                        }
                    }

                    if (!message.equals("")){
                        textViewWeather.setText(message);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Could Not found Weather :(", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Could Not found Weather :(", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}