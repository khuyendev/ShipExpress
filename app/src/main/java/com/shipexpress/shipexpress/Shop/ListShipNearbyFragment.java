package com.shipexpress.shipexpress.Shop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shipexpress.shipexpress.Adapter.ListShipperAdapter;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Ship.statusShipper;
import com.solidfire.gson.Gson;

import java.util.ArrayList;


public class ListShipNearbyFragment extends Fragment {
    private RecyclerView recyclerView;
    private ListShipperAdapter adapter;
    private ArrayList<statusShipper> listStatus = new ArrayList<>();
    //listenner
    private BroadcastReceiver broadcastReceiver;
    private Intent intent;
    private DatabaseReference mData;
    private FirebaseUser user ;
    public ListShipNearbyFragment() {
        // Required empty public constructor
    }

    public static ListShipNearbyFragment newInstance() {
        Bundle args = new Bundle();
        ListShipNearbyFragment fragment = new ListShipNearbyFragment();
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
                    addShipper(intent);
                if (intent.getFlags() == MapShopService.onChildChanged)
                    changeShipper(intent);
                if (intent.getFlags() == MapShopService.onChildRemoved)
                    removeShipper(intent);
            }
        };
        intent = new Intent(getActivity(), MapShopService.class);
        mData = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((broadcastReceiver),
                new IntentFilter(MapShopService.MapShopService));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fgmentra
        View view = inflater.inflate(R.layout.fragment_shop_list_ship_nearby, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.listShipperNearby);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new ListShipperAdapter(listStatus, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        getInforShop();
    }

    private void addShipper(Intent intent) {
        statusShipper shipper = getStatusShipper(intent);
        listStatus.add(shipper);
        adapter.notifyDataSetChanged();
    }

    private void changeShipper(Intent intent) {
        statusShipper shipper = getStatusShipper(intent);
        Log.d("onChildChanged", "onChildChanged" + shipper.getUIDShipper());
        for (int i = 0; i < listStatus.size(); i++) {

            if(listStatus.get(i).getUIDShipper().equals(shipper.getUIDShipper())){
                listStatus.get(i).setAllValue(shipper);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void removeShipper(Intent intent) {
        statusShipper shipper = getStatusShipper(intent);
        for (int i = 0; i < listStatus.size(); i++) {
            if(listStatus.get(i).getUIDShipper().equals(shipper.getUIDShipper())){
                listStatus.remove(i);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private statusShipper getStatusShipper(Intent intent) {
        return new Gson().fromJson(intent.getExtras().getString(MapShopService.keyIntentShipper), statusShipper.class);
    }
    private void getInforShop() {
        mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_INFO).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Shop shop = dataSnapshot.getValue(Shop.class);
                adapter.setShop(shop);
                adapter.notifyDataSetChanged();
                mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_INFO).removeEventListener(this);
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
