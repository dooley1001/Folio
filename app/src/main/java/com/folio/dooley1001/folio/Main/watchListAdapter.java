package com.folio.dooley1001.folio.Main;

import java.util.ArrayList;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.folio.dooley1001.folio.R;
import com.folio.dooley1001.folio.models.easyrest.CMCCoinParcelable;
import com.folio.dooley1001.folio.models.easyrest.watchListStructures;
import com.folio.dooley1001.folio.dataBase.DatabaseHelper;
import android.widget.ImageView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import com.folio.dooley1001.folio.CustomClickListener;
import com.squareup.picasso.Picasso;
import java.lang.ref.WeakReference;


/*
*
* Main Adapter class for our Watchlist tab - similar setup as our main activities adapter
*
 */

public class watchListAdapter extends RecyclerView.Adapter<watchListAdapter.ViewHolder> {
    private ArrayList<CMCCoinParcelable> currencyList;
    private watchListAdapter.ViewHolder viewHolder;
    private String pctChangeNotAvailableStringResource;
    private String negativePercentStringResource;
    private String positivePercentStringResource;
    private String priceStringResource;
    private String mktCapStringResource;
    private String volumeStringResource;
    private String symbolAndFullNameStringResource;
    private int positiveGreenColor;
    private int negativeRedColor;

    private CustomClickListener rowListener;
    private WeakReference<AppCompatActivity> contextRef;
    private WeakReference<DatabaseHelper> dbRef;
    private WeakReference<watchListFragment.AllCoinsListUpdater> favsUpdateCallbackRef;
    private watchListAdapter me;

    public watchListAdapter(watchListFragment.AllCoinsListUpdater favsUpdateCallback, ArrayList<CMCCoinParcelable> currencyList, DatabaseHelper db, AppCompatActivity context, CustomClickListener listener) {

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
        this.me = this;

    }


    public void setFavoriteButtonClickListener(final watchListAdapter.ViewHolder holder, final int position) {
        //click listener for the trashbutton
        holder.trashButton.setOnClickListener(new View.OnClickListener() {
            CMCCoinParcelable item = currencyList.get(position);
            @Override
            public void onClick(View v) {
                //dialog for when the button is clicked
                new AlertDialog.Builder(contextRef.get())
                        .setMessage("Are you sure you want to remove " + item.getSymbol() + "from your watchList ?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            //click listerner for the dialog YES/NO
                            public void onClick(DialogInterface dialog, int whichButton) {
                                watchListStructures favs = dbRef.get().getFavorites();
                                favs.watchlistMap.remove(item.getSymbol());
                                favs.watchList.remove(item.getSymbol());
                                dbRef.get().saveCoinFavorites(favs);
                                currencyList.remove(position);
                                notifyDataSetChanged();
                                favsUpdateCallbackRef.get().allCoinsModifyFavorites(item);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }
    @Override
    public void onBindViewHolder(final watchListAdapter.ViewHolder holder, final int position) {
        CMCCoinParcelable item = currencyList.get(position);
        //ChangeTextViews based on the precentage change
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
            holder.rankTextViewFavs.setText("N/A");
        } else {
            holder.rankTextViewFavs.setText(item.getRank());
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

        if (item.getQuickSearchID() != -1) {
            Picasso.with(contextRef.get()).load(String.format(cryptoListTabsActivity.IMAGE_URL_FORMAT, Integer.toString(item.getQuickSearchID()))).into(holder.currencyListCoinImageView);
        }
        setFavoriteButtonClickListener(holder, position);
    }

    @Override
    public watchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_watch_list_obj, parent, false);
        viewHolder = new watchListAdapter.ViewHolder(itemLayoutView, rowListener);
        return viewHolder;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //viewholder for our coin object XML layout

        private TextView currencyListfullNameTextView;
        private TextView oneHourChangeTextView;
        private TextView dayChangeTextView;
        private TextView weekChangeTextView;
        private TextView currencyListCurrPriceTextView;
        private TextView currencyListVolumeTextView;
        private TextView rankTextViewFavs;
        private TextView currencyListMarketcapTextView;
        private ImageView currencyListCoinImageView;
        protected ImageView trashButton;
        private CustomClickListener listener;

        private ViewHolder(View itemLayoutView, CustomClickListener listener)
        {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            rankTextViewFavs = itemLayoutView.findViewById(R.id.rankTextViewWatchlist);
            currencyListVolumeTextView = itemLayoutView.findViewById(R.id.currencyListVolumeTextView);
            currencyListMarketcapTextView = itemLayoutView.findViewById(R.id.currencyListMarketcapTextView);
            currencyListfullNameTextView = itemLayoutView.findViewById(R.id.currencyListfullNameTextView);
            currencyListCoinImageView = itemLayoutView.findViewById(R.id.currencyListCoinImageView);
            currencyListCurrPriceTextView = itemLayoutView.findViewById(R.id.currencyListCurrPriceTextView);
            dayChangeTextView = itemLayoutView.findViewById(R.id.dayChangeTextView);
            weekChangeTextView = itemLayoutView.findViewById(R.id.weekChangeTextView);
            trashButton = itemLayoutView.findViewById(R.id.favsCurrencyListTrashImage);
            oneHourChangeTextView = itemLayoutView.findViewById(R.id.oneHourChangeTextView);
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
    }

    public ArrayList<CMCCoinParcelable> getCurrencyList() {
        return currencyList;
    }
}
