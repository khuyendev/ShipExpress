package com.shipexpress.shipexpress.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by QuangCoi on 10/28/2016.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> listfragment = new ArrayList<>();
    private List<String> listtitle = new ArrayList<>();
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return listfragment.get(position);
    }

    @Override
    public int getCount() {
        return listfragment.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return listtitle.get(position);
    }
    public void addFragment(Fragment fragment,String title){
        listfragment.add(fragment);
        listtitle.add(title);
    }
}
