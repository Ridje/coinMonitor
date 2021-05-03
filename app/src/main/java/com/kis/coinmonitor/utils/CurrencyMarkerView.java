package com.kis.coinmonitor.utils;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.kis.coinmonitor.R;


public class CurrencyMarkerView extends MarkerView {

    final TextView content;

    public CurrencyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        content = findViewById(R.id.currency_value);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        content.setText(String.valueOf(e.getY()));

        super.refreshContent(e, highlight);
    }

}
