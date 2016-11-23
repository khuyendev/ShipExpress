package com.shipexpress.shipexpress;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Ship.Shipper;
import com.shipexpress.shipexpress.Utility.sLocation;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterShipFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private EditText editEmail, editPassword, editRepassword, editHoTen, editSoDienThoai, editDiaChi, editChiTiet;
    private Button btnRegister;
    private ProgressDialog progressDialog;

    public RegisterShipFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ship_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    public void initView() {
        editEmail = (EditText)getView(). findViewById(R.id.editEmail);
        editPassword = (EditText)getView().  findViewById(R.id.editPassword);
        editRepassword = (EditText)getView().  findViewById(R.id.re_password);
        editHoTen = (EditText)getView().  findViewById(R.id.editTencCuaHang);
        editSoDienThoai = (EditText) getView(). findViewById(R.id.editSDT);
        editDiaChi = (EditText) getView(). findViewById(R.id.editDiachi);
        editChiTiet = (EditText) getView(). findViewById(R.id.editChiTiet);
        btnRegister = (Button)getView().  findViewById(R.id.sign_up_button);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRegister();
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
        } else if (TextUtils.isEmpty(editHoTen.getText())) {
            editHoTen.setError("Chưa nhập họ tên");
        } else if (TextUtils.isEmpty(editSoDienThoai.getText())) {
            editSoDienThoai.setError("Chưa nhập số điện thoại");
        } else if (TextUtils.isEmpty(editDiaChi.getText())) {
            editDiaChi.setError("Chưa nhập địa chỉ");
        } else if (TextUtils.isEmpty(editChiTiet.getText())) {
            editChiTiet.setError("Chưa nhập chi tiết");
        } else if (editPassword.getText().length() < 5) {
            Toast.makeText(getContext(), "Mật khẩu phải trên 5 ký tự", Toast.LENGTH_SHORT).show();
            editPassword.setError("Mật khẩu phải trên 5 ký tự");
        } else if (!pw.equals(rpw)) {
            editRepassword.setError("Mật khẩu không giống nhau");
            Toast.makeText(getContext(), "Mật khẩu không giống nhau", Toast.LENGTH_SHORT).show();
        } else {
            createUserWithEmail(editEmail.getText().toString(),editPassword.getText().toString(),new Shipper(editEmail.getText().toString(), editHoTen.getText().toString(), editDiaChi.getText().toString(), editSoDienThoai.getText().toString(), editChiTiet.getText().toString(), new sLocation()) );
            Toast.makeText(getContext(), "ok", Toast.LENGTH_SHORT).show();
        }


    }
    public void createUserWithEmail(final String email, final String password ,final Shipper shipper) {
        progressDialog = ProgressDialog.show(getContext(), "Đăng ký...", "Đang đăng ký");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), R.string.auth_success,
                                    Toast.LENGTH_SHORT).show();
                            completeRegisterShip(shipper);
                        } else {
                            progressDialog.hide();
                            Toast.makeText(getContext(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void completeRegisterShip(Shipper shipper) {
        user = mAuth.getCurrentUser();
        progressDialog.setMessage("Đang tạo thông tin...");
        //addTYPEUSERShop
        databaseReference.child(var.CHILD_USER).child(user.getUid()).child(var.CHILD_TYPE_USER).setValue(var.TYPE_SHIP);
        //addINFOSHOP
        databaseReference.child(var.CHILD_SHIP).child(user.getUid()).child(var.CHILD_INFO).push().setValue(shipper).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.hide();
                startActivity(new Intent(getContext(), MainShipActivity.class));
            }
        });
    }

}
