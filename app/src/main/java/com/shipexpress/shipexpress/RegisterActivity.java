package com.shipexpress.shipexpress;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.shipexpress.shipexpress.FirebaseHelper.var;

public class RegisterActivity extends AppCompatActivity {
    Toolbar toolbar;

    int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }
    private void initView(){
        Intent intent = getIntent();
        type = intent.getExtras().getInt(var.CHILD_USER);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    var.ACCESS_FINE_LOCATION_CODE);
        }else {
            switch (type){
                case var.TYPE_SHIP:
                    initFragment(new RegisterShipFragment(),"Đăng ký ship");
                    break;
                case var.TYPE_SHOP:
                    initFragment(new RegisterShopFragment(),"Đăng ký shop");
                    break;
                default:
                    getSupportActionBar().setTitle("Lỗi không xác định");
                    break;
            }
        }
    }
    private void initFragment(Fragment fragment, String titleActionBar) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.initTypeRegisterFragment, fragment, fragmentTag);
            ft.commit();
        }
        getSupportActionBar().setTitle(titleActionBar);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case var.ACCESS_FINE_LOCATION_CODE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    switch (type){
                        case var.TYPE_SHIP:
                            initFragment(new RegisterShipFragment(),"Đăng ký ship");
                            break;
                        case var.TYPE_SHOP:
                            initFragment(new RegisterShopFragment(),"Đăng ký shop");
                            break;
                        default:
                            getSupportActionBar().setTitle("Lỗi không xác định");
                            break;
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Phải đồng ý truy cập vị trí", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            var.ACCESS_FINE_LOCATION_CODE);
                }
                return;
            }
        }
    }
}
