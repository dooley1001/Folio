package com.folio.dooley1001.folio.Main;

import android.widget.TextView;

/*
 * class for setting our precentage change textviews
 */
public class cryptoListAdapterUtils {
    //pass in our attributes
    public static void setPercentChangeTextView(TextView textView, String pctChange, String time, String negativePercentStringResource, String positivePercentStringResource, int negativeRedColor, int positiveGreenColor, String pctChangeNotAvailableStringResource) {

        if (pctChange == null) {
            textView.setText(String.format(pctChangeNotAvailableStringResource, time));
        } else {
            double change = Double.parseDouble(pctChange);
            if (change < 0) {
                textView.setText(String.format(negativePercentStringResource, time, change));
                textView.setTextColor(negativeRedColor);
            } else {
                textView.setText(String.format(positivePercentStringResource, time, change));
                textView.setTextColor(positiveGreenColor);
            }
        }
    }

}
