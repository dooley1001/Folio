package com.folio.dooley1001.folio.models.easyrest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TradingPairNode {
    @JsonProperty("fromSymbol")
    private String fromSymbol;
    @JsonProperty("toSymbol")
    private String toSymbol;

    public String getFromSymbol() {
        return fromSymbol;
    }

    public String getToSymbol() {
        return toSymbol;
    }


}
