package com.shipexpress.shipexpress.Ship;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Shop.DirectionShopActivity;
import com.shipexpress.shipexpress.Shop.MapShopService;
import com.shipexpress.shipexpress.Shop.Shop;
import com.shipexpress.shipexpress.Shop.statusShop;
import com.shipexpress.shipexpress.Utility.sLocation;
import com.solidfire.gson.Gson;

import static android.R.attr.order;

public class ShipOrderService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, FirebaseAuth.AuthStateListener {
    private LocalBroadcastManager broadcastManager;
    private Intent intent;
    public final static String OrderService = "ShipOrderService";
    public final static String OrderNeabyService = "ShipOrderNearbyService";
    public final static String Direction = "Direction";
    public final static String INFORSHIP = "inforship";
    public final static int ActionDirection = 44;

    public final static int ActionGetOrder = 10;
    public final static int ActionGetInforShipper = 343;
    public final static int ActionRemoveListenner = 12;
    public final static int ActionCommitOrder = 11;
    public final static int ActionDSignouut = 2323;
    public final static int ActionOffline = 898;
    public final static int onChildAdded = 0;
    public final static int onChildChanged = 1;
    public final static int onChildRemoved = 2;
    public final static int onAvableOrder = 3;
    //mapshop
    //firebase
    private DatabaseReference mData;
    private FirebaseUser user;
    private ChildEventListener listenerMapShop;
    private ChildEventListener listenerShopOrderAvaible;
    private ChildEventListener listenerOrderBystatus;
    private ChildEventListener InforOrderAndShop;
    private ValueEventListener ListennerShopCommitOrReject;
    private FirebaseAuth mAuth;
    //location
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public static LatLng latLngCurrentLocation;
    private int timeInterval = 50000;

    //listorder
    public static boolean onProgress = false;
    boolean haveinfor = false;
    //noti
    Notification notification;
    public static NotificationManager notificationManager;

    public ShipOrderService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
        mData = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        getInforShip();
        getOrderbyStatus();
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        notification = new Notification.Builder(this)
                .setContentTitle("")
                .setContentText("").setSmallIcon(android.R.drawable.ic_dialog_alert).setAutoCancel(false)
                .build();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        //       notificationManager.notify(IDNOTIFICATION, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            intentAction(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void intentAction(Intent intent) {
        if (intent.getFlags() == ActionGetOrder) {
            getAllShopNearby();
        }
        if (intent.getFlags() == ActionRemoveListenner && listenerMapShop != null) {
            mData.child(var.CHILD_MAPSHOP).removeEventListener(listenerMapShop);
        }
        if (intent.getFlags() == ActionCommitOrder) {
            DetailOrder detailOrder = new Gson().fromJson(intent.getStringExtra("detailOrder"), DetailOrder.class);
            detailOrder.setUIDShip(user.getUid());
            detailOrder.setStatusOrder(getString(R.string.isCommit));
            setCommitOrder(detailOrder);
        }
        if (intent.getFlags() == ActionDirection) {
            intent = new Intent(Direction);
            intent.setFlags(ActionDirection);
            intent.putExtra("Latlng", new Gson().toJson(latLngCurrentLocation));
            broadcastManager.sendBroadcast(intent);
        }
        if (intent.getFlags() == ActionDSignouut) {
            mData.child(var.CHILD_MAPSHIP).child(user.getUid()).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    mAuth.signOut();
                    Log.e("ActionDirection", "ActionDirection");
                }
            });
        }
        if (intent.getFlags() == ActionOffline) {
            mData.child(var.CHILD_MAPSHIP).child(user.getUid()).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Toast.makeText(ShipOrderService.this, "Đã offline", Toast.LENGTH_SHORT).show();
                }
            });
            stopSelf();
        }
        if (intent.getFlags() == ActionGetInforShipper) {
            getInforShip();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        onProgress = false;
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(timeInterval); // Update location every second
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        latLngCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (haveinfor) {
            try {
                mData.child(var.CHILD_MAPSHIP).child(user.getUid()).child("location").setValue(new sLocation(0, 0, location.getLatitude(), location.getLongitude()));
                mData.child(var.CHILD_MAPSHIP).child(user.getUid()).child("timestampCreated").child("timestamp").setValue(ServerValue.TIMESTAMP);
            } catch (Exception e) {

            }
        }

    }

    private void getAllShopNearby() {
        if (listenerMapShop == null) {

            //dieu khien map shipper
            listenerMapShop = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    statusShop shop = dataSnapshot.getValue(statusShop.class);
                    if (shop.isAvableOrder()) {
                        getOrderByShopStatus(shop.getUID());
                    }
                    intent = new Intent(OrderService);
                    intent.setFlags(onChildAdded);
                    intent.putExtra("SHOP", new Gson().toJson(shop));
                    broadcastManager.sendBroadcast(intent);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    statusShop shop = dataSnapshot.getValue(statusShop.class);
                    if (shop.isAvableOrder()) {
                        getOrderByShopStatus(shop.getUID());
                    } else if (!shop.isAvableOrder()) {
                        removeListennerShopAivable(shop.getUID());
                    }
                    intent = new Intent(OrderService);
                    intent.setFlags(onChildChanged);
                    intent.putExtra("SHOP", new Gson().toJson(shop));
                    broadcastManager.sendBroadcast(intent);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    statusShop shop = dataSnapshot.getValue(statusShop.class);
                    intent = new Intent(OrderService);
                    intent.setFlags(onChildRemoved);
                    intent.putExtra("SHOP", new Gson().toJson(shop));
                    broadcastManager.sendBroadcast(intent);
                    if (shop.isAvableOrder()) {
                        removeListennerShopAivable(shop.getUID());
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        } else {
            mData.child(var.CHILD_MAPSHOP).removeEventListener(listenerMapShop);
        }
        mData.child(var.CHILD_MAPSHOP).addChildEventListener(listenerMapShop);
    }

    //lay don hang cac shop co don hang dang cho
    public void getOrderByShopStatus(String UID) {
        if (listenerShopOrderAvaible == null) {
            listenerShopOrderAvaible = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    sendOrderResult(dataSnapshot, OrderNeabyService, onChildAdded, "onAvableOrder");
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    sendOrderResult(dataSnapshot, OrderNeabyService, onChildChanged, "onAvableOrder");
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    sendOrderResult(dataSnapshot, OrderNeabyService, onChildRemoved, "onAvableOrder");
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        } else {
            mData.child(var.CHILD_SHOP).child(UID).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(getString(R.string.isWatting)).removeEventListener(listenerShopOrderAvaible);
        }
        mData.child(var.CHILD_SHOP).child(UID).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(getString(R.string.isWatting)).addChildEventListener(listenerShopOrderAvaible);
    }

    private void removeListennerShopAivable(String UID) {
        if (listenerShopOrderAvaible != null) {
            mData.child(var.CHILD_SHOP).child(UID).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(getString(R.string.isWatting)).removeEventListener(listenerShopOrderAvaible);
        }
    }

    private void sendOrderResult(DataSnapshot dataSnapshot, String typeIntent, int flatIntent, String extraIntent) {
        DetailOrder detailOrder = dataSnapshot.getValue(DetailOrder.class);
        detailOrder.setKey(dataSnapshot.getKey());
        intent = new Intent(typeIntent);
        intent.setFlags(flatIntent);
        intent.putExtra(extraIntent, new Gson().toJson(detailOrder));
        broadcastManager.sendBroadcast(intent);
    }

    private void getOrderbyStatus() {
        listenerOrderBystatus = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Mot khi shop xac nhan, ham nay se chay
                DetailOrder detailOrder = dataSnapshot.getValue(DetailOrder.class);
                if (detailOrder.getStatusOrder().equals(var.onProgress)) {
                    getInforOrerAndShop(detailOrder);
                    onProgress = true;
                    mData.child(var.CHILD_MAPSHIP).child(user.getUid()).child("available").setValue(false);
                    Log.e("onProgress", "onProgress");
                }
                if (detailOrder.getStatusOrder().equals(var.isDeposit)) {
                    getInforOrerAndShop(detailOrder);
                    onProgress = true;
                    Log.e("isDeposit", "isDeposit");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                DetailOrder detailOrder = dataSnapshot.getValue(DetailOrder.class);
                if (detailOrder.getStatusOrder().equals(var.onProgress)) {
                    getInforOrerAndShop(detailOrder);
                    onProgress = true;
                }
                if (detailOrder.getStatusOrder().equals(var.isDeposit)) {
                    Log.e("isDeposit", "isDeposit");
                    Toast.makeText(ShipOrderService.this, "isDeposit", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                onProgress = false;
                mData.child(var.CHILD_MAPSHIP).child(user.getUid()).child("available").setValue(true);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        try {
            mData.child(var.CHILD_SHIP).child(user.getUid()).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(var.isDeposit).addChildEventListener(listenerOrderBystatus);
            mData.child(var.CHILD_SHIP).child(user.getUid()).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(var.onProgress).addChildEventListener(listenerOrderBystatus);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        }
    }

    private void getInforOrerAndShop(final DetailOrder detailOrder) {
        //ham nay lay thong tin shop+ don hang thoi , hihii
        if (InforOrderAndShop != null) {
            mData.child(var.CHILD_SHOP).child(detailOrder.getUIDShop()).child(var.CHILD_INFO).removeEventListener(InforOrderAndShop);
        }
        InforOrderAndShop = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mData.child(var.CHILD_SHOP).child(detailOrder.getUIDShop()).child(var.CHILD_INFO).removeEventListener(InforOrderAndShop);
                Shop shop = dataSnapshot.getValue(Shop.class);
                Toast.makeText(ShipOrderService.this, "COS hangf", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), DirectionShipActivity.class);
                intent.putExtra("Order", new Gson().toJson(detailOrder));
                intent.putExtra("shop", new Gson().toJson(shop));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                notification = new Notification.Builder(ShipOrderService.this)
                        .setContentTitle("Đơn hàng đã nhận !")
                        .setContentText("Đơn hàng " + detailOrder.getName() + " đang vận chuyển").setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .build();
                notification.flags |= Notification.FLAG_NO_CLEAR;
                notificationManager.notify(25333, notification);
                mData.child(var.CHILD_MAPSHIP).child(user.getUid()).child("available").setValue(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mData.child(var.CHILD_MAPSHIP).child(user.getUid()).child("available").setValue(true);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mData.child(var.CHILD_SHOP).child(detailOrder.getUIDShop()).child(var.CHILD_INFO).addChildEventListener(InforOrderAndShop);
    }

    private void setCommitOrder(final DetailOrder detailOrder) {
        //addOrderToListOrderShip
        DatabaseReference databaseReference = mData.child(var.CHILD_SHIP).child(user.getUid()).child(var.CHILD_LISTORDER).push();
        detailOrder.setKeyship(databaseReference.getKey());
        databaseReference.setValue(detailOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mData.child(var.CHILD_SHOP).child(detailOrder.getUIDShop()).child(var.CHILD_LISTORDER).child(detailOrder.getKey()).setValue(detailOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ShipOrderService.this, "đã yêu cầu", Toast.LENGTH_SHORT).show();
                        mData.child(var.CHILD_SHIP).child(user.getUid()).child(var.CHILD_LISTORDER).child(detailOrder.getKeyship()).child("statusOrder").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue().equals(var.isCOMMIT)) {
                                    Log.e("dataSnapshot", "isCOMMIT=");
                                }
                                if (dataSnapshot.getValue().equals(var.isREJECTED)) {
                                    onProgress = false;
                                    mData.child(var.CHILD_MAPSHIP).child(user.getUid()).child("available").setValue(true);
                                    Toast.makeText(ShipOrderService.this, "Shop từ chối yêu cầu", Toast.LENGTH_SHORT).show();
                                }
                                Log.e("dataSnapshot", "dataSnapshot=" + dataSnapshot.toString());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });
    }
    private void getInforShip() {
        if (user != null) {
            mData.child(var.CHILD_SHIP).child(user.getUid()).child(var.CHILD_INFO).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.e("ActionGetInforShipper", dataSnapshot.toString());
                    Shipper shipper = dataSnapshot.getValue(Shipper.class);
                    intent = new Intent(INFORSHIP);
                    intent.setFlags(ActionGetInforShipper);
                    intent.putExtra("shipper", new Gson().toJson(shipper));
                    haveinfor = true;
                    mData.child(var.CHILD_MAPSHIP).child(user.getUid()).setValue(new statusShipper(true, true, shipper.getNameShipper(), new sLocation(0, 0, mLastLocation.getLatitude(), mLastLocation.getLongitude()), user.getUid(), "12"));
                    broadcastManager.sendBroadcast(intent);
                    mData.child(var.CHILD_SHIP).child(user.getUid()).child(var.CHILD_INFO).removeEventListener(this);
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
        } else {
            stopSelf();
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            user = firebaseAuth.getCurrentUser();
            stopSelf();
        }

    }

}
