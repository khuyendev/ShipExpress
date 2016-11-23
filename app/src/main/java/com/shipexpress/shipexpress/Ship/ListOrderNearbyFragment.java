package com.shipexpress.shipexpress.Ship;


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

import com.shipexpress.shipexpress.Adapter.ListOrderAdapter;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.R;
import com.solidfire.gson.Gson;

import java.util.ArrayList;


public class ListOrderNearbyFragment extends Fragment {

    private BroadcastReceiver broadcastReceiver;
    private Intent intent;

    ArrayList<DetailOrder> detailOrderArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListOrderAdapter adapter;

    public ListOrderNearbyFragment() {
        // Required empty public constructor
    }

    public static ListOrderNearbyFragment newInstance() {
        Bundle args = new Bundle();
        ListOrderNearbyFragment fragment = new ListOrderNearbyFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent!=null&&intent.getFlags() == ShipOrderService.onChildAdded) {
                    DetailOrder detailOrder = new Gson().fromJson(intent.getStringExtra("onAvableOrder"), DetailOrder.class);
                    Log.d("key","key="+detailOrder.getKey());
                    for (int i = 0; i <detailOrderArrayList.size() ; i++) {
                        if(detailOrderArrayList.get(i).getKey().equals(detailOrder.getKey())){
                            detailOrderArrayList.remove(i);
                        }
                    }
                    detailOrderArrayList.add(detailOrder);
                    adapter.notifyDataSetChanged();
                }
                if (intent!=null&&intent.getFlags() == ShipOrderService.onChildChanged) {
                    DetailOrder detailOrder = new Gson().fromJson(intent.getStringExtra("onAvableOrder"), DetailOrder.class);
                    Log.d("key","key="+detailOrder.getKey());
                    detailOrderArrayList.add(detailOrder);
                    adapter.notifyDataSetChanged();
                }
                if (intent!=null&&intent.getFlags() == ShipOrderService.onChildRemoved) {
                    DetailOrder detailOrder = new Gson().fromJson(intent.getStringExtra("onAvableOrder"), DetailOrder.class);
                    Log.d("onChildRemoved","onChildRemoved="+detailOrder.getKey());
                    for(int i=0;i<detailOrderArrayList.size();i++){
                        if(detailOrderArrayList.get(i).getKey().equals(detailOrder.getKey())){
                            detailOrderArrayList.remove(i);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        };
        intent = new Intent(getActivity(), ShipOrderService.class);
        adapter = new ListOrderAdapter(getContext(),detailOrderArrayList, var.INT_isCOMMIT);
    }
    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((broadcastReceiver),
                new IntentFilter(ShipOrderService.OrderNeabyService));
    }
    @Override
    public void onStop() {
        super.onStop();
        detailOrderArrayList.clear();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver((broadcastReceiver));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ship_list_order_nearby, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.listOrdernearby);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
