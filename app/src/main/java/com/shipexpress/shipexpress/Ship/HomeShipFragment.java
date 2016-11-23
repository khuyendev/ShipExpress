package com.shipexpress.shipexpress.Ship;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Shop.statusShop;
import com.shipexpress.shipexpress.Utility.GMapV2Direction;
import com.shipexpress.shipexpress.Utility.GMapV2DirectionAsyncTask;
import com.solidfire.gson.Gson;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;

import static com.shipexpress.shipexpress.Ship.ShipOrderService.latLngCurrentLocation;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeShipFragment extends Fragment implements OnMapReadyCallback {
    public GoogleMap mMap;
    //service
    private BroadcastReceiver broadcastReceiver;
    private Intent intent;
    HashMap<String, Object> listMarker = new HashMap<>();


    public HomeShipFragment() {
        // Required empty public constructor
    }

    public static HomeShipFragment newInstance() {
        Bundle args = new Bundle();
        HomeShipFragment fragment = new HomeShipFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getFlags() == 0)
                    upDateMarker(intent);
                if (intent.getFlags() == 1)
                    changePostionMarker(intent);
                if (intent.getFlags() == 2)
                    deleteMarker(intent);
                if (intent.getFlags() == 3) {

                }
            }
        };
        intent = new Intent(getActivity(), ShipOrderService.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        getContext().startService(intent.setFlags(ShipOrderService.ActionGetOrder));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((broadcastReceiver),
                new IntentFilter(ShipOrderService.OrderService));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver((broadcastReceiver));
        getContext().startService(intent.setFlags(ShipOrderService.ActionRemoveListenner));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ship_home, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapship);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMap();
    }

    public void setUpMap() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                route(latLngCurrentLocation,marker.getPosition(),"",marker);
                return false;
            }
        });
    }

    private void upDateMarker(Intent intent) {
        Marker marker;
        statusShop shop = new Gson().fromJson(intent.getExtras().getString("SHOP"), statusShop.class);
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(shop.getLatLng().getLat(), shop.getLatLng().getLng())).title(shop.getNameShop()));
        if (shop.isAvableOrder()) {
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_shop_avaible));
        } else {
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_shop_notavaible));
        }
        listMarker.put(shop.getUID(), marker);
    }

    private void changePostionMarker(Intent intent) {
        statusShop shop = new Gson().fromJson(intent.getExtras().getString("SHOP"), statusShop.class);
        Marker marker1 = (Marker) listMarker.get(shop.getUID());
        marker1.setPosition(new LatLng(shop.getLatLng().getLat(), shop.getLatLng().getLng()));
        if (shop.isAvableOrder()) {
            marker1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_shop_avaible));
        } else {
            marker1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_shop_notavaible));
        }
        marker1.setTitle(shop.getNameShop());
    }

    private void deleteMarker(Intent intent) {
        statusShop shop = new Gson().fromJson(intent.getExtras().getString("SHOP"), statusShop.class);
        Marker marker2 = (Marker) listMarker.get(shop.getUID());
        marker2.remove();
        listMarker.remove(shop.getUID());
    }

    private void route(LatLng sourcePosition, LatLng destPosition, String mode, final Marker marker) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    GMapV2Direction md = new GMapV2Direction();
//                                        ArrayList<LatLng> directionPoint = md.getDirection(doc);
//                    PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.BLUE).visible(true);
//                    for (int i = 0; i < directionPoint.size(); i++) {
//                        rectLine.add(directionPoint.get(i));
//                    }
//
//                    md.getDurationText(doc);
//
//                    md.getStartAddress(doc);
//                    md.getDistanceText(doc);
                    marker.setSnippet(md.getDistanceText(doc)+"");
                    marker.showInfoWindow();
                } catch (Exception e) {
                }
            }
        };
        new GMapV2DirectionAsyncTask(handler, sourcePosition, destPosition, GMapV2Direction.MODE_DRIVING).execute();
    }
}

