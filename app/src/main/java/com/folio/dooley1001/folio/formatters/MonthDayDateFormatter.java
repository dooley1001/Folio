package com.folio.dooley1001.folio.formatters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;       //original repo: https://github.com/PhilJay/MPAndroidChart

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//formatter for our dates before axis is drawn

public class MonthDayDateFormatter implements IAxisValueFormatter {
    @Override
    public String getFormattedValue(float unixSeconds, AxisBase axis) {
        Date date = new Date((long)unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.ENGLISH);
        return sdf.format(date);
    }
}
