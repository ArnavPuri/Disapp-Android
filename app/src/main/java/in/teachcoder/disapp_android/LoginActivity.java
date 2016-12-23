package in.teachcoder.disapp_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    EditText userEmail, userContact;
    Button submit;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String existingEmail = sharedPreferences.getString(Constants.USER_EMAIL, "");
        Log.d("Login", existingEmail);
        if(!existingEmail.equals("")){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        submit = (Button) findViewById(R.id.login_button);
        userEmail = (EditText) findViewById(R.id.email);
        userContact = (EditText) findViewById(R.id.contact_no);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor spe = sp.edit();
                spe.putString(Constants.USER_EMAIL, userEmail.getText().toString());
                spe.putString(Constants.USER_CONTACT, userContact.getText().toString());
                spe.apply();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
