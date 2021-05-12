package com.kis.coinmonitor.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Calendar;
import java.util.Locale;

public class TimestampFormatter extends ValueFormatter {

    private final ChartInterval chartInterval;

    public TimestampFormatter() {
        this(null);
    }

    public TimestampFormatter(ChartInterval chartInterval) {
        super();
        if (chartInterval == ChartInterval.ONE_DAY || chartInterval == ChartInterval.ONE_WEEK) {
            this.chartInterval = null;
        } else {
            this.chartInterval = chartInterval;
        }
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {

        Calendar x = Calendar.getInstance();
        x.setTimeInMillis((long)value);

        if (chartInterval == null) {
            return x.get(Calendar.HOUR_OF_DAY) + "" + x.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.getDefault());
        } else if (chartInterval != ChartInterval.ALL) {
            return x.get(Calendar.DAY_OF_MONTH) + " " + x.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        } else {
            return x.get(Calendar.YEAR) + " " + x.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        }

    }
}
