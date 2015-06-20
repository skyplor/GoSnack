package com.skypayjm.tco15.gosnack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.skypayjm.tco15.gosnack.R;
import com.skypayjm.tco15.gosnack.adapters.SnacksAdapter;
import com.skypayjm.tco15.gosnack.models.Snack;

import java.util.ArrayList;
import java.util.List;


public class SnacksActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    private SnacksAdapter mAdapter;
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snack);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Find RecyclerView and bind to adapter
        final RecyclerView rvSnacks = (RecyclerView) findViewById(R.id.rvSnacks);

        // allows for optimizations
        rvSnacks.setHasFixedSize(true);

        // Define 2 column grid layout
        final GridLayoutManager layout = new GridLayoutManager(SnacksActivity.this, 2);

        // Unlike ListView, you have to explicitly give a LayoutManager to the RecyclerView to position items on the screen.
        // There are three LayoutManager provided at the moment: GridLayoutManager, StaggeredGridLayoutManager and LinearLayoutManager.
        rvSnacks.setLayoutManager(layout);

        // get data
        List<Snack> snacks = getContacts();

        // Create an adapter
        mAdapter = new SnacksAdapter(SnacksActivity.this, snacks);

        // Bind adapter to list
        rvSnacks.setAdapter(mAdapter);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.snack_drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
    }

    private List<Snack> getContacts() {
        List<Snack> snacks = new ArrayList<>();
        snacks.add(new Snack(1, "Ben's Chili Bowl Half Smoke", R.drawable.benschilihalfsmoke, "$6.75"));
        snacks.add(new Snack(2, "Cheesy Corn Brisket-Acho", R.drawable.brisketacho, "$8.00"));
        snacks.add(new Snack(3, "Chicken and Waffle Cone", R.drawable.chickenwafflecone, "$7.50"));
        snacks.add(new Snack(4, "The Choomongous", R.drawable.choomongous, "$7.25"));
        snacks.add(new Snack(5, "Chickie's and Pete's Crabfries", R.drawable.crabfries, "$6.00"));
        snacks.add(new Snack(6, "Gilroy Garlic Fries", R.drawable.gilroygarlic, "$6.00"));
        snacks.add(new Snack(7, "Halo Dog", R.drawable.halo_dog, "$5.20"));
        snacks.add(new Snack(8, "Marlin's Ceviche", R.drawable.marlinsceviche, "$10.00"));
        snacks.add(new Snack(9, "Mr. Red's Smokehouse Pulled Pork", R.drawable.smokehousepulledpork, "$8.25"));
        snacks.add(new Snack(10, "Bacon on a stick", R.drawable.baconstick, "$5.50"));
        snacks.add(new Snack(11, "Nacho Helmet", R.drawable.dodgernachohelmet2, "$15.25"));
        snacks.add(new Snack(12, "Rocky Mountain Oysters", R.drawable.rockymountainoysters, "$12.55"));
        return snacks;
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Intent intent = null;
        switch (position) {
            case 0:
                break;
            case 1:
                intent = new Intent(this, BuyerSellerActivity.class);
                startActivity(intent);
                this.finish();
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
