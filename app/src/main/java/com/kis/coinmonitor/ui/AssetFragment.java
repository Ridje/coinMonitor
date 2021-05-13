package com.kis.coinmonitor.ui;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.kis.coinmonitor.R;
import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.model.standardAPI.AssetHistory;
import com.kis.coinmonitor.model.standardAPI.AssetHistoryValue;
import com.kis.coinmonitor.utils.CurrencyMarkerView;
import com.kis.coinmonitor.utils.ChartInterval;
import com.kis.coinmonitor.utils.Locales;
import com.kis.coinmonitor.utils.TimestampFormatter;
import com.kis.coinmonitor.viewmodel.AssetViewModel;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class AssetFragment extends Fragment {

    private static final String LOG_TAG = AssetFragment.class.getName();
    private AssetViewModel mViewModel;
    private static final String KEY_ASSET_ID = "asset_id";
    private static final String KEY_CHART_INTERVAL = "asset_chart_interval";

    private TextView tvRank;
    private TextView tvName;
    private TextView tvPrice;
    private TextView tvChange;
    private ImageView tvChangeSignDependable;
    private TextView tvMarketCap;
    private TextView tvVolume24Hr;
    private TextView tvMarketSupply;

    private FrameLayout flAssetDetailsContainer;
    private View lAssetDetailsData;
    private ShimmerFrameLayout lAssetDetailsPlaceholder;

    private ConstraintLayout flAssetHeaderContainer;
    private ShimmerFrameLayout lAssetHeaderPlaceholder;

    private TabLayout tlAssetChartIntervalSwitcher;
    private ChartInterval lastChartInterval = null;
    private Button btDetailsPlaceholder;

    private ImageView ivAssetLogo;
    private TextView tvDetailsName;
    private TextView tvDetailsHigh;
    private TextView tvDetailsLow;
    private TextView tvDetailsAverage;
    private TextView tvDetailsChange;

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (lastChartInterval != null) {
            outState.putString(KEY_CHART_INTERVAL, lastChartInterval.name());
        }
    }

    private LineChart lcAssetHistory;

    public static AssetFragment newInstance(String assetID) {
        AssetFragment fragment = new AssetFragment();
        Bundle args = new Bundle();
        args.putString(KEY_ASSET_ID, assetID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.asset_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvRank = view.findViewById(R.id.asset_fragment_rank);
        tvName = view.findViewById(R.id.asset_fragment_currencyName);
        tvPrice = view.findViewById(R.id.asset_fragment_currencyPrice);
        tvChange = view.findViewById(R.id.asset_fragment_currencyChange);
        tvChangeSignDependable = view.findViewById(R.id.asset_fragment_currencyChange_signDependable);
        tvMarketCap = view.findViewById(R.id.asset_fragment_marketCap);
        tvVolume24Hr = view.findViewById(R.id.asset_fragment_marketVolume);
        tvMarketSupply = view.findViewById(R.id.asset_fragment_marketSupply);


        flAssetDetailsContainer = view.findViewById(R.id.asset_fragment_details_frame);
        lAssetDetailsData = flAssetDetailsContainer.findViewById(R.id.asset_details_layout);
        lAssetDetailsData.setVisibility(View.GONE);
        lAssetDetailsPlaceholder = flAssetDetailsContainer.findViewById(R.id.asset_details_layout_placeholder);
        lAssetDetailsPlaceholder.setVisibility(View.VISIBLE);
        lAssetDetailsPlaceholder.startShimmer();
        btDetailsPlaceholder = lAssetDetailsPlaceholder.findViewById(R.id.asset_button_show_details_placeholder);
        btDetailsPlaceholder.setVisibility(View.GONE);


        flAssetHeaderContainer = view.findViewById(R.id.asset_fragment_header_container);
        flAssetHeaderContainer.setVisibility(View.GONE);
        lAssetHeaderPlaceholder = view.findViewById(R.id.asset_fragment_header_placeholder);
        lAssetHeaderPlaceholder.setVisibility(View.VISIBLE);
        lAssetHeaderPlaceholder.startShimmer();


        tlAssetChartIntervalSwitcher = view.findViewById(R.id.asset_details_chart_tab);
        tlAssetChartIntervalSwitcher.setVisibility(View.VISIBLE);

        String[] intervalsArray = getResources().getStringArray(R.array.allowed_chart_intervals);

        if (savedInstanceState != null) {
            String savedChartInterval = savedInstanceState.getString(KEY_CHART_INTERVAL);
            if (savedChartInterval != null) {
                lastChartInterval = ChartInterval.valueOf(savedChartInterval);
            }
        }

        for (String s : intervalsArray) {
            ChartInterval newInterval = ChartInterval.valueOf(s);
            TabLayout.Tab tab = tlAssetChartIntervalSwitcher.newTab();
            tab.setText(newInterval.getTitle());
            tab.setTag(newInterval);
            tlAssetChartIntervalSwitcher.addTab(tab);
            if (lastChartInterval == newInterval) {
                tlAssetChartIntervalSwitcher.selectTab(tab);
            }
        }

        tlAssetChartIntervalSwitcher.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                lastChartInterval = (ChartInterval) tab.getTag();
                lAssetDetailsData.setVisibility(View.GONE);
                lAssetDetailsPlaceholder.setVisibility(View.VISIBLE);
                lAssetDetailsPlaceholder.startShimmer();
                switchChartPeriod(lastChartInterval);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ivAssetLogo = view.findViewById(R.id.asset_details_image);
        tvDetailsName = view.findViewById(R.id.asset_details_description);
        tvDetailsHigh = view.findViewById(R.id.asset_details_high);
        tvDetailsLow = view.findViewById(R.id.asset_details_low);
        tvDetailsAverage = view.findViewById(R.id.asset_details_average);
        tvDetailsChange = view.findViewById(R.id.asset_details_change);
        lcAssetHistory = view.findViewById(R.id.asset_details_chart);

        AssetViewModel.Factory factory = new AssetViewModel.Factory(requireActivity().getApplication()
                , requireArguments().getString(KEY_ASSET_ID)
                , ChartInterval.ONE_DAY);
        mViewModel = new ViewModelProvider(this, factory).get(AssetViewModel.class);
        subscribeUI();
    }

    private void subscribeUI() {
        mViewModel.getAssetLiveData().observe(getViewLifecycleOwner(), this::bindAssetData);
        mViewModel.getAssetHistoryLiveData().observe(getViewLifecycleOwner(), this::bindAssetHistory);

    }

    private void bindAssetData(Asset asset) {
        tvRank.setText(String.format("#" + asset.getRank().toString()));
        tvName.setText(String.format("%s (%s)", asset.getName(), asset.getSymbol()));
        tvPrice.setText(Locales.formatCurrency(asset.getPriceUsd()));
        tvChange.setText(Locales.formatCurrencyWithPercents(asset.getChangePercent24Hr()));
        if (asset.getChangePercent24Hr().compareTo(new BigDecimal(0)) == -1) {
            tvChange.setTextColor(tvChange.getResources().getColor(R.color.negative_number, tvChange.getContext().getTheme()));
            tvChangeSignDependable.setImageDrawable(ContextCompat.getDrawable(tvChangeSignDependable.getContext(), R.drawable.down_icon));
        } else {
            tvChange.setTextColor(tvChange.getResources().getColor(R.color.positive_number, tvChange.getContext().getTheme()));
            tvChangeSignDependable.setImageDrawable(ContextCompat.getDrawable(tvChangeSignDependable.getContext(), R.drawable.up_icon));
        }
        tvMarketCap.setText(Locales.formatCompactCurrency(asset.getMarketCapUsd()));
        tvVolume24Hr.setText(Locales.formatCompactCurrency(asset.getVolumeUsd24Hr()));
        tvMarketSupply.setText(Locales.formatCompactNumber(asset.getSupply()));

        Picasso.get()
                .load("https://static.coincap.io/assets/icons/" + asset.getSymbol().toLowerCase() + "@2x.png")
                .error(R.mipmap.ic_default_asset_image).into(ivAssetLogo);

        tvDetailsName.setText(String.format("%s (%s)", asset.getName(), asset.getSymbol()));
        lAssetHeaderPlaceholder.stopShimmer();
        lAssetHeaderPlaceholder.setVisibility(View.GONE);
        flAssetHeaderContainer.setVisibility(View.VISIBLE);
    }

    private void bindAssetHistory(AssetHistory assetHistory) {

        List<Entry> dataHistoryValues = new ArrayList<>();

        List<AssetHistoryValue> dataHistory = assetHistory.getData();
        AssetHistoryValue firstPoint = dataHistory.get(0);
        AssetHistoryValue lastPoint = dataHistory.get(dataHistory.size() - 1);
        BigDecimal maxPrice = dataHistory.get(0).getPriceUsd();
        BigDecimal minPrice = dataHistory.get(0).getPriceUsd();
        BigDecimal changePrice = lastPoint.getPriceUsd().subtract(firstPoint.getPriceUsd());
        BigDecimal sum = new BigDecimal(0);

        for (int i = 0; i < dataHistory.size() - 1; i++) {
            dataHistoryValues.add(new Entry((float) (dataHistory.get(i).getTime().getTime()), dataHistory.get(i).getPriceUsd().floatValue()));
            if (dataHistory.get(i).getPriceUsd().compareTo(maxPrice) == 1) {
                maxPrice = dataHistory.get(i).getPriceUsd();
            }

            if (dataHistory.get(i).getPriceUsd().compareTo(minPrice) == -1) {
                minPrice = dataHistory.get(i).getPriceUsd();
            }
            sum = sum.add(dataHistory.get(i).getPriceUsd());
        }

        BigDecimal avgPrice = sum.divide(new BigDecimal(dataHistory.size()), RoundingMode.HALF_UP);
        changePrice = changePrice.divide(firstPoint.getPriceUsd(), RoundingMode.HALF_UP).multiply(new BigDecimal(100));

        tvDetailsHigh.setText(Locales.formatCurrency(maxPrice));
        tvDetailsLow.setText(Locales.formatCurrency(minPrice));
        tvDetailsAverage.setText(Locales.formatCurrency(avgPrice));

        tvDetailsChange.setText(Locales.formatCurrencyWithPercents(changePrice));
        if (changePrice.compareTo(new BigDecimal(0)) == 1) {
            tvDetailsChange.setTextColor(tvDetailsChange.getResources().getColor(R.color.positive_number, tvDetailsChange.getContext().getTheme()));
        } else {
            tvDetailsChange.setTextColor(tvDetailsChange.getResources().getColor(R.color.negative_number, tvDetailsChange.getContext().getTheme()));
        }

        LineDataSet dataSet = new LineDataSet(dataHistoryValues, "");
        LineData lineData = new LineData(dataSet);
        dataSet.setDrawCircles(false);
        dataSet.setDrawFilled(true);
        dataSet.setLineWidth(2f);

        int colorBelowLine = ContextCompat.getColor(lcAssetHistory.getContext(), R.color.positive_changes);
        int colorLine = ContextCompat.getColor(lcAssetHistory.getContext(), R.color.positive_number);
        if (dataHistoryValues.get(0).getY() > dataHistoryValues.get(dataHistoryValues.size() - 1).getY()) {
            colorBelowLine = ContextCompat.getColor(lcAssetHistory.getContext(), R.color.negative_changes);
            colorLine = ContextCompat.getColor(lcAssetHistory.getContext(), R.color.negative_number);
        }
        dataSet.setFillColor(colorBelowLine);
        dataSet.setColor(colorLine);
        dataSet.setHighlightLineWidth(2f);

        int descriptionColor = ContextCompat.getColor(lcAssetHistory.getContext(), R.color.chart_text_color);
        lcAssetHistory.getXAxis().setValueFormatter(new TimestampFormatter(lastChartInterval));
        lcAssetHistory.getXAxis().setLabelRotationAngle(-75f);
        lcAssetHistory.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lcAssetHistory.getXAxis().setTextColor(descriptionColor);
        lcAssetHistory.getXAxis().setDrawGridLines(false);
        lcAssetHistory.getXAxis().setLabelCount(24, true);
        lcAssetHistory.getXAxis().setTextSize(14f);

        MarkerView myMarker = new CurrencyMarkerView(lcAssetHistory.getContext(), R.layout.currency_marker_view);
        myMarker.setOffset(-200, -140);
        myMarker.setChartView(lcAssetHistory);
        lcAssetHistory.setMarker(myMarker);

        lcAssetHistory.getAxisRight().setDrawAxisLine(false);
        lcAssetHistory.getAxisLeft().setDrawAxisLine(false);
        lcAssetHistory.getAxisRight().setTextColor(descriptionColor);
        lcAssetHistory.getAxisRight().setTextSize(12f);
        lcAssetHistory.getAxisRight().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        lcAssetHistory.getAxisLeft().setDrawLabels(false);
        lcAssetHistory.getLegend().setEnabled(false);
        lcAssetHistory.getDescription().setEnabled(false);
        lcAssetHistory.setDoubleTapToZoomEnabled(false);
        lcAssetHistory.setData(lineData);

        lcAssetHistory.setExtraBottomOffset(20f);

        lcAssetHistory.animateX(200);
        lcAssetHistory.invalidate();
        lAssetDetailsPlaceholder.stopShimmer();
        lAssetDetailsPlaceholder.setVisibility(View.GONE);
        lAssetDetailsData.setVisibility(View.VISIBLE);
    }

    private void switchChartPeriod(ChartInterval chartInterval) {
        mViewModel.loadAssetHistory(requireArguments().getString(KEY_ASSET_ID), chartInterval);
    }
}