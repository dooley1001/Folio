package com.folio.dooley1001.folio;

import com.folio.dooley1001.folio.models.easyrest.CMCCoinParcelable;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;

/*
*
* This class is for our sorting of our parcelable currency list
*
 */

public class SortingClass {
    public static void sortList(ArrayList<CMCCoinParcelable> currencyList, int number) {
        switch (number) {

            // Sort from  A-Z
            case 0:
                Collections.sort(currencyList, new Comparator<CMCCoinParcelable>() {
                    @Override
                    public int compare(CMCCoinParcelable lhs, CMCCoinParcelable rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                break;
            // Sort by Market Cap
            case 1:
                Collections.sort(currencyList, new Comparator<CMCCoinParcelable>() {
                    @Override
                    public int compare(CMCCoinParcelable lhs, CMCCoinParcelable rhs) {
                        return Integer.parseInt(lhs.getRank()) < Integer.parseInt(rhs.getRank()) ? -1 : Integer.parseInt(lhs.getRank()) > Integer.parseInt(rhs.getRank()) ? +1 : 0;
                    }
                });
                break;
            // Sort by Price
            case 2:
                Collections.sort(currencyList, new Comparator<CMCCoinParcelable>() {
                    @Override
                    public int compare(CMCCoinParcelable lhs, CMCCoinParcelable rhs) {
                        if (lhs.getPrice_usd() == null && rhs.getPrice_usd() == null) {
                            return 0;
                        }
                        if (lhs.getPrice_usd() == null) {
                            return 1;
                        }
                        if (rhs.getPrice_usd() == null) {
                            return -1;
                        }
                        float comp = Float.parseFloat(rhs.getPrice_usd()) - Float.parseFloat(lhs.getPrice_usd());
                        return floatComp(comp);
                    }
                });
                break;
            // Sort by Volume 24h
            case 3:
                Collections.sort(currencyList, new Comparator<CMCCoinParcelable>() {
                    @Override
                    public int compare(CMCCoinParcelable lhs, CMCCoinParcelable rhs) {
                        if (lhs.getVolume_usd_24h() == null && rhs.getVolume_usd_24h() == null) {
                            return 0;
                        }
                        if (lhs.getVolume_usd_24h() == null) {
                            return 1;
                        }
                        if (rhs.getVolume_usd_24h() == null) {
                            return -1;
                        }
                        float comp = Float.parseFloat(rhs.getVolume_usd_24h()) - Float.parseFloat(lhs.getVolume_usd_24h());
                        return floatComp(comp);
                    }
                });
                break;
            //Sort by  Change 1h
            case 4:
                Collections.sort(currencyList, new Comparator<CMCCoinParcelable>() {
                    @Override
                    public int compare(CMCCoinParcelable lhs, CMCCoinParcelable rhs) {
                        if (lhs.getPercent_change_1h() == null && rhs.getPercent_change_1h() == null) {
                            return 0;
                        }
                        if (lhs.getPercent_change_1h() == null) {
                            return 1;
                        }
                        if (rhs.getPercent_change_1h() == null) {
                            return -1;
                        }
                        float comp = Float.parseFloat(rhs.getPercent_change_1h()) - Float.parseFloat(lhs.getPercent_change_1h());
                        return floatComp(comp);
                    }
                });
                break;
            // Sort by Change 24h
            case 5:
                Collections.sort(currencyList, new Comparator<CMCCoinParcelable>() {
                    @Override
                    public int compare(CMCCoinParcelable lhs, CMCCoinParcelable rhs) {
                        if (lhs.getPercent_change_24h() == null && rhs.getPercent_change_24h() == null) {
                            return 0;
                        }
                        if (lhs.getPercent_change_24h() == null) {
                            return 1;
                        }
                        if (rhs.getPercent_change_24h() == null) {
                            return -1;
                        }
                        float comp = Float.parseFloat(rhs.getPercent_change_24h()) - Float.parseFloat(lhs.getPercent_change_24h());
                        return floatComp(comp);
                    }
                });
                break;
            // Sort by Change 7d
            case 6:
                Collections.sort(currencyList, new Comparator<CMCCoinParcelable>() {
                    @Override
                    public int compare(CMCCoinParcelable lhs, CMCCoinParcelable rhs) {
                        if (lhs.getPercent_change_7d() == null && rhs.getPercent_change_7d() == null) {
                            return 0;
                        }
                        if (lhs.getPercent_change_7d() == null) {
                            return 1;
                        }
                        if (rhs.getPercent_change_7d() == null) {
                            return -1;
                        }
                        float comp = Float.parseFloat(rhs.getPercent_change_7d()) - Float.parseFloat(lhs.getPercent_change_7d());
                        return floatComp(comp);
                    }
                });
                break;
        }
    }
    private static int floatComp(float f) {
        if (f == 0) {
            return 0;
        } else if (f < 0) {
            return -1;
        } else {
            return 1;
        }
    }
}
