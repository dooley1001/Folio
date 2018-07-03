package com.folio.dooley1001.folio.coinDetails.marketsTab;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.folio.dooley1001.folio.CustomClickListener;
import com.folio.dooley1001.folio.R;
import com.folio.dooley1001.folio.models.easyrest.MarketNode;
import com.folio.dooley1001.folio.dataBase.CurrencyFormat;
import java.lang.ref.WeakReference;
import java.util.List;

/*
*
* Class for the displaying marketsTab data in a recyclerView
*
*
 */
public class marketsTabAdapter extends RecyclerView.Adapter<marketsTabAdapter.ViewHolder> {

    private CustomClickListener listener;
    private marketsTabAdapter.ViewHolder viewHolder;
    private List<MarketNode> markets;
    private WeakReference<AppCompatActivity> contextRef;

    String negativePctFormat;
    String positivPctFormat;
    private int positiveGreenColor;
    private int negativeRedColor;
    private CurrencyFormat currencyFormatter;

    public marketsTabAdapter(List<MarketNode> markets, AppCompatActivity context, CustomClickListener listener) {
        this.markets = markets;
        this.currencyFormatter = CurrencyFormat.getInstance(context);
        this.contextRef = new WeakReference<>(context);
        this.listener = listener;
        this.negativePctFormat = context.getString(R.string.negative_pct_format);
        this.positivPctFormat = context.getString(R.string.positive_pct_format);
        this.negativeRedColor = this.contextRef.get().getResources().getColor(R.color.percentNegativeRed);
        this.positiveGreenColor = this.contextRef.get().getResources().getColor(R.color.percentPositiveGreen);
    }
    //viewholder for our precentage changes
    @Override
    public void onBindViewHolder(final marketsTabAdapter.ViewHolder holder, final int position) {
        MarketNode node = markets.get(position);
        holder.exchangeNameTextView.setText(node.getMarket());
        //if postive turn green else red
        if (node.getChangePct24h() >= 0) {
            holder.changeTextView.setTextColor(positiveGreenColor);
            holder.changeTextView.setText(String.format(positivPctFormat, node.getChangePct24h()));
        } else {
            holder.changeTextView.setTextColor(negativeRedColor);
            holder.changeTextView.setText(String.format(negativePctFormat, node.getChangePct24h()));
        }
        //format price to the currency selected and display price in textview
        String formattedPrice = this.currencyFormatter.format(node.getPrice(), node.getToSymbol());
        holder.priceTextView.setText(formattedPrice);
    }

    @Override
    public marketsTabAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.markets_tab_list, parent, false);
        viewHolder = new marketsTabAdapter.ViewHolder(itemLayoutView, listener);
        return viewHolder;
    }

    public int getItemCount() {
        return markets.size();
    }


    //view holder for the full list with a click listener
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CustomClickListener listener;
        private TextView exchangeNameTextView;
        private TextView changeTextView;
        private TextView priceTextView;

        private ViewHolder(View itemLayoutView, CustomClickListener listener)
        {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            //textViews for each market
            this.exchangeNameTextView = itemLayoutView.findViewById(R.id.exchangeNameTextView);
            changeTextView = itemLayoutView.findViewById(R.id.precentageChangeTextView);
            this.priceTextView = itemLayoutView.findViewById(R.id.priceTextView);
            this.listener = listener;
        }
        //when clicked get position
        @Override
        public void onClick(View v) {
            this.listener.onItemClick(getAdapterPosition(), v);
        }
    }
    public void setMarketsList(List<MarketNode> newMarketsList) {
        this.markets.clear();
        this.markets.addAll(newMarketsList);
    }
}
