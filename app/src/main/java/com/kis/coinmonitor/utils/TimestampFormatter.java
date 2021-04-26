package com.kis.coinmonitor.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Calendar;
import java.util.Locale;

public class TimestampFormatter extends ValueFormatter {

    @Override
    public String getAxisLabel(float value, AxisBase axis) {

        Calendar x = Calendar.getInstance();
        x.setTimeInMillis((long)value);

        return x.get(Calendar.HOUR_OF_DAY) + "" + x.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.getDefault());
    }
}
