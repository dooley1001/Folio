package com.folio.dooley1001.folio.formatters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//formatter for our dates before axis is drawn

public class TimeDateFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float unixSeconds, AxisBase axis) {
        Date date = new Date((long)unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:MM", Locale.ENGLISH);
        return sdf.format(date);
    }
}
