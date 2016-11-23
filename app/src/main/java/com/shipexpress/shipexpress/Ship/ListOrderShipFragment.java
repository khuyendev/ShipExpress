package com.shipexpress.shipexpress.Ship;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shipexpress.shipexpress.Adapter.ListOrderShipAdapter;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.R;

import java.util.ArrayList;


public class ListOrderShipFragment extends Fragment {
    //init
    private static final String CODE_TYPE_LIST = "param";
    private int CODE;
    private DatabaseReference mData;
    private FirebaseUser user;
    private ChildEventListener listener;
    private ArrayList<DetailOrder> orderArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListOrderShipAdapter adapter;

    public ListOrderShipFragment() {
        // Required empty public constructor
    }

    public static ListOrderShipFragment newInstance(int codeType) {
        Bundle args = new Bundle();
        ListOrderShipFragment fragment = new ListOrderShipFragment();
        args.putInt(CODE_TYPE_LIST, codeType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        adapter = new ListOrderShipAdapter(getContext(), orderArrayList);
        if (getArguments() != null) {
            CODE = getArguments().getInt(CODE_TYPE_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ship_list_order, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.listOrder);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        switch (CODE) {
            case var.INT_isSUCCESS:
                getOrder(var.isSUCCESS);
                break;
            case var.INT_isCANCEL:
                getOrder(var.isCANCEL);
                break;
            case var.INT_isREJECTED:
                getOrder(var.isREJECTED);
                break;
        }
    }

    private void getOrder(String status) {
        if (listener != null) {
            mData.child(var.CHILD_SHIP).child(user.getUid()).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(status).removeEventListener(listener);

        }
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DetailOrder order = dataSnapshot.getValue(DetailOrder.class);
                orderArrayList.add(order);
                adapter.notifyDataSetChanged();
                Log.e("asdsad", dataSnapshot.getValue(DetailOrder.class).getName() + "");
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
        mData.child(var.CHILD_SHIP).child(user.getUid()).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(status).addChildEventListener(listener);
    }

}
