package com.shipexpress.shipexpress.Ship;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shipexpress.shipexpress.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpShipFragment extends Fragment {


    public HelpShipFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ship_help, container, false);
    }

}
