package com.kis.coinmonitor.utils;

public enum ChartInterval {

    FIVE_MINUTES("5m", "m5", 5 * 60 * 1000L, null),
    THIRTY_MINUTES("30m", "m30", 30 * 60 * 1000L, null),
    TWO_HOURS("2h", "h2", 2 * 3600 * 1000L, null),
    SIX_HOURS("6h", "h6", 6 * 3600 * 1000L, null),
    TWELVE_HOURS("12h", "h12", 12 * 3600 * 1000L, null),
    ONE_DAY("1D", "d1", 24 * 3600 * 1000L, FIVE_MINUTES),
    ONE_WEEK("1W", "d7", 7 * 24 * 3600 * 1000L, THIRTY_MINUTES),
    ONE_MONTH("1M", "d30", 30 * 24 * 3600 * 1000L, TWO_HOURS),
    THREE_MONTHS("3M", "d90", 90 * 24 * 3600 * 1000L, SIX_HOURS),
    SIX_MONTHS("6M", "d180", 180 * 24 * 3600 * 1000L, TWELVE_HOURS),
    ONE_YEAR("1Y", "d365", 365 * 24 * 3600 * 1000L, ONE_DAY),
    //7305 - max days allowed by coincap api - 1 day just in case
    ALL("ALL", "all", 7304 * 24 * 3600 * 1000L, ONE_DAY);

    private final String intervalValue;
    private final Long defaultTimeDifference;
    private final ChartInterval defaultIntervalBetweenPoints;

    private final String title;

    ChartInterval(String title, String intervalValue, Long defaultTimeDifference, ChartInterval defaultIntervalBetweenPoints) {
        this.intervalValue = intervalValue;
        this.defaultTimeDifference = defaultTimeDifference;
        this.defaultIntervalBetweenPoints = defaultIntervalBetweenPoints;
        this.title = title;
    }


    public String getIntervalValue() {
        return intervalValue;
    }

    public ChartInterval getDefaultIntervalBetweenPoints() {
        return defaultIntervalBetweenPoints;
    }

    public Long getDefaultTimeDifference() {
        return defaultTimeDifference;
    }

    public String getTitle() {
        return title;
    }
}
