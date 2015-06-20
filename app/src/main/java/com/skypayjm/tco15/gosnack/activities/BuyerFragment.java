package com.skypayjm.tco15.gosnack.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skypayjm.tco15.gosnack.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuyerFragment extends Fragment {


    public BuyerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buyer, container, false);
    }


}
