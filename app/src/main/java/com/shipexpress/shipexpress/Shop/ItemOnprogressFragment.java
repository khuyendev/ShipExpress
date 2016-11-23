package com.shipexpress.shipexpress.Shop;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Ship.Shipper;
import com.shipexpress.shipexpress.Ship.statusShipper;
import com.shipexpress.shipexpress.Utility.GMapV2Direction;
import com.shipexpress.shipexpress.Utility.GMapV2DirectionAsyncTask;
import com.shipexpress.shipexpress.Utility.MapScrollViewSupport;
import com.solidfire.gson.Gson;

import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemOnprogressFragment extends Fragment implements OnMapReadyCallback {
    private ScrollView scrollView;
    private GoogleMap mMap;
    private Polyline polylin;
    private LatLng shipLocation, toAddressLocation, ShopLocation;
    private Marker markerShip, markerShop, markerToAddress;
    private Shipper shipper;
    private DetailOrder detailOrder;
    private Button btnOnprogress;
    //firebase
    private DatabaseReference mData;
    private boolean isRoute = true;

    public ItemOnprogressFragment() {
        // Required empty public constructor
    }

    public static ItemOnprogressFragment newInstance(DetailOrder order, Shipper shipper) {
        Bundle args = new Bundle();
        ItemOnprogressFragment fragment = new ItemOnprogressFragment();
        args.putString("order", new Gson().toJson(order));
        args.putString("shipper", new Gson().toJson(shipper));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = FirebaseDatabase.getInstance().getReference();
        if (getArguments() != null) {
            detailOrder = new Gson().fromJson(getArguments().getString("order"), DetailOrder.class);
            shipper = new Gson().fromJson(getArguments().getString("shipper"), Shipper.class);
            ShopLocation = new LatLng(DirectionShopActivity.shop.getsLocation().getLat(), DirectionShopActivity.shop.getsLocation().getLng());
            toAddressLocation = new LatLng(detailOrder.getToAddress().getsLocation().getLat(), detailOrder.getToAddress().getsLocation().getLng());

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_onprogress, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollView = (ScrollView) view.findViewById(R.id.scrollViewShop);
        btnOnprogress = (Button) view.findViewById(R.id.btnOnprogress);
        btnOnprogress.setVisibility(View.INVISIBLE);
        initView();
    }

    private void initView() {
        MapScrollViewSupport mapFragment = (MapScrollViewSupport) getChildFragmentManager().findFragmentById(R.id.mapDirectionShop);
        mapFragment.setListener(new MapScrollViewSupport.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        markerShop = mMap.addMarker(new MarkerOptions().position(ShopLocation).title(DirectionShopActivity.shop.nameShop).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_shop)));
        markerToAddress = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_guest)).position(toAddressLocation).title(detailOrder.getToAddress().getNameAddress()));
        trackerShipper();
    }

    private void trackerShipper() {
        mData.child(var.CHILD_MAPSHIP).child(detailOrder.getUIDShip()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{

               // Log.d("dataSnapshot", dataSnapshot.toString() + "");
                statusShipper shipper = dataSnapshot.getValue(statusShipper.class);
                shipLocation = new LatLng(shipper.getLocation().getLatCurrentLocation(), shipper.getLocation().getLngCurrentLocation());
                RT();
                if (markerShip == null) {
                    markerShip = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_ship_f)).position(new LatLng(shipper.getLocation().getLatCurrentLocation(), shipper.getLocation().getLngCurrentLocation())).title(shipper.getNameShipper()));
                }else {
                    markerShip.setPosition(new LatLng(shipper.getLocation().getLatCurrentLocation(), shipper.getLocation().getLngCurrentLocation()));
                }}
                catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void RT() {
        if (isRoute == true) {
            isRoute = false;
            if (detailOrder.getStatusOrder().equals(var.isDeposit)) {
                btnOnprogress.setVisibility(View.VISIBLE);
                route(shipLocation, ShopLocation, "");
                onProgressOrder();
            } else if (detailOrder.getStatusOrder().equals(var.onProgress)) {
                route(shipLocation, toAddressLocation, "");

            }
        }
    }

    private void route(LatLng sourcePosition, LatLng destPosition, String mode) {
        if (polylin != null) {
            polylin.remove();
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    GMapV2Direction md = new GMapV2Direction();
                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
                    PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.BLUE).visible(true);
                    for (int i = 0; i < directionPoint.size(); i++) {
                        rectLine.add(directionPoint.get(i));
                    }
                    polylin = mMap.addPolyline(rectLine);
                    md.getDurationText(doc);
                    md.getDistanceText(doc);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(shipLocation, 14f));
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Không thể hiện chỉ đường...", Toast.LENGTH_SHORT).show();
                }
            }
        };
        new GMapV2DirectionAsyncTask(handler, sourcePosition, destPosition, GMapV2Direction.MODE_DRIVING).execute();
    }

    private void onProgressOrder() {
        btnOnprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailOrder.setStatusOrder(var.onProgress);
                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext());
                dialog.setTitle("Xác nhận đã nhận tiền cọc?").setPositiveButton("cancel", null).setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mData.child(var.CHILD_SHOP).child(detailOrder.getUIDShop()).child(var.CHILD_LISTORDER).child(detailOrder.getKey()).setValue(detailOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mData.child(var.CHILD_SHIP).child(detailOrder.getUIDShip()).child(var.CHILD_LISTORDER).child(detailOrder.getKeyship()).setValue(detailOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        btnOnprogress.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        });
                    }
                }).create().show();
            }
        });
    }

}
