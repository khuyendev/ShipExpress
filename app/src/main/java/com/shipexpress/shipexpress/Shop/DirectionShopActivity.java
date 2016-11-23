package com.shipexpress.shipexpress.Shop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shipexpress.shipexpress.Adapter.ListOrderProgressShopAdapter;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Ship.DirectionShipActivity;
import com.shipexpress.shipexpress.Ship.ShipOrderService;
import com.shipexpress.shipexpress.Ship.Shipper;
import com.shipexpress.shipexpress.Ship.statusShipper;
import com.shipexpress.shipexpress.Utility.GMapV2Direction;
import com.shipexpress.shipexpress.Utility.GMapV2DirectionAsyncTask;
import com.shipexpress.shipexpress.Utility.MapScrollViewSupport;
import com.solidfire.gson.Gson;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Map;

public class DirectionShopActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    //view
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private RecyclerView list_onprogress;
    ListOrderProgressShopAdapter adapter;
    ArrayList<DetailOrder> orders = new ArrayList<>();
    ArrayList<Shipper> shippers = new ArrayList<>();
    public static Shop shop;
    //ob
    private DetailOrder detailOrder;
    //firebase
    //service
    private BroadcastReceiver broadcastReceiver;
    private Intent intent;

    //static
    private static final String GET_ORDER_ONPROGRESS = "GET_ORDER_ONPROGRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_shop);
        initView();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getFlags() == MapShopService.onChildAdded) {
                    orders.add(new Gson().fromJson(intent.getExtras().getString("order"), DetailOrder.class));
                    shippers.add(new Gson().fromJson(intent.getExtras().getString("shipper"), Shipper.class));
                }
                if (intent.getFlags() == MapShopService.ActionGetInforShop) {
                    shop = new Gson().fromJson(intent.getExtras().getString("InforShop"), Shop.class);
                    Log.d("shop", "shop" + shop.getsLocation().getLat());
                }
                if (intent.getFlags() == MapShopService.onChildRemoved) {
                    DetailOrder order = new Gson().fromJson(intent.getExtras().getString("order"), DetailOrder.class);
                    for(int i=0;i<orders.size();i++){
                        if(orders.get(i).getKey().equals(order.getKey())){
                            orders.remove(i);
                            shippers.remove(i);
                            break;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        };
        intent = new Intent(DirectionShopActivity.this, MapShopService.class);
    }
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        list_onprogress = (RecyclerView) findViewById(R.id.list_onprogress);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        list_onprogress.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list_onprogress.getContext(),
                layoutManager.getItemCount());
        list_onprogress.addItemDecoration(dividerItemDecoration);
        adapter = new ListOrderProgressShopAdapter(this, orders, shippers);
        list_onprogress.setAdapter(adapter);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, MapShopService.class).setFlags(MapShopService.ActionGetOrderProgress));
        startService(new Intent(this, MapShopService.class).setFlags(MapShopService.ActionGetInforShop));
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter(MapShopService.RequestOrderProgress));
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter(MapShopService.Info));
    }

    @Override
    protected void onPause() {
        super.onPause();
        MapShopService.FlagOnactivity = false;
        LocalBroadcastManager.getInstance(DirectionShopActivity.this).unregisterReceiver((broadcastReceiver));
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
