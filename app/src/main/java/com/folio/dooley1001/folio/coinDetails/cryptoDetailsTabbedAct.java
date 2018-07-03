package com.folio.dooley1001.folio.coinDetails;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.folio.dooley1001.folio.R;
import com.folio.dooley1001.folio.coinDetails.graphTab.GraphTabFragment;

/**********
* Main activity for when a coin is selected known as our "details" activity
***********/
public class cryptoDetailsTabbedAct extends AppCompatActivity {


    private Tabs mSectionsPagerAdapter;
    public CustomView mViewPager;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_currency_list_details_tabs); //set vie to XML
        mToolbar = findViewById(R.id.toolbar_currency_details);
        setSupportActionBar(mToolbar);

        //set variables to the current coins Symbol and id
        String symbol = getIntent().getStringExtra(GraphTabFragment.CRYPTO_SYMBOL);
        String id = getIntent().getStringExtra(GraphTabFragment.ARG_ID);
        mViewPager = findViewById(R.id.currencyTabsViewPager);

        mSectionsPagerAdapter = new Tabs(getSupportFragmentManager(), symbol, id);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2); //2 tabs

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        getSupportActionBar().setTitle(symbol);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.currency_tabs_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

}
