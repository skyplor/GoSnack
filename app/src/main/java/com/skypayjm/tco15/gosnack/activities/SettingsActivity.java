package com.skypayjm.tco15.gosnack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;

import com.facebook.login.LoginManager;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.rey.material.widget.Button;
import com.rey.material.widget.RadioButton;
import com.skypayjm.tco15.gosnack.R;

import net.grandcentrix.tray.TrayAppPreferences;

public class SettingsActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private Button logout;
    private AuthData mAuthData;
    private Firebase firebaseRef;
    private RadioButton buyerSettings, sellerSettings;
    private TrayAppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        appPreferences = new TrayAppPreferences(this);
        mAuthData = firebaseRef.getAuth();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        logout = (Button) findViewById(R.id.logoutBtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogout();
            }
        });

        buyerSettings = (RadioButton) findViewById(R.id.buyerSettings);
        sellerSettings = (RadioButton) findViewById(R.id.sellerSettings);

        setDefaults();
        buyerSettings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sellerSettings.setChecked(false);
                    savePreference();
                }
            }
        });
        sellerSettings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buyerSettings.setChecked(false);
                    savePreference();
                }
            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.settings_drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
    }

    private void setDefaults() {
        String userType = appPreferences.getString("userType", "");
        if (userType.equals("buyer")) buyerSettings.setChecked(true);
        else if (userType.equals("seller")) sellerSettings.setChecked(true);
    }

    private void savePreference() {
        // save a key value pair
        if (buyerSettings.isChecked())
            appPreferences.put("userType", "buyer");
        else
            appPreferences.put("userType", "seller");
    }

    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    private void doLogout() {
        if (this.mAuthData != null) {
            /* logout of Firebase */
            firebaseRef.unauth();
            appPreferences.put("userType", "");
            /* Logout of any of the Frameworks. This step is optional, but ensures the user is not logged into
             * Facebook after logging out of Firebase. */
            if (this.mAuthData.getProvider().equals("facebook")) {
                /* Logout from Facebook */
                LoginManager.getInstance().logOut();
            }
        }

        if (firebaseRef.getAuth() == null) {
            Intent loginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            this.finish();
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Intent intent = null;
        switch (position) {
            case 0:
                intent = new Intent(this, SnacksActivity.class);
                startActivity(intent);
                this.finish();
                break;
            case 1:
                intent = new Intent(this, BuyerSellerActivity.class);
                startActivity(intent);
                this.finish();
                break;
            case 2:
                break;
            default:
                break;
        }
    }
}
