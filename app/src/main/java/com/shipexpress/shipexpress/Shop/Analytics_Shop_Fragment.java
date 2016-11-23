package com.shipexpress.shipexpress.Shop;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Order.DetailOrder;
import com.shipexpress.shipexpress.R;

import java.util.ArrayList;


public class Analytics_Shop_Fragment extends Fragment {
    PieChart chart;
    LinearLayout rl;
    ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
    float hoanthanh = 0f;
    float tuchoi = 0f;
    float huy = 0f;
    PieEntry ht, tc, h;
    int tong ,phiship;
    TextView txtThunhap,txtPhiship;
    //FirebaseTổng phí ship:
    DatabaseReference mData;
    public Analytics_Shop_Fragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = FirebaseDatabase.getInstance().getReference().child(var.CHILD_SHOP).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(var.CHILD_LISTORDER);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analytics__shop, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtThunhap  = (TextView) view.findViewById(R.id.txtThunhapp);
        txtPhiship  = (TextView) view.findViewById(R.id.txtPhiship);
        ht = new PieEntry(hoanthanh, "Hoàn thành");
        tc = new PieEntry(tuchoi, "Từ chối");
        h = new PieEntry(huy, "Hủy");
        chart = (PieChart) view.findViewById(R.id.chartshop);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        entries.add(ht);
        entries.add(tc);
        entries.add(h);
        setDataColor();
        mData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DetailOrder order = dataSnapshot.getValue(DetailOrder.class);
                if (order.getStatusOrder().equals(var.isSUCCESS)) {
                    tong += tong + order.getPrice();
                    phiship += phiship + order.getFreight();
                    txtThunhap.setText("Tổng thu nhập: "+tong+ " VND");
                    txtPhiship.setText("Tổng phí ship: "+phiship+ " VND");
                    hoanthanh = hoanthanh + 1f;
                    ht = new PieEntry(hoanthanh, "Hoàn thành");
                    entries.set(0, ht);
                    chart.invalidate();
                    chart.animateX(500);
                    setDataColor();
                }
                if (order.getStatusOrder().equals(var.isCANCEL)) {
                    tuchoi = tuchoi + 1f;
                    tc = new PieEntry(tuchoi, "Từ chối");
                    entries.set(1, tc);
                    chart.invalidate();
                    chart.animateX(500);
                    setDataColor();
                }
                if (order.getStatusOrder().equals(var.isREJECTED)) {
                    huy = huy + 1f;
                    h = new PieEntry(huy, "Từ chối");
                    entries.set(2, h);
                    chart.invalidate();
                    chart.animateX(500);
                    setDataColor();
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
    private void setDataColor() {
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);

        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);
    }
}
