package com.folio.dooley1001.folio.models.easyrest;

import java.util.HashMap;
import java.util.List;

//structures for our watclist of coins the user saves
public class watchListStructures {
    public List<String> watchList;

    public HashMap<String, String> watchlistMap;
    //constructor
    public watchListStructures(List<String> watchList, HashMap<String, String> watchlistMap) {
        this.watchList = watchList;
        this.watchlistMap = watchlistMap;
    }
}
