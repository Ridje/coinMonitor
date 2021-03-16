package com.kis.coinmonitor.utils;

import android.icu.text.CompactDecimalFormat;
import android.icu.util.Currency;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Locales {

    private static NumberFormat numberFormatCurrency;
    private static DecimalFormat numberFormatPercents;
    private static CompactDecimalFormat compactCurrencyDecimalFormat;

    public static String formatCurrency(Number value) {
        if (value == null) {
            return "null";
        }
        return getNumberFormatCurrency().format(value);
    }

    public static String formanCurrencyWithPercents(Number value) {
        if (value == null) {
            return "null";
        }
        return getNumberFormatPercents().format(value);
    }

    public static String formatCompactCurrency(Number value) {
        if (value == null) {
            return "null";
        }
        return getCompactCurrencyDecimalFormat().format(value);
    }

    public static NumberFormat getNumberFormatCurrency() {
        if (numberFormatCurrency == null) {
            numberFormatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
        }
        return numberFormatCurrency;
    }

    public static DecimalFormat getNumberFormatPercents() {
        if (numberFormatPercents == null) {
            numberFormatPercents = new DecimalFormat("#.##'%'");
        }
        return numberFormatPercents;
    }

    public static CompactDecimalFormat getCompactCurrencyDecimalFormat() {
        if (compactCurrencyDecimalFormat == null) {
            compactCurrencyDecimalFormat = CompactDecimalFormat.getInstance(Locale.US, CompactDecimalFormat.CompactStyle.SHORT);
            compactCurrencyDecimalFormat.setCurrency(Currency.getInstance(Locale.US));
        }
        return compactCurrencyDecimalFormat;
    }
}