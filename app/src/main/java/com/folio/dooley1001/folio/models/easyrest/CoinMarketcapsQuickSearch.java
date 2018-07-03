package com.folio.dooley1001.folio.models.easyrest;

import com.fasterxml.jackson.annotation.JsonProperty; //https://github.com/FasterXML/jackson-annotations

//using CMC quick search we can search using the string slug as the delimiter
public class CoinMarketcapsQuickSearch {

    @JsonProperty("slug")
    private String slug;
    @JsonProperty("id")
    private int id = -1;

    public String getSlug() {
        return slug;
    }

    public int getId() {
        return id;
    }
}
