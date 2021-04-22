package com.kis.coinmonitor.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kis.coinmonitor.R;
import com.kis.coinmonitor.concurrency.assetsdownload.TaskDownloadAssets;
import com.kis.coinmonitor.concurrency.assetsdownload.TaskDownloadAssetsListener;
import com.kis.coinmonitor.concurrency.priceupdater.TaskListenPricesChanges;
import com.kis.coinmonitor.concurrency.priceupdater.TaskListenPricesChangesListener;
import com.kis.coinmonitor.concurrency.TaskUpdatePricesFromCache;
import com.kis.coinmonitor.concurrency.UpdateablePrices;
import com.kis.coinmonitor.model.websocketAPI.CachedPrices;
import com.kis.coinmonitor.model.standardAPI.Asset;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class AssetsListFragment extends Fragment
        implements TaskDownloadAssetsListener, TaskListenPricesChangesListener, UpdateablePrices {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    final List<Asset> listOfAssets = new ArrayList<>();
    private final Integer LIMIT_PER_DOWNLOAD = 20;
    private final CachedPrices cachePrices = new CachedPrices();
    public Integer mCurrentOffset = 0;

    private static ExecutorService serviceDownloadAssets;
    private ProgressBar mProgressBarView;
    private String updatedableAssetsList;
    Thread taskListenToPricesChanges = null;

    private static final String ASSETS_KEY = "com.kis.coinmonitor.ui.assets";

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assets_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.assets_list_recycler_view);
        mProgressBarView = view.findViewById(R.id.assets_list_progress_bar);
        initAdapter();
        restoreAssetsFromSavedState(savedInstanceState);
        if (serviceDownloadAssets == null) {
            serviceDownloadAssets = Executors.newFixedThreadPool(1);
            dowloadMore();
        }
        initScrollListener();

        taskListenToPricesChanges = new Thread(new TaskListenPricesChanges(updatedableAssetsList, cachePrices, this));
        taskListenToPricesChanges.start();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ASSETS_KEY, (ArrayList<Asset>) recyclerViewAdapter.mItemList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (taskListenToPricesChanges != null) {
            taskListenToPricesChanges.interrupt();
        }
    }

    private void restoreAssetsFromSavedState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getParcelableArrayList(ASSETS_KEY) != null) {
                List<Asset> savedListOfAssets = savedInstanceState.getParcelableArrayList(ASSETS_KEY);
                recyclerViewAdapter.addAssets(savedListOfAssets);
                mCurrentOffset = savedListOfAssets.size();
                compileAssetsParameter();
            }
        }
    }

    private void initAdapter() {
        recyclerViewAdapter = new RecyclerViewAdapter(listOfAssets);
        recyclerView.setAdapter(recyclerViewAdapter);
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
                    dowloadMore();
                }
            }
        });
    }

    private void dowloadMore() {
        if (!isAdded()) { return; }
        requireActivity().runOnUiThread(() -> mProgressBarView.setVisibility(View.VISIBLE));
        serviceDownloadAssets.execute(new TaskDownloadAssets(null, null, LIMIT_PER_DOWNLOAD, mCurrentOffset, this));
    }

    private synchronized void compileAssetsParameter() {
        updatedableAssetsList = listOfAssets.stream().map(Asset::getId).collect(Collectors.joining(","));
        if (taskListenToPricesChanges != null) {
            TaskListenPricesChanges.assetsListChanged(updatedableAssetsList);
        }
    }

    @Override
    public void onTaskPaused() {
        serviceDownloadAssets.execute(new TaskUpdatePricesFromCache(cachePrices.clearWithCopy(), listOfAssets, this));
    }

    @Override
    public void onTaskRan() {
        mCurrentOffset += LIMIT_PER_DOWNLOAD;
    }

    @Override
    public void onResponse(List list) {
        if (!isAdded()) { return; }
        requireActivity().runOnUiThread(() -> mProgressBarView.setVisibility(View.GONE));
        recyclerViewAdapter.addAssets(list);
        if (!isAdded()) { return; }
        requireActivity().runOnUiThread(() -> recyclerViewAdapter.notifyDataSetChanged());
        compileAssetsParameter();
    }

    @Override
    public void onFailure() {
        // TODO: 19-Mar-21 don't know yet what to do decide later
    }
    @Override
    public void onPricesUpdated(Integer itemPosition) {
        if (!isAdded()) { return; }
        requireActivity().runOnUiThread(() -> recyclerViewAdapter.notifyItemChanged(itemPosition));
    }

}
