package com.folio.dooley1001.folio.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.materialdialogs.MaterialDialog;
import com.folio.dooley1001.folio.CustomClickListener;
import com.folio.dooley1001.folio.R;
import com.folio.dooley1001.folio.models.easyrest.CMCCoinParcelable;
import com.folio.dooley1001.folio.models.easyrest.CoinMarketcapsQuickSearch;
import com.folio.dooley1001.folio.coinDetails.cryptoDetailsTabbedAct;
import com.folio.dooley1001.folio.coinDetails.graphTab.GraphTabFragment;
import com.folio.dooley1001.folio.models.easyrest.watchListStructures;
import com.folio.dooley1001.folio.api.CoinMarketCapService;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.folio.dooley1001.folio.dataBase.DatabaseHelper;
import java.util.ArrayList;
import java.util.HashMap;
import com.grizzly.rest.Model.afterTaskFailure;
import java.util.Iterator;
import static com.folio.dooley1001.folio.coinDetails.graphTab.GraphTabFragment.SHAREDPREF_SETTINGS;
import static com.folio.dooley1001.folio.Main.cryptoListTabsActivity.SORT_SETTING;
import static android.content.Context.MODE_PRIVATE;
import static com.folio.dooley1001.folio.SortingClass.sortList;

public class watchListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseHelper db;
    private RecyclerView currencyRecyclerView;
    private watchListAdapter adapter;
    private ArrayList<CMCCoinParcelable> currencyItemFavsList = new ArrayList<>();
    private AllCoinsListUpdater favsUpdateCallback;
    private AppCompatActivity mContext;
    private HashMap<String, Integer> slugToIDMap = new HashMap<>();
    private SharedPreferences sharedPreferences;

    public interface AllCoinsListUpdater {
        void allCoinsModifyFavorites(CMCCoinParcelable coin);
        void performAllCoinsSort();
    }
    public watchListFragment() {
    }
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static watchListFragment newInstance() {
        return new watchListFragment();
    }
    public void performFavsSort() {
        int sortType = sharedPreferences.getInt(SORT_SETTING, 1);
        sortList(adapter.getCurrencyList(), sortType);
        adapter.notifyDataSetChanged();
    }
    public void getCurrencyList() {
        swipeRefreshLayout.setRefreshing(true);
        CoinMarketCapService.getAllCoins(getActivity(), new afterTaskCompletion<CMCCoinParcelable[]>() {
            @Override
            public void onTaskCompleted(CMCCoinParcelable[] cmcCoinList) {
                Parcelable recyclerViewState;
                recyclerViewState = currencyRecyclerView.getLayoutManager().onSaveInstanceState();
                currencyItemFavsList.clear();
                watchListStructures favs = db.getFavorites();
                try {
                    for (CMCCoinParcelable coin : cmcCoinList) {
                        if (favs.watchlistMap.get(coin.getSymbol()) != null) {
                            currencyItemFavsList.add(coin);
                        }
                    }
                    getQuickSearch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
                currencyRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            }
        }, new afterTaskFailure() {
            @Override
            public void onTaskFailed(Object o, Exception e) {
                Log.e("ERROR", "Server Error: " + e.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        }, true);
    }

    public void getQuickSearch() {
        CoinMarketCapService.getCMCQuickSearch(mContext, new afterTaskCompletion<CoinMarketcapsQuickSearch[]>() {
            @Override
            public void onTaskCompleted(CoinMarketcapsQuickSearch[] quickSearchNodeList) {
                slugToIDMap = new HashMap<>();
                Parcelable recyclerViewState;
                recyclerViewState = currencyRecyclerView.getLayoutManager().onSaveInstanceState();
                for (CoinMarketcapsQuickSearch node : quickSearchNodeList) {
                    slugToIDMap.put(node.getSlug(), node.getId());
                }
                for (CMCCoinParcelable coin : currencyItemFavsList) {
                    coin.setQuickSearchID(slugToIDMap.get(coin.getId()));
                }
                adapter.setCurrencyList(currencyItemFavsList);
                int sortType = sharedPreferences.getInt(SORT_SETTING, 1);
                sortList(adapter.getCurrencyList(), sortType);
                adapter.notifyDataSetChanged();
                favsUpdateCallback.performAllCoinsSort();
                currencyRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new afterTaskFailure() {
            @Override
            public void onTaskFailed(Object o, Exception e) {
                Log.e("ERROR", "Server Error: " + e.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        }, true);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        favsUpdateCallback = (AllCoinsListUpdater) activity;
        mContext = (AppCompatActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_watch_list, container, false);
        setHasOptionsMenu(true);
        this.db = DatabaseHelper.getInstance(getActivity());
        sharedPreferences = getContext().getSharedPreferences(SHAREDPREF_SETTINGS, MODE_PRIVATE);
        currencyRecyclerView = rootView.findViewById(R.id.currency_favs_recycler_view);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(getActivity()).build();
        currencyRecyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        currencyRecyclerView.setLayoutManager(llm);
        currencyItemFavsList = new ArrayList<>();

        adapter = new watchListAdapter(favsUpdateCallback, currencyItemFavsList, db, (AppCompatActivity) getActivity(), new CustomClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(getActivity(), cryptoDetailsTabbedAct.class);
                intent.putExtra(GraphTabFragment.CRYPTO_SYMBOL, currencyItemFavsList.get(position).getSymbol());
                intent.putExtra(GraphTabFragment.ARG_ID, currencyItemFavsList.get(position).getId());
                intent.putExtra(GraphTabFragment.COIN_OBJECT, currencyItemFavsList.get(position));
                getActivity().startActivity(intent);
            }
        });
        currencyRecyclerView.setAdapter(adapter);
        swipeRefreshLayout = rootView.findViewById(R.id.currency_favs_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getCurrencyList();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rootView != null) { // Hide keyboard when we enter this tab
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }
        mContext.getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.saved_coin_tab_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.sort_favs_button:
                int sortType = sharedPreferences.getInt(SORT_SETTING, 1);
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.sort_by)
                        .items(R.array.sort_options)
                        .buttonRippleColor(getContext().getResources().getColor(R.color.colorPrimary))
                        .itemsCallbackSingleChoice(sortType, new MaterialDialog.ListCallbackSingleChoice() {

                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                sortList(adapter.getCurrencyList(), which);
                                adapter.notifyDataSetChanged();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt(SORT_SETTING, which);
                                editor.apply();
                                favsUpdateCallback.performAllCoinsSort();
                                Toast toast = Toast.makeText(getContext(), "Sorting by: " + text, Toast.LENGTH_SHORT);
                                toast.show();
                                return true;
                            }
                        })
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        getCurrencyList();
    }

    //remove and add coin to watchlist
    public void removeFavorite(CMCCoinParcelable coin) {
        ArrayList<CMCCoinParcelable> currentFavs = adapter.getCurrencyList();
        Iterator<CMCCoinParcelable> currFavsIterator = currentFavs.iterator();

        while (currFavsIterator.hasNext()) {
            CMCCoinParcelable currCoin = currFavsIterator.next();
            if (currCoin.getId().equals(coin.getId())) {
                currFavsIterator.remove();
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }
    public void addFavorite(CMCCoinParcelable coin) {
        currencyItemFavsList.add(0, coin);
        adapter.notifyDataSetChanged();
    }
}
