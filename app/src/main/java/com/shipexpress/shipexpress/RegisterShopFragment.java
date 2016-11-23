package com.shipexpress.shipexpress;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Shop.Shop;
import com.shipexpress.shipexpress.Shop.statusShop;
import com.shipexpress.shipexpress.Utility.MapScrollViewSupport;
import com.shipexpress.shipexpress.Utility.sLocation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.shipexpress.shipexpress.R.id.editAddressReceived;

/**
 * A placeholder fragment containing a simple view.
 */
public class RegisterShopFragment extends Fragment implements OnMapReadyCallback {
    ScrollView scrollView;
    private GoogleMap mMap;
    Geocoder geocoder;
    List<Address> addresses;
    String address;
    Marker marker = null;
    private EditText editEmail, editPassword, editRepassword, editTenCuaHang, editSoDienThoai, editDiaChi, editChiTiet;
    private CheckBox checkBox;
    public Button btnRegister;
    private ProgressDialog progressDialog;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    public RegisterShopFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        MapScrollViewSupport mapFragment = (MapScrollViewSupport) getChildFragmentManager().findFragmentById(R.id.mapRegisterShop);
        mapFragment.setListener(new MapScrollViewSupport.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
        mapFragment.getMapAsync(this);
    }

    private void initView() {
        scrollView = (ScrollView) getView().findViewById(R.id.viewRegisterShop);
        editEmail = (EditText) getView().findViewById(R.id.editEmail);
        editPassword = (EditText) getView().findViewById(R.id.editPassword);
        editRepassword = (EditText) getView().findViewById(R.id.re_password);
        editTenCuaHang = (EditText) getView().findViewById(R.id.editTencCuaHang);
        editSoDienThoai = (EditText) getView().findViewById(R.id.editSDT);
        editDiaChi = (EditText) getView().findViewById(R.id.editDiachi);
        checkBox = (CheckBox) getView().findViewById(R.id.checkCommitLocation);
        checkBox.setEnabled(false);
        editChiTiet = (EditText) getView().findViewById(R.id.editChiTiet);
        btnRegister = (Button) getView().findViewById(R.id.sign_up_button);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegister();
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17f));
                    mMap.getUiSettings().setAllGesturesEnabled(false);
                    mMap.getUiSettings().setZoomControlsEnabled(false);
                    mMap.setMyLocationEnabled(false);
                    setMapClickable(false);
                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15f));
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    setMapClickable(true);
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        setMapClickable(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Location a = mMap.getMyLocation();
                LatLng ll = new LatLng(a.getLatitude(), a.getLongitude());
                try {
                    addresses = geocoder.getFromLocation(a.getLatitude(), a.getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        address = addresses.get(0).getAddressLine(0);
                        mMap.clear();
                        marker = mMap.addMarker(new MarkerOptions().position(ll).title("Vị trí bản đồ").snippet(address).flat(true));
                        marker.showInfoWindow();
                        editDiaChi.setText(""+addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(3));
                        checkBox.setEnabled(true);
                    }
                } catch (IOException e) {
                    mMap.clear();
                    marker = mMap.addMarker(new MarkerOptions().position(ll).title("Vị trí bản đồ").flat(true));
                    marker.showInfoWindow();
                    checkBox.setEnabled(true);
                }
                return false;
            }
        });
    }
    public void submitRegister() {
        String pw, rpw;
        pw = editPassword.getText().toString();
        rpw = editRepassword.getText().toString();
        if (TextUtils.isEmpty(editEmail.getText())) {
            editEmail.setFocusable(true);
            editEmail.setError("Chưa nhập email");
        } else if (TextUtils.isEmpty(editPassword.getText())) {
            editPassword.setError("Chưa nhập mật khẩu");
        } else if (TextUtils.isEmpty(editRepassword.getText())) {
            editRepassword.setError("Chưa xác nhận mật khẩu");
        } else if (TextUtils.isEmpty(editTenCuaHang.getText())) {
            editTenCuaHang.setError("Chưa nhập tên cửa hàng");
        } else if (TextUtils.isEmpty(editSoDienThoai.getText())) {
            editSoDienThoai.setError("Chưa nhập số điện thoại");
        } else if (TextUtils.isEmpty(editDiaChi.getText())) {
            editDiaChi.setError("Chưa nhập địa chỉ");
        } else if (!checkBox.isChecked()) {
            checkBox.setError("Chưa xác nhận vị trí");
        } else if (TextUtils.isEmpty(editChiTiet.getText())) {
            editChiTiet.setError("Chưa nhập chi tiết");
        } else if (editPassword.getText().length() < 5) {
            Toast.makeText(getContext(), "Mật khẩu phải trên 5 ký tự", Toast.LENGTH_SHORT).show();
            editPassword.setError("Mật khẩu phải trên 5 ký tự");
        } else if (!pw.equals(rpw)) {
            editRepassword.setError("Mật khẩu không giống nhau");
            Toast.makeText(getContext(), "Mật khẩu không giống nhau", Toast.LENGTH_SHORT).show();
        } else if (marker == null) {
            checkBox.setError("Chưa xác nhận vị trí");
        } else {
            registerShopWithEmail(editEmail.getText().toString(), editPassword.getText().toString(), new Shop(editEmail.getText().toString(), editTenCuaHang.getText().toString(), editDiaChi.getText().toString(), editSoDienThoai.getText().toString(), editChiTiet.getText().toString(), new sLocation(marker.getPosition().latitude, marker.getPosition().longitude, 0, 0)));
            // Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
        }
    }
    public void registerShopWithEmail(String email, String password, final Shop shop) {
        progressDialog = ProgressDialog.show(getContext(), "Đăng ký...", "Đang đăng ký");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            completeRegisterShop(shop);
                        } else {
                            progressDialog.hide();
                            Toast.makeText(getContext(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void setMapClickable(boolean clickable) {
        if (clickable) {
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    try {
                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        if (addresses != null && addresses.size() > 0) {
                            address = addresses.get(0).getAddressLine(0);
                            mMap.clear();
                            editDiaChi.setText(""+addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(3));
                            marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Vị trí bản đồ").snippet(address).flat(true));
                            marker.showInfoWindow();
                            checkBox.setEnabled(true);
                        }
                    } catch (IOException e) {
                        mMap.clear();
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Vị trí bản đồ").flat(true));
                        marker.showInfoWindow();
                        checkBox.setEnabled(true);
                    }

                }
            });
        } else {
            mMap.setOnMapClickListener(null);
        }
    }
    private void completeRegisterShop(Shop shop) {
        user = mAuth.getCurrentUser();
        progressDialog.setMessage("Đang tạo thông tin...");
        //addTYPEUSERShop
        databaseReference.child(var.CHILD_USER).child(user.getUid()).child(var.CHILD_TYPE_USER).setValue(var.TYPE_SHOP);
        //addINFOSHOP
        databaseReference.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_INFO).push().setValue(shop);
        //addMap
        databaseReference.child(var.CHILD_MAPSHOP).child(user.getUid()).setValue(new statusShop(shop.getNameShop(), user.getUid(), shop.getsLocation(), false, null)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.hide();
                    startActivity(new Intent(getContext(), MainShopActivity.class));
                }
            }
        });
    }
}
