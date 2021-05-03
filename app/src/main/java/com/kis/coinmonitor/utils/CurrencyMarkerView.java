package com.kis.coinmonitor.utils;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.kis.coinmonitor.R;

import java.util.Calendar;

public class CurrencyMarkerView extends MarkerView {

    final TextView content;
    final TextView dateContent;

    public CurrencyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        content = findViewById(R.id.currency_value);
        dateContent = findViewById(R.id.currency_date);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        content.setText(String.valueOf(e.getY()));


        Calendar x = Calendar.getInstance();
        x.setTimeInMillis((long)e.getX());

        String time = Locales.formatTime(x.getTime());
        dateContent.setText(time);

        super.refreshContent(e, highlight);
    }

}
