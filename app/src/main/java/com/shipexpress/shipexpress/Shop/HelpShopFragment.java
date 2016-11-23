package com.shipexpress.shipexpress.Shop;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shipexpress.shipexpress.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpShopFragment extends Fragment {


    public HelpShopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_help, container, false);
    }

}
