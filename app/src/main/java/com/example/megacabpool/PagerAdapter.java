package com.example.megacabpool;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
    private int noOfTabs;

    public PagerAdapter(@NonNull FragmentManager fm, int noOfTabs) {
        super(fm);
                this.noOfTabs=noOfTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                return new chat();
            case 1:
                return new directory();
                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
