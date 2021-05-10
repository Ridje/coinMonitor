package com.kis.coinmonitor.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.kis.coinmonitor.R;
import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.model.standardAPI.AssetHistory;
import com.kis.coinmonitor.model.standardAPI.AssetHistoryValue;
import com.kis.coinmonitor.utils.CurrencyMarkerView;
import com.kis.coinmonitor.utils.Locales;
import com.kis.coinmonitor.utils.TimestampFormatter;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Asset> mItemList;
    private OnItemClickListener itemClickListener;
    private Asset expandedAsset = null;

    public RecyclerViewAdapter() {
        mItemList = new ArrayList<>();
    }

    public void addAssets(List<Asset> assets) {
        for (Asset asset : assets) {
            if (!mItemList.contains(asset)) {
                mItemList.add(asset);
                this.notifyItemInserted(mItemList.size() - 1);
            }
        }
    }

    public void updateAssets(List<Asset> assets) {
        for (Asset asset : assets) {
            updateAsset(asset);
        }
    }

    public void updateAsset(Asset asset) {
        int position = mItemList.indexOf(asset);
        if (position != -1) {
            this.notifyItemChanged(position);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_row, parent, false);
            return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((ItemViewHolder) viewHolder).bind(mItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }


    public interface OnItemClickListener {
        void onItemClick(View view , int position);
        void onButtonItemClick(View view, int position);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        final TextView tvAsset_name;
        final TextView tvAsset_price_usd;
        final TextView tvAsset_rank;
        final TextView tvAsset_symbol;
        final TextView tvAsset_change_24hrs;
        final TextView tvAsset_market_24hrs;
        final CardView cardView;
        final ValueAnimator positiveChange;
        final ValueAnimator negativeChange;
        final LineChart assetChart;
        final View hiddenContainer;
        final View hiddenLayoutFrame;
        final CardView visibleCardView;
        final Button buttonShowDetails;
        final ImageView visibleAssetImage;
        final TextView hiddenMax;
        final TextView hiddenLow;
        final TextView hiddenAverage;
        final TextView hiddenChange;
        final TextView hiddenAssetDescription;
        final ImageView hiddenAssetImage;
        final ShimmerFrameLayout hiddenPlaceholder;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.asset_holder);
            tvAsset_name = itemView.findViewById(R.id.asset_name);
            tvAsset_price_usd = itemView.findViewById(R.id.asset_price_usd);
            tvAsset_rank = itemView.findViewById(R.id.asset_rank);
            tvAsset_symbol = itemView.findViewById(R.id.asset_symbol);
            tvAsset_change_24hrs = itemView.findViewById(R.id.asset_change_24hrs);
            tvAsset_market_24hrs = itemView.findViewById(R.id.asset_volume_24hrs);

            hiddenContainer = itemView.findViewById(R.id.asset_details_hidden_frame);
            hiddenContainer.setVisibility(View.GONE);
            hiddenPlaceholder = itemView.findViewById(R.id.asset_details_layout_placeholder);
            hiddenPlaceholder.setVisibility(View.INVISIBLE);
            hiddenLayoutFrame = itemView.findViewById(R.id.asset_details_layout);
            hiddenLayoutFrame.setVisibility(View.INVISIBLE);

            visibleCardView = itemView.findViewById(R.id.asset_visible_cardview);

            assetChart = itemView.findViewById(R.id.asset_details_chart);

            buttonShowDetails = itemView.findViewById(R.id.asset_button_show_details);
            visibleAssetImage = itemView.findViewById(R.id.asset_image_visible);
            hiddenMax = itemView.findViewById(R.id.asset_details_high);
            hiddenLow = itemView.findViewById(R.id.asset_details_low);
            hiddenAverage = itemView.findViewById(R.id.asset_details_average);
            hiddenChange = itemView.findViewById(R.id.asset_details_change);
            hiddenAssetDescription = itemView.findViewById(R.id.asset_details_description);
            hiddenAssetImage = itemView.findViewById(R.id.asset_details_image);
            positiveChange = ObjectAnimator.ofInt(visibleCardView, "CardBackgroundColor",
                    visibleCardView.getCardBackgroundColor().getDefaultColor(),
                    ContextCompat.getColor(visibleCardView.getContext(), R.color.negative_changes));
            positiveChange.setDuration(550);
            positiveChange.setEvaluator(new ArgbEvaluator());
            positiveChange.setRepeatCount(1);
            positiveChange.setRepeatMode(ValueAnimator.REVERSE);

            negativeChange = ObjectAnimator.ofInt(visibleCardView, "CardBackgroundColor",
                    visibleCardView.getCardBackgroundColor().getDefaultColor(),
                    ContextCompat.getColor(visibleCardView.getContext(), R.color.positive_changes));
            negativeChange.setDuration(550);
            negativeChange.setEvaluator(new ArgbEvaluator());
            negativeChange.setRepeatCount(1);
            negativeChange.setInterpolator(new AccelerateInterpolator());
            negativeChange.setRepeatMode(ValueAnimator.REVERSE);

            itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    int position = getAdapterPosition();
                    Asset currentAsset = mItemList.get(position);
                    if (expandedAsset == currentAsset) {
                        expandedAsset = null;
                        notifyItemChanged(position);
                    } else {
                        hiddenPlaceholder.startShimmer();
                        if (expandedAsset != null) {
                            notifyItemChanged(mItemList.indexOf(expandedAsset));
                        }
                        expandedAsset = currentAsset;
                        notifyItemChanged(position);
                        itemClickListener.onItemClick(v, position);
                    }
                }
            });

            buttonShowDetails.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onButtonItemClick(v, getAdapterPosition());
                }
            });
        }



        public void bind(Asset asset) {

            boolean priceChanged = false;

            if (tvAsset_name.getText().equals(asset.getName())) {
                BigDecimal oldPriceValue = (BigDecimal) Locales.parseCurrency((String) tvAsset_price_usd.getText());
                BigDecimal newFormattedValue = (BigDecimal) Locales.parseCurrency(Locales.formatCurrency(asset.getPriceUsd()));
                BigDecimal differenceWithNewValue = oldPriceValue.subtract(newFormattedValue);
                if (differenceWithNewValue.compareTo(new BigDecimal(0)) == 1) {
                    positiveChange.start();
                    priceChanged = true;
                } else if (differenceWithNewValue.compareTo(new BigDecimal(0)) == -1) {
                    negativeChange.start();
                    priceChanged = true;
                }
            }

            tvAsset_name.setText(asset.getName());
            tvAsset_price_usd.setText(Locales.formatCurrency(asset.getPriceUsd()));
            tvAsset_rank.setText(asset.getRank().toString());
            tvAsset_symbol.setText(asset.getSymbol());
            tvAsset_change_24hrs.setText(Locales.formatCurrencyWithPercents(asset.getChangePercent24Hr()));
            tvAsset_market_24hrs.setText(Locales.formatCompactCurrency(asset.getVolumeUsd24Hr()));
            if (asset.getChangePercent24Hr() != null && asset.getChangePercent24Hr().compareTo(BigDecimal.ZERO) < 0) {
                tvAsset_change_24hrs.setTextColor(tvAsset_change_24hrs.getResources().getColor(R.color.negative_number, tvAsset_change_24hrs.getContext().getTheme()));
            } else {
                tvAsset_change_24hrs.setTextColor(tvAsset_change_24hrs.getResources().getColor(R.color.positive_number, tvAsset_change_24hrs.getContext().getTheme()));
            }

            Picasso.get()
                    .load("https://static.coincap.io/assets/icons/" + asset.getSymbol().toLowerCase() + "@2x.png")
                    .error(R.mipmap.ic_default_asset_image).into(visibleAssetImage);

            hiddenContainer.setVisibility(expandedAsset == asset ? View.VISIBLE : View.GONE);
            if (expandedAsset == asset && !priceChanged) {

                boolean showPlaceholder = asset.getHistory() == null;

                hiddenPlaceholder.setVisibility(showPlaceholder ? View.VISIBLE : View.INVISIBLE);
                hiddenLayoutFrame.setVisibility(showPlaceholder ? View.INVISIBLE : View.VISIBLE);

                if (!showPlaceholder) {

                    hiddenPlaceholder.stopShimmer();

                    AssetHistory currentAssetHistory = asset.getHistory();
                    List<Entry> dataHistoryValues = new ArrayList<>();

                    List<AssetHistoryValue> dataHistory = currentAssetHistory.getData();
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
                    buttonShowDetails.setVisibility(View.VISIBLE);

                    Picasso.get()
                            .load("https://static.coincap.io/assets/icons/" + asset.getSymbol().toLowerCase() + "@2x.png")
                            .error(R.mipmap.ic_default_asset_image).into(hiddenAssetImage);

                    hiddenAssetDescription.setText(String.format("%s (%s)", asset.getName(), asset.getSymbol()));
                    hiddenMax.setText(Locales.formatCurrency(maxPrice));
                    hiddenLow.setText(Locales.formatCurrency(minPrice));
                    hiddenAverage.setText(Locales.formatCurrency(avgPrice));

                    hiddenChange.setText(Locales.formatCurrency(changePrice));
                    if (changePrice.compareTo(new BigDecimal(0)) == 1) {
                        hiddenChange.setTextColor(hiddenChange.getResources().getColor(R.color.positive_number, hiddenChange.getContext().getTheme()));
                    } else {
                        hiddenChange.setTextColor(hiddenChange.getResources().getColor(R.color.negative_number, hiddenChange.getContext().getTheme()));
                    }

                    LineDataSet dataSet = new LineDataSet(dataHistoryValues, "Last 27 hours");
                    LineData lineData = new LineData(dataSet);
                    dataSet.setDrawCircles(false);
                    dataSet.setDrawFilled(true);
                    dataSet.setLineWidth(2f);

                    int colorBelowLine = ContextCompat.getColor(assetChart.getContext(), R.color.positive_changes);
                    int colorLine = ContextCompat.getColor(assetChart.getContext(), R.color.positive_number);
                    if (dataHistoryValues.get(0).getY() > dataHistoryValues.get(dataHistoryValues.size() - 1).getY()) {
                        colorBelowLine = ContextCompat.getColor(assetChart.getContext(), R.color.negative_changes);
                        colorLine = ContextCompat.getColor(assetChart.getContext(), R.color.negative_number);
                    }
                    dataSet.setFillColor(colorBelowLine);
                    dataSet.setColor(colorLine);
                    dataSet.setHighlightLineWidth(2f);

                    int descriptionColor = ContextCompat.getColor(assetChart.getContext(), R.color.chart_text_color);
                    assetChart.getXAxis().setValueFormatter(new TimestampFormatter());
                    assetChart.getXAxis().setLabelRotationAngle(-90f);
                    assetChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    assetChart.getXAxis().setTextColor(descriptionColor);
                    assetChart.getXAxis().setDrawGridLines(false);
                    assetChart.getXAxis().setLabelCount(10);
                    assetChart.getXAxis().setTextSize(14f);

                    MarkerView myMarker = new CurrencyMarkerView(assetChart.getContext(), R.layout.currency_marker_view);
                    myMarker.setOffset(-200, -140);
                    assetChart.setMarker(myMarker);

                    assetChart.getAxisRight().setDrawAxisLine(false);
                    assetChart.getAxisLeft().setDrawAxisLine(false);
                    assetChart.getAxisRight().setTextColor(descriptionColor);
                    assetChart.getAxisRight().setTextSize(12f);
                    assetChart.getAxisRight().setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                    assetChart.getAxisLeft().setDrawLabels(false);
                    assetChart.getLegend().setEnabled(true);
                    assetChart.getDescription().setEnabled(false);
                    assetChart.setDoubleTapToZoomEnabled(false);
                    assetChart.setData(lineData);

                    assetChart.invalidate();
                }
            }
        }

    }
}
