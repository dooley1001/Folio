package com.folio.dooley1001.folio.api;

import android.content.Context;

import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;
import com.folio.dooley1001.folio.models.easyrest.CoinList;
import com.folio.dooley1001.folio.models.easyrest.MarketsResponse;
import com.folio.dooley1001.folio.models.easyrest.TradingPair;
import org.springframework.http.HttpMethod;
import com.grizzly.rest.GenericRestCall;


public class CryptoCompareService {

    public static final String ALL_COINS_LIST_URL = "https://min-api.cryptocompare.com/data/all/coinlist";
    public static final String TOP_PAIRS_URL = "https://min-api.cryptocompare.com/data/top/pairs?fsym=%s&limit=20";
    public static final String PAIR_MARKET_URL = "https://min-api.cryptocompare.com/data/top/exchanges/full?fsym=%s&tsym=%s&limit=25";



    public static void getAllCoins(Context context, afterTaskCompletion<CoinList> taskCompletion, afterTaskFailure failure, boolean async) {
        new GenericRestCall<>(Void.class, CoinList.class, String.class)
                ///list of coins from cmc
                .setUrl(ALL_COINS_LIST_URL)  //Our URL we pass in
                .setContext(context.getApplicationContext())
                .isCacheEnabled(true)
                .setCacheTime(604800000L) //time to cache in milliseconds
                .setMethodToCall(HttpMethod.GET)
                .setTaskCompletion(taskCompletion)
                .setTaskFailure(failure)
                .setAutomaticCacheRefresh(true).execute(async);
    }


    public static void getPairsMarket(Context context, String tsymbol, String fsymbol, afterTaskCompletion<MarketsResponse> taskCompletion, afterTaskFailure failure) {
        String url = String.format(PAIR_MARKET_URL, tsymbol, fsymbol);

        //Get the pairings
        new GenericRestCall<>(Void.class, MarketsResponse.class, String.class)
                .setUrl(url)
                .setContext(context.getApplicationContext())
                .isCacheEnabled(true)
                .setCacheTime(300000L)
                .setMethodToCall(HttpMethod.GET)
                .setTaskCompletion(taskCompletion)
                .setTaskFailure(failure)
                .setAutomaticCacheRefresh(false).execute(true);
    }

    public static void getTopPairs(Context context, String symbol, afterTaskCompletion<TradingPair> taskCompletion, afterTaskFailure failure) {
        String url = String.format(TOP_PAIRS_URL, symbol);
        new GenericRestCall<>(Void.class, TradingPair.class, String.class)
                .setUrl(url)
                .setContext(context.getApplicationContext())
                .isCacheEnabled(true)
                // Cache top pairs for 1hr
                .setCacheTime(3600000L)
                .setMethodToCall(HttpMethod.GET)
                .setTaskCompletion(taskCompletion)
                .setTaskFailure(failure)
                .setAutomaticCacheRefresh(false).execute(true);
    }
}
