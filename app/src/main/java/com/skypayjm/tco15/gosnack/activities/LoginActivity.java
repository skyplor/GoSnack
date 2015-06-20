package com.skypayjm.tco15.gosnack.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.skypayjm.tco15.gosnack.R;

import net.grandcentrix.tray.TrayAppPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();
    Firebase firebaseRef;
    private EditText email, password;
    private Button create_login;

    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;

    /* Data from the authenticated user */
    private AuthData mAuthData;
    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;

    /* *************************************
    *              TWITTER                *
    ***************************************/
    public static final int RC_TWITTER_LOGIN = 2;

    private Button mTwitterLoginButton;

    /* *************************************
     *              FACEBOOK               *
     ***************************************/
    /* The login button for Facebook */
    private LoginButton mFacebookLoginButton;
    /* The callback manager for Facebook */
    private CallbackManager mFacebookCallbackManager;
    /* Used to track user logging in/out off Facebook */
    private AccessTokenTracker mFacebookAccessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Firebase.setAndroidContext(this);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        create_login = (Button) findViewById(R.id.create_login);

        create_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                if (emailValid(emailString) && passwordValid(passwordString)) {
                    loginNormal(emailString, passwordString);
                }
            }
        });

        /* *************************************
         *                TWITTER              *
         ***************************************/
        mTwitterLoginButton = (Button) findViewById(R.id.tw_login_button);
        mTwitterLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithTwitter();
            }
        });

        /* Load the Facebook login button and set up the tracker to monitor access token changes */
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
//        mFacebookLoginButton.setBackgroundResource(R.drawable.)
        mFacebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.i(TAG, "Facebook.AccessTokenTracker.OnCurrentAccessTokenChanged");
                LoginActivity.this.onFacebookAccessTokenChange(currentAccessToken);
            }
        };
        /* *************************************
         *               GENERAL               *
         ***************************************/

        /* Create the Firebase ref that is used for all authentication with Firebase */
        firebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();

        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                mAuthProgressDialog.dismiss();
                setAuthenticatedUser(authData);
            }
        };
        /* Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
         * user and hide hide any login buttons */
        firebaseRef.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // if user logged in with Facebook, stop tracking their token
        if (mFacebookAccessTokenTracker != null) {
            mFacebookAccessTokenTracker.stopTracking();
        }

        // if changing configurations, stop tracking firebase session.
        firebaseRef.removeAuthStateListener(mAuthStateListener);
    }

    private boolean emailValid(String text) {
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
//        return true;
    }

    private boolean passwordValid(String text) {
        if (text.length() == 0) return false;
        return true;
    }

    private void loginNormal(final String userEmail, final String userPassword) {
        mAuthProgressDialog.show();
        firebaseRef.authWithPassword(userEmail, userPassword, new AuthResultHandler("password", userEmail, userPassword));
    }

    /**
     * This method will attempt to authenticate a user to firebase given an oauth_token (and other
     * necessary parameters depending on the provider)
     */
    private void authWithFirebase(final String provider, Map<String, String> options) {
        if (options.containsKey("error")) {
            showErrorDialog(options.get("error"));
        } else {
            mAuthProgressDialog.show();
            if (provider.equals("twitter")) {
                // if the provider is twitter, we pust pass in additional options, so use the options endpoint
                firebaseRef.authWithOAuthToken(provider, options, new AuthResultHandler(provider));
            } else {
                // if the provider is not twitter, we just need to pass in the oauth_token
                firebaseRef.authWithOAuthToken(provider, options.get("oauth_token"), new AuthResultHandler(provider));
            }
        }
    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Map<String, String> options = new HashMap<String, String>();
        if (requestCode == RC_TWITTER_LOGIN) {
            options.put("oauth_token", data.getStringExtra("oauth_token"));
            options.put("oauth_token_secret", data.getStringExtra("oauth_token_secret"));
            options.put("user_id", data.getStringExtra("user_id"));
            authWithFirebase("twitter", options);
        } else {
            /* Otherwise, it's probably the request by the Facebook login button, keep track of the session */
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void createUser(final String userEmail, final String userPassword) {
        firebaseRef.createUser(userEmail, userPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                loginNormal(userEmail, userPassword);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // there was an error
            }
        });
    }

    /**
     * Utility class for authentication results
     */
    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;
        private String userEmail, userPassword;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        public AuthResultHandler(String provider, String userEmail, String userPassword) {
            this.provider = provider;
            this.userEmail = userEmail;
            this.userPassword = userPassword;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.dismiss();
            Log.i(TAG, provider + " auth successful");
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.dismiss();
            if (provider.equals("password")) {

                if (firebaseError.getCode() == FirebaseError.USER_DOES_NOT_EXIST) {
                    createUser(userEmail, userPassword);
                }
            } else
                showErrorDialog(firebaseError.toString());
        }
    }

    /* ************************************
     *             FACEBOOK               *
     **************************************
     */
    private void onFacebookAccessTokenChange(AccessToken token) {
        if (token != null) {
            mAuthProgressDialog.show();
            firebaseRef.authWithOAuthToken("facebook", token.getToken(), new AuthResultHandler("facebook"));
        } else {
            // Logged out of Facebook and currently authenticated with Firebase using Facebook, so do a logout
            if (this.mAuthData != null && this.mAuthData.getProvider().equals("facebook")) {
                firebaseRef.unauth();
                setAuthenticatedUser(null);
            }
        }
    }

    /* ************************************
     *               TWITTER              *
     **************************************
     */
    private void loginWithTwitter() {
        startActivityForResult(new Intent(this, TwitterOAuthActivity.class), RC_TWITTER_LOGIN);
    }

    private void goToNextActivity() {
        final TrayAppPreferences appPreferences = new TrayAppPreferences(this); //this Preference comes for free from the library
        final String type = appPreferences.getString("userType", "");
        if (type.isEmpty()) {
            Intent buysellIntent = new Intent(LoginActivity.this, SelectUserTypeActivity.class);
            startActivity(buysellIntent);
            this.finish();
        } else {
            Intent snacksIntent = new Intent(LoginActivity.this, SnacksActivity.class);
            startActivity(snacksIntent);
            this.finish();
        }
    }

    /**
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.
     */
    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            goToNextActivity();
        } else {
            Snackbar.make((View) findViewById(R.id.snackBar), "User is not authenticated", Snackbar.LENGTH_LONG).show();
        }
        this.mAuthData = authData;
        /* invalidate options menu to hide/show the logout button */
//        supportInvalidateOptionsMenu();
    }
}
