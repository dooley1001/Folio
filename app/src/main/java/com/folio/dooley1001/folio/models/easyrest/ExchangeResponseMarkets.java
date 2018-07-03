package com.folio.dooley1001.folio.models.easyrest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
/*
 *
 *Class for the markets tab where we return a list of markets
 *
 */
public class ExchangeResponseMarkets {
    @JsonProperty("Exchanges")
    private List<MarketNode> marketsList;

    public List<MarketNode> getMarketsList() {
        return marketsList;
    }

}
