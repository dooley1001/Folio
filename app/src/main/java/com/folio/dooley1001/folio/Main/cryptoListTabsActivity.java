package com.folio.dooley1001.folio.Main;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.folio.dooley1001.folio.R;
import com.folio.dooley1001.folio.models.easyrest.CMCCoinParcelable;

/*
*
* Main activity for the entire application
 */

public class cryptoListTabsActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, watchListFragment.AllCoinsListUpdater, cryptoListFragment.FavoritesListUpdater{

    private cryptoListTabAdapter mSectionsPagerAdapter;
    public ViewPager mViewPager;

    private Toolbar mToolbar;
    boolean doubleBackToExitPressedOnce = false;
    public static String IMAGE_URL_FORMAT = "https://s2.coinmarketcap.com/static/img/coins/32x32/%s.png";
    public final static String DAY = "24h";
    public final static String WEEK = "7d";
    public final static String HOUR = "1h";
    public final static String SORT_SETTING = "sort_setting";
    public AppCompatActivity context;


    //ON CREATE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list_tabs); // set view to XML
        context = this;
        mToolbar = findViewById(R.id.toolbar_currency_list);
        setSupportActionBar(mToolbar);

        TabLayout tabLayout = findViewById(R.id.currency_list_tabs);
        mViewPager = findViewById(R.id.currency_list_tabs_container);

        mSectionsPagerAdapter = new cryptoListTabAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2); //2 tabs
        mViewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        Fragment fragment = mSectionsPagerAdapter.getFragment(position);
        if (fragment != null) {
            fragment.onResume();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    //when back button is  double tapped -- exit application
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Double tap to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1000);
    }
    //
    public void removeFavorite(CMCCoinParcelable coin) {                             //Postition is the tab targeted
        watchListFragment frag = (watchListFragment) mSectionsPagerAdapter.getFragment(1);
        if (frag != null) {
            frag.removeFavorite(coin);
        }
    }
    public void addFavorite(CMCCoinParcelable coin) {
        watchListFragment frag = (watchListFragment) mSectionsPagerAdapter.getFragment(1);
        if (frag != null) {
            frag.addFavorite(coin);
        }
    }
    public void allCoinsModifyFavorites(CMCCoinParcelable coin) {
        cryptoListFragment frag = (cryptoListFragment) mSectionsPagerAdapter.getFragment(0);
        if (frag != null) {
            frag.getAdapter().notifyDataSetChanged();
        }
    }
    public void performFavsSort() {
        watchListFragment frag = (watchListFragment) mSectionsPagerAdapter.getFragment(1);
        if (frag != null) {
            frag.performFavsSort();
        }
    }

    public void performAllCoinsSort() {
        cryptoListFragment frag = (cryptoListFragment) mSectionsPagerAdapter.getFragment(0);
        if (frag != null) {
            frag.performAllCoinsSort();
        }
    }

}
