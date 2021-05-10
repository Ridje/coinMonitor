package com.kis.coinmonitor.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kis.coinmonitor.R;
import com.kis.coinmonitor.model.standardAPI.Asset;
import com.kis.coinmonitor.viewmodel.AssetsListViewModel;

public class AssetsListFragment extends Fragment implements RecyclerViewAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ProgressBar progressBarView;
    private AssetsListViewModel assetsListViewModel;
    private Asset lastExpandedAsset;


    private static final String LOG_TAG = AssetsListFragment.class.getName();

    public AssetsListFragment() {}


    public static AssetsListFragment newInstance() {
        AssetsListFragment fragment = new AssetsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setOnItemClickListener(this);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
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
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == recyclerViewAdapter.getItemCount() - 1) {
                    assetsListViewModel.downloadAssets();
                }
            }
        });
    }

    private void subscribeUI() {
        assetsListViewModel.getIsAssetsDownloading().observe(getViewLifecycleOwner(), aBoolean ->
                progressBarView.setVisibility(aBoolean ? View.VISIBLE : View.GONE));
        assetsListViewModel.getAssetsListObservable().observe(getViewLifecycleOwner(), assets -> {
            if (assets != null) {
                recyclerViewAdapter.addAssets(assets);
            }
        });
        assetsListViewModel.getUpdatedAssetsPricesLiveData().observe(getViewLifecycleOwner(), updatedAssets -> {
            if (updatedAssets != null) {
                recyclerViewAdapter.updateAssets(updatedAssets);
            }
        });
        assetsListViewModel.getDownloadedAssetHistoryLiveData().observe(getViewLifecycleOwner(), asset ->
        {
            if (asset != null) {
                recyclerViewAdapter.updateAsset(asset);
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        assetsListViewModel.downloadAssetHistory(recyclerViewAdapter.mItemList.get(position));
    }

    @Override
    public void onButtonItemClick(View view, int position) {
        final Asset asset = recyclerViewAdapter.mItemList.get(position);
        FragmentManager thisManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = thisManager.beginTransaction();
        transaction.replace(R.id.fragments_container, AssetFragment.newInstance());
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
