package com.shipexpress.shipexpress;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shipexpress.shipexpress.Adapter.ViewPagerAdapter;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Ship.HomeShipFragment;
import com.shipexpress.shipexpress.Ship.ListOrderNearbyFragment;
import com.shipexpress.shipexpress.Shop.HomeShopFragment;
import com.shipexpress.shipexpress.Shop.ListShipNearbyFragment;
import com.shipexpress.shipexpress.Shop.statusShop;

import java.util.ArrayList;


public class initHomeFragment extends Fragment {
    //init
    private static final String CODE_TYPE_LIST = "param";
    private int CODE;
    //view
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private FloatingActionButton fabs;
    //service
    ArrayList<statusShop> s = new ArrayList<>();

    public initHomeFragment() {
        // Required empty public constructor
    }

    public static initHomeFragment newInstance(int codeType) {
        Bundle args = new Bundle();
        initHomeFragment fragment = new initHomeFragment();
        args.putInt(CODE_TYPE_LIST, codeType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            CODE = getArguments().getInt(CODE_TYPE_LIST);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_init_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setViewPager();

    }

    private void initView() {
        viewPager = (ViewPager) getView().findViewById(R.id.viewPager);
        tabLayout = (TabLayout) getView().findViewById(R.id.tabs);
        if(CODE==var.TYPE_SHOP){
        fabs = (FloatingActionButton)getActivity().findViewById(R.id.fab);
        fabs.hide();}
    }

    private void setViewPager() {
        pagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.addOnPageChangeListener(new PageChangeListener());
        viewPager.setOffscreenPageLimit(2);
        switch (CODE) {
            case var.TYPE_SHOP:
                pagerAdapter.addFragment(HomeShopFragment.newInstance(), "BẢN ĐỒ");
                pagerAdapter.addFragment(ListShipNearbyFragment.newInstance(), "SHIPPER");
                break;
            case var.TYPE_SHIP:
                pagerAdapter.addFragment(HomeShipFragment.newInstance(), "BẢN ĐỒ");
                pagerAdapter.addFragment(ListOrderNearbyFragment.newInstance(), "ĐƠN HÀNG");
                break;
        }
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(CODE==var.TYPE_SHOP){
            switch (position){
                case 0:
                    fabs.hide();
                    break;
                case 1:
                    fabs.show();
                    break;
            }}else {
//                fabs.hide();
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if(CODE==var.TYPE_SHOP){
        if (!fabs.isShown()){
            fabs.show();
        }}
    }
}
