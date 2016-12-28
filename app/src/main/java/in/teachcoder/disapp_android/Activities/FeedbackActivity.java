package in.teachcoder.disapp_android.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.teachcoder.disapp_android.Helpers.Constants;
import in.teachcoder.disapp_android.Helpers.RequestHandler;
import in.teachcoder.disapp_android.R;

public class FeedbackActivity extends AppCompatActivity {
    RatingBar userRating;
    EditText feedback;
    Button submit;
    RequestHandler handler;

    String useremail, contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        userRating = (RatingBar) findViewById(R.id.rating);
        feedback = (EditText) findViewById(R.id.input_feedback);
        submit = (Button) findViewById(R.id.submit_feedback);


        handler = new RequestHandler(FeedbackActivity.this, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        contact = sharedPreferences.getString(Constants.USER_CONTACT, " ");
        useremail = sharedPreferences.getString(Constants.USER_EMAIL, " ");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject postData = new JSONObject();
                JSONArray postArray = new JSONArray();
                try {
                    postData.put("useremail", useremail);
                    postData.put("contact", contact);
                    postData.put("rating", userRating.getRating());
                    postData.put("feedback", feedback.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("FeedbackData", postData.toString());
//                postArray.put(postData);

                handler.execute(Constants.FEEDBACK_URL, postData.toString());
            }
        });


    }
}
