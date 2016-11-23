package com.shipexpress.shipexpress.Ship;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Shop.Shop;
import com.shipexpress.shipexpress.Shop.statusShop;
import com.shipexpress.shipexpress.Utility.GMapV2Direction;
import com.shipexpress.shipexpress.Utility.GMapV2DirectionAsyncTask;
import com.shipexpress.shipexpress.Utility.MapScrollViewSupport;
import com.solidfire.gson.Gson;

import org.w3c.dom.Document;

import java.util.ArrayList;

import static com.shipexpress.shipexpress.R.id.diachikk;
import static com.shipexpress.shipexpress.R.id.view;
import static com.shipexpress.shipexpress.Ship.ShipOrderService.notificationManager;



public class DirectionShipActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ScrollView scrollView;
    private Toolbar toolbar;
    private GoogleMap mMap;
    private DetailOrder detailOrder;
    private Shop shop;
    //service
    private BroadcastReceiver broadcastReceiver;
    private Intent intent;
    LatLng currentLocation, toAddressLocation, shopLocation;
    private Marker mtoAddressLocation, mShopLocation;
    //view
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private info.hoang8f.widget.FButton btnShopDir, btnCustomeDir, btnFinishOrder;
    private Polyline polylin;
    //firebase
    private DatabaseReference mData;
    private TextView tenkhachhang,diachi,ghichu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        mData = FirebaseDatabase.getInstance().getReference();
        initView();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getFlags() == ShipOrderService.ActionDirection) {
                    currentLocation = new Gson().fromJson(intent.getExtras().getString("Latlng"), LatLng.class);
                    shopLocation = new LatLng(shop.getsLocation().getLat(), shop.getsLocation().getLng());
                    route(currentLocation, toAddressLocation, "");
                    mtoAddressLocation = mMap.addMarker(new MarkerOptions().title("Khách").position(toAddressLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_guest)));
                    mtoAddressLocation = mMap.addMarker(new MarkerOptions().title("Cửa hàng").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_shop_avaible)).position(shopLocation));

                }
            }
        };
        intent = new Intent(DirectionShipActivity.this, ShipOrderService.class);
        btnShopDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                route(ShipOrderService.latLngCurrentLocation, shopLocation, "");
            }
        });
        btnCustomeDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                route(ShipOrderService.latLngCurrentLocation, toAddressLocation, "");
            }
        });

    }

    private void initView() {
        tenkhachhang = (TextView)findViewById(R.id.tenkhachhang) ;
        diachi = (TextView)findViewById(R.id.diachikk) ;
        ghichu = (TextView)findViewById(R.id.ghichu) ;
        detailOrder = new Gson().fromJson(getIntent().getExtras().getString("Order"), DetailOrder.class);
        shop = new Gson().fromJson(getIntent().getExtras().getString("shop"), Shop.class);
        toAddressLocation = new LatLng(detailOrder.getToAddress().getsLocation().getLat(), detailOrder.getToAddress().getsLocation().getLng());
        scrollView = (ScrollView) findViewById(R.id.scrollViewDi);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tenkhachhang.setText(detailOrder.getToAddress().getNameReceived());
        diachi.setText(detailOrder.getToAddress().getNameAddress());
        ghichu.setText(detailOrder.getToAddress().getNote());
        setSupportActionBar(toolbar);
        btnShopDir = (info.hoang8f.widget.FButton) findViewById(R.id.btnShopDir);
        btnCustomeDir = (info.hoang8f.widget.FButton) findViewById(R.id.btnToAdressDir);
        setButtonSubmitOrder();
        MapScrollViewSupport mapFragment = (MapScrollViewSupport) getSupportFragmentManager().findFragmentById(R.id.mapDirection);
        mapFragment.setListener(new MapScrollViewSupport.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(DirectionShipActivity.this).unregisterReceiver((broadcastReceiver));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        startService(intent.setFlags(ShipOrderService.ActionDirection));
        LocalBroadcastManager.getInstance(DirectionShipActivity.this).registerReceiver((broadcastReceiver),
                new IntentFilter(ShipOrderService.Direction));
    }

    private void route(final LatLng sourcePosition, LatLng destPosition, String mode) {
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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sourcePosition, 14f));

                } catch (Exception e) {
                    Toast.makeText(DirectionShipActivity.this, "Không thể hiện chỉ đường, đang thử lại...", Toast.LENGTH_SHORT).show();
                }
            }
        };
        new GMapV2DirectionAsyncTask(handler, sourcePosition, destPosition, GMapV2Direction.MODE_DRIVING).execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void setButtonSubmitOrder() {
        btnFinishOrder = (info.hoang8f.widget.FButton) findViewById(R.id.btnFinish);
        if (detailOrder.getStatusOrder().equals(var.isDeposit)) {
            btnFinishOrder.setText("Hủy đơn hàng");
            btnFinishOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(DirectionShipActivity.this);
                    dialog.setTitle("Xác nhận hủy hàng ?").setPositiveButton("cancel", null).setNegativeButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            detailOrder.setStatusOrder(var.isWAITTING);
                            cancelOrder(detailOrder);
                          //  notificationManager.cancel(25333);
                        }
                    }).create().show();


                }
            });
        }
        if (detailOrder.getStatusOrder().equals(var.onProgress)) {
            btnFinishOrder.setText("Trả hàng");
            btnFinishOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(DirectionShipActivity.this);
                    dialog.setTitle("Xác nhận trả hàng ?").setPositiveButton("cancel", null).setNegativeButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            detailOrder.setStatusOrder(var.isSUCCESS);
                            completeOrder(detailOrder);
                           //
                        }
                    }).create().show();
                }
            });
        }

    }

    private void completeOrder(final DetailOrder detailOrder) {
        mData.child(var.CHILD_SHOP).child(detailOrder.getUIDShop()).child(var.CHILD_LISTORDER).child(detailOrder.getKey()).setValue(detailOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mData.child(var.CHILD_SHIP).child(detailOrder.getUIDShip()).child(var.CHILD_LISTORDER).child(detailOrder.getKeyship()).setValue(detailOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        notificationManager.cancel(25333);
                      finish();
                    }
                });
            }
        });

    }

    private void cancelOrder(final DetailOrder detailOrder) {
        mData.child(var.CHILD_SHOP).child(detailOrder.getUIDShop()).child(var.CHILD_LISTORDER).child(detailOrder.getKey()).setValue(detailOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                detailOrder.setStatusOrder(var.isCANCEL);
                mData.child(var.CHILD_SHIP).child(detailOrder.getUIDShip()).child(var.CHILD_LISTORDER).child(detailOrder.getKeyship()).setValue(detailOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        notificationManager.cancel(25333);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}
