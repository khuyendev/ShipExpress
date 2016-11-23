package com.shipexpress.shipexpress.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Utility.GMapV2Direction;
import com.shipexpress.shipexpress.Utility.GMapV2DirectionAsyncTask;

import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Created by PhongPhan on 11/14/2016.
 */

public class ListOrderShipAdapter extends RecyclerView.Adapter<ListOrderShipAdapter.ListOrderShipViewHolder> {
    Context context;
    ArrayList<DetailOrder> orders;

    public ListOrderShipAdapter(Context context, ArrayList<DetailOrder> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public ListOrderShipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_order_item_ship, parent, false);
        ListOrderShipViewHolder holder = new ListOrderShipViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ListOrderShipViewHolder holder, int position) {
        holder.nameOrder.setText(orders.get(position).getName());
        holder.txtToAddress.setText("" + orders.get(position).getToAddress().getNameAddress());
        holder.txtPrice.setText(orders.get(position).getPrice() + "");
        holder.txtfreight.setText(orders.get(position).getFreight() + "");
        try {
            route(new LatLng(orders.get(position).getsLocation().getLat(), orders.get(position).getsLocation().getLng()), new LatLng(orders.get(position).getToAddress().getsLocation().getLat(), orders.get(position).getToAddress().getsLocation().getLng()), "sd", holder);
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ListOrderShipViewHolder extends RecyclerView.ViewHolder {
        TextView nameOrder,txtAddress, txtToAddress, txtPrice, txtfreight, txtDuration;
        //RelativeLayout layout;

        public ListOrderShipViewHolder(View itemView) {
            super(itemView);
            nameOrder = (TextView) itemView.findViewById(R.id.txtNameOrderShip);
            txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
            txtToAddress = (TextView) itemView.findViewById(R.id.txtToAddress);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            txtfreight = (TextView) itemView.findViewById(R.id.txtfreight);
            txtDuration = (TextView) itemView.findViewById(R.id.txtDuration);
          //  layout = (RelativeLayout) itemView.findViewById(R.id.rootLayout);
        }
    }
    private void route(LatLng sourcePosition, LatLng destPosition, String mode, final ListOrderShipViewHolder  holder) {
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
