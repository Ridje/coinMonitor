package com.kis.coinmonitor.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kis.coinmonitor.R;
import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.utils.Locales;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final List<Asset> mItemList;

    public RecyclerViewAdapter(List<Asset> itemList) {
        mItemList = itemList;
    }

    public void addAssets(List<Asset> assets) {
        mItemList.addAll(assets);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_row, parent, false);
            return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        viewHolder.setIsRecyclable(false);
        ((ItemViewHolder) viewHolder).bind(mItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {

        final TextView tvAsset_name;
        final TextView tvAsset_price_usd;
        final TextView tvAsset_rank;
        final TextView tvAsset_symbol;
        final TextView tvAsset_change_24hrs;
        final TextView tvAsset_market_24hrs;
        final CardView cardView;
        private Boolean firstInit = true;
        final ValueAnimator positiveChange;
        final ValueAnimator negativeChange;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.asset_holder);
            tvAsset_name = itemView.findViewById(R.id.asset_name);
            tvAsset_price_usd = itemView.findViewById(R.id.asset_price_usd);
            tvAsset_rank = itemView.findViewById(R.id.asset_rank);
            tvAsset_symbol = itemView.findViewById(R.id.asset_symbol);
            tvAsset_change_24hrs = itemView.findViewById(R.id.asset_change_24hrs);
            tvAsset_market_24hrs = itemView.findViewById(R.id.asset_volume_24hrs);

            positiveChange = ObjectAnimator.ofInt(cardView, "CardBackgroundColor",
                    cardView.getCardBackgroundColor().getDefaultColor(),
                    ContextCompat.getColor(cardView.getContext(), R.color.negative_changes));
            positiveChange.setDuration(550);
            positiveChange.setEvaluator(new ArgbEvaluator());
            positiveChange.setRepeatCount(1);
            positiveChange.setRepeatMode(ValueAnimator.REVERSE);

            negativeChange = ObjectAnimator.ofInt(cardView, "CardBackgroundColor",
                    cardView.getCardBackgroundColor().getDefaultColor(),
                    ContextCompat.getColor(cardView.getContext(), R.color.positive_changes));
            negativeChange.setDuration(550);
            negativeChange.setEvaluator(new ArgbEvaluator());
            negativeChange.setRepeatCount(1);
            negativeChange.setInterpolator(new AccelerateInterpolator());
            negativeChange.setRepeatMode(ValueAnimator.REVERSE);
        }


        public void bind(Asset asset) {

            if (!firstInit) {
                if (((BigDecimal) Locales.parseCurrency((String) tvAsset_price_usd.getText())).compareTo(asset.getPriceUsd()) == -1) {
                    positiveChange.start();
                } else if (((BigDecimal) Locales.parseCurrency((String) tvAsset_price_usd.getText())).compareTo(asset.getPriceUsd()) == 1) {
                    negativeChange.start();
                }
            }

            firstInit = false;

            tvAsset_name.setText(asset.getName());
            tvAsset_price_usd.setText(Locales.formatCurrency(asset.getPriceUsd()));
            tvAsset_rank.setText(asset.getRank().toString());
            tvAsset_symbol.setText(asset.getSymbol());
            tvAsset_change_24hrs.setText(Locales.formanCurrencyWithPercents(asset.getChangePercent24Hr()));
            tvAsset_market_24hrs.setText(Locales.formatCompactCurrency(asset.getVolumeUsd24Hr()));
            if (asset.getChangePercent24Hr() != null && asset.getChangePercent24Hr().compareTo(BigDecimal.ZERO) < 0) {
                tvAsset_change_24hrs.setTextColor(tvAsset_change_24hrs.getResources().getColor(R.color.negative_number, tvAsset_change_24hrs.getContext().getTheme()));
            } else {
                tvAsset_change_24hrs.setTextColor(tvAsset_change_24hrs.getResources().getColor(R.color.positive_number, tvAsset_change_24hrs.getContext().getTheme()));
            }
        }

    }


}
