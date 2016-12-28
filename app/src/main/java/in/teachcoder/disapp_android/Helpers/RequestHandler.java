package in.teachcoder.disapp_android.Helpers;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import in.teachcoder.disapp_android.Activities.MainActivity;
import in.teachcoder.disapp_android.Activities.ResultActivity;

/*
 * Created by Arnav on 26-Dec-16.
 */

public class RequestHandler extends AsyncTask<String, Void, String> {
    Activity activity;
    Boolean change;


    public RequestHandler(Activity activity, Boolean change) {
        this.activity = activity;
        this.change = change;
    }

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
        if (change) {
            Intent resultIntent = new Intent(activity, ResultActivity.class);
            activity.startActivity(resultIntent);
        } else {

            Intent resultIntent = new Intent(activity, MainActivity.class);
            activity.startActivity(resultIntent);
        }
    }
}
