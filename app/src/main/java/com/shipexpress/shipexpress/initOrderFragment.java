package com.shipexpress.shipexpress;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shipexpress.shipexpress.Adapter.ViewPagerAdapter;
import com.shipexpress.shipexpress.FirebaseHelper.var;
import com.shipexpress.shipexpress.Ship.ListOrderShipFragment;
import com.shipexpress.shipexpress.Shop.ListOrderShopFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class initOrderFragment extends Fragment {
    //init
    private static final String CODE_TYPE_LIST = "param";
    private int CODE;
    //view
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    public initOrderFragment() {
        // Required empty public constructor
    }

    public static initOrderFragment newInstance(int codeType) {
        Bundle args = new Bundle();
        initOrderFragment fragment = new initOrderFragment();
        args.putInt(CODE_TYPE_LIST, codeType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            CODE = getArguments().getInt(CODE_TYPE_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_init_order, container, false);
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
    }

    private void setViewPager() {
        pagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.addOnPageChangeListener(new PageChangeListener());
        viewPager.setOffscreenPageLimit(2);
        switch (CODE) {
            case var.TYPE_SHOP:
                pagerAdapter.addFragment(new ListOrderShopFragment().newInstance(var.INT_isWAITTING), "CHỜ");
                pagerAdapter.addFragment(new ListOrderShopFragment().newInstance(var.INT_isSUCCESS), "HOÀN THÀNH");
                pagerAdapter.addFragment(new ListOrderShopFragment().newInstance(var.INT_isCANCEL), "HỦY");
                break;
            case var.TYPE_SHIP:
                pagerAdapter.addFragment(new ListOrderShipFragment().newInstance(var.INT_isSUCCESS), "Hoàn thành");
                pagerAdapter.addFragment(new ListOrderShipFragment().newInstance(var.INT_isREJECTED), "từ chối");
                pagerAdapter.addFragment(new ListOrderShipFragment().newInstance(var.INT_isCANCEL), "hủy");
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
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
