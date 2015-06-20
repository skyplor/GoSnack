package com.skypayjm.tco15.gosnack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.skypayjm.tco15.gosnack.R;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Firebase firebaseRef;
    private EditText email, password;
    private Button create_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase("https://gosnack.firebaseio.com");
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        create_login = (Button) findViewById(R.id.create_login);

        create_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                if (emailValid(emailString) && passwordValid(passwordString)) {
                    login(emailString, passwordString);
                }
            }
        });
    }

    private boolean emailValid(String text) {
//        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
//        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
//        Matcher matcher = pattern.matcher(text);
//        return matcher.matches();
        return true;
    }

    private boolean passwordValid(String text) {
        if (text.length() == 0) return false;
        return true;
    }

    private void login(final String userEmail, final String userPassword) {
        firebaseRef.authWithPassword(userEmail, userPassword, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                goToNextActivity();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
                if (firebaseError.getCode() == FirebaseError.USER_DOES_NOT_EXIST) {
                    createUser(userEmail, userPassword);
                }
            }
        });
    }

    private void createUser(final String userEmail, final String userPassword) {
        firebaseRef.createUser(userEmail, userPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                login(userEmail, userPassword);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // there was an error
            }
        });
    }

    private void goToNextActivity() {
        Intent searchIntent = new Intent(LoginActivity.this, SnacksActivity.class);
        startActivity(searchIntent);
        this.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
