package com.folio.dooley1001.folio.models.easyrest;

import com.fasterxml.jackson.annotation.JsonProperty;


public class MarketsResponse {
    @JsonProperty("Data")
    private ExchangeResponseMarkets data;

    public ExchangeResponseMarkets getData() {
        return data;
    }

}
