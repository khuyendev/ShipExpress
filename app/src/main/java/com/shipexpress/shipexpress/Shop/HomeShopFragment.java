package com.shipexpress.shipexpress.Shop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Ship.statusShipper;
import com.solidfire.gson.Gson;

import java.util.HashMap;


public class HomeShopFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private BroadcastReceiver broadcastReceiver;
    private Intent intent;
    HashMap<String, Object> listMarker = new HashMap<>();
    HashMap<String, statusShipper> lisShipperHashMap = new HashMap<>();
    Marker marker;
    Marker markerShop;
    ImageButton btnLocation;

    public HomeShopFragment() {
    }

    public static HomeShopFragment newInstance() {
        Bundle args = new Bundle();
        HomeShopFragment fragment = new HomeShopFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getFlags() == MapShopService.onChildAdded)
                    upDateMarker(intent);
                if (intent.getFlags() == MapShopService.onChildChanged)
                    changePostionMarker(intent);
                if (intent.getFlags() == MapShopService.onChildRemoved)
                    deleteMarker(intent);
                if (intent.getFlags() == MapShopService.ActionGetInforShop) {
                    final Shop shop = new Gson().fromJson(intent.getExtras().getString("InforShop"), Shop.class);
                    markerShop = mMap.addMarker(new MarkerOptions().position(new LatLng(shop.getsLocation().getLat(), shop.getsLocation().getLng())).title(shop.getNameShop()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(shop.getsLocation().getLat(), shop.getsLocation().getLng()), 14f));
                    markerShop.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.home));
                    btnLocation.setVisibility(View.VISIBLE);
                    btnLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(shop.getsLocation().getLat(), shop.getsLocation().getLng()), 14f));
                            markerShop.showInfoWindow();
                        }
                    });
                }
            }
        };
        intent = new Intent(getActivity(), MapShopService.class);
    }


    @Override
    public void onStart() {
        super.onStart();
        getActivity().startService(intent.setFlags(MapShopService.ActionGetShip));
        getActivity().startService(intent.setFlags(MapShopService.ActionGetInforShop));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((broadcastReceiver),
                new IntentFilter(MapShopService.MapShopService));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((broadcastReceiver),
                new IntentFilter(MapShopService.Info));
    }

    @Override
    public void onStop() {
        super.onStop();
        intent = new Intent(getActivity(), MapShopService.class);
        getActivity().startService(intent.setFlags(MapShopService.RemoveActionGetShip));
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver((broadcastReceiver));
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapShop);
        mapFragment.getMapAsync(this);
        btnLocation = (ImageButton) view.findViewById(R.id.btnLocation);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {
                    statusShipper statusShipper = lisShipperHashMap.get(marker.getId());
                    marker.setSnippet(DateUtils.getRelativeTimeSpanString(statusShipper.getTimeStamp()).toString());
                } catch (Exception e) {

                }
                return false;
            }
        });
    }

    private void upDateMarker(Intent intent) {
        statusShipper shipper = getStatusShipper(intent);
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(shipper.getLocation().getLatCurrentLocation(), shipper.getLocation().getLngCurrentLocation())).title(shipper.getNameShipper()));
        if (shipper.isAvailable()) {
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_ship));
        } else {
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_ship_f));
        }
        Log.d("sd", shipper.getTimeStamp() + "");
        marker.setSnippet(DateUtils.getRelativeTimeSpanString(shipper.getTimeStamp()).toString());
        listMarker.put(shipper.getUIDShipper(), marker);
        lisShipperHashMap.put(marker.getId(), shipper);
    }

    private void changePostionMarker(Intent intent) {
        statusShipper shipper = getStatusShipper(intent);

        Marker marker1 = (Marker) listMarker.get(shipper.getUIDShipper());
        lisShipperHashMap.put(marker1.getId(), shipper);
        marker1.setSnippet(DateUtils.getRelativeTimeSpanString(shipper.getTimeStamp()).toString());
        if (marker1 != null) {
            marker1.setPosition(new LatLng(shipper.getLocation().getLatCurrentLocation(), shipper.getLocation().getLngCurrentLocation()));
            marker1.setTitle(shipper.getNameShipper());
        }
        if (shipper.isAvailable()) {
            marker1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_ship));
        } else {
            marker1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_ship_f));
        }
    }

    private void deleteMarker(Intent intent) {
        statusShipper shipper = getStatusShipper(intent);
        Marker marker2 = (Marker) listMarker.get(shipper.getUIDShipper());
        marker2.remove();
        listMarker.remove(shipper.getUIDShipper());
    }

    private statusShipper getStatusShipper(Intent intent) {
        return new Gson().fromJson(intent.getExtras().getString(MapShopService.keyIntentShipper), statusShipper.class);
    }
}
