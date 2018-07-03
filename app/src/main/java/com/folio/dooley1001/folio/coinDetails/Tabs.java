package com.folio.dooley1001.folio.coinDetails;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.folio.dooley1001.folio.coinDetails.graphTab.GraphTabFragment;
import com.folio.dooley1001.folio.coinDetails.marketsTab.marketsTabFragment;

/**
 * extends fragmentPagerAdapter to return the details about a fragment in relation to
 * the tabs in the menue
 */
// https://developer.android.com/reference/android/support/v4/app/FragmentPagerAdapter
public class Tabs extends FragmentPagerAdapter {

    private String symbol;
    private String id;

    protected Tabs(FragmentManager fm, String symbol, String id) {
        super(fm);
        this.symbol = symbol;
        this.id = id;
    }
    @Override
    public Fragment getItem(int position) {
        // Created a new instand for a given coin based on the tab selection
        switch (position) {
            case 0:
                return GraphTabFragment.newInstance(this.symbol, this.id);
            case 1:
                return marketsTabFragment.newInstance(this.symbol);
        }
        return null;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            //our tab titles
            case 0:
                return "Chart";
            case 1:
                return "Markets";
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        // Total pages to show
        return 2;
    }

}
