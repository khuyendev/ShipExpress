package com.shipexpress.shipexpress.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Utility.GMapV2Direction;
import com.shipexpress.shipexpress.Utility.GMapV2DirectionAsyncTask;

import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Created by PhongPhan on 11/14/2016.
 */

public class ListOrderShopAdapter extends RecyclerView.Adapter<ListOrderShopAdapter.ListOrderShopViewHolder> {
    private Context context;
    private ArrayList<DetailOrder> list;
    private int CODE;
    private DatabaseReference mData;
    private FirebaseUser user;

    public ListOrderShopAdapter(Context context, ArrayList<DetailOrder> list, int c) {
        this.context = context;
        this.list = list;
        this.CODE = c;
        mData = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }


    @Override
    public ListOrderShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_order_item_shop, parent, false);
        ListOrderShopViewHolder holder = new ListOrderShopViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ListOrderShopViewHolder holder, final int position) {
        holder.txtNameOrder.setText(list.get(position).getName());
        holder.txtToAddress.setText("" + list.get(position).getToAddress().getNameAddress());
        holder.txtPrice.setText(list.get(position).getPrice() + "");
        holder.txtfreight.setText(list.get(position).getFreight() + "");
        try {
            route(new LatLng(list.get(position).getsLocation().getLat(), list.get(position).getsLocation().getLng()), new LatLng(list.get(position).getToAddress().getsLocation().getLat(), list.get(position).getToAddress().getsLocation().getLng()), "sd", holder);
        } catch (Exception e) {

        }
        if (CODE == var.INT_isWAITTING) {
            holder.btnRejectShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Bạn muốn hủy đơn hàng này" ).setPositiveButton("cancel", null).setNegativeButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mData.child(var.CHILD_SHOP).child(user.getUid()).child(var.CHILD_LISTORDER).child(list.get(position).getKey()).child("statusOrder").setValue(var.isCANCEL).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                                }
                            });
                            list.remove(position);
                            notifyDataSetChanged();
                        }
                    }).create().show();
                }
            });
        }else {
            holder.btnRejectShop.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListOrderShopViewHolder extends RecyclerView.ViewHolder {
        TextView txtNameOrder, txtAddress, txtToAddress, txtPrice, txtfreight, txtDuration, txtDistance;
        RelativeLayout layout;
        Button btnRejectShop;

        public ListOrderShopViewHolder(View itemView) {
            super(itemView);
            layout = (RelativeLayout) itemView.findViewById(R.id.rootLayout);
            txtNameOrder = (TextView) itemView.findViewById(R.id.txtNameOrderShop);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
            txtToAddress = (TextView) itemView.findViewById(R.id.txtToAddress);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            txtfreight = (TextView) itemView.findViewById(R.id.txtfreight);
            txtDuration = (TextView) itemView.findViewById(R.id.txtDuration);
            btnRejectShop = (Button) itemView.findViewById(R.id.btnRejectShop);
        }
    }
    private void route(LatLng sourcePosition, LatLng destPosition, String mode, final ListOrderShopAdapter.ListOrderShopViewHolder holder) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    GMapV2Direction md = new GMapV2Direction();
                    holder.txtDuration.setText(md.getDistanceText(doc));
                    holder.txtAddress.setText(md.getStartAddress(doc));
                } catch (Exception e) {
                }
            }
        };
        new GMapV2DirectionAsyncTask(handler, sourcePosition, destPosition, GMapV2Direction.MODE_DRIVING).execute();
    }
}
