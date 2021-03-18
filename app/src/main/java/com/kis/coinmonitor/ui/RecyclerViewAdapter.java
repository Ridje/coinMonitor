package com.kis.coinmonitor.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kis.coinmonitor.R;
import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.utils.Locales;

import java.math.BigDecimal;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public List<Asset> mItemList;

    public RecyclerViewAdapter(List<Asset> itemList) {
        mItemList = itemList;
    }

    public void addAssets(List<Asset> assets) {
        mItemList.addAll(assets);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_row, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ((RecyclerViewHolder) viewHolder).bind(mItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    abstract class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bind(Asset asset);
    }

    public class ItemViewHolder extends RecyclerViewHolder {

        TextView tvAsset_name;
        TextView tvAsset_price_usd;
        TextView tvAsset_rank;
        TextView tvAsset_symbol;
        TextView tvAsset_change_24hrs;
        TextView tvAsset_market_24hrs;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAsset_name = itemView.findViewById(R.id.asset_name);
            tvAsset_price_usd = itemView.findViewById(R.id.asset_price_usd);
            tvAsset_rank = itemView.findViewById(R.id.asset_rank);
            tvAsset_symbol = itemView.findViewById(R.id.asset_symbol);
            tvAsset_change_24hrs = itemView.findViewById(R.id.asset_change_24hrs);
            tvAsset_market_24hrs = itemView.findViewById(R.id.asset_volume_24hrs);
        }

        public void bind(Asset asset) {
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

    private class LoadingViewHolder extends RecyclerViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.assets_list_progress_bar);
        }

        void bind(Asset asset) {
        }
    }

}
