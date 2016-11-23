package com.shipexpress.shipexpress.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.R;
import com.shipexpress.shipexpress.Ship.Shipper;
import com.shipexpress.shipexpress.Shop.ItemOnprogressFragment;

import java.util.ArrayList;

/**
 * Created by PhongPhan on 11/11/2016.
 */

public class ListOrderProgressShopAdapter extends RecyclerView.Adapter<ListOrderProgressShopAdapter.ListOrderProgressShopViewHolder> {
    AppCompatActivity context;
    ArrayList<DetailOrder> orders;
    ArrayList<Shipper> shippers;

    public ListOrderProgressShopAdapter(AppCompatActivity context, ArrayList<DetailOrder> orders, ArrayList<Shipper> shippers) {
        this.context = context;
        this.orders = orders;
        this.shippers = shippers;
    }

    @Override
    public ListOrderProgressShopAdapter.ListOrderProgressShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_onprogress_shop, parent, false);
        ListOrderProgressShopViewHolder holder = new ListOrderProgressShopViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ListOrderProgressShopAdapter.ListOrderProgressShopViewHolder holder, final int position) {
        holder.txtNameOD.setText(orders.get(position).getName());
        holder.textView8.setText(shippers.get(position).getNameShipper());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFragment(ItemOnprogressFragment.newInstance(orders.get(position),shippers.get(position)),orders.get(position).getName());
            }
        });
    }

    private void initFragment(Fragment fragment, String titleActionBar) {
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;
        FragmentManager manager = context.getSupportFragmentManager();
       //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.initFragmentL, fragment, fragmentTag);
            //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        context.getSupportActionBar().setTitle(titleActionBar);
        context.onBackPressed();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ListOrderProgressShopViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layout;
        TextView txtNameOD,textView8;

        public ListOrderProgressShopViewHolder(View itemView) {
            super(itemView);
            layout = (RelativeLayout) itemView.findViewById(R.id.rootRLT);
            txtNameOD = (TextView) itemView.findViewById(R.id.txtNameOD);
            textView8 = (TextView) itemView.findViewById(R.id.textView8);
        }
    }
}
