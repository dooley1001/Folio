package com.folio.dooley1001.folio.formatters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//formatter for our months/year before axis is drawn


public class MonthYearFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float unixSeconds, AxisBase axis) {

        Date date = new Date((long)unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM/yy", Locale.ENGLISH);
        return sdf.format(date);
    }
}