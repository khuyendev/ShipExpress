package com.shipexpress.shipexpress;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Ship.Analytics_Ship_Fragment;
import com.shipexpress.shipexpress.Ship.InforShipFragment;
import com.shipexpress.shipexpress.Ship.ShipOrderService;
import com.shipexpress.shipexpress.Ship.Shipper;

public class MainShipActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    //private FloatingActionButton fab;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    FirebaseAuth.AuthStateListener authStateListener;

    private TextView nickname, emailp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ship);
        reference = FirebaseDatabase.getInstance().getReference();
        initView();
        initFragment(initHomeFragment.newInstance(var.TYPE_SHIP), "Trang chủ");
        requestLocation();

        auth = FirebaseAuth.getInstance();
        reference.child(var.CHILD_SHIP).child(auth.getCurrentUser().getUid()).child(var.CHILD_INFO).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               // Log.d("nick", dataSnapshot.toString());
                Shipper shipper = dataSnapshot.getValue(Shipper.class);
                nickname.setText(shipper.getNameShipper());
                emailp.setText(shipper.getEmailShipper());
                reference.child(var.CHILD_SHIP).child(auth.getCurrentUser().getUid()).child(var.CHILD_INFO).removeEventListener(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_ship, menu);
        final SwitchCompat switchCompat = (SwitchCompat) menu.findItem(R.id.show_secure).getActionView().findViewById(R.id.actionbar_service_toggle);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                        if (b) {
                                                            switchCompat.setText("ONLINE");
                                                            startService(new Intent(MainShipActivity.this, ShipOrderService.class));
                                                        } else {
                                                            switchCompat.setText("OFFLINE");
                                                            startService(new Intent(MainShipActivity.this, ShipOrderService.class).setFlags(ShipOrderService.ActionOffline));
                                                        }
                                                    }
                                                }
        );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            initFragment(initHomeFragment.newInstance(var.TYPE_SHIP), "Trang chủ");
        } else if (id == R.id.nav_order) {
            initFragment(initOrderFragment.newInstance(var.TYPE_SHIP), "Đơn hàng");
        } else if (id == R.id.nav_statistics) {
            initFragment(new Analytics_Ship_Fragment(), "Thống kê");
        } else if (id == R.id.nav_info) {
            initFragment(new InforShipFragment(), "Thông tin");
        } else if (id == R.id.nav_signout) {
            Log.e("nav_signout", "nav_signout");
            startService(new Intent(MainShipActivity.this, ShipOrderService.class).setFlags(ShipOrderService.ActionDSignouut));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        nickname = (TextView) header.findViewById(R.id.nickname);
        emailp = (TextView) header.findViewById(R.id.emailp);

    }

    private void initFragment(Fragment fragment, String titleActionBar) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.initFragment, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //ft.addToBackStack(backStateName);
            ft.commit();
        }
        getSupportActionBar().setTitle(titleActionBar);
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    var.ACCESS_FINE_LOCATION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case var.ACCESS_FINE_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getApplicationContext(), "Phải đồng ý truy cập vị trí", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            var.ACCESS_FINE_LOCATION_CODE);
                }
                return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setAuthStateListener();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener);
    }

    private void setAuthStateListener() {
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainShipActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }
}
