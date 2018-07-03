package com.folio.dooley1001.folio.Main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the taBs
 */
public class cryptoListTabAdapter extends FragmentPagerAdapter {

    private SparseArray<String> mFragmentTags;
    private FragmentManager mFragmentManager;


    protected cryptoListTabAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags = new SparseArray<>();
    }

    public Fragment getFragment(int position) {
        Fragment fragment = null;
        String tag = mFragmentTags.get(position);
        if (tag != null) {
            fragment = mFragmentManager.findFragmentByTag(tag);
        }
        return fragment;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            String tag = fragment.getTag();
            mFragmentTags.put(position, tag);
        }
        return object;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0://new instance based on case
                return cryptoListFragment.newInstance();
            case 1:
                return watchListFragment.newInstance();
        }
        return null;
    }
    @Override
    public int getCount() {
        // Total pages to show
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                // Tab titles
                return "Top 100 Coins";
            case 1:
                return "WatchList";
            default:
                return null;
        }
    }
}
