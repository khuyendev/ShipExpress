package com.shipexpress.shipexpress.Ship;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InforShipFragment extends Fragment {

    DatabaseReference reference;
    TextView nameShop, mailShop, txtAddress, txtPhone, txtDetail;
    public InforShipFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = FirebaseDatabase.getInstance().getReference().child(var.CHILD_SHIP).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(var.CHILD_INFO);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ship_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameShop = (TextView) view.findViewById(R.id.nameShop);
        mailShop = (TextView) view.findViewById(R.id.mailShop);
        txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        txtPhone = (TextView) view.findViewById(R.id.txtPhone);
        txtDetail = (TextView) view.findViewById(R.id.txtDetail);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Shipper shop = dataSnapshot.getValue(Shipper.class);
                nameShop.setText(shop.getNameShipper());
                mailShop.setText(shop.getEmailShipper());
                txtAddress.setText(shop.getAddressShipper());
                txtPhone.setText(shop.getPhoneNumber());
                txtDetail.setText(shop.getDetailShipper());
                //txtLocation.setText(shop.getAddressShop());
                reference.removeEventListener(this);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
