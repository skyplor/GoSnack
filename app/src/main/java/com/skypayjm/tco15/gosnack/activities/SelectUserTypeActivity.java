package com.skypayjm.tco15.gosnack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;

import com.rey.material.widget.Button;
import com.rey.material.widget.RadioButton;
import com.skypayjm.tco15.gosnack.R;

import net.grandcentrix.tray.TrayAppPreferences;

public class SelectUserTypeActivity extends AppCompatActivity {
    private RadioButton buyer, seller;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);

        buyer = (RadioButton) findViewById(R.id.buyer);
        seller = (RadioButton) findViewById(R.id.seller);
        next = (Button) findViewById(R.id.nextBuyerSeller);

        buyer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) seller.setChecked(false);
            }
        });
        seller.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) buyer.setChecked(false);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefSelected()) {
                    savePreference();
                    goToNextActivity();
                }
            }
        });
    }

    private boolean prefSelected() {
        return buyer.isChecked() || seller.isChecked();
    }

    private void savePreference() {
        // create a preference accessor. This is for global app preferences.
        final TrayAppPreferences appPreferences = new TrayAppPreferences(this); //this Preference comes for free from the library
        // save a key value pair
        if (buyer.isChecked())
            appPreferences.put("userType", "buyer");
        else
            appPreferences.put("userType", "seller");
    }

    private void goToNextActivity() {
        Intent snacksIntent = new Intent(SelectUserTypeActivity.this, SnacksActivity.class);
        startActivity(snacksIntent);
        this.finish();
    }
}
