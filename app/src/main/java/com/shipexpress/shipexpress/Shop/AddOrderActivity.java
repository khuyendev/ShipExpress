package com.shipexpress.shipexpress.Shop;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.Order.toAddress;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Utility.MapScrollViewSupport;
import com.shipexpress.shipexpress.Utility.sLocation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddOrderActivity extends AppCompatActivity implements OnMapReadyCallback {
    Toolbar toolbar;
    private ScrollView view;
    private DatabaseReference mData;
    private GoogleMap mMap;
    private FirebaseUser user;
    private Marker marker;
    List<Address> addresses;
    String address;
    Geocoder geocoder;
    private EditText editNameorder, editPriceOrder, editfreight, editNameReceived, editPhoneReceived, editAddressReceived, editNote;
    private Button btnAddOrder;
    private LatLng positionShop;
    private ProgressDialog dialog;
    private Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        initView();
        user = FirebaseAuth.getInstance().getCurrentUser();
        MapScrollViewSupport mapFragment = (MapScrollViewSupport) getSupportFragmentManager().findFragmentById(R.id.GmapRegisterShop);
        ((MapScrollViewSupport) getSupportFragmentManager().findFragmentById(R.id.GmapRegisterShop)).setListener(new MapScrollViewSupport.OnTouchListener() {
            @Override
            public void onTouch() {
                view.requestDisallowInterceptTouchEvent(true);
            }
        });
        mapFragment.getMapAsync(this);
        mData = FirebaseDatabase.getInstance().getReference();
        dialog = ProgressDialog.show(this, "", "Đang xử lý...");
        dialog.show();
        getLocationShop();
    }

    private void initView() {
        view = (ScrollView) findViewById(R.id.view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        editNameorder = (EditText) findViewById(R.id.editNameorder);
        editPriceOrder = (EditText) findViewById(R.id.editPriceOrder);
        editfreight = (EditText) findViewById(R.id.editfreight);
        editNameReceived = (EditText) findViewById(R.id.editNameReceived);
        editPhoneReceived = (EditText) findViewById(R.id.editPhoneReceived);
        editAddressReceived = (EditText) findViewById(R.id.editAddressReceived);
        btnAddOrder = (Button) findViewById(R.id.btnAddOrder);
        editNote = (EditText) findViewById(R.id.editNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        btnAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(AddOrderActivity.this, "", "Đang đăng đơn hàng...");
                dialog.show();
                double lat = marker.getPosition().latitude;
                double lng = marker.getPosition().longitude;
                mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_LISTORDER).push().setValue(new DetailOrder(editNameorder.getText().toString(), new toAddress(editAddressReceived.getText().toString(), editNameReceived.getText().toString(), Integer.valueOf(editPhoneReceived.getText().toString()), new sLocation(lat, lng, 0, 0), editNote.getText().toString()), Integer.valueOf(editPriceOrder.getText().toString()), Integer.valueOf(editfreight.getText().toString()), new sLocation(positionShop.latitude, positionShop.longitude, 0, 0), getString(R.string.isWatting), user.getUid(), "")).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mData.child(var.CHILD_MAPSHOP).child(user.getUid()).child("avableOrder").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.hide();
                                finish();
                            }
                        });
                    }
                });

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Vị trí bản đồ").flat(true));
        marker.hideInfoWindow();
        geocoder = new Geocoder(AddOrderActivity.this, Locale.getDefault());
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Location a = mMap.getMyLocation();
                LatLng ll = new LatLng(a.getLatitude(), a.getLongitude());
                marker.setPosition(ll);
                try {
                    addresses = geocoder.getFromLocation(a.getLatitude(), a.getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        address = addresses.get(0).getAddressLine(0);
                        marker.setSnippet(address);
                        marker.showInfoWindow();
                    }
                } catch (IOException e) {
                    mMap.clear();
                    marker = mMap.addMarker(new MarkerOptions().position(ll).title("Vị trí bản đồ").flat(true));
                    marker.showInfoWindow();
                }
                return false;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                marker.setPosition(latLng);
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        address = addresses.get(0).getAddressLine(0);
                        editAddressReceived.setText("" + addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(3));
                        marker.setSnippet(address);
                        marker.showInfoWindow();
                    }
                } catch (IOException e) {
                    marker.setPosition(latLng);
                    marker.setSnippet(address);
                    marker.showInfoWindow();
                }

            }
        });
    }

    private void getLocationShop() {
        mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_INFO).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                shop = dataSnapshot.getValue(Shop.class);
                positionShop = new LatLng(shop.getsLocation().getLat(), shop.getsLocation().getLng());
                Log.e("getLocationShop",positionShop.toString()+"" );
                dialog.hide();
                mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_LISTORDER).removeEventListener(this);
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
