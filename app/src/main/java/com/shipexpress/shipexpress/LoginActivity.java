package com.shipexpress.shipexpress;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shipexpress.shipexpress.DialogView.ChooseTypeRegister;
import com.shipexpress.shipexpress.FirebaseHelper.var;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private Button btnSignup, btnLogin, btnReset;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    //check


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                ChooseTypeRegister dialog = new ChooseTypeRegister();
                dialog.setRetainInstance(true);
                dialog.show(fm, "ChooseTypeUserDialog");
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(inputEmail.getText())){
                   inputEmail.setError("Chưa nhập email");
                }
                if(TextUtils.isEmpty(inputPassword.getText())){
                    inputPassword.setError("chưa nhập password");
                }else
                loginWithEmail(inputEmail.getText().toString(), inputPassword.getText().toString());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public void loginWithEmail(String username, String password) {
        progressDialog = ProgressDialog.show(LoginActivity.this,"Đăng nhập","Đang đăng nhập...");
        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.setMessage(getString(R.string.loading));
                    user = mAuth.getCurrentUser();
                    databaseReference.child(var.CHILD_USER).child(user.getUid()).child(var.CHILD_TYPE_USER).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("onAuthStateChanged", "-" + "LoginActivity");
                            progressDialog.hide();
                            switch (Integer.valueOf(dataSnapshot.getValue().toString())) {
                                case var.TYPE_SHOP:
                                    startActivity(new Intent(LoginActivity.this, MainShopActivity.class));
                                    finish();
                                    break;
                                case var.TYPE_SHIP:
                                    startActivity(new Intent(LoginActivity.this, MainShipActivity.class));
                                    finish();
                                    break;
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    progressDialog.hide();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                    dialog.setTitle("Lỗi đăng nhập").setMessage(task.getException().getMessage()).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }
            }
        });
    }
}

