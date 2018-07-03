package com.folio.dooley1001.folio.coinDetails.marketsTab;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.folio.dooley1001.folio.CustomClickListener;
import com.folio.dooley1001.folio.R;
import com.folio.dooley1001.folio.models.easyrest.MarketNode;
import com.folio.dooley1001.folio.models.easyrest.MarketsResponse;
import com.folio.dooley1001.folio.models.easyrest.TradingPair;
import com.folio.dooley1001.folio.models.easyrest.TradingPairNode;
import com.folio.dooley1001.folio.api.CryptoCompareService;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import static com.folio.dooley1001.folio.coinDetails.graphTab.GraphTabFragment.CRYPTO_SYMBOL;


public class marketsTabFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private String symbol;
    private Spinner spinner;
    private String tsymbol = null;
    private String fsymbol = null;
    private List<String> pairs;
    private RecyclerView marketsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private marketsTabAdapter adapter;
    private List<MarketNode> markets;
    private Context mContext;
    private TextView noMarketsText;
    private Button marketsSourceButton;
    private View spinnerDivider;



    public static final String BASE_CRYPTOCOMPARE_OVERVIEW_STRING = "https://www.cryptocompare.com/coins/%s/overview/";
    //url for when market is selected - currently only set to binance
    public static final String BASE_MARKET_URL_CHROME_URL = "https://www.cryptocompare.com/exchanges/binance/overview/%s";

    public marketsTabFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static marketsTabFragment newInstance(String symbol) {

        marketsTabFragment fragment = new marketsTabFragment();

       // Constructs a new empty Bundle that uses a specific ClassLoader for instantiating Parcelable and Serializable objects
        Bundle args = new Bundle();
        //set bundle to current coin
        args.putString(CRYPTO_SYMBOL, symbol);
        //return new instance of this fragment
        fragment.setArguments(args);
        return fragment;
    }

    public void getPairMarket() {

        // make the currency pairs network call and add the response to the market list

        CryptoCompareService.getPairsMarket(getActivity(), tsymbol, fsymbol, new afterTaskCompletion<MarketsResponse>() {
            @Override
            public void onTaskCompleted(MarketsResponse marketsResponse) {
                setVisible();
                markets.clear();
                markets.addAll(marketsResponse.getData().getMarketsList());
                adapter.setMarketsList(marketsResponse.getData().getMarketsList());
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new afterTaskFailure() {
            @Override
            public void onTaskFailed(Object o, Exception e) {
                Log.e("Err.....", "ooops there was an error :P " + e.getMessage());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public void showServerError()
    {
        noMarketsText.setEnabled(true);
        marketsSourceButton.setEnabled(true);
        noMarketsText.setVisibility(View.VISIBLE);
        marketsSourceButton.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
        spinner.setEnabled(false);
        spinnerDivider.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    public void setVisible() {
        marketsRecyclerView.setEnabled(true);
        marketsRecyclerView.setVisibility(View.VISIBLE);
        spinner.setEnabled(true);
        spinner.setVisibility(View.VISIBLE);
        noMarketsText.setVisibility(View.GONE);
        marketsSourceButton.setVisibility(View.GONE);
        noMarketsText.setEnabled(false);
        marketsSourceButton.setEnabled(true);
        spinnerDivider.setVisibility(View.VISIBLE);
    }

    public void getTopPairs() {
        swipeRefreshLayout.setRefreshing(true);

        //CryptoCompareService.getTopPairs  - https://min-api.cryptocompare.com/data/top/pairs?fsym=%s&limit=20"
        CryptoCompareService.getTopPairs(getActivity(), symbol, new afterTaskCompletion<TradingPair>() {
            @Override
            public void onTaskCompleted(TradingPair tradingPair) {
                pairs.clear();
                for (TradingPairNode node : tradingPair.getData()) {
                    pairs.add(node.getFromSymbol() + "/" + node.getToSymbol());
                }
                if (pairs.isEmpty()) {
                    showServerError();
                    return;
                }
                setVisible();
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, pairs);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                spinner.setAdapter(spinnerArrayAdapter);

                if (fsymbol == null || tsymbol == null) {

                    String symbols[] = pairs.get(0).split("/");
                    fsymbol = symbols[1];
                    tsymbol = symbols[0];
                }
                getPairMarket();
            }
        }, new afterTaskFailure() {
            @Override
            public void onTaskFailed(Object o, Exception e) {
                Log.e("ERROR", "Server Error: " + e.getMessage());
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_markets, container, false);
        //set the symbol string to the coin symbol
        symbol = getArguments().getString(CRYPTO_SYMBOL);
        spinnerDivider = rootView.findViewById(R.id.marketSpinnerDivider);

        marketsSourceButton = rootView.findViewById(R.id.marketsSourceButton);
        marketsSourceButton.setPaintFlags(marketsSourceButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //Click listener for when the market is source button is clicked on by the user
        marketsSourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(getActivity(), Uri.parse(String.format(BASE_CRYPTOCOMPARE_OVERVIEW_STRING, symbol)));
            }
        });

        noMarketsText = rootView.findViewById(R.id.noMarketsTextView);
        marketsRecyclerView = rootView.findViewById(R.id.markets_recycler_view);

        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(getActivity()).build();
        marketsRecyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        marketsRecyclerView.setLayoutManager(llm);
        pairs = new ArrayList<>();
        markets = new ArrayList<>();


        //add our custom click listener to the adapater
        adapter = new marketsTabAdapter(markets, (AppCompatActivity) mContext, new CustomClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                String marketStr = markets.get(position).getMarket();
                //launch web intent
                customTabsIntent.launchUrl(getActivity(), Uri.parse(String.format(BASE_MARKET_URL_CHROME_URL, marketStr)));
            }
        });
        marketsRecyclerView.setAdapter(adapter);
        spinner = rootView.findViewById(R.id.top_pairs_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                swipeRefreshLayout.setRefreshing(true);
                String symbols[] = pairs.get(position).split("/");
                fsymbol = symbols[1];
                tsymbol = symbols[0];
                getPairMarket();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Swipe down to refresh
        swipeRefreshLayout = rootView.findViewById(R.id.markets_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getTopPairs();
            }
        });
        return rootView;
    }
    //called when user swipes to refresh
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getPairMarket();
    }
}
