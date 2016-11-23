package com.shipexpress.shipexpress.Shop;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shipexpress.shipexpress.Adapter.ListItemCommitAdapter;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Ship.Shipper;
import com.shipexpress.shipexpress.Ship.statusShipper;
import com.solidfire.gson.Gson;

import java.util.ArrayList;

public class MapShopService extends Service implements FirebaseAuth.AuthStateListener {
    private LocalBroadcastManager broadcastManager;
    private Intent intent;
    public final static String ListOrderShopFragment = "L";
    public final static String MapShopService = "MS";
    public final static String Info = "Info";
    public final static String RequestOrderProgress = "RequestOrderProgress";
    public final static int onChildAdded = 3;
    public final static int onChildChanged = 1;
    public final static int onChildRemoved = 2;

    public final static int ActionGetShip = 4;
    public final static int RemoveActionGetShip = 5;
    public final static int ActionGetInforShop = 6;
    public final static int ActionGetOrderProgress = 7;
    public final static String keyIntentShipper = "shipper";
    //firebase
    private FirebaseUser user;
    private DatabaseReference mData;
    private ChildEventListener listener;
    private ChildEventListener listenerorderavable;
    private ChildEventListener listenerOrderCommit;
    private ChildEventListener listenerOrderBystatus;
    private ChildEventListener getOrderOnprogress;
    private ValueEventListener listenerOrderisDeposit;
    //inject windows
    private WindowManager windowManager;
    public static boolean FlagOnactivity;
    private WindowManager.LayoutParams params = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
    LinearLayout ll;
    LayoutInflater inflater;
    Button btnDongy, btnTuchoi;
    ArrayList<Shipper> shippers = new ArrayList<>();
    ArrayList<DetailOrder> orders = new ArrayList<>();
    RecyclerView recyclerView;
    ListItemCommitAdapter adapter;
    private Shop shop;
    //notification
    Notification notification;
    NotificationManager notificationManager;
    private final int IDNOTIFICATION = 2323;
    private int numorder = 0;
    PendingIntent pendingIntent;
    int item = 0;
    //flatOnwindows
    private boolean isAttachedToWindow = false;

    public MapShopService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        broadcastManager = LocalBroadcastManager.getInstance(this);
        mData = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            stopSelf();
        }
        getInforShop();
        setListennerOrderAvable();
        listennerOrderStatus();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ll = (LinearLayout) inflater.inflate(R.layout.order_shop_commit, null);
        Intent myIntent = new Intent(getBaseContext(), DirectionShopActivity.class);
        pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, myIntent, 0);
        mapControl();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            switch (intent.getFlags()) {
                case ActionGetShip:
                    getShipperOnline();
                    break;
                case RemoveActionGetShip:
                    mData.child(var.CHILD_MAPSHIP).removeEventListener(listener);
                    break;
                case ActionGetInforShop:
                    getInforShop();
                    break;
                case ActionGetOrderProgress:
                    getOrderOnprogress();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void getOrderOnprogress() {
        getOrderOnprogress = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DetailOrder order = dataSnapshot.getValue(DetailOrder.class);
                querryOrderandShipper(order);
                Log.d("onChildAdded", "getOrderOnprogress" + dataSnapshot.toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildChanged", "getOrderOnprogress" + dataSnapshot.toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                DetailOrder order = dataSnapshot.getValue(DetailOrder.class);
                intent = new Intent(RequestOrderProgress);
                intent.setFlags(onChildRemoved);
                intent.putExtra("order", new Gson().toJson(order));
                broadcastManager.sendBroadcast(intent);
                Log.d("onChildRemoved", "getOrderOnprogress" + dataSnapshot.toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(var.onProgress).addChildEventListener(getOrderOnprogress);
        mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(var.isDeposit).addChildEventListener(getOrderOnprogress);
    }

    private void listenerOrderisDeposit(final DetailOrder detailOrder, int type) {
        if (listenerOrderisDeposit != null && type == 2) {
            mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_LISTORDER).child(detailOrder.getKey()).removeEventListener(listenerOrderisDeposit);
        } else {
            listenerOrderisDeposit = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DetailOrder order = dataSnapshot.getValue(DetailOrder.class);
                    if (order.getStatusOrder().equals(var.isWAITTING)) {
                        notification = new Notification.Builder(MapShopService.this)
                                .setContentTitle("Đơn hàng bị hủy !")
                                .setContentText(" Đơn hàng " + order.getName() + " đã bị hủy").setSmallIcon(android.R.drawable.ic_dialog_alert)
                                .setContentIntent(pendingIntent).build();
                        notificationManager.notify(2554, notification);
                    }
                    if (order.getStatusOrder().equals(var.isSUCCESS)) {
                        notification = new Notification.Builder(MapShopService.this)
                                .setContentTitle("Đơn hàng giao thành công !")
                                .setContentText(" Đơn hàng " + order.getName() + " đã giao thành công").setSmallIcon(android.R.drawable.ic_dialog_alert)
                                .setContentIntent(pendingIntent).build();
                        notificationManager.notify(25555, notification);
                    }
                    Log.d("onDataChange", "onDataChange" + dataSnapshot.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("onCancelled", "onCancelled");
                }
            };
            mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_LISTORDER).child(detailOrder.getKey()).addValueEventListener(listenerOrderisDeposit);
        }
    }

    private void getInforShop() {
        mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_INFO).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                shop = dataSnapshot.getValue(Shop.class);
                intent = new Intent(Info);
                intent.setFlags(ActionGetInforShop);
                intent.putExtra("InforShop", new Gson().toJson(shop));
                broadcastManager.sendBroadcast(intent);
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

    private void getShipperOnline() {
        if (listener != null) {
            mData.child(var.CHILD_MAPSHIP).removeEventListener(listener);
        }
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                statusShipper shipper = dataSnapshot.getValue(statusShipper.class);
                try {
                    shipper.setTimeStamp(Long.parseLong(dataSnapshot.child("timestampCreated").child("timestamp").getValue().toString()));

                } catch (Exception e) {
                }

                intent = new Intent(MapShopService);
                intent.setFlags(onChildAdded);
                intent.putExtra(keyIntentShipper, new Gson().toJson(shipper));
                broadcastManager.sendBroadcast(intent);
                Log.d("onChildAdded", "onChildAdded" + dataSnapshot.toString());
                //    Log.d("onChildChanged", "getShipperOnline" + dataSnapshot.child("timestampCreated").child("timestamp").getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                intent = new Intent(MapShopService);
                statusShipper shipper = dataSnapshot.getValue(statusShipper.class);
                if (shipper.getUIDShipper() != null) {
                    shipper.setTimeStamp(Long.parseLong(dataSnapshot.child("timestampCreated").child("timestamp").getValue().toString()));
                    intent.setFlags(onChildChanged);
                    intent.putExtra(keyIntentShipper, new Gson().toJson(shipper));
                    broadcastManager.sendBroadcast(intent);
                }
                //Log.d("onChildChanged", "onChildChanged" + dataSnapshot.toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                intent = new Intent(MapShopService);
                intent.setFlags(onChildRemoved);
                statusShipper shipper = dataSnapshot.getValue(statusShipper.class);
                shipper.setTimeStamp(Long.parseLong(dataSnapshot.child("timestampCreated").child("timestamp").getValue().toString()));
                intent.putExtra(keyIntentShipper, new Gson().toJson(shipper));
                broadcastManager.sendBroadcast(intent);
                Log.d("onChildRemoved", "onChildRemoved" + dataSnapshot.toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mData.child(var.CHILD_MAPSHIP).addChildEventListener(listener);
    }

    private void setListennerOrderAvable() {
        listenerorderavable = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DetailOrder order = dataSnapshot.getValue(DetailOrder.class);
                querryOrderandShipper(order);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildChanged", "setListennerOrderAvable" + dataSnapshot.toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("onChildAdded", "setListennerOrderAvable" + dataSnapshot.toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mData.child(var.CHILD_SHOP).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(getString(R.string.isCommit)).addChildEventListener(listenerorderavable);
        } else {
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void querryOrderandShipper(final DetailOrder order) {
        listenerOrderCommit = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Shipper shipper = dataSnapshot.getValue(Shipper.class);
                if (order.getStatusOrder().equals(var.isCOMMIT)) {
                    injectWindowsCommitOrder(order, shipper);
                }
                if (order.getStatusOrder().equals(var.onProgress) || order.getStatusOrder().equals(var.isDeposit)) {
                    intent = new Intent(RequestOrderProgress);
                    intent.setFlags(onChildAdded);
                    intent.putExtra("order", new Gson().toJson(order));
                    intent.putExtra("shipper", new Gson().toJson(shipper));
                    broadcastManager.sendBroadcast(intent);
                }
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
        };
        mData.child(var.CHILD_SHIP).child(order.getUIDShip()).child(var.CHILD_INFO).addChildEventListener(listenerOrderCommit);
    }

    private void injectWindowsCommitOrder(final DetailOrder order, final Shipper shipper) {
        if (!isAttachedToWindow) {
            shippers.add(shipper);
            orders.add(order);
            adapter = new ListItemCommitAdapter(getApplicationContext(), shippers, orders,shop);
            recyclerView = (RecyclerView) ll.findViewById(R.id.list_commit);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            btnTuchoi = (Button) ll.findViewById(R.id.btnCloseWindows);
            adapter.notifyDataSetChanged();
            btnTuchoi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (shippers.size() == 0) {
                        windowManager.removeView(ll);
                        shippers.clear();
                        orders.clear();
                        isAttachedToWindow = false;
                    } else {
                        Toast.makeText(MapShopService.this, "Chưa xác nhận hết đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            windowManager.addView(ll, params);
            isAttachedToWindow = true;
        } else {
            shippers.add(shipper);
            orders.add(order);
            adapter.notifyDataSetChanged();
        }
    }

    private void listennerOrderStatus() {
        listenerOrderBystatus = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DetailOrder order = dataSnapshot.getValue(DetailOrder.class);
                listenerOrderisDeposit(order, 1);
                if (numorder == 0) {
                    processStartNotification();
                    numorder++;
                } else {
                    numorder++;
                    notification = new Notification.Builder(MapShopService.this)
                            .setContentTitle("ShipDN")
                            .setContentText(numorder + " Đơn hàng đang chờ shipper nhận").setSmallIcon(android.R.drawable.ic_dialog_alert)
                            .setContentIntent(pendingIntent).build();
                    notificationManager.notify(IDNOTIFICATION, notification);
                }
                Log.d("onChildAdded", " onChildAddedlistennerOrderStatus" + dataSnapshot.toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                DetailOrder order = dataSnapshot.getValue(DetailOrder.class);
                Log.d("onChildRemoved", " onChildRemovedlistennerOrderStatus" + dataSnapshot.toString());
                numorder--;
                if (numorder == 0) {
                    notificationManager.cancel(IDNOTIFICATION);

                } else {
                    notification = new Notification.Builder(MapShopService.this)
                            .setContentTitle("ShipDN")
                            .setContentText(numorder + " Đơn hàng đang chờ shipper nhận").setSmallIcon(android.R.drawable.ic_dialog_alert)
                            .setContentIntent(pendingIntent).build();
                    notificationManager.notify(IDNOTIFICATION, notification);
                }
                listenerOrderisDeposit(order, 2);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(var.onProgress).addChildEventListener(listenerOrderBystatus);
        mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(var.isDeposit).addChildEventListener(listenerOrderBystatus);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            user = firebaseAuth.getCurrentUser();
            stopSelf();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void processStartNotification() {
        String notificationTitle = "ShipDN";
        String notificationText = "1 Đơn hàng đang chờ";
        notification = new Notification.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText).setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentIntent(pendingIntent).build();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(IDNOTIFICATION, notification);
    }

    private void mapControl() {

        mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(var.isWAITTING).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                item++;
                Log.d("mapControl", " onChildAdded+" + item + dataSnapshot.toString());
                mData.child(var.CHILD_MAPSHOP).child(user.getUid()).child("avableOrder").setValue(true);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                item--;
                Log.d("mapControl", " onChildRemoved" + item + dataSnapshot.toString());
                if (item != 0) {

                } else {
                    mData.child(var.CHILD_MAPSHOP).child(user.getUid()).child("avableOrder").setValue(false);
                }
                ;
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
