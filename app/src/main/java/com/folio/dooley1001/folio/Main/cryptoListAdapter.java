package com.folio.dooley1001.folio.Main;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.folio.dooley1001.folio.CustomClickListener;
import com.folio.dooley1001.folio.R;
import com.folio.dooley1001.folio.models.easyrest.CMCCoinParcelable;
import com.folio.dooley1001.folio.models.easyrest.watchListStructures;
import com.folio.dooley1001.folio.dataBase.DatabaseHelper;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/*
*
* Main adapter class for our main activty showwing the list of cryptos
 */


public class cryptoListAdapter extends RecyclerView.Adapter<cryptoListAdapter.ViewHolder> {

    private ArrayList<CMCCoinParcelable> currencyList;
    private cryptoListAdapter.ViewHolder viewHolder;
    private String priceStringResource;
    private String mktCapStringResource;
    private String volumeStringResource;
    private String pctChangeNotAvailableStringResource;
    private String negativePercentStringResource;
    private String positivePercentStringResource;
    private String symbolAndFullNameStringResource;
    private int positiveGreenColor;
    private int negativeRedColor;

    private CustomClickListener rowListener;
    private WeakReference<AppCompatActivity> contextRef;
    private WeakReference<DatabaseHelper> dbRef;
    private WeakReference<cryptoListFragment.FavoritesListUpdater> favsUpdateCallbackRef;

    public cryptoListAdapter(cryptoListFragment.FavoritesListUpdater favsUpdateCallback, ArrayList<CMCCoinParcelable> currencyList, DatabaseHelper db, AppCompatActivity context, CustomClickListener listener) {
        // let values = to local variables

        this.currencyList = currencyList;
        this.contextRef = new WeakReference<>(context);
        this.rowListener = listener;
        this.dbRef = new WeakReference<>(db);
        this.mktCapStringResource = this.contextRef.get().getString(R.string.mkt_cap_format);
        this.volumeStringResource = this.contextRef.get().getString(R.string.volume_format);
        this.negativePercentStringResource = this.contextRef.get().getString(R.string.negative_pct_change_format);
        this.positivePercentStringResource = this.contextRef.get().getString(R.string.positive_pct_change_format);
        this.priceStringResource = this.contextRef.get().getString(R.string.unrounded_price_format);
        this.pctChangeNotAvailableStringResource = this.contextRef.get().getString(R.string.not_available_pct_change_text_with_time);
        this.symbolAndFullNameStringResource = this.contextRef.get().getString(R.string.nameAndSymbol);
        this.negativeRedColor = this.contextRef.get().getResources().getColor(R.color.percentNegativeRed);
        this.positiveGreenColor = this.contextRef.get().getResources().getColor(R.color.percentPositiveGreen);
        this.favsUpdateCallbackRef = new WeakReference<>(favsUpdateCallback);
    }
    //Set a click listener for our watchlist button
    public void setWatchListClickListener(final cryptoListAdapter.ViewHolder holder, final int position) {
        holder.favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchListStructures favs = dbRef.get().getFavorites();
                CMCCoinParcelable item = currencyList.get(position);
                if (favs.watchlistMap.get(item.getSymbol()) == null) { // Coin is not a favorite yet. Add it.
                    favs.watchlistMap.put(item.getSymbol(), item.getSymbol());
                    favs.watchList.add(item.getSymbol());
                    holder.favButton.setFavorite(true, true);
                    favsUpdateCallbackRef.get().addFavorite(item);
                } else { // Coin is already a favorite, remove it
                    favs.watchlistMap.remove(item.getSymbol());
                    favs.watchList.remove(item.getSymbol());
                    holder.favButton.setFavorite(false, true);
                    favsUpdateCallbackRef.get().removeFavorite(item);
                }
                dbRef.get().saveCoinFavorites(favs);
            }
        });
    }
    @Override
    public void onBindViewHolder(final cryptoListAdapter.ViewHolder holder, final int position) {

        CMCCoinParcelable item = currencyList.get(position);

        cryptoListAdapterUtils.setPercentChangeTextView(holder.oneHourChangeTextView, item.getPercent_change_1h(),
                cryptoListTabsActivity.HOUR, negativePercentStringResource, positivePercentStringResource, negativeRedColor, positiveGreenColor, pctChangeNotAvailableStringResource);
        cryptoListAdapterUtils.setPercentChangeTextView(holder.dayChangeTextView, item.getPercent_change_24h(),
                cryptoListTabsActivity.DAY, negativePercentStringResource, positivePercentStringResource, negativeRedColor, positiveGreenColor, pctChangeNotAvailableStringResource);
        cryptoListAdapterUtils.setPercentChangeTextView(holder.weekChangeTextView, item.getPercent_change_7d(),
                cryptoListTabsActivity.WEEK, negativePercentStringResource, positivePercentStringResource, negativeRedColor, positiveGreenColor, pctChangeNotAvailableStringResource);

        if (item.getMarket_cap_usd() == null) {
            holder.currencyListMarketcapTextView.setText("N/A");
        } else {
            holder.currencyListMarketcapTextView.setText(String.format(mktCapStringResource, Double.parseDouble(item.getMarket_cap_usd())));
        }
        if (item.getRank() == null) {
            holder.rankTextView.setText("N/A");
        } else {
            holder.rankTextView.setText(item.getRank());
        }
        if (item.getVolume_usd_24h() == null) {
            holder.currencyListVolumeTextView.setText("N/A");
        } else {
            holder.currencyListVolumeTextView.setText(String.format(volumeStringResource, Double.parseDouble(item.getVolume_usd_24h())));
        }
        if (item.getPrice_usd() == null) {
            holder.currencyListCurrPriceTextView.setText("N/A");
        } else {
            holder.currencyListCurrPriceTextView.setText(String.format(priceStringResource, item.getPrice_usd()));
        }
        holder.currencyListfullNameTextView.setText(String.format(this.symbolAndFullNameStringResource, item.getName(), item.getSymbol()));

        //COIN IMAGE
        if (item.getQuickSearchID() != -1) {
            Picasso.with(contextRef.get()).load(String.format(cryptoListTabsActivity.IMAGE_URL_FORMAT, Integer.toString(item.getQuickSearchID()))).into(holder.currencyListCoinImageView);
        }

        watchListStructures favs = this.dbRef.get().getFavorites();
        boolean isFav = favs.watchlistMap.get(item.getSymbol()) != null;
        holder.favButton.setFavorite(isFav, false);
        setWatchListClickListener(holder, position);
    }

    @Override
    public cryptoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_list_obj, parent, false);
        viewHolder = new cryptoListAdapter.ViewHolder(itemLayoutView, rowListener);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView currencyListfullNameTextView;
        private TextView oneHourChangeTextView;
        private TextView dayChangeTextView;
        private TextView weekChangeTextView;
        private TextView rankTextView;
        private TextView currencyListCurrPriceTextView;
        private TextView currencyListVolumeTextView;
        private TextView currencyListMarketcapTextView;
        private ImageView currencyListCoinImageView;
        private MaterialFavoriteButton favButton;
        private CustomClickListener listener;

        private ViewHolder(View itemLayoutView, CustomClickListener listener)
        {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            rankTextView = itemLayoutView.findViewById(R.id.rankTextView);
            currencyListfullNameTextView = itemLayoutView.findViewById(R.id.currencyListfullNameTextView);
            currencyListCurrPriceTextView = itemLayoutView.findViewById(R.id.currencyListCurrPriceTextView);
            currencyListCoinImageView = itemLayoutView.findViewById(R.id.currencyListCoinImageView);
            currencyListVolumeTextView = itemLayoutView.findViewById(R.id.currencyListVolumeTextView);
            currencyListMarketcapTextView = itemLayoutView.findViewById(R.id.currencyListMarketcapTextView);
            favButton = itemLayoutView.findViewById(R.id.currencyListFavButton);
            oneHourChangeTextView = itemLayoutView.findViewById(R.id.oneHourChangeTextView);
            dayChangeTextView = itemLayoutView.findViewById(R.id.dayChangeTextView);
            weekChangeTextView = itemLayoutView.findViewById(R.id.weekChangeTextView);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition(), v);
        }
    }

    public int getItemCount() {
        return currencyList.size();
    }

    public void setCurrencyList(ArrayList<CMCCoinParcelable> newCurrencyList) {
        this.currencyList = newCurrencyList;
        notifyDataSetChanged();
    }

    public ArrayList<CMCCoinParcelable> getCurrencyList() {
        return currencyList;
    }

}
