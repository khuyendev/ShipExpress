package com.shipexpress.shipexpress.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Ship.Shipper;
import com.shipexpress.shipexpress.Ship.statusShipper;
import com.shipexpress.shipexpress.Shop.Shop;
import com.shipexpress.shipexpress.Utility.GMapV2Direction;
import com.shipexpress.shipexpress.Utility.GMapV2DirectionAsyncTask;

import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Created by PhongPhan on 11/8/2016.
 */

public class ListItemCommitAdapter extends RecyclerView.Adapter<ListItemCommitAdapter.ListItemCommitViewHolder> {
    private Context context;
    private ArrayList<Shipper> shippers;
    private ArrayList<DetailOrder> orders;
    private FirebaseUser user;
    private DatabaseReference mData;
    private Shop shop;

    public ListItemCommitAdapter(Context context, ArrayList<Shipper> shippers, ArrayList<DetailOrder> orders, Shop shop) {
        this.context = context;
        this.shippers = shippers;
        this.orders = orders;
        this.shop = shop;
        user = FirebaseAuth.getInstance().getCurrentUser();
        mData = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public ListItemCommitAdapter.ListItemCommitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_commit, parent, false);
        ListItemCommitViewHolder holder = new ListItemCommitViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ListItemCommitAdapter.ListItemCommitViewHolder holder, final int position) {
        holder.textView.setText(shippers.get(position).getNameShipper());
        holder.imageView3.setImageResource(R.drawable.ic_distance);
        holder.txtNameOrderShipCommit.setText(orders.get(position).getName());
        getInfongulol(position, holder);
        holder.btnComit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_LISTORDER).child(orders.get(position).getKey()).child("statusOrder").setValue(var.isDeposit).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mData.child(var.CHILD_SHIP).child(orders.get(position).getUIDShip()).child(var.CHILD_LISTORDER).child(orders.get(position).getKeyship()).child("statusOrder").setValue(var.isDeposit).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Đã chấp nhận đơn hàng", Toast.LENGTH_SHORT).show();
                                shippers.remove(position);
                                orders.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        });
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_LISTORDER).child(orders.get(position).getKey()).child("statusOrder").setValue(var.isWAITTING).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mData.child(var.CHILD_SHIP).child(orders.get(position).getUIDShip()).child(var.CHILD_LISTORDER).child(orders.get(position).getKeyship()).child("statusOrder").setValue(var.isREJECTED).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                shippers.remove(position);
                                orders.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Đã từ chối đơn hàng", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return shippers.size();
    }

    public class ListItemCommitViewHolder extends RecyclerView.ViewHolder {
        TextView textView, txtNameOrderShipCommit, txtaddresscurrenr, txtdistan;
        Button btnComit, btnReject;
        ImageView imageView3;

        public ListItemCommitViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.txtNameShipperCommit);
            btnComit = (Button) itemView.findViewById(R.id.btnCommit);
            btnReject = (Button) itemView.findViewById(R.id.btnReject);
            txtNameOrderShipCommit = (TextView) itemView.findViewById(R.id.txtNameOrderShipCommit);
            imageView3 = (ImageView) itemView.findViewById(R.id.imageView3);
            txtaddresscurrenr = (TextView) itemView.findViewById(R.id.textView11);
            txtdistan = (TextView) itemView.findViewById(R.id.textView13);
        }
    }

    private void route(LatLng sourcePosition, LatLng destPosition, String mode, final ListItemCommitAdapter.ListItemCommitViewHolder holder) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    GMapV2Direction md = new GMapV2Direction();
                    holder.txtdistan.setText(md.getDistanceText(doc));
                    holder.txtaddresscurrenr.setText(md.getEndAddress(doc));
                } catch (Exception e) {
                }
            }
        };
        new GMapV2DirectionAsyncTask(handler, sourcePosition, destPosition, GMapV2Direction.MODE_DRIVING).execute();
    }

    private void getInfongulol(final int position, final ListItemCommitAdapter.ListItemCommitViewHolder holder) {
        mData.child(var.CHILD_MAPSHIP).child(orders.get(position).getUIDShip()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("getInfongulol: ", dataSnapshot.toString());
                statusShipper shipper = dataSnapshot.getValue(statusShipper.class);
                if (shop != null) {
                    route(new LatLng(shop.getsLocation().getLat(), shop.getsLocation().getLng()), new LatLng(shipper.getLocation().getLatCurrentLocation(), shipper.getLocation().getLngCurrentLocation()), "", holder);
                }
                mData.child(var.CHILD_MAPSHIP).child(orders.get(position).getUIDShip()).removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
