package com.shipexpress.shipexpress.DialogView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.RegisterActivity;

/**
 * Created by QuangCoi on 10/28/2016.
 */

public class ChooseTypeRegister extends DialogFragment {
    Intent intent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public ChooseTypeRegister() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_type_register_dialog, container);

        Button buttonShop = (Button)view.findViewById(R.id.btnShop);
        Button buttonShip = (Button)view.findViewById(R.id.btnShip);

        buttonShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getContext(), RegisterActivity.class);
                intent.putExtra(var.CHILD_USER, var.TYPE_SHOP);
                startActivity(intent);
                dismiss();
            }
        });
        buttonShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), RegisterActivity.class);
                intent.putExtra(var.CHILD_USER, var.TYPE_SHIP);
                startActivity(intent);
                dismiss();
            }
        });
        //    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
        //          .findFragmentById(R.id.maps);
        //  mapFragment.getMapAsync(this);
        getDialog().setTitle("Loại người dùng");

        return view;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

    }
}
