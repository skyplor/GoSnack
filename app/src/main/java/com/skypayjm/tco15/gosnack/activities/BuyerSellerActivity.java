package com.skypayjm.tco15.gosnack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.skypayjm.tco15.gosnack.R;

import net.grandcentrix.tray.TrayAppPreferences;

public class BuyerSellerActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {


    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_seller);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.user_type_drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        displayFragment();
    }

    private void displayFragment() {

        final TrayAppPreferences appPreferences = new TrayAppPreferences(this); //this Preference comes for free from the library
        final String type = appPreferences.getString("userType", "buyer");
        Fragment fragment = null;
        String title = getString(R.string.title_activity_buyer_seller);
        if (type.equals("buyer")) {
            //display buyer fragment
            fragment = new BuyerFragment();
            title = getString(R.string.title_fragment_buyer);
        } else if (type.equals("seller")) {
            //display seller fragment
            fragment = new SellerFragment();
            title = getString(R.string.title_fragment_seller);

        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);

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
                break;
            case 2:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                this.finish();
                break;
            default:
                break;
        }
    }
}
