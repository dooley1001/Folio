package com.folio.dooley1001.folio.api;

import android.content.Context;
import android.util.Log;

import com.folio.dooley1001.folio.coinDetails.graphTab.GraphTabFragment;
import com.folio.dooley1001.folio.models.easyrest.CoinMarketCapCoinData;
import com.folio.dooley1001.folio.models.easyrest.CMCCoinParcelable;
import com.folio.dooley1001.folio.models.easyrest.CoinMarketcapsQuickSearch;
import com.grizzly.rest.GenericRestCall;
import com.grizzly.rest.Model.afterTaskCompletion;  //https://github.com/fcopardo/EasyRest/tree/master/src/main/java/com/grizzly/rest/Model
import com.grizzly.rest.Model.afterTaskFailure;
import org.springframework.http.HttpMethod;
                                                    //Thanks to this post for handling the graph api's - https://www.reddit.com/r/CryptoCurrency/comments/844h9o/how_to_download_coinmarketcaps_historical_market/


public class CoinMarketCapService {
                                                                //Public api - https://coinmarketcap.com/api/
    public static final String COINMARKETCAP_ALL_COINS_URL = "https://api.coinmarketcap.com/v1/ticker/?limit=0";
    public static final String COINMARKETCAP_QUICK_SEARCH_URL = "https://s2.coinmarketcap.com/generated/search/quick_search.json";
    public static final String COINMARKETCAP_CHART_URL_WINDOW = "https://graphs2.coinmarketcap.com/currencies/%s/%s/%s/";
    public static final String COINMARKETCAP_CHART_URL_ALL_DATA = "https://graphs2.coinmarketcap.com/currencies/%s/";

    public static void getCMCQuickSearch(Context context, afterTaskCompletion<CoinMarketcapsQuickSearch[]> taskCompletion, afterTaskFailure failure, boolean async) {
        Log.d("I", "Getting URL in getCMCQuickSearch: " + COINMARKETCAP_QUICK_SEARCH_URL);
        new GenericRestCall<>(Void.class, CoinMarketcapsQuickSearch[].class, String.class)
                .setUrl(COINMARKETCAP_QUICK_SEARCH_URL)//the string URL
                .setContext(context.getApplicationContext())// A Map of String, Object with queryparams to be appended to the URL
                .isCacheEnabled(true)/*Defines if caching is activated. If true, checks how old the cache of the call is, and avoids making the call if the cache is fresh enough, or if the API is unreachable and sends the cached data to actions and callbacks*/
                .setCacheTime(20500000L)//Sets the arbitrary caching time, in miliseconds.
                .setMethodToCall(HttpMethod.GET)//The HTTP call. Currently, POST, PUT, GET and DELETE are supported.
                .setTaskCompletion(taskCompletion)
                .setTaskFailure(failure)
                .setAutomaticCacheRefresh(true).execute(async);
    }

    public static void getAllCoins(Context context, afterTaskCompletion<CMCCoinParcelable[]> taskCompletion, afterTaskFailure failure, boolean async) {
        new GenericRestCall<>(Void.class, CMCCoinParcelable[].class, String.class)
                .setUrl(COINMARKETCAP_ALL_COINS_URL) //the string URL
                .setContext(context.getApplicationContext())// A Map of String, Object with queryparams to be appended to the URL
                .isCacheEnabled(true)  /*Defines if caching is activated. If true, checks how old the cache of the call is, and avoids making the call if the cache is fresh enough, or if the API is unreachable and sends the cached data to actions and callbacks*/
                .setCacheTime(200000L) //Sets the arbitrary caching time, in miliseconds.
                .setMethodToCall(HttpMethod.GET) //The HTTP call. Currently, POST, PUT, GET and DELETE are supported.
                .setTaskCompletion(taskCompletion)
                .setTaskFailure(failure)
                .setAutomaticCacheRefresh(true).execute(async);
    }

    public static void getCMCChartData(Context context, String coinID, afterTaskCompletion<CoinMarketCapCoinData> taskCompletion, afterTaskFailure failure, boolean async) {
        Log.d("I", "URL: " + GraphTabFragment.CURRENT_CHART_URL);
        new GenericRestCall<>(Void.class, CoinMarketCapCoinData.class, String.class)
                .setUrl(String.format(GraphTabFragment.CURRENT_CHART_URL, coinID))
                .setContext(context.getApplicationContext())
                .isCacheEnabled(true)
                .setCacheTime(50000L)
                .setMethodToCall(HttpMethod.GET)
                .setTaskCompletion(taskCompletion)
                .setTaskFailure(failure)
                .setAutomaticCacheRefresh(false).execute(async);
    }


}
