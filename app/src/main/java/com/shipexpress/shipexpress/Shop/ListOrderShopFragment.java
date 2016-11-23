package com.shipexpress.shipexpress.Shop;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.shipexpress.shipexpress.Adapter.ListOrderShopAdapter;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListOrderShopFragment extends Fragment {
    //init
    private static final String CODE_TYPE_LIST = "param";
    private int CODE;
    //service
    private DatabaseReference mData;
    private FirebaseUser user;
    private ChildEventListener listener;
    private ArrayList<DetailOrder> orderArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListOrderShopAdapter adapter;
    FloatingActionButton fab;
    public ListOrderShopFragment() {
        // Required empty public constructor
    }

    public static ListOrderShopFragment newInstance(int codeType) {
        Bundle args = new Bundle();
        ListOrderShopFragment fragment = new ListOrderShopFragment();
        args.putInt(CODE_TYPE_LIST, codeType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (getArguments() != null) {
            CODE = getArguments().getInt(CODE_TYPE_LIST);
            adapter = new ListOrderShopAdapter(getContext(), orderArrayList,CODE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_list_order, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.listOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scroll Down
                    if (fab.isShown()) {
                        fab.hide();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!fab.isShown()) {
                        fab.show();
                    }
                }
            }
        });
        switch (CODE) {
            case var.INT_isWAITTING:
                getOrder(var.isWAITTING);
                break;
            case var.INT_isSUCCESS:
                getOrder(var.isSUCCESS);
                break;
            case var.INT_isCANCEL:
                getOrder(var.isCANCEL);
                break;
        }
    }

    private void getOrder(String status) {
        if (listener != null) {
            mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(status).removeEventListener(listener);
        }
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DetailOrder order = dataSnapshot.getValue(DetailOrder.class);
                order.setKey(dataSnapshot.getKey());
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
        mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_LISTORDER).orderByChild("statusOrder").equalTo(status).addChildEventListener(listener);
    }
}
