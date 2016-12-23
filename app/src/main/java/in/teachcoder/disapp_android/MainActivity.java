package in.teachcoder.disapp_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.androidadvance.androidsurvey.SurveyActivity;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int SURVEY_REQUEST = 1338;
    String useremail, contact;
    double userLatitude , userLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start_survey = (Button) findViewById(R.id.start_survey_btn);
        AppCompatCheckBox agreeTerms = (AppCompatCheckBox) findViewById(R.id.start_survey_terms);
        getLocation();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        contact = sharedPreferences.getString(Constants.USER_CONTACT, " ");
        useremail = sharedPreferences.getString(Constants.USER_EMAIL, " ");
        agreeTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Intent i_survey = new Intent(MainActivity.this, SurveyActivity.class);
                    i_survey.putExtra("json_survey", loadSurveyJson("example_survey_1.json"));
                    startActivityForResult(i_survey, SURVEY_REQUEST);
                }
            }
        });

        start_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_survey = new Intent(MainActivity.this, SurveyActivity.class);
                i_survey.putExtra("json_survey", loadSurveyJson("example_survey_1.json"));
                startActivityForResult(i_survey, SURVEY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SURVEY_REQUEST) {
            if (resultCode == RESULT_OK) {
                String answers_json = data.getExtras().getString("answers");
                JSONObject postData = new JSONObject();
                JSONArray postArray = new JSONArray();
                try {
                    JSONObject answerObject = new JSONObject(answers_json);
                    postArray.put(answerObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    postData.put("latitude", userLatitude);
                    postData.put("longitude", userLongitude);
                    postData.put("useremail", useremail);
                    postData.put("contact", contact);
                    postArray.put(postData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                postArray.put(answers_json);

                new SendSurveyResponse().execute("http://139.59.34.32/survey/androidresponse", postArray.toString());
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor spe = sp.edit();
                spe.putString(Constants.USER_RESPONSE, answers_json);
                spe.apply();

            }
        }
    }

    private String loadSurveyJson(String filename) {
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void getLocation() {
        GPS_Tracker gps = new GPS_Tracker(MainActivity.this);

        // Check if GPS enabled
        if (gps.canGetLocation()) {

            userLatitude = gps.getLatitude();
            userLongitude = gps.getLongitude();
            Log.d("Location", "lat" + (long) userLatitude);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor spe = sp.edit();
            spe.putLong(Constants.USER_LONGITUDE, (long) userLongitude);
            spe.putLong(Constants.USER_LATITUDE, (long) userLongitude);
            spe.apply();
            // \n is for new line
//            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }
    }

    private class SendSurveyResponse extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(params[1]);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
            Intent resultIntent = new Intent(MainActivity.this, ResultActivity.class);
            startActivity(resultIntent);
        }
    }
}
