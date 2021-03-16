package com.kis.coinmonitor.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kis.coinmonitor.R;
import com.kis.coinmonitor.network.APIConnector;
import com.kis.coinmonitor.model.Asset;
import com.kis.coinmonitor.model.Assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetsListFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    APIConnector connector = APIConnector.getAPIConnector();
    ArrayList<Asset> listOfAssets = new ArrayList<>();
    private Integer currentOffset = 0;
    private final Integer LIMIT_PER_DOWNLOAD = 20;

    volatile boolean isLoading = false;

    public AssetsListFragment() {
    }

    public static AssetsListFragment newInstance() {
        AssetsListFragment fragment = new AssetsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentOffset = savedInstanceState.getInt("currentOffset");
            if (savedInstanceState.getParcelableArrayList("assets") != null)
                listOfAssets = savedInstanceState.getParcelableArrayList("assets");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!isLoading) {
            outState.putInt("currentOffset", currentOffset);
            outState.putParcelableArrayList("assets", (ArrayList<Asset>) recyclerViewAdapter.mItemList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_assets_list, container, false);

        return inflatedView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.assets_list_recycler_view);
        initAdapter();
        initScrollListener();
        if (listOfAssets.isEmpty()) {
            loadMore();
        }
    }

    private void initAdapter() {
        recyclerViewAdapter = new RecyclerViewAdapter(listOfAssets);
        recyclerView.setAdapter(recyclerViewAdapter);
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

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == recyclerViewAdapter.getItemCount() - 1) {
                        {
                            loadMore();
                        }
                    }
                }
            }
        });
    }

    private void loadMore() {
        isLoading = true;
        recyclerViewAdapter.addLoadingBar();
        connector.getAssets(null, null, LIMIT_PER_DOWNLOAD, currentOffset, new Callback<Assets>() {
            @Override
            public void onResponse(Call<Assets> call, Response<Assets> response) {
                recyclerViewAdapter.removeLoadingBar();
                recyclerViewAdapter.addAssets(response.body().getData());
                currentOffset = recyclerViewAdapter.getItemCount();
                isLoading = false;
            }

            @Override
            public void onFailure(Call<Assets> call, Throwable t) {
                recyclerViewAdapter.removeLoadingBar();
                isLoading = false;
            }
        });
    }

}