package com.folio.dooley1001.folio.models.easyrest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
//Selections for the chart dropdown
public class CoinMarketCapCoinData {
    @JsonProperty("price_btc")
    List<List<Float>> priceBTC;
    @JsonProperty("price_usd")
    List<List<Float>> priceUSD;

    public List<List<Float>> getPriceBTC() {
        return priceBTC;
    }
    public List<List<Float>> getPriceUSD() {
        return priceUSD;
    }
}
