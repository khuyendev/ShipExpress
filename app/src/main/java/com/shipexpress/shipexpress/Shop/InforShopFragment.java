package com.shipexpress.shipexpress.Shop;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
public class InforShopFragment extends Fragment implements OnMapReadyCallback {

    DatabaseReference reference;
    TextView nameShop, mailShop, txtAddress, txtPhone, txtDetail;
    GoogleMap map;
    public InforShopFragment() {
        // Required empty public constructor
    }

    public static InforShopFragment newInstance() {
        Bundle args = new Bundle();
        InforShopFragment fragment = new InforShopFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = FirebaseDatabase.getInstance().getReference().child(var.CHILD_SHOP).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(var.CHILD_INFO);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_shop_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapinffo);
        mapFragment.getMapAsync(this);
        nameShop = (TextView) view.findViewById(R.id.nameShop);
        mailShop = (TextView) view.findViewById(R.id.mailShop);
        txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        txtPhone = (TextView) view.findViewById(R.id.txtPhone);
        txtDetail = (TextView) view.findViewById(R.id.txtDetail);
       // txtLocation = (TextView) view.findViewById(R.id.txtLocation);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setAllGesturesEnabled(false);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Shop shop = dataSnapshot.getValue(Shop.class);
                nameShop.setText(shop.getNameShop());
                mailShop.setText(shop.getEmailShop());
                txtAddress.setText(shop.getAddressShop());
                txtPhone.setText(shop.getPhoneNumber());
                txtDetail.setText(shop.getDetailShop());
                //txtLocation.setText(shop.getAddressShop());
                Marker k =map.addMarker(new MarkerOptions().position(new LatLng(shop.getsLocation().getLat(), shop.getsLocation().getLng())).title(shop.getNameShop()).icon(BitmapDescriptorFactory.fromResource(R.drawable.home)));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(shop.getsLocation().getLat(), shop.getsLocation().getLng()), 14f));
                k.showInfoWindow();
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
