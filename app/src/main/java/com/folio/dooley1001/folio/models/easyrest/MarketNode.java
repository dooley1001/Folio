package com.folio.dooley1001.folio.models.easyrest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MarketNode {
    @JsonProperty("MARKET")
    private String market;
    @JsonProperty("TOSYMBOL")
    private String toSymbol;
    @JsonProperty("PRICE")
    private float price;
    @JsonProperty("CHANGEPCT24HOUR")
    private float changePct24h;


    public String getMarket() {
        return market;
    }

    public String getToSymbol() {
        return toSymbol;
    }

    public float getPrice() {
        return price;
    }

    public float getChangePct24h() {
        return changePct24h;
    }


}
