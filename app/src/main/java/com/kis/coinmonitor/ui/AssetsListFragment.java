package com.kis.coinmonitor.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kis.coinmonitor.MainActivity;
import com.kis.coinmonitor.R;
import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.utils.Preferences;
import com.kis.coinmonitor.viewmodel.AssetsListViewModel;


import java.util.Set;

public class AssetsListFragment extends Fragment implements RecyclerViewAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ProgressBar progressBarView;
    private AssetsListViewModel assetsListViewModel;
    private Asset longClickedAsset = null;
    private boolean onlyFavourite = false;
    private final static String KEY_ONLY_FAVOURITE = "ShowOnlyFavouriteCoins";

    private static final String LOG_TAG = AssetsListFragment.class.getName();

    public AssetsListFragment() {}

    public static AssetsListFragment newInstance(boolean onlyFavourite) {
        AssetsListFragment fragment = new AssetsListFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_ONLY_FAVOURITE, onlyFavourite);
        fragment.setArguments(args);
        return fragment;
    }

    public static AssetsListFragment newInstance() {
        return newInstance(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onlyFavourite = requireArguments().getBoolean(KEY_ONLY_FAVOURITE, false);
        Log.d(LOG_TAG, this.toString() + " : onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, this.toString() + " : onCreateView");
        return inflater.inflate(R.layout.fragment_assets_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.assets_list_recycler_view);
        progressBarView = view.findViewById(R.id.assets_list_progress_bar);
        initAdapter();
        assetsListViewModel = new ViewModelProvider(this).get(AssetsListViewModel.class);
        subscribeUI();
        if (savedInstanceState == null) {
            if (onlyFavourite) {
                String coins = String.join(",", Preferences.getInstance(requireActivity().getApplicationContext()).readSet(Preferences.getKeySettingFavouriteCoins()));
                assetsListViewModel.downloadAssets(coins);
            } else {
                assetsListViewModel.downloadAssets();
            }
        }
        Log.d(LOG_TAG, this.toString() + " : onViewCreated");
        initScrollListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, this.toString() + " : onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, this.toString() + " : onResume");
        assetsListViewModel.startPricesUpdating();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, this.toString() + " : onPause");
        assetsListViewModel.stopPricesUpdating();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, this.toString() + " : onStop");
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, this.toString() + " : onSaveInstanceState");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG_TAG, this.toString() + " : onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, this.toString() + " : onDestroy");
    }

    private void initAdapter() {
        recyclerViewAdapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setOnItemClickListener(this);
        //I don't like how expand animation works and I don't need original change animation
        recyclerView.setItemAnimator(null);
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!onlyFavourite &&  linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == recyclerViewAdapter.getItemCount() - 1) {
                    assetsListViewModel.downloadAssets();
                }
            }
        });
    }

    private void subscribeUI() {
        assetsListViewModel.getAssetsLiveData().observe(getViewLifecycleOwner(), assets -> recyclerViewAdapter.addAssets(assets));
        assetsListViewModel.getIsAssetsDownloadingLiveData().observe(getViewLifecycleOwner(), aBoolean -> progressBarView.setVisibility(aBoolean ? View.VISIBLE : View.GONE));
        assetsListViewModel.getAssetHistoryLiveData().observe(getViewLifecycleOwner(), asset -> {
            if (asset != null) {
                recyclerViewAdapter.setExpandedAsset(asset);
                recyclerViewAdapter.updateAsset(asset);
            }
        });
        assetsListViewModel.getUpdatedAssetsPricesLiveData().observe(getViewLifecycleOwner(), updatedAssets -> {
            if (updatedAssets != null) {
                recyclerViewAdapter.updateAssets(updatedAssets);
            }
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.coin_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_to_fav:
                if (longClickedAsset != null) {
                    Set<String> currentFavourite = Preferences.getInstance(requireActivity().getApplicationContext()).readSet(Preferences.getKeySettingFavouriteCoins());
                    currentFavourite.add(longClickedAsset.getId());
                    Preferences.getInstance(requireActivity().getApplicationContext()).writeSet(Preferences.getKeySettingFavouriteCoins(), currentFavourite);
                    recyclerViewAdapter.notifyItemChanged(recyclerViewAdapter.mItemList.indexOf(longClickedAsset));
                    return true;
                }
        }
        return super.onContextItemSelected(item);

    }

    @Override
    public void onItemClick(View view, int position) {
        Asset clickedAsset = recyclerViewAdapter.mItemList.get(position);
        Asset currentExpandedAsset = recyclerViewAdapter.getExpandedAsset();

        Log.i(LOG_TAG, String.format("%s clicked", clickedAsset.getId()));

        if (currentExpandedAsset != null) {
            recyclerViewAdapter.setExpandedAsset(null);
            recyclerViewAdapter.notifyItemChanged(recyclerViewAdapter.mItemList.indexOf(currentExpandedAsset));
        }

        if (clickedAsset != currentExpandedAsset) {
            recyclerViewAdapter.setExpandedAsset(clickedAsset);
            recyclerViewAdapter.notifyItemChanged(position);
            assetsListViewModel.downloadAssetShortHistory(clickedAsset);
        }
    }

    @Override
    public void onButtonItemClick(View view, int position) {
        final Asset asset = recyclerViewAdapter.mItemList.get(position);
        ((MainActivity) requireActivity()).showFragment(AssetFragment.newInstance(asset.getId()), true, FragmentTransaction.TRANSIT_FRAGMENT_OPEN) ;
    }

    @Override
    public void onItemLongClickListener(View view, int position) {
        longClickedAsset = recyclerViewAdapter.mItemList.get(position);
    }

}
