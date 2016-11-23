package com.shipexpress.shipexpress.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Ship.ShipOrderService;
import com.shipexpress.shipexpress.Shop.Shop;
import com.shipexpress.shipexpress.Utility.GMapV2Direction;
import com.shipexpress.shipexpress.Utility.GMapV2DirectionAsyncTask;
import com.solidfire.gson.Gson;

import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Created by QuangCoi on 11/2/2016.
 */

public class ListOrderAdapter extends RecyclerView.Adapter<ListOrderAdapter.ListOrderViewHolder> {
    private Context context;
    private ArrayList<DetailOrder> list;
    private int CODE_TYPE;
    private DatabaseReference reference;

    public ListOrderAdapter(Context context, ArrayList<DetailOrder> list, int CODE_TYPE) {
        this.context = context;
        this.list = list;
        this.CODE_TYPE = CODE_TYPE;
        reference = FirebaseDatabase.getInstance().getReference().child(var.CHILD_SHOP);
    }

    @Override
    public ListOrderAdapter.ListOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_order_item_1, parent, false);
        ListOrderViewHolder holder = new ListOrderViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ListOrderAdapter.ListOrderViewHolder holder, final int position) {
        holder.txtNameOrder.setText("" + list.get(position).getName());
        //  holder.txtAddress.setText(""+list.get(position).get);
        holder.txtToAddress.setText("" + list.get(position).getToAddress().getNameAddress());
        holder.txtPrice.setText(list.get(position).getPrice() + "");
        holder.txtfreight.setText(list.get(position).getFreight() + "");
        switch (CODE_TYPE) {
            case var.INT_isCOMMIT:
                holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!ShipOrderService.onProgress) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                            dialog.setTitle("Bạn muốn nhận đơn hàng ").setPositiveButton("cancel", null).setNegativeButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DetailOrder detailOrder = list.get(position);
                                    Intent intent = new Intent(context, ShipOrderService.class).setFlags(ShipOrderService.ActionCommitOrder);
                                    intent.putExtra("detailOrder", new Gson().toJson(detailOrder));
                                    context.startService(intent);
                                    ShipOrderService.onProgress = true;
                                }
                            }).create().show();
                        } else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                            dialog.setTitle("Không thể nhận nhiều đơn hàng" + list.get(position).getKey()).setNegativeButton("ok", null).create().show();
                        }
                    }
                });
                break;
        }
        reference.child(list.get(position).getUIDShop()).child(var.CHILD_INFO).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Shop shop = dataSnapshot.getValue(Shop.class);
                Log.d("reference", dataSnapshot.toString() + "");
                Log.d("reference", shop.getAddressShop() + "");
                holder.txtAddress.setText(shop.getAddressShop() + "");
                reference.removeEventListener(this);
                try {
                    route(new LatLng(shop.getsLocation().getLat(), shop.getsLocation().getLng()), new LatLng(list.get(position).getToAddress().getsLocation().getLat(), list.get(position).getToAddress().getsLocation().getLng()), "sd", holder);
                } catch (Exception e) {

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
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListOrderViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rootLayout;
        TextView txtNameOrder, txtAddress, txtToAddress, txtPrice, txtfreight, txtLocation, txtDuration, txtDistance;
        Button btnAccept;

        public ListOrderViewHolder(View itemView) {
            super(itemView);
            rootLayout = (RelativeLayout) itemView.findViewById(R.id.rootLayout);
            txtNameOrder = (TextView) itemView.findViewById(R.id.txtNameOrder);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
            txtToAddress = (TextView) itemView.findViewById(R.id.txtToAddress);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            txtfreight = (TextView) itemView.findViewById(R.id.txtfreight);
         //   txtLocation = (TextView) itemView.findViewById(R.id.txtLocation);
            txtDuration = (TextView) itemView.findViewById(R.id.txtDuration);
            btnAccept = (Button) itemView.findViewById(R.id.btnAccept);
        }
    }

    private void route(LatLng sourcePosition, LatLng destPosition, String mode, final ListOrderAdapter.ListOrderViewHolder holder) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    GMapV2Direction md = new GMapV2Direction();
//                    ArrayList<LatLng> directionPoint = md.getDirection(doc);
//                    PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.BLUE).visible(true);
//                    for (int i = 0; i < directionPoint.size(); i++) {
//                        rectLine.add(directionPoint.get(i));
//                    }

//                    md.getDurationText(doc);
//
//                    md.getStartAddress(doc);
//                    md.getDistanceText(doc);

                    holder.txtDuration.setText(md.getDistanceText(doc));

                } catch (Exception e) {
                }
            }
        };
        new GMapV2DirectionAsyncTask(handler, sourcePosition, destPosition, GMapV2Direction.MODE_DRIVING).execute();
    }
}
