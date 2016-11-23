package com.shipexpress.shipexpress.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Ship.statusShipper;
import com.shipexpress.shipexpress.Shop.Shop;
import com.shipexpress.shipexpress.Utility.GMapV2Direction;
import com.shipexpress.shipexpress.Utility.GMapV2DirectionAsyncTask;

import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Created by QuangCoi on 11/7/2016.
 */

public class ListShipperAdapter extends RecyclerView.Adapter<ListShipperAdapter.ListShipperViewHolder> {
    ArrayList<statusShipper> list;
    Context context;
    Shop shop;

    public ListShipperAdapter(ArrayList<statusShipper> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    @Override
    public ListShipperAdapter.ListShipperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_shipper, parent, false);
        ListShipperViewHolder holder = new ListShipperViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ListShipperAdapter.ListShipperViewHolder holder, final int position) {
        holder.txtTen.setText(list.get(position).getNameShipper());
        if (list.get(position).isAvailable()) {
            holder.textView6.setText("Sẵn sàng");
        } else {
            holder.textView6.setText("Đang giao hàng");
        }
        if (shop != null) {
            route(new LatLng(shop.getsLocation().getLat(), shop.getsLocation().getLng()), new LatLng(list.get(position).getLocation().getLatCurrentLocation(), list.get(position).getLocation().getLngCurrentLocation()), "sds", holder);
        }
        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + list.get(position).getPhoneNumber()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListShipperViewHolder extends RecyclerView.ViewHolder {
        TextView txtTen, txtDuration, textView5, textView6;
        Button btnCall;
        public ListShipperViewHolder(View itemView) {
            super(itemView);
            txtTen = (TextView) itemView.findViewById(R.id.txtTenspnb);
            txtDuration = (TextView) itemView.findViewById(R.id.txtDuration);
            textView5 = (TextView) itemView.findViewById(R.id.textView5);
            textView6 = (TextView) itemView.findViewById(R.id.textView6);
            btnCall = (Button) itemView.findViewById(R.id.btnCall);
        }
    }

    private void route(LatLng sourcePosition, LatLng destPosition, String mode, final ListShipperAdapter.ListShipperViewHolder holder) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    Document doc = (Document) msg.obj;
                    GMapV2Direction md = new GMapV2Direction();
                    holder.txtDuration.setText(md.getDistanceText(doc));
                    holder.textView5.setText(md.getEndAddress(doc));
                } catch (Exception e) {
                }
            }
        };
        new GMapV2DirectionAsyncTask(handler, sourcePosition, destPosition, GMapV2Direction.MODE_DRIVING).execute();
    }
}