package com.folio.dooley1001.folio.models.easyrest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

//return list of trading pairs
public class TradingPair {
    @JsonProperty("Data")
    private List<TradingPairNode> data;

    public List<TradingPairNode> getData() {
        return data;
    }
    public void setData(List<TradingPairNode> data) {
        this.data = data;
    }

}
